package player;

import java.util.ArrayList;

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