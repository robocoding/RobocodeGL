package net.sf.robocode.gl;

import robocode.control.snapshot.ITurnSnapshot;

public final class TurnSnap {
	public final ITurnSnapshot snapshot;
	public final int battlefieldWidth;
	public final int battlefieldHeight;

	public TurnSnap(ITurnSnapshot snapshot) {
		this.snapshot = snapshot;
		battlefieldWidth = 0;
		battlefieldHeight = 0;
	}

	public TurnSnap(int battlefieldWidth, int battlefieldHeight) {
		snapshot = null;
		this.battlefieldWidth = battlefieldWidth;
		this.battlefieldHeight = battlefieldHeight;
	}
}
