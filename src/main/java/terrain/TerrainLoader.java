package terrain;

import renderEngine.Loader;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import water.WaterTile;

public class TerrainLoader {

    private final Loader loader;
    private final int worldGridSize = 8;
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
            for (int gridY = 0; gridY < worldGridSize; gridY++) {
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
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture(blendMapFile));

        // TODO: instead of image, generate random smoothed terrain here
        // TODO: make camera move speed zoom level dependent -> higher view, faster camera

        TerrainSquareArray terrainArray = terrainSquareGenerator.generateTerrainSquare(terrainSquareResolution);

        return new Terrain(gridX, gridY, loader, texturePack, blendMap, terrainArray);
    }

    private WaterTile loadWaterTile() {
        return new WaterTile(175, -175, 0);
    }
}
