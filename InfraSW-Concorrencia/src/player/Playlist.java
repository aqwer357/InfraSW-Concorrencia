package player;

import java.util.Random;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Playlist {
	private final ArrayList<Song> playlist = new ArrayList();

	public void addSong(String song) {
		Song Song = new Song(song);
		playlist.add(Song);
		System.out.println(song + " added to the playlist!");

	}

	public void removeSong(String song) {
		for (int i = 0; i < playlist.size(); i++)
			if (playlist.get(i).getName().equals(song)) {
				playlist.remove(i);
				i--;
			}

		System.out.println(song + " removed from the playlist!");

	}

	public void showPlaylist() {
		int arrSize = playlist.size();
		int i = 0;
		for (Song song : playlist) {
			System.out.println(i + " - " + song.getName());
			i++;
		}

	}

	public ArrayList<Song> getPlaylist() {
		return playlist;
	}
}