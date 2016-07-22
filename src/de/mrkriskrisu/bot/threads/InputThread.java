package de.mrkriskrisu.bot.threads;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;

import de.mrkriskrisu.bot.Bot;
import de.mrkriskrisu.bot.KrackelUtils.Logger;

public class InputThread extends Thread {

    private Bot bot;

    private Socket _socket;
    private BufferedReader _breader;
    private BufferedWriter _bwriter;
    private boolean _isConnected = true;
    private boolean _disposed = false;

    public InputThread(Bot bot, Socket socket, BufferedReader breader, BufferedWriter bwriter) {
	  this.bot = bot;
	  _socket = socket;
	  _breader = breader;
	  _bwriter = bwriter;
	  this.setName(this.getClass() + "-Thread");
    }

    void sendRawLine(String line) {
	  OutputThread.sendRawLine(_bwriter, line);
    }

    boolean isConnected() {
	  return _isConnected;
    }

    public void run() {
	  try {
		boolean running = true;
		while (running) {
		    try {
			  String line = null;
			  while ((line = _breader.readLine()) != null) {
				try {
				    bot.getSocketManager().handleLine(line);
				} catch (Throwable t) {
				    StringWriter sw = new StringWriter();
				    PrintWriter pw = new PrintWriter(sw);
				    t.printStackTrace(pw);
				    pw.flush();
				}
			  }
			  if (line == null) {
				running = false;
			  }
		    } catch (InterruptedIOException iioe) {
			  this.sendRawLine("PING " + (System.currentTimeMillis() / 1000));
		    }
		}
	  } catch (Exception e) {
		Logger.error(e);
	  }

	  try {
		_socket.close();
	  } catch (Exception e) {
		Logger.error(e);
	  }

	  if (!_disposed) {
		Logger.log("*** Disconnected.");
		bot.getSocketManager().onDisconnect();
		System.exit(1);
		return;
	  }

    }

    public void dispose() {
	  try {
		_disposed = true;
		_socket.close();
	  } catch (Exception e) {
		Logger.error(e);
	  }
    }

}
