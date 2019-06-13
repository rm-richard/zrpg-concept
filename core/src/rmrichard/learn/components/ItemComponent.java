package rmrichard.learn.components;

import com.badlogic.ashley.core.Component;

public class ItemComponent implements Component {

    public Type itemType;

    public ItemComponent(Type itemType) {
        this.itemType = itemType;
    }

    public enum Type {
        KEY, DOOR, COIN
    }
}
