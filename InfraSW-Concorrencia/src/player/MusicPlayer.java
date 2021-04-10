package player;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.*;
import java.awt.*;

public class MusicPlayer {

	public static void main(String[] args) {
		ExecutorService executorService;
		SongListCellRenderer customCellRenderer = new SongListCellRenderer();
		JFrame frame = new JFrame("My First GUI");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 300);

		// Criando o painel de cima
		JPanel northPanel = new JPanel();
		JProgressBar progress = new JProgressBar(); // Barra de progresso da musica
		JButton previous = new JButton("<<"); // Botao para musica anterior
		JButton playPause = new JButton(">/||"); // Botao play/pause
		JButton next = new JButton(">>"); // Botao para proxima musica
		JButton addSong = new JButton("Add..."); // Botao para adicionar musica

		// Adicionando os componentes declarados acima ao painel
		northPanel.add(progress);
		northPanel.add(previous);
		northPanel.add(playPause);
		northPanel.add(next);
		northPanel.add(addSong);

		// Criando o painel que vai mostrar a playlist no meio
		JScrollPane scrollPane = new JScrollPane();
		JList songList = new JList();
		songList.setCellRenderer(customCellRenderer); // Altera o que vai ser exibido do objeto na lista
		scrollPane.setViewportView(songList);

		// Criando o painel inferior
		JPanel southPanel = new JPanel();
		JLabel currentSong = new JLabel("Currently playing: ..."); // Mostra a musica sendo tocada
		currentSong.setFont(new Font("Tahoma", Font.PLAIN, 16));
		JButton removeSong = new JButton("X"); // Botao que remove a musica selecionada na lista

		// Adicionando os componentes acima ao painel inferior
		southPanel.add(currentSong);
		southPanel.add(removeSong);

		frame.getContentPane().add(BorderLayout.NORTH, northPanel);
		frame.getContentPane().add(BorderLayout.CENTER, scrollPane);
		frame.getContentPane().add(BorderLayout.SOUTH, southPanel);
		frame.setVisible(true);
		
		try {
			executorService = Executors.newFixedThreadPool(2);
			Scanner in = new Scanner(System.in);
			final Playlist playlist = new Playlist();

			Runnable addRemoveSong = () -> {
				while (true) {
					try {
						String command = in.next();
						String song = in.next();

						if (command.equals("add")) {
							playlist.addSong(song);
						} else if (command.equals("remove")) {
							playlist.removeSong(song);
						}
					} catch (InterruptedException e) {
						System.out.println("Erro em addRemoveSong()");
					}
				}

			};

			Runnable showPlaylist = () -> {
				while (true) {
					try {
						playlist.showPlaylist();
						songList.setListData(playlist.getPlaylist().toArray());

					} catch (InterruptedException e) {
						System.out.println("Erro em showPlaylist()");
					}
				}
			};

			executorService.submit(showPlaylist);
			executorService.submit(addRemoveSong);

		} catch (Exception ignored) {

		}
	}

}
