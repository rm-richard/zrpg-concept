package rmrichard.learn.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import rmrichard.learn.components.BodyComponent;
import rmrichard.learn.components.PlayerComponent;

public class PlayerMovementSystem extends EntitySystem {

    private static final float VELOCITY = 10f;

    private ComponentMapper<BodyComponent> bm = ComponentMapper.getFor(BodyComponent.class);
    private Entity player;

    @Override
    public void addedToEngine(Engine engine) {
        player = engine.getEntitiesFor(Family.one(PlayerComponent.class).get()).first();
    }

    @Override
    public void update(float deltaTime) {
        Body body = bm.get(player).body;
        body.setLinearVelocity(0, 0);

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            addLinearVelocity(body, 0, 1);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            addLinearVelocity(body, 0, -1);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            addLinearVelocity(body, 1, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            addLinearVelocity(body, -1, 0);
        }

        Vector2 v = body.getLinearVelocity();
        v.setLength(VELOCITY);
        body.setLinearVelocity(v.x, v.y);
    }

    private void addLinearVelocity(Body body, float x, float y) {
        Vector2 v = body.getLinearVelocity();
        body.setLinearVelocity(v.x + x, v.y + y);
    }
}
