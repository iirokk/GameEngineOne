package postProcessing;

import lombok.NoArgsConstructor;
import org.lwjgl.opengl.GL11;

@NoArgsConstructor
public class ImageRenderer {

	private FrameBuffer frameBuffer;

	public ImageRenderer(int width, int height) {
		this.frameBuffer = new FrameBuffer(width, height, FrameBuffer.NONE);
	}

	public void renderQuad() {
		if (frameBuffer != null) {
			frameBuffer.bindFrameBuffer();
		}
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
		if (frameBuffer != null) {
			frameBuffer.unbindFrameBuffer();
		}
	}

	public int getOutputTexture() {
		return frameBuffer.getColourTexture();
	}

	public void cleanUp() {
		if (frameBuffer != null) {
			frameBuffer.cleanUp();
		}
	}

}
