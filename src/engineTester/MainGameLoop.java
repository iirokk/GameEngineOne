package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import guis.GuiRenderer;
import guis.GuiTexture;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.*;
import models.RawModel;
import terrain.Terrain;
import terrain.TerrainMap;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.MousePicker;
import toolbox.MouseSelector;

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
		TerrainMap terrainMap = new TerrainMap();
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy2"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("ground_tex"));
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		terrainMap.addTerrain(new Terrain(0, 0, loader, texturePack, blendMap, "heightmap"));
		terrainMap.addTerrain(new Terrain(0, -1, loader, texturePack, blendMap, "heightmap"));
		terrainMap.addTerrain(new Terrain(-1, 0, loader, texturePack, blendMap, "heightmap"));
		terrainMap.addTerrain(new Terrain(-1, -1, loader, texturePack, blendMap, "heightmap"));

		// creating entities list
		List<Entity> entities = new ArrayList<>();

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
			float xPos = randomFloat(random) - 200;
			float zPos = randomFloat(random) - 200;
			float yPos = terrainMap.getHeightOfTerrain(xPos, zPos);
			entities.add(new Entity(texturedModel1, random.nextInt(4), new Vector3f(xPos, yPos, zPos),
					0, randomFloat(random),0,0.5f));
		}
		for (int i = 0; i < 100; i++) {
			float xPos = randomFloat(random, -800, 800);
			float zPos = randomFloat(random, -800, 800);
			float yPos = terrainMap.getHeightOfTerrain(xPos, zPos);
			entities.add(new Entity(texturedModel2, new Vector3f(xPos, yPos, zPos),
					0, randomFloat(random),0,7));
		}

		// Player
		ModelData playerModel = OBJFileLoader.loadOBJ("gameModels/mammoth");
		RawModel playerRawModel = loader.loadToVAO(playerModel.getVertices(), playerModel.getTextureCoords(),
				playerModel.getNormals(), playerModel.getIndices());
		TexturedModel playerTexturedModel = new TexturedModel(playerRawModel, new ModelTexture(loader.loadTexture("black_leather")));
		Player player = new Player(playerTexturedModel, new Vector3f(100, 0, -50), 0, 0, 0, 2);

		Camera camera = new Camera(player);

		// Light
		List<Light> lightSources = new ArrayList<>();
		Vector3f sunOriginalColor = new Vector3f(1.2f, 1.2f,1.2f);
		Light sun = new Light(new Vector3f(0,10,0), sunOriginalColor);
		lightSources.add(sun);
		Light campFire = new Light(new Vector3f(5,10,0), new Vector3f(2.6f,2.0f,1.3f),
				new Vector3f(1,0.001f,0.001f));
		lightSources.add(campFire);

		// GUI
		List<GuiTexture> guis = new ArrayList<>();
		GuiTexture guiCompass = new GuiTexture(loader.loadTexture("gui/compass"), new Vector2f(0f, -0.85f), new Vector2f(0.25f, 0.05f));
		guis.add(guiCompass);
		GuiTexture guiCompassPointer = new GuiTexture(loader.loadTexture("gui/loc_indicator"), new Vector2f(0.1f, -0.85f), new Vector2f(0.02f, 0.03f));
		guis.add(guiCompassPointer);
		GuiTexture guiHealth = new GuiTexture(loader.loadTexture("gui/health_indicator"), new Vector2f(0f, -0.92f), new Vector2f(0.25f, 0.01f));
		guis.add(guiHealth);
		GuiRenderer guiRenderer = new GuiRenderer(loader);

		MasterRenderer renderer = new MasterRenderer(loader);
		MousePicker mousePicker = new MousePicker(renderer.getProjectionMatrix(), camera);
		MouseSelector mouseSelector = new MouseSelector(mousePicker, camera);

		float timeOfDay = 12.60f;
		while (!Display.isCloseRequested()) {
			timeOfDay += 0.01;
			if (timeOfDay >= 24) {
				timeOfDay = 0f;
			}
			float dayNightBlendFactor = calculateDayNightBlendFactor(timeOfDay);
			// set lower sun brightness during night
			lightSources.get(0).setColor(calculateSunColor(dayNightBlendFactor, sunOriginalColor));
			// set sun position during the day
			lightSources.get(0).setPosition(calculateSunPosition(timeOfDay));

			camera.move();
			player.move(terrainMap);
			mousePicker.update();  // always update after camera update

			// game logic


			
			// render
			for (Entity entity:entities) {
				renderer.processEntity(entity);
			}
			for (Terrain terrain:terrainMap.getAllTerrains()) {
				renderer.processTerrain(terrain);
			}
			renderer.render(lightSources, camera, dayNightBlendFactor);
			renderer.processEntity(player);

			guiRenderer.render(guis);

			DisplayManager.updateDisplay();
		}
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
		float yPosition = (float) (Math.cos(Math.PI * (timeOfDay-12) / 12) +1)/2 * 1500;
		float zPosition = (float) -(Math.cos(Math.PI * (timeOfDay-12) / 12) +0.5)/2 * 2000;
		float xPosition = (float) (Math.cos(Math.PI * (timeOfDay-12) / 12))/16 * 2000;

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
