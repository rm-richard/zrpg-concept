package rmrichard.learn.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import rmrichard.learn.components.PlayerComponent;
import rmrichard.learn.components.PositionComponent;
import rmrichard.learn.components.TextureComponent;

public class DrawSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;
    private SpriteBatch spriteBatch;

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<TextureComponent> tm = ComponentMapper.getFor(TextureComponent.class);

    private OrthographicCamera camera;

    public DrawSystem(SpriteBatch spriteBatch, OrthographicCamera camera) {
        this.spriteBatch = spriteBatch;
        this.camera = camera;
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(PositionComponent.class, TextureComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        Entity player = getEngine().getEntitiesFor(Family.one(PlayerComponent.class).get()).first();
        PositionComponent playerPos = pm.get(player);
        camera.position.set(playerPos.x, playerPos.y, 0);
        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);

        for (int i = 0; i < entities.size(); ++i) {
            Entity entity = entities.get(i);
            PositionComponent pos = pm.get(entity);
            TextureComponent tex = tm.get(entity);

            spriteBatch.draw(tex.texture, pos.x, pos.y);
        }
    }
}
