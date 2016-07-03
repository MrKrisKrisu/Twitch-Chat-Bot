package de.mrkriskrisu.bot.errors;

import de.mrkriskrisu.bot.type.Command;

public class CommandAlreadyRegisteredException extends Exception {

	private static final long serialVersionUID = -6825971206872402394L;
	private Command cmd;

	public CommandAlreadyRegisteredException(Command cmd) {
		this.cmd = cmd;
	}

}
