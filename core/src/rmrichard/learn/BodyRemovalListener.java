package rmrichard.learn;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.gdx.physics.box2d.World;
import rmrichard.learn.components.BodyComponent;

public class BodyRemovalListener implements EntityListener {

    private ComponentMapper<BodyComponent> bm = ComponentMapper.getFor(BodyComponent.class);

    private World box2dWorld;

    public BodyRemovalListener(World box2dWorld) {
        this.box2dWorld = box2dWorld;
    }

    @Override
    public void entityRemoved(Entity entity) {
        BodyComponent bc = bm.get(entity);
        box2dWorld.destroyBody(bc.body);
    }

    @Override
    public void entityAdded(Entity entity) {}
}
