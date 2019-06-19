package rmrichard.learn.components;

import com.badlogic.ashley.core.Component;

public class TransformComponent implements Component {
    public float x = 0.0f;
    public float y = 0.0f;
    public float scale = 1.0f;
    public float offsetX = 0.0f;
    public float offsetY = 0.0f;

    public TransformComponent(float x, float y) {
        this(x, y, 1.0f);
    }

    public TransformComponent(float x, float y, float scale) {
        this(x, y, scale, 0.0f, 0.0f);
    }

    public TransformComponent(float x, float y, float scale, float offsetX, float offsetY) {
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }
}
