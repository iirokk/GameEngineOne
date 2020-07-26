package particles;

public class ParticleTexture {

    private int textureID;
    private int numberOfRows;
    private boolean additive = false;

    public ParticleTexture(int textureID, int numberOfRows) {
        this.textureID = textureID;
        this.numberOfRows = numberOfRows;
    }

    public ParticleTexture(int textureID, int numberOfRows, boolean additiveBlending) {
        this.textureID = textureID;
        this.numberOfRows = numberOfRows;
        this.additive = additiveBlending;
    }

    public int getTextureID() {
        return textureID;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public boolean isAdditive() {
        return additive;
    }

    public void setAdditive(boolean additive) {
        this.additive = additive;
    }
}
