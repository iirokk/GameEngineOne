package terrain;

import util.OpenSimplexNoise;

import java.util.Random;

public class TerrainSquareGenerator {

    private final Random random;
    private final OpenSimplexNoise openSimplexNoise;

    private final Double TERRAIN_NOISE_FACTOR = 0.0;
    private final float TERRAIN_MULTIPLIER = 10.0f;

    public TerrainSquareGenerator() {
        this.random = new Random();
        this.openSimplexNoise = new OpenSimplexNoise(random.nextInt());
    }

    public TerrainSquareArray generateTerrainSquare(int size, int startCoordinateX, int startCoordinateY) {
        TerrainSquareArray terrainSquareArray = new TerrainSquareArray();
        Float[][] terrainArray = new Float[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                double eval = openSimplexNoise.eval(startCoordinateX + i, startCoordinateY + j, TERRAIN_NOISE_FACTOR);
                terrainArray[i][j] = (float) eval * TERRAIN_MULTIPLIER;
            }
        }
        terrainSquareArray.setArray(terrainArray);
        return terrainSquareArray;
    }
}
