package net.sf.robocode.gl;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public final class MyGdxGame extends ApplicationAdapter {
	private OrthographicCamera camera;

	private Texture body;
	private Stage stage;
	private final float worldWidth = 800;
	private final float worldHeight = 600;

	@Override
	public void create() {
		body = hiTexture("body.png");

		camera = new OrthographicCamera();
		Viewport viewport = new ExtendViewport(worldWidth, worldHeight, camera);
		viewport.apply();

		stage = new Stage(viewport);

		stage.addActor(new Actor() {
			@Override
			public void draw(Batch batch, float parentAlpha) {
				batch.draw(body, 50, 50);
			}
		});

		resize(viewport.getScreenWidth(), viewport.getScreenHeight());
	}

	@Override
	public void render() {
		camera.update();

		Gdx.gl.glClearColor(.5f, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.draw();
	}

	@Override
	public void dispose() {
		body.dispose();
		stage.dispose();
	}


	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, false);
		stage.getCamera().position.set(worldWidth * .5f, worldHeight * .5f, 0);
	}

	private Texture hiTexture(String path) {
		Texture texture = new Texture(new MyFileHandle("net/sf/robocode/gl/" + path, Files.FileType.Classpath));
		texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Nearest);
		return texture;
	}
}
