package org.king.apps.lunchvote.exception;

public class RoomNotFoundException extends Exception {

	private static final long serialVersionUID = -1567679272449818116L;

	public RoomNotFoundException() {}

	public RoomNotFoundException(String arg0) {
		super(arg0);
	}

	public RoomNotFoundException(Throwable arg0) {
		super(arg0);
	}

	public RoomNotFoundException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public RoomNotFoundException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
