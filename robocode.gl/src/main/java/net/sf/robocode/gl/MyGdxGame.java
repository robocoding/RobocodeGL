package net.sf.robocode.gl;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import robocode.control.snapshot.IBulletSnapshot;
import robocode.control.snapshot.IRobotSnapshot;
import robocode.control.snapshot.ITurnSnapshot;

import java.util.concurrent.BlockingQueue;

import static java.lang.Math.max;
import static java.lang.Math.sqrt;

public final class MyGdxGame extends ApplicationAdapter {
	private final BlockingQueue<TurnSnap> snapshotQue;
	private OrthographicCamera camera;

	private ShapeRenderer shapeRenderer;

	private Texture bodyLarge;
	private TextureAtlas robotAtlas;
	private TextureRegion gunLarge;
	private TextureRegion radarLarge;
	private TextureAtlas explosions;
	private TextureRegion[] explosion1;
	private TextureRegion[] explosion2;
	private Texture explodeDebris;

	private Stage stage;
	private final float worldWidth;
	private final float worldHeight;

	private ITurnSnapshot s;
	private ShaderProgram robotShader;

	private long count = 0;

	public MyGdxGame(BlockingQueue<TurnSnap> snapshotQue, float worldWidth, float worldHeight) {
		this.snapshotQue = snapshotQue;
		this.worldWidth = worldWidth;
		this.worldHeight = worldHeight;
	}

	@Override
	public void create() {
		robotShader = createRobotShader();

		shapeRenderer = new ShapeRenderer();

		explodeDebris = hiTexture("explode_debris.png");

		robotAtlas = new TextureAtlas(internal("robot.atlas"), internal(""));
		bodyLarge = hiTexture("body-large.png");
		gunLarge = robotAtlas.findRegion("turret9");
		radarLarge = robotAtlas.findRegion("radar11");

		explosions = new TextureAtlas(internal("explosions.atlas"), internal(""));
		explosion1 = new TextureRegion[17];
		for (int i = 1; i <= 17; ++i) {
			explosion1[i - 1] = explosions.findRegion("explosion1-" + i);
		}
		explosion2 = new TextureRegion[71];
		for (int i = 1; i <= 71; ++i) {
			explosion2[i - 1] = explosions.findRegion("explosion2-" + i);
		}

		camera = new OrthographicCamera();
		Viewport viewport = new ExtendViewport(worldWidth, worldHeight, camera);
		viewport.apply();

		stage = new Stage(viewport);

		stage.addActor(new RobotsActor());
		stage.addActor(new BulletsActor());

		resize(viewport.getScreenWidth(), viewport.getScreenHeight());
	}

	@Override
	public void render() {
		camera.update();
		shapeRenderer.setProjectionMatrix(stage.getCamera().combined);

		if (s == null || (count & 1) == 0) {
			try {
				TurnSnap snap = snapshotQue.take();
				s = snap.snapshot;
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			}
		}

		++count;

		// Gdx.gl.glClearColor(.5f, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.draw();
	}

	@Override
	public void dispose() {
		robotShader.dispose();

		shapeRenderer.dispose();

		robotAtlas.dispose();
		bodyLarge.dispose();
		explosions.dispose();
		explodeDebris.dispose();

		stage.dispose();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, false);
		stage.getCamera().position.set(worldWidth * .5f, worldHeight * .5f, 0);
	}

	private final class RobotsActor extends Actor {
		final float bodyLargeDx = bodyLarge.getWidth() * .5f;
		final float bodyLargeDy = bodyLarge.getHeight() * .5f;
		final float gunLargeDx = gunLarge.getRegionWidth() * .5f;
		final float gunLargeDy = gunLarge.getRegionHeight() * .5f;
		final float radarLargeDx = radarLarge.getRegionWidth() * .5f;
		final float radarLargeDy = radarLarge.getRegionHeight() * .5f;

		@Override
		public void draw(Batch batch, float parentAlpha) {
			if (s == null) return;

			for (IRobotSnapshot robot : s.getRobots()) {
				if (robot.getState().isDead()) {
					float x = (float) robot.getX();
					float y = (float) robot.getY();
					float w = explodeDebris.getWidth();
					float h = explodeDebris.getHeight();

					batch.draw(explodeDebris, x - w * .5f, y - h * .5f);
				}
			}

			batch.setShader(robotShader);

			for (IRobotSnapshot robot : s.getRobots()) {
				if (robot.getState().isAlive()) {
					float robotX = (float) robot.getX();
					float robotY = (float) robot.getY();

					float bodyHeading = (float) Math.toDegrees(robot.getBodyHeading());
					float gunHeading = (float) Math.toDegrees(robot.getGunHeading());
					float radarHeading = (float) Math.toDegrees(robot.getRadarHeading());

					batch.setPackedColor(getPackedColorBlend(robot.getBodyColor()));
					draw(batch, bodyLarge, bodyLargeDx, bodyLargeDy, -bodyHeading, robotX, robotY, .18f);
					batch.setPackedColor(getPackedColorBlend(robot.getGunColor()));
					draw(batch, gunLarge, gunLargeDx, gunLargeDy, -(gunHeading), robotX, robotY, .18f);

					if (!robot.isDroid()) {
						batch.setPackedColor(getPackedColorBlend(robot.getRadarColor()));
						draw(batch, radarLarge, radarLargeDx, radarLargeDy, -(radarHeading), robotX, robotY, .18f);
					}

					batch.setPackedColor(Color.WHITE_FLOAT_BITS);
				}
			}

			batch.setShader(null);
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
	}

	private final class BulletsActor extends Actor {
		@Override
		public void draw(Batch batch, float parentAlpha) {
			if (s == null) return;

			for (IBulletSnapshot bullet : s.getBullets()) {
				if (!bullet.getState().isActive()) {
					int explosionIndex = bullet.getExplosionImageIndex();
					int frame = bullet.getFrame();

					float x = (float) bullet.getPaintX();
					float y = (float) bullet.getPaintY();

					float explosionScale = 1f;
					TextureRegion f = null;

					if (explosionIndex == 0 && 0 <= frame && frame < 17) {
						f = explosion1[frame];
						explosionScale = (float) sqrt(1000 * bullet.getPower()) / 128f;
					} else if (explosionIndex == 1 && 0 <= frame && frame < 71) {
						f = explosion2[frame];
					}

					if (f != null) {
						int w = f.getRegionWidth();
						int h = f.getRegionHeight();
						batch.draw(f, x - w * .5f, y - h * .5f, w * .5f, h * .5f, w, h, explosionScale, explosionScale, 0, false);
					}
				}
			}

			batch.end();

			shapeRenderer.setColor(Color.WHITE);
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

			for (IBulletSnapshot bullet : s.getBullets()) {
				if (bullet.getState().isActive()) {
					float bulletX = (float) bullet.getPaintX();
					float bulletY = (float) bullet.getPaintY();

					float radius = (float) max(2 * sqrt(2.5 * bullet.getPower()), 2) * .5f;

					shapeRenderer.circle(bulletX, bulletY, radius);
				}
			}

			shapeRenderer.end();

			batch.begin();
		}
	}

	private static Texture hiTexture(String path) {
		Texture texture = new Texture(internal(path));
		texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Nearest);
		return texture;
	}

	private static FileHandle internal(String path) {
		return new MyFileHandle("net/sf/robocode/gl/" + path, Files.FileType.Internal);
	}

	private float getPackedColor(int color) {
		return Color.toFloatBits(
			(color >> 16) & 0xFF,
			(color >> 8) & 0xFF,
			(color >> 0) & 0xFF,
			(color >> 24) & 0xff
		);
	}

	private float getPackedColorBlend(int color) {
		return Color.toFloatBits(
			(color >> 16) & 0xFF,
			(color >> 8) & 0xFF,
			(color >> 0) & 0xFF,
			0
		);
	}

	private static ShaderProgram createRobotShader() {
		String vertexShader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
			+ "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n"
			+ "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n"
			+ "uniform mat4 u_projTrans;\n"
			+ "varying vec4 v_color;\n"
			+ "varying vec2 v_texCoords;\n"
			+ "\n"
			+ "void main()\n"
			+ "{\n"
			+ "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n"
			+ "   v_color.a = v_color.a * (255.0/254.0);\n"
			+ "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n"
			+ "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
			+ "}\n";
		String fragmentShader = "#ifdef GL_ES\n" +
			"#define LOWP lowp\n" +
			"precision mediump float;\n" +
			"#else\n" +
			"#define LOWP \n" +
			"#endif\n" +
			"\n" +
			"vec3 rgb2hsv(vec3 c)\n" +
			"{\n" +
			"  vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);\n" +
			"  vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));\n" +
			"  vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));\n" +
			"\n" +
			"  float d = q.x - min(q.w, q.y);\n" +
			"  float e = 1.0e-10;\n" +
			"  return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);\n" +
			"}\n" +
			"\n" +
			"vec3 hsv2rgb(vec3 c)\n" +
			"{\n" +
			"  vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);\n" +
			"  vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);\n" +
			"  return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);\n" +
			"}\n" +
			"\n" +
			"vec3 hsv2hsl(vec3 c) {\n" +
			"  float h = c.x;\n" +
			"  float s = c.y;\n" +
			"  float v = c.z;\n" +
			"  float l = v-v*.5*s;\n" +
			"  float m = min(l,1.-l);\n" +
			"\n" +
			"  return vec3(h, m!=0. ? (v-l)/m : 0., l);\n" +
			"}\n" +
			"vec3 hsl2hsv(vec3 c) {\n" +
			"  float h = c.x;\n" +
			"  float s = c.y;\n" +
			"  float l = c.z;\n" +
			"  float v = s*min(l,1.-l)+l;\n" +
			"\n" +
			"  return vec3(h, v!=0. ? 2.-2.*l/v : 0., v);\n" +
			"}\n" +
			"\n" +
			"varying LOWP vec4 v_color;\n" +
			"varying vec2 v_texCoords;\n" +
			"uniform sampler2D u_texture;\n" +
			"void main()\n" +
			"{\n" +
			"  vec4 f_color = texture2D(u_texture, v_texCoords);\n" +
			"  vec3 v = hsv2hsl(rgb2hsv(v_color.rgb));\n" +
			"  vec3 f = hsv2hsl(rgb2hsv(f_color.rgb));\n" +
			"\n" +
			"  vec3 g = hsv2rgb(hsl2hsv(vec3(v.x, v.y, min(1., .5*(v.z-f.z)+f.z + .142857*v.z))));\n" +
			"\n" +
			"  gl_FragColor = f.y == 0. || v_color.w == 1. ? f_color : vec4(g.x, g.y, g.z, f_color.w);\n" +
			"}\n";

		ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
		if (!shader.isCompiled()) throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
		return shader;
	}


}
