package player;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MusicPlayer {

	public static void main(String[] args) {
		ExecutorService executorService;

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
						} else {
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
