package postProcessing.gaussianBlur;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import postProcessing.ImageRenderer;
import postProcessing.PostProcessingEffect;

public class HorizontalBlur implements PostProcessingEffect {
	
	private final ImageRenderer imageRenderer;
	private final HorizontalBlurShader shader;
	
	public HorizontalBlur(int targetFboWidth, int targetFboHeight){
		shader = new HorizontalBlurShader();
		shader.start();
		shader.loadTargetWidth(targetFboWidth);
		shader.stop();
		imageRenderer = new ImageRenderer(targetFboWidth, targetFboHeight);
	}
	
	public void render(int texture){
		shader.start();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		imageRenderer.renderQuad();
		shader.stop();
	}
	
	public int getOutputTexture(){
		return imageRenderer.getOutputTexture();
	}
	
	public void cleanUp(){
		imageRenderer.cleanUp();
		shader.cleanUp();
	}

}
