package terrain;

public class TerrainSquareGenerator {

    public TerrainSquareGenerator() {
    }

    public TerrainSquareArray generateTerrainSquare(int size) {
        TerrainSquareArray terrainSquareArray = new TerrainSquareArray();
        Float[][] terrainArray = new Float[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                terrainArray[i][j] = 2f;  // set flat terrain
            }
        }
        terrainSquareArray.setArray(terrainArray);
        return terrainSquareArray;
    }
}
