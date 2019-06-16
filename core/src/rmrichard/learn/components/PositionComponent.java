package rmrichard.learn.components;

import com.badlogic.ashley.core.Component;

public class PositionComponent implements Component {
    public float x = 0.0f;
    public float y = 0.0f;
    public float scale = 1.0f;

    public PositionComponent(float x, float y) {
        this(x, y, 1.0f);
    }

    public PositionComponent(float x, float y, float scale) {
        this.x = x;
        this.y = y;
        this.scale = scale;
    }
}
