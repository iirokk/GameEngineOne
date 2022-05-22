package terrain;

import water.WaterTile;

import java.util.*;


public class TerrainMap {
    private static Map<TerrainPosition, Terrain> terrains;
    private static List<WaterTile> waterTiles;

    public TerrainMap() {
        terrains = new HashMap<>();
        waterTiles = new ArrayList<>();
    }

    public void addTerrain(Terrain terrain) {
        terrains.put(new TerrainPosition(terrain.getGridX(), terrain.getGridZ()), terrain);
    }

    public void addWaterTile(WaterTile waterTile) {
        waterTiles.add(waterTile);
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
        waterTiles.get(0).setPosition(worldX, worldZ);
    }

    public static TerrainPosition getTerrainGridPosition(float worldX, float worldZ) {
        int gridX = (int) Math.floor(worldX / Terrain.SIZE);  //terrainPositionX
        int gridZ =  (int) Math.floor(worldZ / Terrain.SIZE);  //terrainPositionZ
        return new TerrainPosition(gridX, gridZ);
    }

    private static Terrain getTerrainOfPosition(float worldX, float worldZ) {
        getTerrainGridPosition(worldX, worldZ);
        return terrains.get(getTerrainGridPosition(worldX, worldZ));
    }

    private static List<Terrain> getRenderedTerrainsOfPosition(float worldX, float worldZ) {
        Set<Terrain> renderedTerrainsSet = new HashSet<>();
        TerrainPosition terrainGridPosition = getTerrainGridPosition(worldX, worldZ);
        for (int dX = -1; dX <= 1; dX++) {
            for (int dY = -1; dY <= 1; dY++) {
                Terrain terrain = terrains.get(new TerrainPosition(terrainGridPosition.getGridX() + dX, terrainGridPosition.getGridZ() + dY));
                if (terrain != null) {
                    renderedTerrainsSet.add(terrain);
                }
            }
        }
        return new ArrayList<>(renderedTerrainsSet);
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
