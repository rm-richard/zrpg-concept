package rmrichard.learn;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import rmrichard.learn.components.BodyComponent;
import rmrichard.learn.components.PlayerComponent;
import rmrichard.learn.components.PositionComponent;
import rmrichard.learn.components.TextureComponent;
import rmrichard.learn.systems.DrawSystem;
import rmrichard.learn.systems.PhysicSystem;
import rmrichard.learn.systems.PhysicsDebugSystem;
import rmrichard.learn.systems.PlayerMovementSystem;

import static rmrichard.learn.Constants.VIEW_HEIGHT;
import static rmrichard.learn.Constants.VIEW_WIDTH;

public class IdleRpgGame extends ApplicationAdapter {
	private SpriteBatch batch;
	private Engine engine;
	private World world;
	private OrthographicCamera camera;

	private TiledMap tiledMap;
	private TiledMapRenderer tiledMapRenderer;
	private OrthographicCamera tiledCamera;
	private int[] backgroundLayers;
	private int[] foregroundLayers;

	@Override
	public void create () {
		batch = new SpriteBatch();
		engine = new Engine();
		world = new World(new Vector2(0, 0), true);
		camera = new OrthographicCamera(VIEW_WIDTH, VIEW_HEIGHT);
		camera.position.set(new Vector2(VIEW_WIDTH / 2, VIEW_HEIGHT / 2), 0);

		createTileMap();

		engine.addSystem(new DrawSystem(batch, camera));
		engine.addSystem(new PhysicSystem(world));
		engine.addSystem(new PlayerMovementSystem());
		//engine.addSystem(new PhysicsDebugSystem(world, camera));
	}

	private void createPlayer(float x, float y) {
		Entity playerEntity = new Entity();
		playerEntity.add(new PositionComponent(x, y));
		playerEntity.add(new TextureComponent(new Texture("general-single.png")));
		playerEntity.add(new PlayerComponent());

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(x / 10, y / 10);
		bodyDef.fixedRotation = true;
		Body body = world.createBody(bodyDef);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(4.2f / 2, 4.8f / 2, new Vector2(4.2f /2, 4.8f / 2), 0);
		body.createFixture(shape, 0);
		shape.dispose();

		playerEntity.add(new BodyComponent(body));
		engine.addEntity(playerEntity);
	}

	private void createTileMap() {
		tiledMap = new TmxMapLoader().load("game-map.tmx");
		backgroundLayers = new int[] {0, 1};
		foregroundLayers = new int[] {2};
		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
		tiledCamera = new OrthographicCamera();
		tiledCamera.setToOrtho(false, VIEW_WIDTH, VIEW_HEIGHT);
		tiledCamera.update();

		Texture coinTexture = new Texture("coin.png");
		Texture doorTexture = new Texture("door.png");
		Texture keyTexture = new Texture("key.png");

		MapObjects objects = tiledMap.getLayers().get("ObjectData").getObjects();
		for (MapObject object : objects) {
			String name = object.getName();
			RectangleMapObject rectangleObject = (RectangleMapObject) object;
			Rectangle r = rectangleObject.getRectangle();

			switch (name) {
				case "player":
					createPlayer(r.x, r.y);
					break;
				case "coin":
					Entity coinEntity = new Entity();
					coinEntity.add(new PositionComponent(r.x, r.y));
					coinEntity.add(new TextureComponent(coinTexture));
					engine.addEntity(coinEntity);
					break;
				case "door":
					Entity doorEntity = new Entity();
					doorEntity.add(new PositionComponent(r.x, r.y));
					doorEntity.add(new TextureComponent(doorTexture));
					engine.addEntity(doorEntity);
					break;
				case "key":
					Entity keyEntity = new Entity();
					keyEntity.add(new PositionComponent(r.x, r.y));
					keyEntity.add(new TextureComponent(keyTexture));
					engine.addEntity(keyEntity);
					break;
				default:
					System.out.println("WARN: unknown tilemap object");
			}
		}

		objects = tiledMap.getLayers().get("PhysicsData").getObjects();
		for (MapObject object : objects) {
			RectangleMapObject rectangleObject = (RectangleMapObject) object;
			Rectangle r = rectangleObject.getRectangle();

			BodyDef bodyDef = new BodyDef();
			bodyDef.type = BodyDef.BodyType.StaticBody;
			bodyDef.position.set(r.x / 10, r.y / 10);
			bodyDef.fixedRotation = true;
			Body body = world.createBody(bodyDef);
			PolygonShape shape = new PolygonShape();
			shape.setAsBox(r.width / 20, r.height / 20, new Vector2(r.width / 20, r.height / 20), 0);
			body.createFixture(shape, 0);
			shape.dispose();

			Entity wallEntity = new Entity();
			wallEntity.add(new PositionComponent(r.x, r.y));
			wallEntity.add(new BodyComponent(body));
			engine.addEntity(wallEntity);
		}
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.2f, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		tiledCamera.position.x = camera.position.x;
       	tiledCamera.position.y = camera.position.y;
		tiledCamera.update();
		tiledMapRenderer.setView(tiledCamera);

		tiledMapRenderer.render(backgroundLayers);

		batch.begin();
		engine.update(Gdx.graphics.getDeltaTime());
		batch.end();

		tiledMapRenderer.render(foregroundLayers);
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
