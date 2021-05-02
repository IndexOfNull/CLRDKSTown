package com.relaygrid.clrdkstown.commands;

@SuppressWarnings("serial")
public class BadArgumentException extends Exception {
	public BadArgumentException() {
		super("");
	}
	
	public BadArgumentException(final String string) {
		super(string);
	}
	
	public BadArgumentException(final Throwable ex) {
		super("", ex);
	}
}
