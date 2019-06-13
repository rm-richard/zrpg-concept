package rmrichard.learn.listeners;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.*;
import rmrichard.learn.components.CollisionComponent;

public class EntityContactListener implements ContactListener {

    private ComponentMapper<CollisionComponent> cm = ComponentMapper.getFor(CollisionComponent.class);

    @Override
    public void beginContact(Contact contact) {
        Object aData = contact.getFixtureA().getBody().getUserData();
        Object bData = contact.getFixtureB().getBody().getUserData();

        if (aData instanceof Entity && bData instanceof Entity) {
            setCollisionComponent((Entity) aData, (Entity) bData);
            setCollisionComponent((Entity) bData, (Entity) aData);
        }
    }

    private void setCollisionComponent(Entity entity, Entity collided) {
        CollisionComponent collisionComponent = cm.get(entity);
        if (collisionComponent != null) {
            collisionComponent.collidedEntity = collided;
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
