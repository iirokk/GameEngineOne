package terrain;

import water.WaterTile;

import java.util.*;


public class TerrainMap {
    private static int RENDERED_TERRAINS_RANGE = 2;

    private static Map<TerrainPosition, Terrain> terrains;
    private static List<WaterTile> waterTiles;

    public TerrainMap() {
        terrains = new HashMap<>();
        waterTiles = new ArrayList<>();
    }

    public void addTerrain(Terrain terrain) {
        terrains.put(new TerrainPosition(terrain.getGridX(), terrain.getGridZ()), terrain);

        float halfTerrainSize = Terrain.SIZE / 2;
        if (terrain.lowestPointHeight() < 0) {
            waterTiles.add(new WaterTile(halfTerrainSize, 0, terrain.getX() + halfTerrainSize, terrain.getZ() + halfTerrainSize));
        }
    }

    public void updateRenderedTerrains(float worldX, float worldZ) {
        // set only adjacent terrains for rendering
        // TODO: render only relevant water tiles
        for (Terrain terrain:getAllTerrains()) {
            terrain.setRendered(false);
        }
        List<Terrain> renderedTerrains = getRenderedTerrainsOfPosition(worldX, worldZ);
        for (Terrain terrain:renderedTerrains) {
            terrain.setRendered(true);
        }
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
        for (int dX = -RENDERED_TERRAINS_RANGE; dX <= RENDERED_TERRAINS_RANGE; dX++) {
            for (int dY = -RENDERED_TERRAINS_RANGE; dY <= RENDERED_TERRAINS_RANGE; dY++) {
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
