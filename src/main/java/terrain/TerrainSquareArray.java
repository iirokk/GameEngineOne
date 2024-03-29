package terrain;

import lombok.AllArgsConstructor;
import util.SquareArray;

@AllArgsConstructor
public class TerrainSquareArray extends SquareArray {

    public TerrainSquareArray(Float[][] array) {
        super(array);
    }

    public float getTerrainHeight(int x, int y) {
        return super.getArray()[x][y];
    }
}
