package player;

import javax.xml.transform.stream.StreamSource;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Playlist {
    private final   ArrayList<String> playlist = new ArrayList();
    private final Lock lock = new ReentrantLock();
    private final Condition stackEmptyCondition = lock.newCondition();
    private final Condition stackFullCondition = lock.newCondition();

    public void add(String song) throws InterruptedException{
        try{
            lock.lock();
            playlist.add(song);
            System.out.println(song + " added to playlist!");

        }finally {
            lock.unlock();
        }
    }

    public void remove(String song) throws InterruptedException{
        try{
            lock.lock();
            playlist.add(song);
            System.out.println(song + " added to playlist!");

        }finally {
            lock.unlock();
        }
    }

    public void show() throws InterruptedException{
        try{
            lock.lock();
            int i = 0;
            for(String song: playlist){
                System.out.println(i + " - " + song);
                i++;
            }
        }finally {
            lock.unlock();
        }
    }
}