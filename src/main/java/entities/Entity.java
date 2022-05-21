package entities;

import lombok.Getter;
import lombok.Setter;
import models.TexturedModel;
import org.lwjgl.util.vector.Vector3f;

@Getter
@Setter
public class Entity extends PositionalObject {

    private TexturedModel model;

    private float scale;
    private int textureIndex = 0;

    public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        this.model = model;
        super.setPosition(position);
        super.setRotX(rotX);
        super.setRotY(rotY);
        super.setRotZ(rotZ);
        this.scale = scale;
    }

    public Entity(TexturedModel model, int textureIndex, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        this.model = model;
        super.setPosition(position);
        super.setRotX(rotX);
        super.setRotY(rotY);
        super.setRotZ(rotZ);
        this.scale = scale;
        this.textureIndex = textureIndex;
    }

    public float getTextureXOffset() {
        int column = textureIndex%model.getTexture().getNumberOfRows();
        return (float) column / (float) model.getTexture().getNumberOfRows();
    }

    public float getTextureYOffset() {
        int row = textureIndex/model.getTexture().getNumberOfRows();
        return (float) row / (float) model.getTexture().getNumberOfRows();
    }
}
