package rmrichard.learn.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import rmrichard.learn.components.BodyComponent;
import rmrichard.learn.components.TransformComponent;

public class PhysicSystem extends EntitySystem {

    public static float STEP = 1f / 45.0f;
    private float accumulator = 0f;

    private ComponentMapper<BodyComponent> bm = ComponentMapper.getFor(BodyComponent.class);
    private ComponentMapper<TransformComponent> pm = ComponentMapper.getFor(TransformComponent.class);
    private ImmutableArray<Entity> entities;

    private World world;

    public PhysicSystem(World world) {
        this.world = world;
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(BodyComponent.class, TransformComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        while (accumulator >= STEP) {
            world.step(STEP, 6, 2);
            accumulator -= STEP;
        }

        for (Entity entity : entities) {
            BodyComponent bc = bm.get(entity);
            TransformComponent pc = pm.get(entity);

            Vector2 position = bc.body.getPosition();
            pc.x = position.x * 10;
            pc.y = position.y * 10;
        }
    }
}
