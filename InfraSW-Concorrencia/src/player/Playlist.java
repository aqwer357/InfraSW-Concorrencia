package player;

import java.util.ArrayList;

public class Playlist {
	private final ArrayList<Song> playlist = new ArrayList();

	public void addSong(String song) {
		Song Song = new Song(song);
		playlist.add(Song);

	}

	public void removeSong(int songIndex) {
		playlist.remove(songIndex);
	}

	public ArrayList<Song> getPlaylist() {
		return playlist;
	}
	
	public Song getLastSong() {
		return playlist.get(playlist.size() - 1);
	}
}