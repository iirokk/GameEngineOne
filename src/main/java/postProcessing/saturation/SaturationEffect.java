package postProcessing.saturation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import postProcessing.ImageRenderer;
import postProcessing.PostProcessingEffect;


public class SaturationEffect implements PostProcessingEffect {

    private final ImageRenderer imageRenderer;
    private final SaturationShader saturationShader;

    public SaturationEffect(int targetFboWidth, int targetFboHeight) {
        saturationShader = new SaturationShader();
        imageRenderer = new ImageRenderer(targetFboWidth, targetFboHeight);
    }

    public void render(int texture) {
        saturationShader.start();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        imageRenderer.renderQuad();
        saturationShader.stop();
    }

    public void cleanUp() {
        imageRenderer.cleanUp();
        saturationShader.cleanUp();
    }

    public int getOutputTexture() {
        return imageRenderer.getOutputTexture();
    }
}
