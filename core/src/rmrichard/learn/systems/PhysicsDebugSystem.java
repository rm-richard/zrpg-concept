package rmrichard.learn.systems;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

public class PhysicsDebugSystem extends EntitySystem {

    private Box2DDebugRenderer box2DDebugRenderer;
    private World world;
    private OrthographicCamera camera;
    private OrthographicCamera debugCamera;

    private boolean enabled = false;

    public PhysicsDebugSystem(World world, OrthographicCamera camera) {
        this.world = world;
        this.camera = camera;
        this.debugCamera = new OrthographicCamera(camera.viewportWidth / 10, camera.viewportHeight / 10);
        this.box2DDebugRenderer = new Box2DDebugRenderer();
        box2DDebugRenderer.setDrawVelocities(true);
    }

    @Override
    public void update(float deltaTime) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            enabled = !enabled;
        }

        if (enabled) {
            debugCamera.position.set(camera.position.x / 10, camera.position.y / 10, 0);
            debugCamera.update();
            box2DDebugRenderer.render(world, debugCamera.combined);
        }
    }
}
