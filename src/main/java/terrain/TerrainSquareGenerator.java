package terrain;

import lombok.NoArgsConstructor;
import util.OpenSimplexNoise;

import java.util.Random;


@NoArgsConstructor
public class TerrainSquareGenerator {

    private final Double TERRAIN_NOISE_FACTOR = 0.0;
    private final float TERRAIN_MULTIPLIER = 10.0f;
    // TODO: normalize to some range, where 0 is water level

    Random random = new Random();
    OpenSimplexNoise openSimplexNoise;

    public TerrainSquareArray generateTerrainSquare(int size) {
        return generateTerrainSquare(size, random.nextInt());
    }

    public TerrainSquareArray generateTerrainSquare(int size, int seed) {
        openSimplexNoise = new OpenSimplexNoise(seed);

        TerrainSquareArray terrainSquareArray = new TerrainSquareArray();
        Float[][] terrainArray = new Float[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                double eval = openSimplexNoise.eval(i, j, TERRAIN_NOISE_FACTOR);
                terrainArray[i][j] = (float) eval * TERRAIN_MULTIPLIER;
            }
        }
        terrainSquareArray.setArray(terrainArray);
        return terrainSquareArray;
    }
}
