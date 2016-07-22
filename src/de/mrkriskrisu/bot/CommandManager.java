package de.mrkriskrisu.bot;

import java.util.ArrayList;
import java.util.List;

import de.mrkriskrisu.bot.KrackelUtils.Logger;
import de.mrkriskrisu.bot.errors.CommandAlreadyRegisteredException;
import de.mrkriskrisu.bot.type.Command;

public class CommandManager {

    private Bot bot;
    private List<Command> commands = new ArrayList<Command>();

    public CommandManager(Bot bot) {
	  this.bot = bot;
    }

    public List<Command> getCommands() {
	  return commands;
    }

    public void registerCommand(Command cmd) throws CommandAlreadyRegisteredException {
	  if (getCommand(cmd.getCommand()) != null) {
		throw new CommandAlreadyRegisteredException(cmd);
	  }
	  Logger.debug("Command " + cmd.getCommand() + " successfully registered");
	  commands.add(cmd);
    }

    public Command getCommand(String com) {
	  for (Command cmd : commands)
		if (cmd.getCommand() == com)
		    return cmd;
	  return null;
    }

    public void handleMessage(String username, String message) {
	  for (Command cmd : commands) {
		if (message.toLowerCase().startsWith(cmd.getCommand().toLowerCase())) {
		    Logger.log("Run command " + cmd.getCommand() + " (Username:" + username + ")");
		    bot.sendMessage(cmd.getMessage());
		}
	  }
    }

}
