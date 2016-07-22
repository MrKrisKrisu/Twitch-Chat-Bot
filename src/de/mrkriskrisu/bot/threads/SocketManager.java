package de.mrkriskrisu.bot.threads;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.StringTokenizer;

import de.mrkriskrisu.bot.Bot;
import de.mrkriskrisu.bot.KrackelUtils.Logger;

public class SocketManager {

    private Bot bot;

    private Socket socket;
    private static InputThread _inputThread;
    private static OutputThread _outputThread;

    private static BufferedWriter bwriter;
    private static BufferedReader breader;

    private static Queue _outQueue = new Queue();

    public SocketManager(Bot bot) {
	  this.bot = bot;
    }

    public void connect(String username, String oauth, String channel) {
	  try {
		socket = new Socket("irc.twitch.tv", 6667);
		Logger.debug("Connection to irc.twitch.tv etablished");

		InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8"));
		breader = new BufferedReader(inputStreamReader);

		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8"));
		bwriter = new BufferedWriter(outputStreamWriter);

		_inputThread = new InputThread(bot, socket, breader, bwriter);

		sendLogin(oauth, username, channel);
		handleLogin();

		Logger.log("Chatbot successfully started! Have fun!");
	  } catch (IOException e) {
		Logger.error(e);
	  }

    }

    private void sendLogin(String oauth, String username, String channel) {
	  OutputThread.sendRawLine(bwriter, "PASS " + oauth);
	  OutputThread.sendRawLine(bwriter, "NICK " + username);
	  OutputThread.sendRawLine(bwriter, "JOIN #" + channel);
    }

    private void handleLogin() throws IOException {
	  String line = null;
	  while ((line = breader.readLine()) != null) {

		handleLine(line);

		int firstSpace = line.indexOf(" ");
		int secondSpace = line.indexOf(" ", firstSpace + 1);
		if (secondSpace >= 0) {
		    String code = line.substring(firstSpace + 1, secondSpace);
		    if (code.equals("004")) {
			  break;
		    } else if (code.equals("433")) {
			  socket.close();
			  _inputThread = null;
			  Logger.log("Nick in use");
		    } else if (code.startsWith("5") || code.startsWith("4")) {
			  socket.close();
			  _inputThread = null;
			  Logger.log("Could not log into the IRC server: " + line);
		    }
		}
	  }

	  _inputThread.start();
	  if (_outputThread == null) {
		_outputThread = new OutputThread(_outQueue);
	  }
	  _outputThread.start();
    }

    public void handleLine(String line) {
	  Logger.debug(">>> " + line);
	  if (line.startsWith("PING ")) {
		sendRawLine("PONG :tmi.twitch.tv");
		return;
	  }

	  String sourceNick = "";

	  StringTokenizer tokenizer = new StringTokenizer(line);
	  String senderInfo = tokenizer.nextToken();
	  String command = tokenizer.nextToken();
	  String target = null;

	  int exclamation = senderInfo.indexOf("!");
	  int at = senderInfo.indexOf("@");
	  if (senderInfo.startsWith(":")) {
		if (exclamation > 0 && at > 0 && exclamation < at) {
		    sourceNick = senderInfo.substring(1, exclamation);
		} else {

		    if (tokenizer.hasMoreTokens()) {
			  String token = command;

			  int code = -1;
			  try {
				code = Integer.parseInt(token);
			  } catch (NumberFormatException e) {

			  }

			  if (code != -1)
				return;

			  sourceNick = senderInfo;
			  target = token;

		    } else
			  return;

		}
	  }

	  command = command.toUpperCase();
	  if (sourceNick.startsWith(":")) {
		sourceNick = sourceNick.substring(1);
	  }
	  if (target == null) {
		target = tokenizer.nextToken();
	  }
	  if (target.startsWith(":")) {
		target = target.substring(1);
	  }

	  if (command.equals("PRIVMSG")) {
		bot.onMessage(sourceNick, line.substring(line.indexOf(" :") + 2));
	  }

    }

    public static void sendRawLine(String line) {
	  Logger.debug("<<< " + line);
	  OutputThread.sendRawLine(bwriter, line);
    }

    public void onDisconnect() {
	  Logger.log("Connection to chat disethablished. Bot will exit...");
	  System.exit(1);
    }

}
