package rmrichard.learn.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import rmrichard.learn.components.CollisionComponent;
import rmrichard.learn.components.ItemComponent;
import rmrichard.learn.components.PlayerComponent;

public class PlayerCollisionSystem extends IteratingSystem {

    private ComponentMapper<PlayerComponent> pm = ComponentMapper.getFor(PlayerComponent.class);
    private ComponentMapper<CollisionComponent> cm = ComponentMapper.getFor(CollisionComponent.class);
    private ComponentMapper<ItemComponent> im = ComponentMapper.getFor(ItemComponent.class);

    public PlayerCollisionSystem() {
        super(Family.all(PlayerComponent.class, CollisionComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PlayerComponent playerComponent = pm.get(entity);
        CollisionComponent collisionComponent = cm.get(entity);
        Entity collidedEntity = collisionComponent.collidedEntity;

        if (collidedEntity == null) {
            return;
        }

        ItemComponent itemComponent = im.get(collidedEntity);
        if (itemComponent == null) {
            return;
        }

        switch (itemComponent.itemType) {
            case KEY:
                System.out.println("Acquired key!");
                playerComponent.hasKey = true;
                getEngine().removeEntity(collidedEntity);
                break;
            case COIN:
                System.out.println("Picked up a coin!");
                getEngine().removeEntity(collidedEntity);
                break;
            case DOOR:
                System.out.print("Collided with DOOR - ");
                if (playerComponent.hasKey) {
                    System.out.println("and successfully unlocked it!");
                    getEngine().removeEntity(collidedEntity);
                 } else  {
                    System.out.println("but you dont have the key!");
                }
                break;
            default:
                System.out.println("WARN: Collided with unknown item type");
        }

        collisionComponent.collidedEntity = null;
    }
}
