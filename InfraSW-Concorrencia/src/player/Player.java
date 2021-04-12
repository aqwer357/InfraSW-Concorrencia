package player;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.*;

public class Player extends SwingWorker<Boolean, Integer> {
	private boolean paused = false;
	private JProgressBar progress = null; // Para poder editar a barra de progresso da GUI
	private int musicLength = 0; // Para determinar quanto tempo a musica deve ser "tocada"

	private final Lock lock = new ReentrantLock();
	private final Condition pauseCondition = lock.newCondition();

	public Player(JProgressBar pBar, int mLength) {
		this.progress = pBar;
		this.musicLength = mLength;
	}

	public void pause() {
		paused = true;	
	}

	public void resume() {
		lock.lock(); // Faz lock para poder fazer signalAll
		paused = false;
		pauseCondition.signalAll();
		lock.unlock();
	}

	public boolean isPaused() {
		return paused;
	}

	@Override
	protected Boolean doInBackground() throws Exception {
		try {
			lock.lock();
			// Contando o tempo de execucao da musica
			for (int i = 0; i <= musicLength; i++) {
				if (paused)
					pauseCondition.await();
				Thread.sleep(1000);
				publish(i);
			}

		} finally {
			lock.unlock();
		}
		// Podemos utilizar um boolean para dizer quando a musica termina?
		return true;
	}

	// Pode atualizar a GUI aqui, executa depois do de doInBackground
	protected void done() {

		boolean status;
		try {
			// Retrieve the return value of doInBackground.
			MusicPlayer.start(1);
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
		progress.setString(Integer.toString(mostRecentValue) + "/" + musicLength);
	}

	public int getMusicLength() {
		return musicLength;
	}

}
