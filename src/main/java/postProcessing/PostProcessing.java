package postProcessing;

import models.RawModel;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import postProcessing.contrast.ContrastEffect;
import postProcessing.gaussianBlur.HorizontalBlur;
import postProcessing.gaussianBlur.VerticalBlur;
import postProcessing.saturation.SaturationEffect;
import renderEngine.Loader;

import java.util.HashMap;
import java.util.Map;

public class PostProcessing {
    public static boolean gaussianBlur = false;
    private static final int GAUSSIAN_BLUR_AMOUNT = 8;  // default: 1

    private static final float[] POSITIONS = {-1, 1, -1, -1, 1, 1, 1, -1};
    private static RawModel quad;
    private static Map<String, PostProcessingEffect> postProcessingEffects = new HashMap<>();

    public static void init(Loader loader) {
        quad = loader.loadToVAO(POSITIONS, 2);

        // Second gaussian blur stage on smaller image to reduce flickering
        postProcessingEffects.put("gaussianBlurH2",
                new HorizontalBlur(Display.getWidth() / GAUSSIAN_BLUR_AMOUNT, Display.getHeight() / GAUSSIAN_BLUR_AMOUNT));
        postProcessingEffects.put("gaussianBlurV2",
                new VerticalBlur(Display.getWidth() / GAUSSIAN_BLUR_AMOUNT, Display.getHeight() / GAUSSIAN_BLUR_AMOUNT));
        postProcessingEffects.put("gaussianBlurH1",
                new HorizontalBlur(Display.getWidth() / 2, Display.getHeight() / 2));
        postProcessingEffects.put("gaussianBlurV1",
                new VerticalBlur(Display.getWidth() / 2, Display.getHeight() / 2));
        postProcessingEffects.put("saturation",
                new SaturationEffect(Display.getWidth(), Display.getHeight()));
        postProcessingEffects.put("contrast", new ContrastEffect());
    }

    public static void doPostProcessing(int colourTexture) {
        start();
        int outputTexture;
        if (gaussianBlur) {
            PostProcessingEffect gaussianBlurH1 = postProcessingEffects.get("gaussianBlurH1");
            PostProcessingEffect gaussianBlurV1 = postProcessingEffects.get("gaussianBlurV1");
            PostProcessingEffect gaussianBlurH2 = postProcessingEffects.get("gaussianBlurH1");
            PostProcessingEffect gaussianBlurV2 = postProcessingEffects.get("gaussianBlurV1");
            gaussianBlurH1.render(colourTexture);
            gaussianBlurV1.render(gaussianBlurH1.getOutputTexture());
            gaussianBlurH2.render(gaussianBlurV1.getOutputTexture());
            gaussianBlurV2.render(gaussianBlurH2.getOutputTexture());
            outputTexture = gaussianBlurV2.getOutputTexture();
        } else {
            outputTexture = colourTexture;
        }

        PostProcessingEffect saturation = postProcessingEffects.get("saturation");
        saturation.render(outputTexture);
        postProcessingEffects.get("contrast").render(saturation.getOutputTexture());
        end();
    }

    public static void cleanUp() {
        postProcessingEffects.values().forEach(PostProcessingEffect::cleanUp);
    }

    private static void start() {
        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    private static void end() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }


}
