package player;

import java.util.List;
import java.util.concurrent.CancellationException;
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
		lock.lock();
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
			// Contando o tempo de execucao da musica, roda-se o loop 10 vezes por segundo para evitar delay
			// quando o usuário pausar a musica.
			for (int i = 0; i <= musicLength*10; i++) {
				
				// Se pausado, vai esperar resume mandar o sinal para despausar antes de continuar a contagem
				if (paused)
					pauseCondition.await();
				
				Thread.sleep(100);
				publish(i/10); // Envia para process o tempo decorrido em segundos
			}

		} finally {
			lock.unlock();
		}
		
		return true;
	}

	protected void done() {

		boolean status;
		try {
			if (!this.isCancelled())
				MusicPlayer.start(1); // Inicia a tocar a proxima musica
			
			status = get();
		} catch (InterruptedException e) {

		} catch (ExecutionException e) {

		} catch (CancellationException e) {
			
		}
	}

	@Override
	// Recebe os valores passados com .publish()
	protected void process(List<Integer> chunks) {
		// Necessario pois podem vir mais que um valor de uma vez
		int mostRecentValue = chunks.get(chunks.size() - 1);
		
		// Atualiza a barra de progresso no formato M:ss
		progress.setString(mostRecentValue/60 + ":" + String.format("%02d", mostRecentValue % 60) + "/"
						  + musicLength/60 + ":" + String.format("%02d", musicLength % 60));
		
	}

}
