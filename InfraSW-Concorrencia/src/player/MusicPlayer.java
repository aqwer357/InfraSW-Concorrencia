package player;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MusicPlayer {
	private final static Playlist playlist = new Playlist();
	
	public static void main(String[] args) {
		SongListCellRenderer customCellRenderer = new SongListCellRenderer();
		final JFileChooser fc = new JFileChooser();

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

		// Declarando os ActionListeners dos botoes
		addSong.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent button) {
				int r = fc.showOpenDialog(null);

				if (r == JFileChooser.APPROVE_OPTION) {
					String songName = fc.getSelectedFile().getName();
					
					playlist.addSong(songName);
					songList.setListData(playlist.getPlaylist().toArray());
					
				}
			}
		});

		frame.getContentPane().add(BorderLayout.NORTH, northPanel);
		frame.getContentPane().add(BorderLayout.CENTER, scrollPane);
		frame.getContentPane().add(BorderLayout.SOUTH, southPanel);
		frame.setVisible(true);

	}

}
