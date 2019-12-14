package net.sf.robocode.gl;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public final class MyGdxGame extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture body;

	@Override
	public void create() {
		batch = new SpriteBatch();
		body = hiTexture("body.png");
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();

		batch.draw(body, 200, 200);

		batch.end();
	}

	@Override
	public void dispose() {
		batch.dispose();
		body.dispose();
	}

	private Texture hiTexture(String path) {
		Texture texture = new Texture(new MyFileHandle("net/sf/robocode/gl/" + path, Files.FileType.Classpath));
		texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Nearest);
		return texture;
	}
}
