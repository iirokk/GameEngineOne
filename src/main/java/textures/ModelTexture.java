package textures;

import lombok.Data;

@Data
public class ModelTexture {

    private int textureID;
    private float shineDamper = 1;
    private float reflectivity = 0;
    private boolean hasTransparency = false;
    private boolean useFakeLighting = false;
    private int numberOfRows = 1;

    public ModelTexture(int id) {
        this.textureID = id;
    }
}
