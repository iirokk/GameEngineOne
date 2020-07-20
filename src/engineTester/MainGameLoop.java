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
		List<Entity> entities = new ArrayList<Entity>();
		entities.add(new Entity(texturedModel, new Vector3f(0,0,-25), 0,0,0,1));
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
			renderer.render(light, camera);
			
			DisplayManager.updateDisplay();
		}

		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
}
