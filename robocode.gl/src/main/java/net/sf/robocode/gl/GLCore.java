package net.sf.robocode.gl;

import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import java.awt.*;

public final class GLCore implements IGLCore {
	private LwjglAWTCanvas canvas;

	private static final LwjglApplicationConfiguration config;

	static {
		config = new LwjglApplicationConfiguration();

		float width = 800;
		float height = 600;

		config.width = (int) width;
		config.height = (int) height;
		config.useHDPI = true;
		config.vSyncEnabled = true;
		config.title = "Robocode OpenGL";
		config.samples = 4;
		LwjglApplicationConfiguration.disableAudio = true;
	}

	public GLCore() {
		// getCanvas();
	}

	@Override
	public Canvas getCanvas() {
		if (canvas == null) {
			canvas = new LwjglAWTCanvas(new MyGdxGame(), config);
		}
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

	@Override
	public LwjglApplication show() {
		return new LwjglApplication(new MyGdxGame(), config);
	}
}
