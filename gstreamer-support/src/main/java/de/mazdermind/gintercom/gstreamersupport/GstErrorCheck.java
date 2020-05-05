package de.mazdermind.gintercom.gstreamersupport;

import org.freedesktop.gstreamer.GstException;
import org.freedesktop.gstreamer.StateChangeReturn;

public class GstErrorCheck {
	public static void expectSuccess(boolean returnValue) {
		if (!returnValue) {
			throw new GstException("Expected a return-value of true, was false");
		}
	}

	public static void expectAsyncOrSuccess(StateChangeReturn stateChangeReturn) {
		if (stateChangeReturn != StateChangeReturn.SUCCESS && stateChangeReturn != StateChangeReturn.ASYNC) {
			throw new GstException(String.format("Expected a return-value of SUCCESS or ASYNC, was %s", stateChangeReturn));
		}
	}

	public static void expectSuccess(StateChangeReturn stateChangeReturn) {
		if (stateChangeReturn != StateChangeReturn.SUCCESS) {
			throw new GstException(String.format("Expected a return-value of SUCCESS, was %s", stateChangeReturn));
		}
	}

	public static void expectNull(Object o) {
		if (o != null) {
			throw new GstException(String.format("Expected a return-value of null, was %s", o));
		}
	}
}
