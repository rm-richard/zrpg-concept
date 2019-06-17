package rmrichard.learn;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import rmrichard.learn.components.*;
import rmrichard.learn.listeners.BodyRemovalListener;
import rmrichard.learn.listeners.EntityContactListener;
import rmrichard.learn.systems.*;

import static rmrichard.learn.Constants.VIEW_HEIGHT;
import static rmrichard.learn.Constants.VIEW_WIDTH;
import static rmrichard.learn.components.ItemComponent.Type.*;

public class IdleRpgGame extends Game {
	private SpriteBatch batch;
	private Engine engine;
	private World world;
	private OrthographicCamera camera;

	private TiledMap tiledMap;
	private TiledMapRenderer tiledMapRenderer;

	@Override
	public void create () {
		batch = new SpriteBatch();
		engine = new Engine();
		world = new World(new Vector2(0, 0), true);
		world.setContactListener(new EntityContactListener());
		camera = new OrthographicCamera(VIEW_WIDTH, VIEW_HEIGHT);
		camera.position.set(new Vector2(VIEW_WIDTH / 2, VIEW_HEIGHT / 2), 0);

		createTileMap();

		engine.addSystem(new DrawSystem(batch, camera, tiledMapRenderer));
		engine.addSystem(new PhysicSystem(world));
		engine.addSystem(new PlayerMovementSystem());
		engine.addSystem(new PlayerCollisionSystem());
		//engine.addSystem(new PhysicsDebugSystem(world, camera));

		engine.addEntityListener(Family.all(BodyComponent.class).get(),
				new BodyRemovalListener(world));
	}

	private void createPlayer(float x, float y) {
		Entity playerEntity = new Entity();
		playerEntity.add(new TransformComponent(x, y, 0.6f));
		playerEntity.add(new TextureComponent(new Texture("experiment/lpc-male-preview.png")));
		playerEntity.add(new PlayerComponent());
		playerEntity.add(new BodyComponent(createRectangleBody(playerEntity, BodyType.DynamicBody, x, y, 18, 10)));
		playerEntity.add(new CollisionComponent());
		engine.addEntity(playerEntity);
	}

	private void createTileMap() {
		tiledMap = new TmxMapLoader().load("experiment/test-map.tmx");
		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

		Texture coinTexture = new Texture("coin.png");
		Texture doorTexture = new Texture("door.png");
		Texture keyTexture = new Texture("key.png");

		MapObjects objects = tiledMap.getLayers().get("ObjectLayer").getObjects();
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
					coinEntity.add(new TransformComponent(r.x, r.y, 0.4f));
					coinEntity.add(new TextureComponent(coinTexture));
					coinEntity.add(new ItemComponent(COIN));
					coinEntity.add(new BodyComponent(createRectangleBody(
							coinEntity, BodyType.StaticBody, r.x, r.y, r.width * 0.5f, r.height * 0.5f)));
					engine.addEntity(coinEntity);
					break;
				case "door":
					Entity doorEntity = new Entity();
					doorEntity.add(new TransformComponent(r.x, r.y));
					doorEntity.add(new TextureComponent(doorTexture));
					doorEntity.add(new ItemComponent(DOOR));
					doorEntity.add(new BodyComponent(createRectangleBody(
							doorEntity, BodyType.StaticBody, r.x, r.y, r.width, r.height)));
					engine.addEntity(doorEntity);
					break;
				case "key":
					Entity keyEntity = new Entity();
					keyEntity.add(new TransformComponent(r.x, r.y, 0.5f));
					keyEntity.add(new TextureComponent(keyTexture));
					keyEntity.add(new ItemComponent(KEY));
					Body body = createRectangleBody(keyEntity, BodyType.StaticBody, r.x, r.y, r.width * 0.6f, r.height * 0.6f);
					body.getFixtureList().first().setSensor(true);
					keyEntity.add(new BodyComponent(body));
					engine.addEntity(keyEntity);
					break;
				default:
					System.out.println("WARN: unknown tilemap object");
			}
		}

		objects = tiledMap.getLayers().get("PhysicsLayer").getObjects();
		for (MapObject object : objects) {
			Entity wallEntity = new Entity();

			if (object instanceof RectangleMapObject) {
				RectangleMapObject rectangleObject = (RectangleMapObject) object;
				Rectangle r = rectangleObject.getRectangle();

				wallEntity.add(new BodyComponent(createRectangleBody(wallEntity, BodyType.StaticBody, r.x, r.y, r.width, r.height)));
			} else if (object instanceof PolygonMapObject) {
				PolygonMapObject polygonMapObject = (PolygonMapObject) object;
				Polygon polygon = polygonMapObject.getPolygon();

				wallEntity.add(new BodyComponent(createPolygonBody(wallEntity, BodyType.StaticBody, polygon)));
			}

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

	private Body createPolygonBody(Entity entity, BodyType type, Polygon polygon) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = type;
		bodyDef.fixedRotation = true;
		Body body = world.createBody(bodyDef);
		body.setUserData(entity);

		PolygonShape shape = new PolygonShape();

		float[] verticles = polygon.getTransformedVertices();
		float[] transformedVerticles = new float[verticles.length];
		for (int i = 0; i < verticles.length; i++) {
			transformedVerticles[i] = verticles[i] / 10;
		}
		shape.set(transformedVerticles);
		body.createFixture(shape, 0);
		shape.dispose();

		return body;
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.2f, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		engine.update(Gdx.graphics.getDeltaTime());
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
