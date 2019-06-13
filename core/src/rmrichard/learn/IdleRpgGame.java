package rmrichard.learn;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
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
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import rmrichard.learn.components.*;
import rmrichard.learn.systems.*;

import static rmrichard.learn.Constants.VIEW_HEIGHT;
import static rmrichard.learn.Constants.VIEW_WIDTH;
import static rmrichard.learn.components.ItemComponent.Type.*;

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
		world.setContactListener(new EntityContactListener());
		camera = new OrthographicCamera(VIEW_WIDTH, VIEW_HEIGHT);
		camera.position.set(new Vector2(VIEW_WIDTH / 2, VIEW_HEIGHT / 2), 0);

		createTileMap();

		engine.addSystem(new DrawSystem(batch, camera));
		engine.addSystem(new PhysicSystem(world));
		engine.addSystem(new PlayerMovementSystem());
		engine.addSystem(new PlayerCollisionSystem());
		//engine.addSystem(new PhysicsDebugSystem(world, camera));
	}

	private void createPlayer(float x, float y) {
		Entity playerEntity = new Entity();
		playerEntity.add(new PositionComponent(x, y));
		playerEntity.add(new TextureComponent(new Texture("general-single.png")));
		playerEntity.add(new PlayerComponent());
		playerEntity.add(new BodyComponent(createRectangleBody(playerEntity, BodyType.DynamicBody, x, y, 42, 48)));
		playerEntity.add(new CollisionComponent());
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
					coinEntity.add(new ItemComponent(COIN));
					coinEntity.add(new BodyComponent(createRectangleBody(
							coinEntity, BodyType.StaticBody, r.x, r.y, r.width, r.height)));
					engine.addEntity(coinEntity);
					break;
				case "door":
					Entity doorEntity = new Entity();
					doorEntity.add(new PositionComponent(r.x, r.y));
					doorEntity.add(new TextureComponent(doorTexture));
					doorEntity.add(new ItemComponent(DOOR));
					doorEntity.add(new BodyComponent(createRectangleBody(
							doorEntity, BodyType.StaticBody, r.x, r.y, r.width, r.height)));
					engine.addEntity(doorEntity);
					break;
				case "key":
					Entity keyEntity = new Entity();
					keyEntity.add(new PositionComponent(r.x, r.y));
					keyEntity.add(new TextureComponent(keyTexture));
					keyEntity.add(new ItemComponent(KEY));
					Body body = createRectangleBody(keyEntity, BodyType.StaticBody, r.x, r.y, r.width, r.height);
					body.getFixtureList().first().setSensor(true);
					keyEntity.add(new BodyComponent(body));
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

			Entity wallEntity = new Entity();
			wallEntity.add(new PositionComponent(r.x, r.y));
			wallEntity.add(new BodyComponent(createRectangleBody(wallEntity, BodyType.StaticBody, r.x, r.y, r.width, r.height)));
			engine.addEntity(wallEntity);
		}
	}


	private Body createRectangleBody(Entity entity, BodyType type, float x, float y, float w, float h) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = type;
		bodyDef.position.set(x / 10, y / 10);
		bodyDef.fixedRotation = true;
		Body body = world.createBody(bodyDef);
		body.setUserData(entity);

		PolygonShape shape = new PolygonShape();
		float tx = w / 10f / 2;
		float ty = h / 10f / 2;
		shape.setAsBox(tx, ty, new Vector2(tx, ty), 0);
		body.createFixture(shape, 0);
		shape.dispose();

		return body;
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
