package rmrichard.learn.components;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimationComponent implements Component {
    public Map<String, Animation<TextureRegion>> animations = new HashMap<>();
    public String activeAnimation;
    public float frameTime = 0.0f;
    public boolean paused = false;

    public void addAnimation(String name, Animation<TextureRegion> animation) {
        animations.put(name, animation);
    }

    public Animation<TextureRegion> getActiveAnimation() {
        return animations.get(activeAnimation);
    }
}
