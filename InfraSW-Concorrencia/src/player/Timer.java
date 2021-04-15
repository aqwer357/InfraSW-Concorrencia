package player;

import javax.swing.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Timer implements Runnable {
	private boolean paused = false;
	private JProgressBar progress = null; // Para poder editar a barra de progresso da GUI
	private int musicLength = 0; // Para determinar quanto tempo a musica deve ser "tocada"
	private boolean running = true;

	private final Lock lock = new ReentrantLock();
	private final Condition pauseCondition = lock.newCondition();

	private static int counter = 0;

	public Timer(JProgressBar pBar, int mLength) {
		this.progress = pBar;
		this.musicLength = mLength;
	}

	public void pause() {
		paused = true;
	}

	public void resume() {
		lock.lock();
		paused = false;
		pauseCondition.signalAll();
		lock.unlock();
	}

	public boolean isPaused() {
		return paused;
	}

	public void newSong(int mLength) {
		this.musicLength = mLength;
		counter = 0;
	}

	@Override
	public void run() {
		try {
			lock.lock();
			counter = 0;
			// Contando o tempo de execucao da musica, roda-se o loop 10 vezes por segundo
			// para evitar delay
			// quando o usuario pausar a musica.
			while (counter <= musicLength * 10) {

				// Se pausado, vai esperar resume mandar o sinal para despausar antes de
				// continuar a contagem
				if (paused)
					pauseCondition.await();

				Thread.sleep(100);
				increaseTimer();
			}

		} catch (InterruptedException e) {
			lock.unlock();
		} finally {
			lock.unlock();
			MusicPlayer.start(1);
		}
	}

	private void increaseTimer() {
		try {
			lock.lock();
			// Atualiza a barra de progresso no formato M:ss
			progress.setString(counter / 10 / 60 + ":" + String.format("%02d", counter / 10 % 60) + "/"
					+ musicLength / 60 + ":" + String.format("%02d", musicLength % 60));
			counter++;
		} finally {
			lock.unlock();
		}
	}
}
