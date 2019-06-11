package rmrichard.learn.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.physics.box2d.Body;
import rmrichard.learn.components.BodyComponent;
import rmrichard.learn.components.PlayerComponent;

public class PlayerMovementSystem extends EntitySystem {

    private ComponentMapper<BodyComponent> bm = ComponentMapper.getFor(BodyComponent.class);
    private Entity player;

    @Override
    public void addedToEngine(Engine engine) {
        player = engine.getEntitiesFor(Family.one(PlayerComponent.class).get()).first();
    }

    @Override
    public void update(float deltaTime) {
        Body body = bm.get(player).body;
        body.setLinearDamping(10f);

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            body.applyLinearImpulse(0, 5000, 0, 0, true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            body.applyLinearImpulse(0, -5000, 0, 0, true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            body.applyLinearImpulse(5000, 0, 0, 0, true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            body.applyLinearImpulse(-5000, 0, 0, 0, true);
        }
    }
}
