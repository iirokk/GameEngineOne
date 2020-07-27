package terrain;

import water.WaterTile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TerrainMap {
    private static Map<String, Terrain> terrains;
    private static List<WaterTile> waterTiles;

    public TerrainMap() {
        terrains = new HashMap<>();
        waterTiles = new ArrayList<>();
    }

    public void addTerrain(Terrain terrain) {
        String key = terrain.getGridX() + ":" + terrain.getGridZ();
        terrains.put(key, terrain);
    }

    public void updateRenderedTerrains(float worldX, float worldZ) {
        // set only adjacent terrains for rendering
        for (Terrain terrain:getAllTerrains()) {
            terrain.setRendered(false);
        }
        List<Terrain> renderedTerrains = getRenderedTerrainsOfPosition(worldX, worldZ);
        for (Terrain terrain:renderedTerrains) {
            terrain.setRendered(true);
        }
    }

    public void addWaterTile(WaterTile waterTile) {
        waterTiles.add(waterTile);
    }

    public static int[] getTerrainGridPosition(float worldX, float worldZ) {
        int[] terrainGridXY = new int[2];
        terrainGridXY[0] = (int) Math.floor(worldX / Terrain.SIZE);  //terrainPositionX
        terrainGridXY[1] = (int) Math.floor(worldZ / Terrain.SIZE);  //terrainPositionZ
        return terrainGridXY;
    }

    private static Terrain getTerrainOfPosition(float worldX, float worldZ) {
        int[] gridXZ = getTerrainGridPosition(worldX, worldZ);
        return terrains.get(gridXZ[0] + ":" + gridXZ[1]);
    }

    private static List<Terrain> getRenderedTerrainsOfPosition(float worldX, float worldZ) {
        int[] gridXZ = getTerrainGridPosition(worldX, worldZ);
        List<Terrain> renderedTerrains = new ArrayList<>();
        for (int dX = -1; dX < 2; dX++) {
            for (int dY = -1; dY < 2; dY++) {
                Terrain terrain = terrains.get((gridXZ[0]+dX) + ":" + (gridXZ[1]+dY));
                if (terrain != null) {
                    renderedTerrains.add(terrain);
                }
            }
        }
        return renderedTerrains;
    }

    public float getHeightOfTerrain(float worldX, float worldZ) {
        Terrain terrain = getTerrainOfPosition(worldX, worldZ);
        if (terrain == null) {
            return 0;
        }
        return terrain.getHeightOfTerrain(worldX, worldZ);
    }

    public ArrayList<Terrain> getAllTerrains() {
        return new ArrayList<>(terrains.values());
    }

    public List<WaterTile> getWaterTiles() {
        return waterTiles;
    }
}
