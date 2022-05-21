package terrain;

import renderEngine.Loader;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import water.WaterTile;

public class TerrainLoader {

    private final Loader loader;
    private final int gridSize = 8;

    public TerrainLoader(Loader loader) {
        this.loader = loader;
    }

    public TerrainMap generateTerrainMap() {
        TerrainMap terrainMap = new TerrainMap();
        TerrainTexturePack texturePack = loadTerrainTexturePack();
        for (int gridX = 0; gridX < gridSize; gridX++) {
            for (int gridY = 0; gridY < gridSize; gridY++) {
                terrainMap.addTerrain(loadTerrainTile(texturePack, gridX, gridY));
            }
        }
        terrainMap.addWaterTile(loadWaterTile());

        return terrainMap;
    }

    private TerrainTexturePack loadTerrainTexturePack() {
        String backgroundTexture1 = "grassy2";
        String rTexture1 = "mud";
        String gTexture1 = "grassFlowers";
        String bTexture1 = "ground_tex";

        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture(backgroundTexture1));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture(rTexture1));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture(gTexture1));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture(bTexture1));
        return new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
    }

    private Terrain loadTerrainTile(TerrainTexturePack texturePack, int gridX, int gridY) {
        String blendMapFile = "blendMap";
        int gridID = getTerrainID(gridX, gridY);
        String heightMapFile = "terrain/heightmap/image_part_" + String.format("%3s", gridID).replace(' ', '0');
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture(blendMapFile));
        return new Terrain(gridX, gridY, loader, texturePack, blendMap, heightMapFile);
    }

    private int getTerrainID(int gridX, int gridY) {
        return (gridSize - gridX - 1) * 16 + gridY + 1;
    }

    private WaterTile loadWaterTile() {
        WaterTile waterTile = new WaterTile(175, -175, -1.5f);
        return waterTile;
    }
}
