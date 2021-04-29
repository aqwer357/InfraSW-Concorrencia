package player;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
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
	private static int songSelectedIndex;
	private static ArrayList<Integer> shufflePlaylist = new ArrayList<Integer>(); // Lista de indexes das musicas que
																					// faltam ser tocadas
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
	private static JLabel currentSong = new JLabel("Currently playing:..."); // Mostra a musica sendo tocada
	private static JButton removeSong = new JButton("X"); // Botao que remove a musica selecionada na lista

	// Array de timers, cada musica sera um novo thread
	private static Timer timer = new Timer(progress, 0);

	public static void main(String[] args) {
		SongListCellRenderer customCellRenderer = new SongListCellRenderer();
		final JFileChooser fc = new JFileChooser();

		JFrame frame = new JFrame("Music Player");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 375);

		// Adicionando os componentes ao painel superior
		northPanel.add(progress);
		northPanel.add(previous);
		northPanel.add(playPause);
		northPanel.add(next);
		northPanel.add(randomBtn);
		northPanel.add(addSong);

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

					if (random) { // Caso se adicione mais musicas a playlist no modo aleatorio, e necessario
									// reembaralhar shufflePlaylist
						
						index = shufflePlaylist.get(index); // Alterando o index para poder manipular corretamente
															// shufflePlaylist
						
						shufflePlaylist.clear();
						// Thread para criar a playlist aleatoria, serve para evitar que a GUI sofra de
						// latencia
						Thread createShuffle = new Thread(CreateShufflePlaylist);
						createShuffle.start();
					}

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
				songSelectedIndex = songList.getSelectedIndex(); // Obtem o index do item selecionado

				if (songSelectedIndex != -1) { // -1 == nenhum item foi selecionado na GUI
					playlist.removeSong(songSelectedIndex);// Remove a musica da playlist
					songList.setListData(playlist.getPlaylist().toArray());

					// Se remover uma musica, shufflePlaylist deve ser reembaralhada
					if (random) {
						index = shufflePlaylist.get(index); // Alterando o index para poder manipular corretamente
															// shufflePlaylist

						// Caso a musica tocando seja removida, a que tomou seu index na playlist e
						// tocada
						if (index == songSelectedIndex) {
							random = false;
							start(0);
							random = true; // Essa coisa do random eh p ter certeza q ele so comece a tocar a musica certa msm, sem ser em ordem aleatoria
						}
						shufflePlaylist.clear();
						Thread createShuffle = new Thread(CreateShufflePlaylist);
						createShuffle.start();

					} else if (songSelectedIndex == index) {// Caso a musica que esta tocando seja removida da playlist,
															// a proxima toca
						start(0);
					}

				}
			}
		});

		playPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent button) {
				if (queueIsEmpty) { // Se nao tem musica selecionada e nem esta tocando, comeca da primeira musica
					queueIsEmpty = false;
					index = 0;
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
				random = !random; // Alterna o modo shuffle quando o botao eh pressionado
				if (random) {
					Thread createShuffle = new Thread(CreateShufflePlaylist);
					createShuffle.start();
					randomBtn.setText("-?");
				} else {
					random = false;
					index = shufflePlaylist.get(index); // Caso desative shuffle, volta a ordem normal a partir da
														// musica atual
					shufflePlaylist.clear();
					randomBtn.setText("->");
				}
			}
		});

		progress.setStringPainted(true);
		progress.setString("00:00");

		// Tornando a GUI visivel
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

		if (index + range < playlist.getPlaylist().size()) { // verfica se ha musica no index + range
			index += range; // atualiza o index

			// Para caso se aperte previous na primeira musica da fila
			if (index < 0)
				index = 0;

			Song song;

			if (random && shufflePlaylist.size() > 0) {
				int shuffleIndex = shufflePlaylist.get(index);
				song = playlist.getPlaylist().get(shuffleIndex);
			} else {
				song = playlist.getPlaylist().get(index);
			}
			currentSong.setText("Current playing: " + song.getName());

			// Criando novo thread para a musica nova
			timer.newSong(song.getDuration());
			exec.submit(timer);
			queueIsEmpty = false;
		} else { // Quando a playlist acaba, colocamos o timer como 00:00
			currentSong.setText("Current playing: ...");
			progress.setString("00:00");
			timer.newSong(0);
			queueIsEmpty = true;
		}
	}

	static Runnable CreateShufflePlaylist = () -> {
		for (int i = 0; i < playlist.getPlaylist().size(); i++) { // Adiciona-se os indexes a shufflePlaylist
			if (i != index) { // Exceto o da musica atual, que sera sempre a primeira
				shufflePlaylist.add(i);
			}
		}

		Collections.shuffle(shufflePlaylist); // Mistura shufflePlaylist

		shufflePlaylist.add(0, index);

		index = 0;

	};

}
