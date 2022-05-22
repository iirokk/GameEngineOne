package water;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WaterTile {

    public static final float TILE_SIZE = 800;

    private float height;
    private float x;
    private float z;

    public void setPosition(float x, float z) {
        setX(x);
        setZ(z);
    }
}
