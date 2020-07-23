package renderEngine;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;
import shaders.StaticShader;
import shaders.TerrainShader;
import skybox.SkyboxRenderer;
import terrain.Terrain;
import terrain.TerrainMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterRenderer {

    private static final float FOV = 70;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000;  // also modify sky box size accordingly
    private static float SKY_RED;
    private static float SKY_GREEN;
    private static float SKY_BLUE;

    private static final float nightSkyRed = 0.01f;
    private static final float nightSkyGreen = 0.03f;
    private static final float nightSkyBlue = 0.03f;
    private static final float daySkyRed = 0.48f;
    private static final float daySkyGreen = 0.61f;
    private static final float daySkyBlue = 0.70f;

    private Matrix4f projectionMatrix;
    private StaticShader shader = new StaticShader();
    private EntityRenderer entityRenderer;
    private TerrainRenderer terrainRenderer;
    private TerrainShader terrainShader = new TerrainShader();
    private SkyboxRenderer skyboxRenderer;

    private Map<TexturedModel, List<Entity>> entities = new HashMap<>();
    private List<Terrain> terrains = new ArrayList<>();

    public MasterRenderer(Loader loader) {
        enableCulling();
        createProjectionMatrix();
        entityRenderer = new EntityRenderer(shader, projectionMatrix);
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
        skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
    }

    public void renderScene(List<Entity> entities, TerrainMap terrainMap, List<Light> lightSources, Camera camera,
                            float dayNightBlendFactor, Vector4f clippingPlane) {
        for (Entity entity:entities) {
            processEntity(entity);
        }
        for (Terrain terrain:terrainMap.getAllTerrains()) {
            processTerrain(terrain);
        }
        render(lightSources, camera, dayNightBlendFactor, clippingPlane);
    }

    public void render(List<Light> lights, Camera camera, float dayNightBlendFactor, Vector4f clippingPlane){
        calculateSkyColor(dayNightBlendFactor);
        prepare();
        shader.start();
        shader.loadClippingPlane(clippingPlane);
        shader.loadSkyColor(SKY_RED, SKY_GREEN, SKY_BLUE);
        shader.loadLights(lights);
        shader.loadViewMatrix(camera);
        entityRenderer.render(entities);
        shader.stop();
        terrainShader.start();
        terrainShader.loadClippingPlane(clippingPlane);
        terrainShader.loadSkyColor(SKY_RED, SKY_GREEN, SKY_BLUE);
        terrainShader.loadLights(lights);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains);
        terrainShader.stop();
        skyboxRenderer.render(camera, SKY_RED, SKY_GREEN, SKY_BLUE, dayNightBlendFactor);
        terrains.clear();
        entities.clear();
    }

    public void prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClearColor( SKY_RED, SKY_GREEN, SKY_BLUE, 1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
    }

    private void createProjectionMatrix() {
        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        projectionMatrix.m33 = 0;
    }

    public void  processEntity(Entity entity) {
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = entities.get(entityModel);
        if (batch!=null) {
            batch.add(entity);
        } else {
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            entities.put(entityModel, newBatch);
        }
    }

    public void processTerrain(Terrain terrain) {
        terrains.add(terrain);
    }

    public static void enableCulling(){
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    public static void disableCulling(){
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    public void cleanUp(){
        shader.cleanUp();
        terrainShader.cleanUp();
        skyboxRenderer.cleanUp();
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    private static void calculateSkyColor(float dayNightBlendFactor) {
        SKY_RED = nightSkyRed * dayNightBlendFactor + (1 - dayNightBlendFactor) * daySkyRed;
        SKY_GREEN = nightSkyGreen * dayNightBlendFactor + (1 - dayNightBlendFactor) * daySkyGreen;
        SKY_BLUE = nightSkyBlue * dayNightBlendFactor + (1 - dayNightBlendFactor) * daySkyBlue;
    }
}
