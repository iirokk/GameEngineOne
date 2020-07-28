package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import fontMeshCreator.FontType;
import fontMeshCreator.GUIText;
import fontRendering.TextMaster;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import particles.Particle;
import particles.ParticleMaster;
import particles.ParticleSystem;
import particles.ParticleTexture;
import renderEngine.*;
import models.RawModel;
import shadows.ShadowMapMasterRenderer;
import terrain.TerrainLoader;
import terrain.TerrainMap;
import textures.ModelTexture;
import toolbox.MousePicker;
import toolbox.MouseSelector;
import water.WaterFrameBuffers;
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
		TerrainMap terrainMap = terrainLoader.generateTerrainMap();

		// Water
		List<WaterTile> waterTiles = terrainMap.getWaterTiles();
		WaterFrameBuffers frameBuffers = new WaterFrameBuffers();
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

		ModelData modelData2 = OBJFileLoader.loadOBJ("gameModels/dead_tree");
		RawModel model2 = loader.loadToVAO(modelData2.getVertices(), modelData2.getTextureCoords(), modelData2.getNormals(),
				modelData2.getIndices());
		TexturedModel texturedModel2 = new TexturedModel(model2,
				new ModelTexture(loader.loadTexture("ground_tex")));
		ModelTexture texture2 = texturedModel2.getTexture();
		texture2.setShineDamper(5);
		texture2.setReflectivity(0.1f);

		Random random = new Random();
		for (int i = 0; i < 500; i++) {
			float xPos = randomFloat(random);
			float zPos = randomFloat(random);
			float yPos = terrainMap.getHeightOfTerrain(xPos, zPos);
			entities.add(new Entity(texturedModel1, random.nextInt(4), new Vector3f(xPos, yPos, zPos),
					0, randomFloat(random),0,0.5f));
		}
		for (int i = 0; i < 100; i++) {
			float xPos = randomFloat(random, 0, 1600);
			float zPos = randomFloat(random, 0, 1600);
			float yPos = terrainMap.getHeightOfTerrain(xPos, zPos);
			Entity e = new Entity(texturedModel2, new Vector3f(xPos, yPos, zPos),
					0, randomFloat(random),0,7);
			entities.add(e);
			selectableEntities.add(e);
		}

		// Player
		ModelData playerModel = OBJFileLoader.loadOBJ("gameModels/mammoth");
		RawModel playerRawModel = loader.loadToVAO(playerModel.getVertices(), playerModel.getTextureCoords(),
				playerModel.getNormals(), playerModel.getIndices());
		TexturedModel playerTexturedModel = new TexturedModel(playerRawModel, new ModelTexture(loader.loadTexture("black_leather")));
		Player player = new Player(playerTexturedModel, new Vector3f(50, 0, 50), 0, 0, 0, 2);
		entities.add(player);
		Camera camera = new Camera(player, terrainMap);

		// Light
		List<Light> lightSources = new ArrayList<>();
		Vector3f sunOriginalColor = new Vector3f(1.2f, 1.2f,1.2f);
		Light sun = new Light(new Vector3f(0,10,0), sunOriginalColor);
		lightSources.add(sun);
		Light campFire = new Light(new Vector3f(5,10,0), new Vector3f(2.6f,2.0f,1.3f),
				new Vector3f(1,0.001f,0.001f));
		lightSources.add(campFire);

		// Particles
		ParticleTexture particleTexture = new ParticleTexture(loader.loadTexture("particleAtlas"), 4);
		ParticleSystem playerParticleSystem = new ParticleSystem(particleTexture, 50, 60, 1, 1, 1.5f);
		ParticleTexture fireTexture = new ParticleTexture(loader.loadTexture("fire"), 8, true);
		ParticleSystem fireParticles = new ParticleSystem(fireTexture, 20, 0.01f, -0.08f, 3, 8);
		fireParticles.randomizeRotation();
		fireParticles.setDirection(new Vector3f(0,1,0), 0.2f);
		fireParticles.setScaleVariance(5f);

		// Create renderers
		MasterRenderer renderer = new MasterRenderer(loader, camera);
		WaterRenderer waterRenderer = new WaterRenderer(loader,waterShader, renderer.getProjectionMatrix(), frameBuffers);
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
		GuiTexture shadowMap = new GuiTexture(renderer.getShadowMapTexture(), new Vector2f(0.5f, 0.5f), new Vector2f(0.5f, 0.5f));
		guiTextures.add(shadowMap);

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

		float timeOfDay = 12.60f;
		while (!Display.isCloseRequested()) {
			timeOfDay += DisplayManager.getFrameTimeSeconds() / 100f;
			timeOfDay %= 24;
			float dayNightBlendFactor = calculateDayNightBlendFactor(timeOfDay);
			// set lower sun brightness during night
			sun.setColor(calculateSunColor(dayNightBlendFactor, sunOriginalColor));
			// set sun position during the day
			sun.setPosition(calculateSunPosition(timeOfDay));

			camera.move();
			player.move(terrainMap);
			terrainMap.updateRenderedTerrains(player.getPosition().x, player.getPosition().z);

			playerParticleSystem.generateParticles(player.getPosition());
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
			float cameraReflectionDistance = 2 * (camera.getPosition().y - waterTiles.get(0).getHeight());
			camera.getPosition().y -= cameraReflectionDistance;
			camera.invertPitch();
			renderer.renderScene(entities, terrainMap, lightSources, camera, dayNightBlendFactor,
					new Vector4f(0, 1, 0, -waterTiles.get(0).getHeight()));
			camera.getPosition().y += cameraReflectionDistance;
			camera.invertPitch();

			// render refraction
			frameBuffers.bindRefractionFrameBuffer();
			renderer.renderScene(entities, terrainMap, lightSources, camera, dayNightBlendFactor,
					new Vector4f(0, -1, 0, waterTiles.get(0).getHeight()+1f));
			// raise clipping plane level to reduce glitching at water edge (try removing later)
			GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
			frameBuffers.unbindCurrentFrameBuffer();

			renderer.renderScene(entities, terrainMap, lightSources, camera, dayNightBlendFactor,
					new Vector4f(0, 0, 0, 0));
			waterRenderer.render(waterTiles, camera, lightSources.get(0));

			// render particles
			ParticleMaster.renderParticles(camera);

			// Render GUI & texts
			guiRenderer.render(guiTextures);
			TextMaster.updateTextString(textFPS, "FPS: " + Math.round(1/DisplayManager.getFrameTimeSeconds()));
			TextMaster.updateTextString(debugText, "xyz: " + camera.getPosition());
			TextMaster.render();

			DisplayManager.updateDisplay();
		}
		// Clean up
		ParticleMaster.cleanUp();
		TextMaster.cleanUp();
		frameBuffers.cleanUp();
		waterShader.cleanUp();
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}

	private static float calculateDayNightBlendFactor(float timeOfDay) {
		if (timeOfDay < 2 || timeOfDay > 22) {
			return 1f;  // full night
		} else if (timeOfDay > 5 && timeOfDay < 18){
			return 0f; // full day
		} else if (timeOfDay >= 2 && timeOfDay <= 5) {
			return 1- (timeOfDay - 2) / 3; // morning
		} else if (timeOfDay >= 19 && timeOfDay <= 22) {
			return (timeOfDay - 19) / 3; // evening
		} else {
			return 0f;
		}
	}

	private static Vector3f calculateSunPosition(float timeOfDay) {
		// y upward, x east-west, z north-south
		float yPosition = (float) (Math.cos(Math.PI * (timeOfDay-12) / 12) +1)/2 * 1500000;
		float zPosition = (float) -(Math.cos(Math.PI * (timeOfDay-12) / 12) +0.5)/2 * 2000000;
		float xPosition = (float) (Math.cos(Math.PI * (timeOfDay-12) / 12))/16 * 2000000;

		Vector3f newPosition = new Vector3f();
		newPosition.x = xPosition;
		newPosition.y = yPosition;
		newPosition.z = zPosition;
		return newPosition;
	}

	private static Vector3f calculateSunColor(float nightBlendFactor, Vector3f sunColor) {
		Vector3f shadingVector = new Vector3f(1 + nightBlendFactor, 1 + 0.35f * nightBlendFactor, 1);
		shadingVector.scale(1- nightBlendFactor);

		Vector3f finalColor = new Vector3f();
		finalColor.x = sunColor.x * shadingVector.x;
		finalColor.y = sunColor.y * shadingVector.y;
		finalColor.z = sunColor.z * shadingVector.z;
		return finalColor;
	}
}
