package terrain;

import lombok.Getter;
import lombok.Setter;
import models.RawModel;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.Loader;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.Maths;
import util.SquareArray;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Getter
@Setter
public class Terrain {
    public static final float SIZE = 800;
    private static final float MAX_HEIGHT = 20;
    private static final float MAX_PIXEL_COLOR = 256 * 256 * 256;

    private float x;
    private float z;
    private int gridX;
    private int gridZ;
    private RawModel model;
    private TerrainTexturePack texturePack;
    private TerrainTexture blendMap;
    private float[][] heights;
    private boolean isRendered = false;

    public Terrain(int gridX, int gridZ, Loader loader, TerrainTexturePack texturePack, TerrainTexture blendMap, String heightMap) {
        this.texturePack = texturePack;
        this.blendMap = blendMap;
        this.x = gridX * SIZE;
        this.z = gridZ * SIZE;
        this.model = createTerrainFromHeightmap(loader, heightMap);
        this.gridX = gridX;
        this.gridZ = gridZ;
    }

    public Terrain(int gridX, int gridZ, Loader loader, TerrainTexturePack texturePack, TerrainTexture blendMap, TerrainSquareArray terrainArray) {
        this.texturePack = texturePack;
        this.blendMap = blendMap;
        this.x = gridX * SIZE;
        this.z = gridZ * SIZE;
        this.model = createTerrain(loader, terrainArray);
        this.gridX = gridX;
        this.gridZ = gridZ;
    }

    private RawModel createTerrain(Loader loader, TerrainSquareArray terrainArray){
        int VERTEX_COUNT = terrainArray.getSize();
        heights = new float[VERTEX_COUNT][VERTEX_COUNT];

        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count*2];
        int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
        int vertexPointer = 0;

        for(int i=0;i<VERTEX_COUNT;i++){
            for(int j=0;j<VERTEX_COUNT;j++){
                vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT - 1) * SIZE;
                float height = getHeight(i, VERTEX_COUNT - j - 1, terrainArray);
                heights[j][i] = height;
                vertices[vertexPointer*3+1] = height;
                vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * SIZE;
                Vector3f normal = calculateNormal(i, VERTEX_COUNT - j - 1, terrainArray);
                normals[vertexPointer*3] = normal.x;
                normals[vertexPointer*3+1] = normal.y;
                normals[vertexPointer*3+2] = normal.z;
                textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
                textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
                vertexPointer++;
            }
        }
        int pointer = 0;
        for(int gz=0;gz<VERTEX_COUNT-1;gz++){
            for(int gx=0;gx<VERTEX_COUNT-1;gx++){
                int topLeft = (gz*VERTEX_COUNT)+gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }
        return loader.loadToVAO(vertices, textureCoords, normals, indices);
    }

    private RawModel createTerrainFromHeightmap(Loader loader, String heightMap){
        TerrainSquareArray terrainArray = (TerrainSquareArray) getBufferedImageHeightmap(heightMap);
        return this.createTerrain(loader, terrainArray);
    }

    private SquareArray getBufferedImageHeightmap(String heightMap) {
        BufferedImage imageTerrain = null;
        try {
            imageTerrain = ImageIO.read(new File("res/" + heightMap + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (imageTerrain == null) {
            throw new RuntimeException("Null imageTerrain from BufferedImage.");
        }
        return new SquareArray(imageTerrain);
    }

    private float getHeight(int x, int z, TerrainSquareArray imageTerrain) {
        if (x < 0 || x >= imageTerrain.getSize() || z < 0 || z >= imageTerrain.getSize()) {
            return 0;
        }
        return imageTerrain.getTerrainHeight(x, z);
    }

    private Vector3f calculateNormal(int x, int z, TerrainSquareArray image) {
        float heightL = getHeight(x-1, z, image);
        float heightR = getHeight(x+1, z, image);
        float heightD = getHeight(x, z-1, image);
        float heightU = getHeight(x, z+1, image);
        Vector3f normal = new Vector3f(heightL-heightR, 2f, heightD-heightU);
        normal.normalise();
        return normal;
    }

    public float getHeightOfTerrain(float worldX, float worldZ) {
        // get location relative to current terrain
        float terrainX = worldX - this.x;
        float terrainZ = worldZ - this.z;

        // find location within terrain square
        float gridSquareSize = SIZE / ((float) heights.length -1);
        int gridX = (int) Math.floor(terrainX / gridSquareSize);
        int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
        if (gridX >= heights.length - 1 || gridZ >= heights.length - 1 || gridX < 0 || gridZ < 0) {
            return 0;
        }

        // find height within terrain vertex
        float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
        float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;
        float pointHeight;
        if (xCoord <= 1 - zCoord) {
            pointHeight = Maths.barycentricFunction(new Vector3f(0, heights[gridX][gridZ], 0),
                    new Vector3f(1, heights[gridX + 1][gridZ], 0),
                    new Vector3f(0, heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
        } else {
            pointHeight = Maths.barycentricFunction(new Vector3f(1, heights[gridX + 1][gridZ], 0),
                    new Vector3f(1, heights[gridX + 1][gridZ + 1], 1),
                    new Vector3f(0, heights[gridX][gridZ + 1], 1), new Vector2f(xCoord, zCoord));
        }
        return pointHeight;
    }
}
