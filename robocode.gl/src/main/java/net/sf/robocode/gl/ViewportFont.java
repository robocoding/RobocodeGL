package net.sf.robocode.gl;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.carrotsearch.hppc.IntObjectMap;
import com.carrotsearch.hppc.IntObjectOpenHashMap;
import com.carrotsearch.hppc.cursors.IntObjectCursor;

import java.util.Iterator;
import java.util.LinkedHashSet;

public final class ViewportFont {
	private static final int MIN_FONT_SIZE = 8;

	private final IntObjectMap<BitmapFont> fonts = new IntObjectOpenHashMap<BitmapFont>();
	private final LinkedHashSet<Integer> lru = new LinkedHashSet<Integer>();
	private final Viewport viewport;

	private final Vector2 p = new Vector2();
	private final Vector2 q = new Vector2();
	private FileHandle fontFile;

	public ViewportFont(Viewport viewport, FileHandle internal) {
		this.viewport = viewport;
		fontFile = internal;
	}

	public BitmapFont get(float worldSize) {
		p.set(0f, 0f);
		q.set(1f, 1f);
		viewport.project(p);
		viewport.project(q);

		float scale = q.x - p.x;

		int size = Math.round(worldSize * scale);
		if (size < MIN_FONT_SIZE) size = MIN_FONT_SIZE;

		BitmapFont font = fonts.get(size);
		if (font != null) {
			lru.remove(size);
			lru.add(size);

			font.getData().setScale(1f / scale);
			return font;
		}

		if (fonts.size() > 10) {
			Iterator<Integer> it = lru.iterator();
			int first = it.next();

			it.remove();
			fonts.remove(first).dispose();
		}

		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = size;
		parameter.minFilter = Texture.TextureFilter.Linear;
		parameter.magFilter = Texture.TextureFilter.Linear;
		font = generator.generateFont(parameter);
		font.setUseIntegerPositions(false);
		generator.dispose();

		fonts.put(size, font);
		lru.add(size);

		font.getData().setScale(1f / scale);
		return font;
	}

	public void dispose() {
		for (IntObjectCursor<BitmapFont> font : fonts) {
			font.value.dispose();
		}
		fonts.clear();
		lru.clear();
	}
}
