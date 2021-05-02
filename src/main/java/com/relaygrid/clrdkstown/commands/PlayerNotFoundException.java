package com.relaygrid.clrdkstown.commands;

public class PlayerNotFoundException extends Exception {
	public PlayerNotFoundException() {
		super("");
	}
	
	public PlayerNotFoundException(final String string) {
		super(string);
	}
	
	public PlayerNotFoundException(final Throwable ex) {
		super("", ex);
	}
}
