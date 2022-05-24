package postProcessing.contrast;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import postProcessing.ImageRenderer;
import postProcessing.PostProcessingEffect;


public class ContrastEffect implements PostProcessingEffect {

    private ImageRenderer imageRenderer;
    private ContrastShader contrastShader;

    public ContrastEffect() {
        this.imageRenderer = new ImageRenderer();
        this.contrastShader = new ContrastShader();
    }

    public void render(int texture) {
        contrastShader.start();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        imageRenderer.renderQuad();
        contrastShader.stop();
    }

    public void cleanUp() {
        imageRenderer.cleanUp();
        contrastShader.cleanUp();
    }

    @Override
    public int getOutputTexture() {
        throw new RuntimeException("Method not usable on class ContrastEffect.");
    }
}
