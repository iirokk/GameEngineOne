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
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.*;
import models.RawModel;
import shaders.StaticShader;
import terrain.Terrain;
import terrain.TerrainMap;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainGameLoop {

	public static float randomFloat(Random random) {
		int min = 0;
		int max = 400;
		return min + random.nextFloat() * (max - min);
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

		Random random = new Random();
		for (int i = 0; i < 500; i++) {
			float xPos = randomFloat(random) - 200;
			float zPos = randomFloat(random) - 200;
			float yPos = terrainMap.getHeightOfTerrain(xPos, zPos);
			entities.add(new Entity(texturedModel1, random.nextInt(4), new Vector3f(xPos, yPos, zPos),
					0, randomFloat(random),0,1));
		}

		// Player
		ModelData playerModel = OBJFileLoader.loadOBJ("gameModels/mammoth");
		RawModel playerRawModel = loader.loadToVAO(playerModel.getVertices(), playerModel.getTextureCoords(),
				playerModel.getNormals(), playerModel.getIndices());
		TexturedModel playerTexturedModel = new TexturedModel(playerRawModel, new ModelTexture(loader.loadTexture("black_leather")));
		Player player = new Player(playerTexturedModel, new Vector3f(100, 0, -50), 0, 0, 0, 4);

		Light light = new Light(new Vector3f(0,100,-20), new Vector3f(1,1,1));
		Camera camera = new Camera(player);
		MasterRenderer renderer = new MasterRenderer();

		// GUI
		List<GuiTexture> guis = new ArrayList<>();
		GuiTexture guiCompass = new GuiTexture(loader.loadTexture("gui/compass"), new Vector2f(0f, -0.85f), new Vector2f(0.25f, 0.05f));
		guis.add(guiCompass);
		GuiTexture guiCompassPointer = new GuiTexture(loader.loadTexture("gui/loc_indicator"), new Vector2f(0.1f, -0.85f), new Vector2f(0.02f, 0.03f));
		guis.add(guiCompassPointer);
		GuiTexture guiHealth = new GuiTexture(loader.loadTexture("gui/health_indicator"), new Vector2f(0f, -0.92f), new Vector2f(0.25f, 0.01f));
		guis.add(guiHealth);
		GuiRenderer guiRenderer = new GuiRenderer(loader);

		while (!Display.isCloseRequested()) {
			camera.move();
			player.move(terrainMap);
			
			// game logic
			
			// render
			for (Entity entity:entities) {
				renderer.processEntity(entity);
			}
			for (Terrain terrain:terrainMap.getAllTerrains()) {
				renderer.processTerrain(terrain);
			}
			renderer.render(light, camera);
			renderer.processEntity(player);

			guiRenderer.render(guis);

			DisplayManager.updateDisplay();
		}
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
}
