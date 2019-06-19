package rmrichard.learn;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import rmrichard.learn.components.*;
import rmrichard.learn.listeners.BodyRemovalListener;
import rmrichard.learn.listeners.EntityContactListener;
import rmrichard.learn.systems.*;

import static rmrichard.learn.Constants.VIEW_HEIGHT;
import static rmrichard.learn.Constants.VIEW_WIDTH;
import static rmrichard.learn.components.ItemComponent.Type.*;

public class ZrpgConceptGame extends Game {
	private SpriteBatch batch;
	private Engine engine;
	private World world;
	private OrthographicCamera camera;

	private TiledMap tiledMap;
	private TiledMapRenderer tiledMapRenderer;

	private Stage uiStage;
	private Label coinCollectedLabel;
	private Label fpsLabel;

	@Override
	public void create () {
		batch = new SpriteBatch();
		engine = new Engine();
		world = new World(new Vector2(0, 0), true);
		world.setContactListener(new EntityContactListener());
		camera = new OrthographicCamera(VIEW_WIDTH, VIEW_HEIGHT);
		camera.position.set(new Vector2(VIEW_WIDTH / 2, VIEW_HEIGHT / 2), 0);
		createUiStage();
		createTileMap();

        engine.addSystem(new PlayerMovementSystem());
        engine.addSystem(new PhysicSystem(world));
        engine.addSystem(new AnimationSystem());
        engine.addSystem(new DrawSystem(batch, camera, tiledMapRenderer));
        engine.addSystem(new PlayerCollisionSystem(coinCollectedLabel));
        engine.addSystem(new PhysicsDebugSystem(world, camera));

		engine.addEntityListener(Family.all(BodyComponent.class).get(),
				new BodyRemovalListener(world));
	}

	private void createUiStage() {
		uiStage = new Stage();
		Label.LabelStyle style = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
		coinCollectedLabel = new Label("Coins collected: 0", style);
		coinCollectedLabel.setFontScale(1.5f);
		coinCollectedLabel.setColor(0.75f, 0.85f, 0.2f, 1.0f);
		coinCollectedLabel.setPosition(560, 20);
		uiStage.addActor(coinCollectedLabel);

		fpsLabel = new Label("FPS: ", style);
		fpsLabel.setFontScale(1.5f);
		fpsLabel.setColor(0.2f, 0.8f, 0.7f, 1.0f);
		fpsLabel.setPosition(20, 20);
		uiStage.addActor(fpsLabel);
	}

	private void createPlayer(float x, float y) {
		Texture playerTexture = new Texture("lpc-male-walk3.png");
		TextureRegion[][] regions = TextureRegion.split(playerTexture, 48, 64);

		Entity playerEntity = new Entity();
		AnimationComponent ac = new AnimationComponent();
		ac.addAnimation("up", new Animation<>(0.1f, new Array<>(regions[0]), Animation.PlayMode.LOOP));
		ac.addAnimation("right", new Animation<>(0.1f, new Array<>(regions[1]), Animation.PlayMode.LOOP));
		ac.addAnimation("down", new Animation<>(0.1f, new Array<>(regions[2]), Animation.PlayMode.LOOP));
		ac.addAnimation("left", new Animation<>(0.1f, new Array<>(regions[3]), Animation.PlayMode.LOOP));
		ac.activeAnimation = "down";
		playerEntity.add(ac);

		playerEntity.add(new TransformComponent(x, y, 0.6f, -5, 0));
		playerEntity.add(new TextureComponent(regions[2][0]));
		playerEntity.add(new PlayerComponent());
		playerEntity.add(new BodyComponent(createRectangleBody(playerEntity, BodyType.DynamicBody, x, y, 18, 10)));
		playerEntity.add(new CollisionComponent());

		engine.addEntity(playerEntity);
	}

	private void createTileMap() {
		tiledMap = new TmxMapLoader().load("test-map.tmx");
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

		fpsLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
		uiStage.draw();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
