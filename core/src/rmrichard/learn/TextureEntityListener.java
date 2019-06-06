package rmrichard.learn;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;

public class TextureEntityListener implements EntityListener {
    private ComponentMapper<TextureComponent> tm = ComponentMapper.getFor(TextureComponent.class);

    @Override
    public void entityAdded(Entity entity) {
        TextureComponent textureComponent = tm.get(entity);
        System.out.println("add called");
    }

    @Override
    public void entityRemoved(Entity entity) {
        TextureComponent textureComponent = tm.get(entity);
        textureComponent.texture.dispose();
        System.out.println("remove called");
    }
}
