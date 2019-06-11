package rmrichard.learn;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import rmrichard.learn.components.BodyComponent;
import rmrichard.learn.components.PlayerComponent;
import rmrichard.learn.components.PositionComponent;
import rmrichard.learn.components.TextureComponent;
import rmrichard.learn.systems.DrawSystem;
import rmrichard.learn.systems.PhysicSystem;
import rmrichard.learn.systems.PhysicsDebugSystem;
import rmrichard.learn.systems.PlayerMovementSystem;

public class IdleRpgGame extends ApplicationAdapter {
	private SpriteBatch batch;
	private Engine engine;
	private World world;
	private OrthographicCamera camera;

	private TiledMap tiledMap;
	private TiledMapRenderer tiledMapRenderer;
	private OrthographicCamera tiledCamera;

	@Override
	public void create () {
		batch = new SpriteBatch();
		engine = new Engine();
		world = new World(new Vector2(0, 0), true);
		camera = new OrthographicCamera(640, 480);
		camera.position.set(new Vector2(320, 240), 0);

		createPlayer();

		engine.addSystem(new DrawSystem(batch, camera));
		engine.addSystem(new PhysicSystem(world));
		engine.addSystem(new PlayerMovementSystem());
		engine.addSystem(new PhysicsDebugSystem(world, camera));

		tiledMap = new TmxMapLoader().load("game-map.tmx");
		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
		tiledCamera = new OrthographicCamera();
		tiledCamera.setToOrtho(false, 640, 480);
		tiledCamera.update();
	}

	private void createPlayer() {
		Entity playerEntity = new Entity();
		playerEntity.add(new PositionComponent(100, 100));
		playerEntity.add(new TextureComponent(new Texture("general-single.png")));
		playerEntity.add(new PlayerComponent());

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(100, 100);
		bodyDef.fixedRotation = true;
		Body body = world.createBody(bodyDef);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(42, 48);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.friction = 2.2f;
		fixtureDef.density = 0.01f;

		body.createFixture(fixtureDef);
		shape.dispose();

		playerEntity.add(new BodyComponent(body));
		engine.addEntity(playerEntity);
	}

	private void createSpriteBox() {
		Entity imgEntity = new Entity();
		imgEntity.add(new PositionComponent(10, 10));
		imgEntity.add(new TextureComponent(new Texture("badlogic.jpg")));
		BodyDef bd = new BodyDef();
		bd.type = BodyDef.BodyType.DynamicBody;
		bd.position.set(10, 10);
		Body body = world.createBody(bd);
		body.applyLinearImpulse(50, 20, 50, 30, true);

		PolygonShape shape = new PolygonShape();
		shape.set(new float[]{ 0,0, 256,0, 256,256, 0,256 });

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 0.0001f;
		fixtureDef.friction = 0.1f;
		body.createFixture(fixtureDef);
		shape.dispose();

		imgEntity.add(new BodyComponent(body));
		engine.addEntity(imgEntity);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.2f, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		tiledCamera.position.x = camera.position.x;
       	tiledCamera.position.y = camera.position.y;
		tiledCamera.update();
		tiledMapRenderer.setView(tiledCamera);
		tiledMapRenderer.render();

		batch.begin();
		engine.update(Gdx.graphics.getDeltaTime());
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
