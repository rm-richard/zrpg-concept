package rmrichard.learn;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class IdleRpgGame extends ApplicationAdapter {
	SpriteBatch batch;
	private Engine engine;

	@Override
	public void create () {
		batch = new SpriteBatch();
		engine = new Engine();

		engine.addEntityListener(Family.one(TextureComponent.class).get(), new TextureEntityListener());

		Entity imgEntity = new Entity();
		imgEntity.add(new PositionComponent(10, 10));
		imgEntity.add(new TextureComponent(new Texture("badlogic.jpg")));
		engine.addEntity(imgEntity);


		DrawSystem drawSystem = new DrawSystem(batch);
		engine.addSystem(drawSystem);

		engine.removeEntity(imgEntity);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
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
