package de.mrkriskrisu.bot;

import de.mrkriskrisu.bot.KrackelUtils.Logger;
import de.mrkriskrisu.bot.threads.SocketManager;

public class Bot {

    private String botName;
    private String botChannel;
    private String oauth;
    private SocketManager socketmanager;
    private CommandManager cmdManager;

    public Bot(String username, String oauth, String channel) {
	  this.botName = username;
	  this.oauth = oauth;
	  this.botChannel = channel;

	  socketmanager = new SocketManager(this);
	  cmdManager = new CommandManager(this);
    }

    public void start() {
	  Logger.log("");
	  Logger.log("**********************");
	  Logger.log("");
	  Logger.log("SimpleChatBot made by MrKrisKrisu");
	  Logger.log("Source and current Version at https://github.com/MrKrisKrisu/Twitch-Chat-Bot");
	  Logger.log("");
	  Logger.log("**********************");
	  Logger.log("");
	  Logger.log("Initialize Bot...");
	  socketmanager.connect(botName, oauth, botChannel);
    }

    public void onMessage(String username, String message) {
	  Logger.log("[MSG] " + username + " > " + message);
	  cmdManager.handleMessage(username, message);
    }

    /**
     * Send Message to Channel
     * 
     * @param message
     */
    protected void sendMessage(String message) {
	  SocketManager.sendRawLine("PRIVMSG #" + botChannel + " :" + message);
    }

    public void timeout(String username, int seconds, String reason) {
	  sendMessage("/timeout " + seconds);
	  if (reason != null)
		sendMessage("@" + username + ": " + reason);
    }

    public SocketManager getSocketManager() {
	  return socketmanager;
    }

    public CommandManager getCommandManager() {
	  return cmdManager;
    }

}
