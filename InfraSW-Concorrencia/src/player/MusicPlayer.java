package player;

import java.util.Scanner;


public class MusicPlayer {

	public static void main(String[] args) {
		try {
			Scanner in = new Scanner(System.in);
			
			final Playlist playlist = new Playlist();
			
			Runnable addRemoveSong = () -> {
				while (true) {
					try {
						String command = in.next();
						String song = in.next();
						
						if (command.equals("add")) {
							playlist.add(song);
						} else {
							playlist.remove(song);
						}
						
					} catch (InterruptedException e) {
						System.out.println("Erro em addRemoveSong()");
					}
				}
				
			};
			
			Runnable showPlaylist = () -> {
				while (true) {
					try {
						playlist.show();
						
					} catch (InterruptedException e) {
						System.out.println("Erro em showPlaylist()");
					}
				}
			};
	
			
			addRemoveSong.run();
			
		} catch (Exception ignored) {
			
		}
	}

}
