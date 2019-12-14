package net.sf.robocode.gl;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import robocode.control.snapshot.ITurnSnapshot;

import java.awt.*;
import java.util.concurrent.BlockingQueue;

public interface IGLCore {
	Canvas getCanvas();

	void exit();

	BlockingQueue<ITurnSnapshot> getSnapshotQue();

	LwjglApplication show();
}
