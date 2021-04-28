package player;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Random;

public class MusicPlayer {
	private final static Playlist playlist = new Playlist();
	private static int index = 0;
	private static boolean queueIsEmpty = true;
	private static boolean random = false;
	private static ArrayList<Integer> lastIndexes = new ArrayList<Integer>(); //Lista de indexes das musicas que faltam ser tocadas
	private static ExecutorService exec = Executors.newFixedThreadPool(1);

	// Criando o painel de cima
	private static JPanel northPanel = new JPanel();
	private static JProgressBar progress = new JProgressBar(); // Barra de progresso da musica
	private static JButton previous = new JButton("<<"); // Botao para musica anterior
	private static JButton playPause = new JButton(">/||"); // Botao play/pause
	private static JButton next = new JButton(">>"); // Botao para proxima musica
	private static JButton addSong = new JButton("Add..."); // Botao para adicionar musica
	private static JButton randomBtn = new JButton("->"); // Botao para ativar o modo aleatorio

	// ScrollPane para exibir a playlist
	private static JScrollPane scrollPane = new JScrollPane();
	private static JList songList = new JList();

	// Criando o painel inferior
	private static JPanel southPanel = new JPanel();
	private static JLabel currentSong = new JLabel("Currently "); // Mostra a musica sendo tocada
	private static JButton removeSong = new JButton("X"); // Botao que remove a musica selecionada na lista

	// Array de timers, cada musica sera um novo thread
	private static Timer timer = new Timer(progress, 0);

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
		northPanel.add(randomBtn);

		// Criando o painel que vai mostrar a playlist no meio
		songList.setCellRenderer(customCellRenderer); // Altera o que vai ser exibido do objeto Song na lista
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
					lastIndexes.add(playlist.getPlaylist().size()-1);
					if (playlist.getPlaylist().size() == 1 && queueIsEmpty) {
						queueIsEmpty = false;
						index = 0;
						start(0);
					}
				}
			}
		});

		removeSong.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent button) {
				int songSelectedIndex = songList.getSelectedIndex(); // Obtem o index do item selecionado

				playlist.removeSong(songSelectedIndex);// Remove a musica da playlist
				songList.setListData(playlist.getPlaylist().toArray());
				lastIndexes.remove(songSelectedIndex);
				// Caso a musica que esta tocando seja removida da playlist, a proxima toca
				if (songSelectedIndex == index)
					start(0);

			}
		});

		playPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent button) {
				if (queueIsEmpty) { // Se nao tem musica selecionada e nem esta tocando, comeca da primeira musica
					queueIsEmpty = false;
					index = 0;
					fillIndexes();
					start(0);
				} else if (timer.isPaused()) {
					currentSong.setText("Currently playing: " + playlist.getPlaylist().get(index).getName());
					timer.resume();
				} else {
					currentSong.setText("Currently paused:" + playlist.getPlaylist().get(index).getName());
					timer.pause();
				}
			}
		});

		previous.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent button) {
				progress.setString("loading...");
				start(-1); // Comeca a tocar a musica anterior da fila
			}
		});

		next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent button) {
				progress.setString("loading...");
				start(1); // Comeca a tocar a proxima musica da fila
			}
		});

		randomBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent button) {
				random = !random;
				if(random) {
					randomBtn.setText("?");
					fillIndexes();
				}else {
					randomBtn.setText("->");
					lastIndexes.clear();
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

	// Inicia musica no index + range
	// Range pode ser utilizado para reposicionar o index no final de musicas, next
	// e prev
	public static void start(int range) {
		progress.setString("loading...");

		if(!lastIndexes.isEmpty()){ //Se a playlist não está vazia entao e aleatorio, pois o modo sequncial sempre limpa a lista de indexes
			index = getIndex();
			range = 0;
		}else if(random){  //Se está vazia e é aleatorio precisa parar
			range = playlist.getPlaylist().size();
		}

		if (index + range < playlist.getPlaylist().size()) { // verfica se ha musica no index + range
			index += range; // atualiza o index

			// Para caso se aperte previous na primeira musica da fila
			if (index < 0)
				index = 0;

			Song song = playlist.getPlaylist().get(index);
			currentSong.setText("Current playing: " + song.getName());

			// Caso o thread ja tenha sido executado, eh necessario criar uma nova instancia
			// dele
			timer.newSong(song.getDuration());
			System.out.println("Index: " + index);
			exec.submit(timer);
			queueIsEmpty = false;
		} else {
			index = 0;
			currentSong.setText("Current playing: ...");
			progress.setString("0/0");
			queueIsEmpty = true;
			timer.newSong(0);
			exec.submit(timer);
		}
	}

	//Escolhe um index randomicamente que ainda não foi escolhido
	private static int getIndex() {
		int aux = 0, newIndex = 0; // recebe uma posicao na lista de indice
		Random rnd = new Random();


		if (lastIndexes.size() > 1) {                         //se a lista tiver 1 item ou 0 não tem opcoes para ser aleatorio
			aux = rnd.nextInt(lastIndexes.size() - 1); //aux recebe uma posicao existente na lista de indexes
			newIndex = lastIndexes.get(aux);
			lastIndexes.remove(aux);
		}else{                                                // se tive apenas uma música, remove-la
			lastIndexes.remove(0);
		}


		return newIndex;
	}

	private static void fillIndexes(){
		for (int i = 0; i < playlist.getPlaylist().size(); i++){
			lastIndexes.add(i);
		}
	}


}

