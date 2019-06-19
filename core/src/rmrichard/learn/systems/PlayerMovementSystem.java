package rmrichard.learn.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import rmrichard.learn.components.AnimationComponent;
import rmrichard.learn.components.BodyComponent;
import rmrichard.learn.components.PlayerComponent;

public class PlayerMovementSystem extends EntitySystem {

    private static final float VELOCITY = 15f;

    private ComponentMapper<BodyComponent> bm = ComponentMapper.getFor(BodyComponent.class);
    private ComponentMapper<AnimationComponent> am = ComponentMapper.getFor(AnimationComponent.class);

    private Entity player;

    @Override
    public void addedToEngine(Engine engine) {
        player = engine.getEntitiesFor(Family.all(PlayerComponent.class, AnimationComponent.class).get()).first();
    }

    @Override
    public void update(float deltaTime) {
        AnimationComponent ac = am.get(player);
        Body body = bm.get(player).body;
        body.setLinearVelocity(0, 0);

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            ac.activeAnimation = "up";
            addLinearVelocity(body, 0, 1);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            ac.activeAnimation = "down";
            addLinearVelocity(body, 0, -1);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            ac.activeAnimation = "right";
            addLinearVelocity(body, 1, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            ac.activeAnimation = "left";
            addLinearVelocity(body, -1, 0);
        }

        Vector2 v = body.getLinearVelocity();
        v.setLength(VELOCITY);
        body.setLinearVelocity(v.x, v.y);
        ac.paused = v.len() < VELOCITY;
    }

    private void addLinearVelocity(Body body, float x, float y) {
        Vector2 v = body.getLinearVelocity();
        body.setLinearVelocity(v.x + x, v.y + y);
    }
}
