package net.sf.robocode.gl;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public final class MyGdxGame extends ApplicationAdapter {
	private OrthographicCamera camera;

	private Texture bodyLarge;
	private TextureAtlas robotAtlas;
	private TextureRegion gunLarge;
	private TextureRegion radarLarge;

	private Stage stage;
	private final float worldWidth = 800;
	private final float worldHeight = 600;

	@Override
	public void create() {
		robotAtlas = new TextureAtlas(internal("robot.atlas"), internal(""));
		bodyLarge = hiTexture("body-large.png");
		gunLarge = robotAtlas.findRegion("turret9");
		radarLarge = robotAtlas.findRegion("radar11");

		camera = new OrthographicCamera();
		Viewport viewport = new ExtendViewport(worldWidth, worldHeight, camera);
		viewport.apply();

		stage = new Stage(viewport);

		stage.addActor(new Actor() {
			final float bodyLargeDx = bodyLarge.getWidth() * .5f;
			final float bodyLargeDy = bodyLarge.getHeight() * .5f;
			final float gunLargeDx = gunLarge.getRegionWidth() * .5f;
			final float gunLargeDy = gunLarge.getRegionHeight() * .5f;
			final float radarLargeDx = radarLarge.getRegionWidth() * .5f;
			final float radarLargeDy = radarLarge.getRegionHeight() * .5f;

			@Override
			public void draw(Batch batch, float parentAlpha) {
				float bodyHeading = 0; // mt * r0.bodyHeading + t * r.bodyHeading;
				float gunHeadingOffset = 0; // mt * r0.gunHeadingOffset + t * r.gunHeadingOffset;
				float radarHeadingOffset = 0; // mt * r0.radarHeadingOffset + t * r.radarHeadingOffset;

				float robotX = 100; // mt * r0.robotX + t * r.robotX;
				float robotY = 100; // mt * r0.robotY + t * r.robotY;

				draw(batch, bodyLarge, bodyLargeDx, bodyLargeDy, -bodyHeading, robotX, robotY, .18f);
				draw(batch, gunLarge, gunLargeDx, gunLargeDy, -(bodyHeading + gunHeadingOffset), robotX, robotY, .18f);
				draw(batch, radarLarge, radarLargeDx, radarLargeDy, -(bodyHeading + gunHeadingOffset + radarHeadingOffset), robotX, robotY, .18f);
			}

			private void draw(Batch batch, Texture texture, float textureDx, float textureDy, float rotate, float robotX, float robotY, float scale) {
				batch.draw(texture,
					robotX - textureDx, robotY - textureDy,
					textureDx, textureDy,
					texture.getWidth(), texture.getHeight(),
					scale, scale,
					this.getRotation() + rotate,
					0, 0, texture.getWidth(), texture.getHeight(),
					false, false);
			}

			private void draw(Batch batch, TextureRegion texture, float textureDx, float textureDy, float rotate, float robotX, float robotY, float scale) {
				batch.draw(texture,
					robotX - textureDx, robotY - textureDy,
					textureDx, textureDy,
					texture.getRegionWidth(), texture.getRegionHeight(),
					scale, scale,
					this.getRotation() + rotate);
			}
		});

		resize(viewport.getScreenWidth(), viewport.getScreenHeight());
	}

	@Override
	public void render() {
		camera.update();

		// Gdx.gl.glClearColor(.5f, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.draw();
	}

	@Override
	public void dispose() {
		robotAtlas.dispose();
		bodyLarge.dispose();
		stage.dispose();
	}


	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, false);
		stage.getCamera().position.set(worldWidth * .5f, worldHeight * .5f, 0);
	}

	private static Texture hiTexture(String path) {
		Texture texture = new Texture(internal(path));
		texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Nearest);
		return texture;
	}

	private static FileHandle internal(String path) {
		return new MyFileHandle("net/sf/robocode/gl/" + path, Files.FileType.Internal);
	}
}
