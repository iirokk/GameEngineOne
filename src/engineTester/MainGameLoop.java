package engineTester;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.*;
import models.RawModel;
import shaders.StaticShader;
import terrain.Terrain;
import textures.ModelTexture;

import java.util.ArrayList;
import java.util.List;

public class MainGameLoop {

	public static void main(String[] args) {
		DisplayManager.createDisplay();
		Loader loader = new Loader();

		RawModel model = OBJLoader.loadObjModel("dragon", loader);
		TexturedModel texturedModel = new TexturedModel(model,
				new ModelTexture(loader.loadTexture("stallTexture")));
		ModelTexture texture = texturedModel.getTexture();
		texture.setShineDamper(5);
		texture.setReflectivity(0.1f);
		texture.setHasTransparency(false);

		RawModel model1 = OBJLoader.loadObjModel("fern", loader);
		TexturedModel texturedModel1 = new TexturedModel(model1,
				new ModelTexture(loader.loadTexture("fern")));
		ModelTexture texture1 = texturedModel1.getTexture();
		texture1.setShineDamper(5);
		texture1.setReflectivity(0.1f);
		texture1.setHasTransparency(true);
		texture1.setUseFakeLighting(true);

		List<Entity> entities = new ArrayList<Entity>();
		entities.add(new Entity(texturedModel, new Vector3f(0,0,-25), 0,0,0,1));
		entities.add(new Entity(texturedModel1, new Vector3f(10,0,25), 0,0,0,1));

		Terrain terrain = new Terrain(0, 0, loader, new ModelTexture(loader.loadTexture("ground_tex")));
		Terrain terrain2 = new Terrain(0, 0, loader, new ModelTexture(loader.loadTexture("ground_tex")));

		Light light = new Light(new Vector3f(0,0,-20), new Vector3f(1,1,1));

		Camera camera = new Camera();

		MasterRenderer renderer = new MasterRenderer();

		while (!Display.isCloseRequested()) {
			camera.move();
			
			// game logic
			
			// render
			for (Entity entity:entities) {
				renderer.processEntity(entity);
			}
			renderer.processTerrain(terrain);
			renderer.processTerrain(terrain2);

			renderer.render(light, camera);
			
			DisplayManager.updateDisplay();
		}

		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
}
