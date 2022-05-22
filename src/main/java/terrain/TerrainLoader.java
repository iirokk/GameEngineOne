package terrain;

import renderEngine.Loader;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import water.WaterTile;

public class TerrainLoader {

    private final Loader loader;
    private final int worldGridSize = 2;
    private final int terrainSquareResolution = 16;
    private final TerrainSquareGenerator terrainSquareGenerator;

    public TerrainLoader(Loader loader) {
        this.loader = loader;
        this.terrainSquareGenerator = new TerrainSquareGenerator();
    }

    public TerrainMap generateTerrainMap() {
        TerrainMap terrainMap = new TerrainMap();
        TerrainTexturePack texturePack = loadTerrainTexturePack();
        for (int gridX = 0; gridX < worldGridSize; gridX++) {
            for (int gridZ = 0; gridZ < worldGridSize; gridZ++) {
                terrainMap.addTerrain(loadTerrainTile(texturePack, gridX, gridZ));
            }
        }
        terrainMap.addWaterTile(loadWaterTile());

        return terrainMap;
    }

    private TerrainTexturePack loadTerrainTexturePack() {
        String backgroundTexture1 = "texture/grass";
        String rTexture1 = "texture/gravel";
        String gTexture1 = "texture/sand";
        String bTexture1 = "texture/stone";

        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture(backgroundTexture1));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture(rTexture1));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture(gTexture1));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture(bTexture1));
        return new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
    }

    private Terrain loadTerrainTile(TerrainTexturePack texturePack, int gridX, int gridZ) {
        TerrainSquareArray terrainArray = terrainSquareGenerator.generateTerrainSquare(terrainSquareResolution);
        return new Terrain(gridX, gridZ, loader, texturePack, terrainArray);
    }

    private WaterTile loadWaterTile() {
        return new WaterTile(0, 0, 0);
    }
}
