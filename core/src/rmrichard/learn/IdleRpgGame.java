package rmrichard.learn;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import rmrichard.learn.components.BodyComponent;
import rmrichard.learn.components.PositionComponent;
import rmrichard.learn.components.TextureComponent;
import rmrichard.learn.systems.DrawSystem;
import rmrichard.learn.systems.PhysicSystem;
import rmrichard.learn.systems.PhysicsDebugSystem;

public class IdleRpgGame extends ApplicationAdapter {
	SpriteBatch batch;
	private Engine engine;

	@Override
	public void create () {
		batch = new SpriteBatch();
		engine = new Engine();

		World world = new World(new Vector2(0, 0), true);

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

		DrawSystem drawSystem = new DrawSystem(batch);
		engine.addSystem(drawSystem);
		engine.addSystem(new PhysicSystem(world));

		engine.addSystem(new PhysicsDebugSystem(world, drawSystem.getCamera()));
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.2f, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		engine.update(Gdx.graphics.getDeltaTime());
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
