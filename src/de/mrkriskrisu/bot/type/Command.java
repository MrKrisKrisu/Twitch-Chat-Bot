package de.mrkriskrisu.bot.type;

public class Command {
	
	private String command;
	private String message;

	public Command(String command, String message) {
		this.command = command;
		this.message = message;
	}

	public String getCommand() {
		return command;
	}

	public String getMessage() {
		return message;
	}
}
