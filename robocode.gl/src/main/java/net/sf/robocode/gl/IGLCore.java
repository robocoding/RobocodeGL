package net.sf.robocode.gl;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

import java.awt.*;

public interface IGLCore {
	Canvas getCanvas();

	void exit();

	LwjglApplication show();
}
