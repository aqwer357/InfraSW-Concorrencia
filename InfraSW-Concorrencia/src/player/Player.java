package player;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.*;

public class Player extends SwingWorker<Boolean, Integer> {
	private boolean paused = false;
	private JProgressBar progress = null;
	private int musicLength = 0;
	
	public Player(JProgressBar pBar, int mLength) {
		this.progress = pBar;
		this.musicLength = mLength;
	}

	public void pause() {
		paused = true;
	}

	public void resume() {
		paused = false;
		this.notify();
	}

	public boolean isPaused() {
		return paused;
	}

	@Override
	protected Boolean doInBackground() throws Exception {
		// Contando o tempo de execução da música
		for (int i = 0; i <= 10; i++) {
			if (paused)
				wait();

			Thread.sleep(1000);
			publish(i);
		}

		// Podemos utilizar um boolean para dizer quando a música termina?
		return true;
	}

	// Pode atualizar a GUI aqui, executa depois do de doInBackground
	protected void done() {

		boolean status;
		try {
			// Retrieve the return value of doInBackground.
			status = get();
		} catch (InterruptedException e) {
			// This is thrown if the thread's interrupted.
		} catch (ExecutionException e) {
			// This is thrown if we throw an exception
			// from doInBackground.
		}
	}

	@Override
	// Pode atualizar a GUI aqui, recebe o que for passado por publish() em
	// doInBackground
	protected void process(List<Integer> chunks) {
		int mostRecentValue = chunks.get(chunks.size() - 1);

		progress.setString(Integer.toString(mostRecentValue) + "/");
	}

}
