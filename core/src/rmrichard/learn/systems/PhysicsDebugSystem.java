package rmrichard.learn.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

public class PhysicsDebugSystem extends EntitySystem {

    private Box2DDebugRenderer box2DDebugRenderer;
    private World world;
    private OrthographicCamera camera;

    public PhysicsDebugSystem(World world, OrthographicCamera camera) {
        this.world = world;
        this.camera = camera;
        this.box2DDebugRenderer = new Box2DDebugRenderer();
        box2DDebugRenderer.setDrawVelocities(true);
        this.camera = new OrthographicCamera(800, 600);
        this.camera.position.set(new Vector2(400, 300), 0);
        this.camera.update();
    }

    @Override
    public void update(float deltaTime) {
        box2DDebugRenderer.render(world, camera.combined);
    }
}
