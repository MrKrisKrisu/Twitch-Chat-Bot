package de.mrkriskrisu.bot.threads;

import java.io.BufferedWriter;

import de.mrkriskrisu.bot.KrackelUtils.Logger;

public class OutputThread extends Thread {

	private Queue _outQueue = null;

	OutputThread(Queue outQueue) {
		_outQueue = outQueue;
		this.setName(this.getClass() + "-Thread");
	}

	public static void sendRawLine(BufferedWriter bwriter, String line) {
		synchronized (bwriter) {
			try {
				bwriter.write(line + "\r\n");
				bwriter.flush();
				Logger.debug(">>>" + line);
			} catch (Exception e) {
				Logger.error(e);
			}
		}
	}

	public void run() {
		try {
			boolean running = true;
			while (running) {
				Thread.sleep(2000);

				String line = (String) _outQueue.next();
				if (line != null) {
					SocketManager.sendRawLine(line);
				} else {
					running = false;
				}
			}
		} catch (InterruptedException e) {
			Logger.error(e);
		}
	}

}
