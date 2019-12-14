package net.sf.robocode.gl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import java.awt.*;

public final class GLCore implements IGLCore {
	private final LwjglAWTCanvas canvas;

	public GLCore() {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		float width = 400;
		float height = 300;

		config.width = (int) width;
		config.height = (int) height;
		config.useHDPI = true;
		config.vSyncEnabled = true;
		config.title = "Robocode OpenGL";
		config.samples = 4;

		// new LwjglApplication(new MyGdxGame(), config);
		canvas = new LwjglAWTCanvas(new MyGdxGame(), config);
	}

	@Override
	public Canvas getCanvas() {
		return canvas.getCanvas();
	}

	@Override
	public void exit() {
		// canvas.postRunnable(new Runnable() {
		// 	@Override
		// 	public void run() {
		// 		canvas.stop();
		// 		System.exit(0);
		// 	}
		// });

		System.exit(0);
	}
}
