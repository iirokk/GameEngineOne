package postProcessing;

public interface PostProcessingEffect {
    void render(int texture);
    void cleanUp();
    int getOutputTexture();
}
