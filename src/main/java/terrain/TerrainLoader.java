package terrain;

import renderEngine.Loader;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import util.SquareArray;
import water.WaterTile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TerrainLoader {

    private final Loader loader;
    private final int worldGridSize = 32;
    private final int terrainSquareResolution = 64;
    private final float TERRAIN_HEIGHT_MULTIPLIER = 0.2f;
    private final float TERRAIN_HEIGHT_MODIFIER = -20f;

    public TerrainLoader(Loader loader) {
        this.loader = loader;
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

    public TerrainMap createTerrainFromHeightmap(String heightMap) {
        TerrainMap terrainMap = new TerrainMap();
        TerrainTexturePack texturePack = loadTerrainTexturePack();
        TerrainSquareArray terrainArray = getBufferedImageHeightmap(heightMap);

        for (int gridX = 0; gridX < worldGridSize; gridX++) {
            for (int gridZ = 0; gridZ < worldGridSize; gridZ++) {
                SquareArray slice = terrainArray.getSlice(
                        gridX * (terrainSquareResolution + 1) - gridX, (gridX + 1) * (terrainSquareResolution + 1) -gridX,
                        gridZ * (terrainSquareResolution + 1) -gridZ, (gridZ + 1) * (terrainSquareResolution + 1) -gridZ);
                slice.flipReverse();
                terrainMap.addTerrain(new Terrain(gridX, worldGridSize - 1 - gridZ, loader, texturePack, new TerrainSquareArray(slice.getArray())));
            }
        }
        return terrainMap;
    }

    private TerrainSquareArray getBufferedImageHeightmap(String heightMap) {
        int requiredSize = terrainSquareResolution * worldGridSize + 1;
        BufferedImage imageTerrain = null;
        try {
            imageTerrain = ImageIO.read(new File("res/" + heightMap + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (imageTerrain == null) {
            throw new RuntimeException("ImageTerrain from BufferedImage is null.");
        }
        if (imageTerrain.getHeight() != requiredSize || imageTerrain.getWidth() != requiredSize) {
            throw new RuntimeException(String.format("ImageTerrain from BufferedImage has wrong dimensions: %s by %s", imageTerrain.getHeight(), imageTerrain.getWidth()));
        }
        return getSquareArrayFromImage(imageTerrain);
    }

    private TerrainSquareArray getSquareArrayFromImage(BufferedImage image) {
        if (image.getWidth() != image.getHeight()) {
            throw new RuntimeException(String.format("Can't create square array from image dimensions: %s, %s", image.getWidth(), image.getHeight()));
        }
        Float[][] array = new Float[image.getHeight()][image.getWidth()];
        for (int i = 0; i < image.getHeight(); i++)
            for (int j = 0; j < image.getWidth(); j++) {
                Color color = new Color(image.getRGB(i, j), false);
                array[i][j] = (float) (color.getBlue() + color.getGreen() + color.getRed()) * TERRAIN_HEIGHT_MULTIPLIER + TERRAIN_HEIGHT_MODIFIER;
            }
        return new TerrainSquareArray(array);
    }
}
