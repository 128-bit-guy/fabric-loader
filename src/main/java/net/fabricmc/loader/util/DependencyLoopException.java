package net.fabricmc.loader.util;

import java.util.ArrayList;

public class DependencyLoopException extends RuntimeException {
	public final ArrayList<Object> loopInfo;
	public boolean complete = false;

	public DependencyLoopException() {
		loopInfo = new ArrayList<>();
	}
}
