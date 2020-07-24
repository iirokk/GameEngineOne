package terrain;

import renderEngine.Loader;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

public class TerrainLoader {

    private final Loader loader;

    public TerrainLoader(Loader loader) {
        this.loader = loader;
    }

    public TerrainMap generateTerrainMap() {
        TerrainMap terrainMap = new TerrainMap();
        TerrainTexturePack texturePack = loadTerrainTexturePack();
        for (int gridX = -2; gridX < 3; gridX++) {
            for (int gridY = -2; gridY < 3; gridY++) {
                terrainMap.addTerrain(loadTerrainTile(texturePack, gridX, gridY));
            }
        }
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
        return new Terrain(gridX, gridY, loader, texturePack, blendMap, "heightmap");
    }
}
