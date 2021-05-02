package com.relaygrid.clrdkstown.commands;

@SuppressWarnings("serial")
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
