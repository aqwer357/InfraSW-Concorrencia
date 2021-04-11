package player;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Player implements Runnable{
    private static volatile boolean running = true;
    private static volatile boolean paused = false;

    private final static SwingWorker<Boolean, Integer> musicTime = new SwingWorker<Boolean, Integer>() {

        @Override
        protected Boolean doInBackground() throws Exception {
            // Contando o tempo de execu��o da m�sica
            for (int i = 0; i <= 10; i++) {
                Thread.sleep(1000);
                publish(i);
            }

            // Podemos utilizar um boolean para dizer quando a m�sica termina?
            return true;
        }

        // Pode atualizar a GUI aqui, executa depois do de doInBackground
        protected void done() {
            boolean status;
            try {
                // Retrieve the return value of doInBackground.
                status = get();
                running = false;
            } catch (InterruptedException e) {
                // This is thrown if the thread's interrupted.
            } catch (ExecutionException e) {
                // This is thrown if we throw an exception
                // from doInBackground.
            }
        }

        @Override
        // Pode atualizar a GUI aqui, recebe o que for passado por publish() em doInBackground
        protected void process(List<Integer> chunks) {
            int mostRecentValue = chunks.get(chunks.size()-1);
            MusicPlayer.setProgress(mostRecentValue);
        }
    };


    public void run() {
        while (running) {
            synchronized (musicTime) {
                if (!running) { // may have changed while waiting to
                    // synchronize on pauseLock
                    break;
                }
                if (paused) {
                    try {
                        synchronized (musicTime) {
                            musicTime.wait(); // will cause this Thread to block until
                            // another thread calls pauseLock.notifyAll()
                            // Note that calling wait() will
                            // relinquish the synchronized lock that this
                            // thread holds on pauseLock so another thread
                            // can acquire the lock to call notifyAll()
                            // (link with explanation below this code)
                        }
                    } catch (InterruptedException ex) {
                        break;
                    }
                    if (!running) { // running might have changed since we paused
                        break;
                    }
                }
            }
            musicTime.execute();
        }
    }

    public void stop() {
        running = false;
        // you might also want to interrupt() the Thread that is
        // running this Runnable, too, or perhaps call:
        resume();
        // to unblock
    }

    public void pause() {
        // you may want to throw an IllegalStateException if !running
        paused = true;
    }

    public void resume() {
        synchronized (musicTime) {
            paused = false;
            musicTime.notifyAll(); // Unblocks thread
        }
    }

    public static boolean isPaused() {
        return paused;
    }

    public static void setPaused(boolean paused) {
        Player.paused = paused;
    }

    public static boolean isRunning() {
        return running;
    }

    public static void setRunning(boolean running) {
        Player.running = running;
    }
}