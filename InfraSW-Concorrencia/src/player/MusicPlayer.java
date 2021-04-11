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
	private static JLabel currentSong = new JLabel("Currently playing: ..."); // Mostra a musica sendo tocada
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
				if (musicTime.isPaused())
					musicTime.resume();
				else
					musicTime.pause();
				
			}
		});

		progress.setStringPainted(true);
		progress.setString("X/X");

		musicTime.execute(); // Inicia o thread de tempo de música

		frame.getContentPane().add(BorderLayout.NORTH, northPanel);
		frame.getContentPane().add(BorderLayout.CENTER, scrollPane);
		frame.getContentPane().add(BorderLayout.SOUTH, southPanel);
		frame.setVisible(true);

	}

}
