package rmrichard.learn.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import rmrichard.learn.components.PlayerComponent;
import rmrichard.learn.components.PositionComponent;
import rmrichard.learn.components.TextureComponent;

import static rmrichard.learn.Constants.*;

public class DrawSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;
    private SpriteBatch spriteBatch;

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<TextureComponent> tm = ComponentMapper.getFor(TextureComponent.class);

    private OrthographicCamera camera;
    private TiledMapRenderer tiledMapRenderer;

    public DrawSystem(SpriteBatch spriteBatch, OrthographicCamera camera, TiledMapRenderer tiledMapRenderer) {
        this.spriteBatch = spriteBatch;
        this.camera = camera;
        this.tiledMapRenderer = tiledMapRenderer;
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(PositionComponent.class, TextureComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        Entity player = getEngine().getEntitiesFor(Family.one(PlayerComponent.class).get()).first();
        PositionComponent playerPos = pm.get(player);
        camera.position.set(
                MathUtils.clamp(playerPos.x, VIEW_WIDTH / 2, MAP_WIDTH - VIEW_WIDTH / 2),
                MathUtils.clamp(playerPos.y, VIEW_HEIGHT / 2, MAP_HEIGHT - VIEW_HEIGHT / 2),
                0);
        camera.update();

        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render(BACKGROUND_LAYERS);

        spriteBatch.begin();
        spriteBatch.setProjectionMatrix(camera.combined);

        for (int i = 0; i < entities.size(); ++i) {
            Entity entity = entities.get(i);
            PositionComponent pos = pm.get(entity);
            TextureComponent tex = tm.get(entity);

            spriteBatch.draw(tex.region, pos.x, pos.y, 0, 0, tex.region.getRegionWidth(), tex.region.getRegionHeight(),
                    pos.scale, pos.scale, 0f);
        }
        spriteBatch.end();

        tiledMapRenderer.render(FOREGROUND_LAYERS);
    }
}
