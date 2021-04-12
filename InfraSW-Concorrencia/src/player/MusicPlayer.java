package player;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MusicPlayer {
	private final static Playlist playlist = new Playlist();
	private static int index = 0;
	private static boolean isPlaying = false;

	// Criando o painel de cima
	private static JPanel northPanel = new JPanel();
	private static JProgressBar progress = new JProgressBar(); // Barra de progresso da musica
	private static JButton previous = new JButton("<<"); // Botao para musica anterior
	private static JButton playPause = new JButton(">/||"); // Botao play/pause
	private static JButton next = new JButton(">>"); // Botao para proxima musica
	private static JButton addSong = new JButton("Add..."); // Botao para adicionar musica

	// ScrollPane para exibir a playlist
	private static JScrollPane scrollPane = new JScrollPane();
	private static JList songList = new JList();

	// Criando o painel inferior
	private static JPanel southPanel = new JPanel();
	private static JLabel currentSong =new JLabel("Currently "); // Mostra a musica sendo tocada
	private static JButton removeSong = new JButton("X"); // Botao que remove a musica selecionada na lista

	// SwingWorker que vai "tocar" a musica
	private static Player musicTime = new Player(progress, 0);

	public static void main(String[] args) {
		SongListCellRenderer customCellRenderer = new SongListCellRenderer();
		final JFileChooser fc = new JFileChooser();

		JFrame frame = new JFrame("Music Player");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 300);

		// Adicionando os componentes ao painel superior
		northPanel.add(progress);
		northPanel.add(previous);
		northPanel.add(playPause);
		northPanel.add(next);
		northPanel.add(addSong);

		// Criando o painel que vai mostrar a playlist no meio
		songList.setCellRenderer(customCellRenderer); // Altera o que vai ser exibido do objeto na lista
		scrollPane.setViewportView(songList);

		currentSong.setFont(new Font("Tahoma", Font.PLAIN, 16));

		// Adicionando os componentes acima ao painel inferior
		southPanel.add(currentSong);
		southPanel.add(removeSong);

		// Declarando os ActionListeners dos botoes
		addSong.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent button) {
				int r = fc.showOpenDialog(null); // Abre a janela para selecionar o arquivo

				if (r == JFileChooser.APPROVE_OPTION) {
					String songName = fc.getSelectedFile().getName(); // Obtem o nome do arquivo selecionado

					playlist.addSong(songName); // Adiciona a musica na playlist
					songList.setListData(playlist.getPlaylist().toArray()); // Atualiza a lista da GUI
					if( playlist.getPlaylist().size() == 1 && !isPlaying) {
						index = 0;
						start(0);
					}
				}
			}
		});

		removeSong.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent button) {
				int songSelectedIndex = songList.getSelectedIndex(); // Obtem o index do item selecionado

				playlist.removeSong(songSelectedIndex); // Remove a musica da playlist
				songList.setListData(playlist.getPlaylist().toArray());

			}
		});

		playPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent button) {
				int songSelectedIndex = songList.getSelectedIndex();
				if(songSelectedIndex >= 0){                  //verifica se há som selecionado
					if(isPlaying){							 // se sim e estiver tocando algo, cancela a música atual
						index = songSelectedIndex-1;		// decrementa o index pois a dunção done da música cancelada chamará start+1, mas não queremos range
						musicTime.cancel(true);
					}else {                                 // Se não estiver tocando começa a partir da música selecionada
						index = songSelectedIndex;
						start(0);
					}
				}
				else if(!isPlaying) {  //Se não tem música selecionada e nem está tocando, começa da primeira música
					index = 0;
					start(0);
				}
				else{
					if(!musicTime.isRunning()){ //Se tiver música tocando, começa da primeira música
						index = 0;
						start(0);
					}
					else if(musicTime.isPaused()) {
						currentSong.setText("Currently playing: "+ playlist.getPlaylist().get(index).getName());
						musicTime.resume();
						isPlaying = true;
					}
					else {
						currentSong.setText("Currently paused:"+ playlist.getPlaylist().get(index).getName());
						musicTime.pause();
					}
				}
			}
		});


		progress.setStringPainted(true);
		progress.setString("0/0");



		frame.getContentPane().add(BorderLayout.NORTH, northPanel);
		frame.getContentPane().add(BorderLayout.CENTER, scrollPane);
		frame.getContentPane().add(BorderLayout.SOUTH, southPanel);
		frame.setVisible(true);


	}

	/// Inicia música no index + range
	//  Range pode ser utilizado para reposicionar o index no final de músicas, next e prev
	public static void start(int range){
		progress.setString("loading...");

		if(index +range < playlist.getPlaylist().size()){        //verfica se há música no index + range
			index +=range;										 // atualiza o index
			musicTime = new Player(progress, 0);
			Song song =  playlist.getPlaylist().get(index);
			String song_name = song.getName();
			int song_duration = song.getDuration();
			currentSong.setText("Current playing: "+song_name);
			musicTime.setMusicLength(song_duration);
			musicTime.execute();
			isPlaying = true;
		}else {
			index = 0;
			progress.setString("0/0");
			isPlaying = false;
		}
	}

}
