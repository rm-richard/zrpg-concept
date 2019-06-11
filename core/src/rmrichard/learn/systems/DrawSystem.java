package rmrichard.learn.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import rmrichard.learn.components.PositionComponent;
import rmrichard.learn.components.TextureComponent;

public class DrawSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;
    private SpriteBatch spriteBatch;

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<TextureComponent> tm = ComponentMapper.getFor(TextureComponent.class);

    private OrthographicCamera camera;

    private Box2DDebugRenderer box2DDebugRenderer;

    public DrawSystem(SpriteBatch spriteBatch) {
        this.spriteBatch = spriteBatch;
        this.camera = new OrthographicCamera(640, 480);
        this.camera.position.set(new Vector2(320, 240), 0);
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(PositionComponent.class, TextureComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);

        for (int i = 0; i < entities.size(); ++i) {
            Entity entity = entities.get(i);
            PositionComponent pos = pm.get(entity);
            TextureComponent tex = tm.get(entity);

            spriteBatch.draw(tex.texture, pos.x, pos.y);
        }
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}
