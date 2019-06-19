package rmrichard.learn.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import rmrichard.learn.components.AnimationComponent;
import rmrichard.learn.components.TextureComponent;

public class AnimationSystem extends IteratingSystem {

    private ComponentMapper<TextureComponent> tm = ComponentMapper.getFor(TextureComponent.class);
    private ComponentMapper<AnimationComponent> am = ComponentMapper.getFor(AnimationComponent.class);

    public AnimationSystem() {
        super(Family.all(TextureComponent.class, AnimationComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TextureComponent tc = tm.get(entity);
        AnimationComponent ac = am.get(entity);

        if (!ac.paused) {
            ac.frameTime += deltaTime;
            tc.region = ac.getActiveAnimation().getKeyFrame(ac.frameTime, true);

            if (ac.frameTime > ac.getActiveAnimation().getAnimationDuration()) {
                ac.frameTime = 0;
            }
        }
    }
}
