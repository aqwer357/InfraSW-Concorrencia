package player;

import javax.swing.*;
import java.awt.*;

public class GUI {

	public static void main(String args[]){
	       JFrame frame = new JFrame("My First GUI");
	       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	       frame.setSize(400,300);
	       
	       // Criando o painel de cima
	       JPanel northPanel = new JPanel();
	       JProgressBar progress = new JProgressBar(); 		// Barra de progresso da musica
	       JButton previous = new JButton("<<");		    // Botao para musica anterior
	       JButton playPause = new JButton(">/||");		    // Botao play/pause
	       JButton next = new JButton(">>"); 			    // Botao para proxima musica
	       JButton addSong = new JButton("Add...");			// Botao para adicionar musica
	       
	       // Adicionando os componentes declarados acima ao painel
	       northPanel.add(progress);
	       northPanel.add(previous);
	       northPanel.add(playPause);
	       northPanel.add(next);
	       northPanel.add(addSong);
	       
	       // Criando o painel que vai mostrar a playlist no meio
	       JScrollPane scrollPane = new JScrollPane();
	       JList playlist = new JList();
	       scrollPane.setViewportView(playlist);
	       
	       // Criando o painel inferior
	       JPanel southPanel = new JPanel();
	       JLabel currentSong= new JLabel("Currently playing: ..."); // Mostra a musica sendo tocada
	       currentSong.setFont(new Font("Tahoma", Font.PLAIN, 16));
	       JButton removeSong = new JButton("X"); // Botao que remove a musica selecionada na lista
	       
	       // Adicionando os componentes acima ao painel inferior
	       southPanel.add(currentSong);
	       southPanel.add(removeSong);
	       
	       
	       frame.getContentPane().add(BorderLayout.NORTH, northPanel);
	       frame.getContentPane().add(BorderLayout.CENTER, scrollPane);
	       frame.getContentPane().add(BorderLayout.SOUTH, southPanel);
	       frame.setVisible(true);
	}

}
