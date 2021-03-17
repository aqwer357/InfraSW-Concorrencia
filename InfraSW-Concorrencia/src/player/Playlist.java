package player;

import javax.xml.transform.stream.StreamSource;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Playlist {
	private final ArrayList<String> playlist = new ArrayList();
	private final Lock lock = new ReentrantLock();
	private final Condition noUpdateCondition = lock.newCondition();

	public void addSong(String song) throws InterruptedException {
		try {
			lock.lock();
			playlist.add(song);
			System.out.println(song + " added to the playlist!");
			noUpdateCondition.signalAll();

		} finally {
			lock.unlock();
		}
	}

	public void removeSong(String song) throws InterruptedException {
		try {
			lock.lock();
			for (int i = 0; i < playlist.size(); i++)
				if (playlist.get(i).equals(song)) {
					playlist.remove(i);
					i--;
				}
			
			System.out.println(song + " removed from the playlist!");
			noUpdateCondition.signalAll();

		} finally {
			lock.unlock();
		}
	}

	public void showPlaylist() throws InterruptedException {
		try {
			lock.lock();
			int arrSize = playlist.size();
			noUpdateCondition.await();
			int i = 0;
			for (String song : playlist) {
				System.out.println(i + " - " + song);
				i++;
			}
		} finally {
			lock.unlock();
		}
	}
}