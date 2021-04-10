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

	public void removeSong(int songIndex) {
		playlist.remove(songIndex);
	}

	public ArrayList<Song> getPlaylist() {
		return playlist;
	}
}