package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.PlayerPosition;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMaster;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import particles.ParticleMaster;
import particles.ParticleSystem;
import particles.ParticleTexture;
import postProcessing.FrameBuffer;
import postProcessing.PostProcessing;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import terrain.TerrainLoader;
import terrain.TerrainMap;
import textures.ModelTexture;
import toolbox.MousePicker;
import toolbox.MouseSelector;
import water.WaterFrameBuffer;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainGameLoop {

    public static float randomFloat(Random random) {
        int min = 0;
        int max = 400;
        return min + random.nextFloat() * (max - min);
    }

    public static float randomFloat(Random random, int min_value, int max_value) {
        return min_value + random.nextFloat() * (max_value - min_value);
    }

    public static void main(String[] args) {
        DisplayManager.createDisplay();
        Loader loader = new Loader();

        // Terrain
        TerrainLoader terrainLoader = new TerrainLoader(loader);
        TerrainMap terrainMap = terrainLoader.createTerrainFromHeightmap("hmap_river");

        // Water
        List<WaterTile> waterTiles = terrainMap.getWaterTiles();
        WaterFrameBuffer frameBuffers = new WaterFrameBuffer();
        WaterShader waterShader = new WaterShader();

        // creating entities list
        List<Entity> entities = new ArrayList<>();
        List<Entity> selectableEntities = new ArrayList<>();

        ModelData modelData = OBJFileLoader.loadOBJ("fern");
        RawModel model1 = loader.loadToVAO(modelData.getVertices(), modelData.getTextureCoords(), modelData.getNormals(),
                modelData.getIndices());
        TexturedModel texturedModel1 = new TexturedModel(model1,
                new ModelTexture(loader.loadTexture("fernAtlas")));
        ModelTexture texture1 = texturedModel1.getTexture();
        texture1.setShineDamper(5);
        texture1.setReflectivity(0.1f);
        texture1.setHasTransparency(true);
        texture1.setNumberOfRows(2);

        ModelData modelData2 = OBJFileLoader.loadOBJ("gameModels/house1");
        RawModel model2 = loader.loadToVAO(modelData2.getVertices(), modelData2.getTextureCoords(), modelData2.getNormals(),
                modelData2.getIndices());
        TexturedModel texturedModel2 = new TexturedModel(model2,
                new ModelTexture(loader.loadTexture("texture/stone")));
        ModelTexture texture2 = texturedModel2.getTexture();
        texture2.setShineDamper(5);
        texture2.setReflectivity(0.1f);

        Random random = new Random();
        for (int i = 0; i < 500; i++) {
            float xPos = randomFloat(random);
            float zPos = randomFloat(random);
            float yPos = terrainMap.getHeightOfTerrain(xPos, zPos);
            entities.add(new Entity(texturedModel1, random.nextInt(4), new Vector3f(xPos, yPos, zPos),
                    0, randomFloat(random), 0, 0.5f));
        }
        for (int i = 0; i < 100; i++) {
            float xPos = randomFloat(random, 0, 1600);
            float zPos = randomFloat(random, 0, 1600);
            float yPos = terrainMap.getHeightOfTerrain(xPos, zPos);
            Entity e = new Entity(texturedModel2, new Vector3f(xPos, yPos, zPos),
                    0, randomFloat(random), 0, 7);
            entities.add(e);
            selectableEntities.add(e);
        }

        // Player
        PlayerPosition playerPosition = new PlayerPosition(new Vector3f(50, 0, 50), 0, 0, 0);
        Camera camera = new Camera(playerPosition, terrainMap);

        // Light
        List<Light> lightSources = new ArrayList<>();
        float sunBrightness = 1.5f;
        Vector3f sunOriginalColor = new Vector3f(1f * sunBrightness, 0.97f * sunBrightness, 0.91f * sunBrightness);
        Light sun = new Light(new Vector3f(-10000f, 10000f, 10000f), sunOriginalColor);
        lightSources.add(sun);
        Light campFire = new Light(new Vector3f(5, 10, 0), new Vector3f(2.6f, 2.0f, 1.3f),
                new Vector3f(1, 0.001f, 0.001f));
        lightSources.add(campFire);

        // Particles
        ParticleTexture particleTexture = new ParticleTexture(loader.loadTexture("particleAtlas"), 4);
        ParticleSystem playerParticleSystem = new ParticleSystem(particleTexture, 6, 6, 1, 10, 0.9f);
        ParticleTexture fireTexture = new ParticleTexture(loader.loadTexture("fire"), 8, true);
        ParticleSystem fireParticles = new ParticleSystem(fireTexture, 20, 0.01f, -0.08f, 3, 8);
        fireParticles.randomizeRotation();
        fireParticles.setDirection(new Vector3f(0, 1, 0), 0.2f);
        fireParticles.setScaleVariance(5f);

        // Create renderers
        MasterRenderer renderer = new MasterRenderer(loader, camera);
        WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), frameBuffers);
        ParticleMaster.init(loader, renderer.getProjectionMatrix());
        GuiRenderer guiRenderer = new GuiRenderer(loader);
        TextMaster.init(loader);

        // GUI
        List<GuiTexture> guiTextures = new ArrayList<>();
        GuiTexture guiCompass = new GuiTexture(loader.loadTexture("gui/compass"), new Vector2f(0f, -0.85f), new Vector2f(0.25f, 0.05f));
        guiTextures.add(guiCompass);
        GuiTexture guiCompassPointer = new GuiTexture(loader.loadTexture("gui/loc_indicator"), new Vector2f(0.1f, -0.85f), new Vector2f(0.02f, 0.03f));
        guiTextures.add(guiCompassPointer);
        GuiTexture guiHealth = new GuiTexture(loader.loadTexture("gui/health_indicator"), new Vector2f(0f, -0.92f), new Vector2f(0.25f, 0.01f));
        guiTextures.add(guiHealth);

        // Texts (after renderers)
        FontType font = new FontType(loader.loadFontTextureAtlas("segoe"), new File("res/fonts/segoe.fnt"));
        GUIText textFPS = new GUIText("FPS", 0.6f, font, new Vector2f(0.95f, 0.01f), 0.05f, false);
        textFPS.setColor(0.8f, 0.8f, 0);
        GUIText debugText = new GUIText("debug", 0.6f, font, new Vector2f(0.5f, 0.01f), 0.2f, false);
        debugText.setColor(0.8f, 0.8f, 0);
        //FontType font2 = new FontType(loader.loadFontTextureAtlas("northumbria"), new File("res/fonts/northumbria.fnt"));
        //GUIText testText = new GUIText("Testing larger font rendering.", 1.6f, font2, new Vector2f(0.5f, 0.1f), 0.2f, true);
        //testText.setColor(0.85f, 0.85f, 0.85f);
        //testText.setBorderWidth(0.45f);
        //testText.setTransparency(0.6f);

        MousePicker mousePicker = new MousePicker(renderer.getProjectionMatrix(), camera);
        MouseSelector mouseSelector = new MouseSelector(mousePicker, camera);

        // FBO: frame buffer object
        FrameBuffer multisampleFrameBuffer = new FrameBuffer(Display.getWidth(), Display.getHeight());
        FrameBuffer outputFrameBuffer = new FrameBuffer(Display.getWidth(), Display.getHeight(), FrameBuffer.DEPTH_TEXTURE);
        PostProcessing.init(loader);


        while (!Display.isCloseRequested()) {
            camera.move();
            playerPosition.move(terrainMap);
            terrainMap.updateRenderedTerrains(playerPosition.getPosition().x, playerPosition.getPosition().z);

            playerParticleSystem.generateParticles(playerPosition.getPosition());
            fireParticles.generateParticles(new Vector3f(0, 0, 0));
            ParticleMaster.update(camera);

            mousePicker.update();  // always update after camera update
            mouseSelector.mouseSelectEntity(selectableEntities); // always after mousePicker update

            // game logic

            // render
            renderer.renderShadowMap(entities, sun);  // always before other rendering calls

            // render reflection
            GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
            frameBuffers.bindReflectionFrameBuffer();
            float cameraReflectionDistance = 2 * (camera.getPosition().y - TerrainMap.WATER_LEVEL);
            camera.getPosition().y -= cameraReflectionDistance;
            camera.invertPitch();
            renderer.renderScene(entities, terrainMap, lightSources, camera, new Vector4f(0, 1, 0, -TerrainMap.WATER_LEVEL));
            camera.getPosition().y += cameraReflectionDistance;
            camera.invertPitch();

            // render refraction
            frameBuffers.bindRefractionFrameBuffer();
            renderer.renderScene(entities, terrainMap, lightSources, camera, new Vector4f(0, -1, 0, TerrainMap.WATER_LEVEL + 1f));
            // raise clipping plane level to reduce glitching at water edge (try removing later)
            GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
            frameBuffers.unbindCurrentFrameBuffer();

            // Render to frame buffer for post processing
            multisampleFrameBuffer.bindFrameBuffer();
            renderer.renderScene(entities, terrainMap, lightSources, camera, new Vector4f(0, 0, 0, 0));
            waterRenderer.render(waterTiles, camera, lightSources.get(0));
            ParticleMaster.renderParticles(camera);
            multisampleFrameBuffer.unbindFrameBuffer();
            multisampleFrameBuffer.resolveToFrameBuffer(outputFrameBuffer);
            PostProcessing.doPostProcessing(outputFrameBuffer.getColourTexture());

            // Render GUI & texts
            guiRenderer.render(guiTextures);
            TextMaster.updateTextString(textFPS, "FPS: " + Math.round(1 / DisplayManager.getFrameTimeSeconds()));
            TextMaster.updateTextString(debugText, "xyz: " + playerPosition.getPosition());
            TextMaster.render();

            DisplayManager.updateDisplay();
        }
        // Clean up
        multisampleFrameBuffer.cleanUp();
        outputFrameBuffer.cleanUp();
        PostProcessing.cleanUp();
        ParticleMaster.cleanUp();
        TextMaster.cleanUp();
        frameBuffers.cleanUp();
        waterShader.cleanUp();
        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
}
