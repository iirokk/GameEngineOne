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

    public void addWaterTile(WaterTile waterTile) {
        waterTiles.add(waterTile);
    }

    private static Terrain getTerrainOfPosition(float worldX, float worldZ) {
        int terrainPositionX = (int) Math.floor(worldX / Terrain.SIZE);
        int terrainPositionZ = (int) Math.floor(worldZ / Terrain.SIZE);
        return terrains.get(terrainPositionX + ":" + terrainPositionZ);
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
