package particles;


import entities.Camera;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import physics.EntityPhysicsQuantities;
import physics.Gravity;
import renderEngine.DisplayManager;

import java.util.zip.DeflaterInputStream;

public class Particle {

    private EntityPhysicsQuantities physicsQuantities;
    private float lifeLength;
    private float rotation;
    private float scale;
    private ParticleTexture texture;

    private float elapsedTime = 0;
    private Vector2f texOffset1 = new Vector2f();
    private Vector2f texOffset2 = new Vector2f();
    private float blendStage;
    private float distanceFromCamera;

    public Particle(ParticleTexture texture, Vector3f position, Vector3f velocity, float gravityFactor, float lifeLength, float rotation, float scale) {
        this.texture = texture;
        this.physicsQuantities = new EntityPhysicsQuantities(position, velocity, gravityFactor);
        this.lifeLength = lifeLength;
        this.rotation = rotation;
        this.scale = scale;
        ParticleMaster.addParticle(this);
    }

    public ParticleTexture getTexture() {
        return texture;
    }

    public Vector3f getPosition() {
        return physicsQuantities.getPosition();
    }

    public float getRotation() {
        return rotation;
    }

    public float getScale() {
        return scale;
    }

    protected boolean update(Camera camera) {
        Gravity.objectPositionVelocityChange(physicsQuantities);
        distanceFromCamera = Vector3f.sub(camera.getPosition(), getPosition(), null).lengthSquared();
        updateTextureCoordsInfo();
        elapsedTime += DisplayManager.getFrameTimeSeconds();
        return elapsedTime < lifeLength;
    }

    private void updateTextureCoordsInfo() {
        int stageCount = texture.getNumberOfRows() * texture.getNumberOfRows();
        float lifeStage = elapsedTime / lifeLength;
        float atlasProgression = lifeStage * stageCount;
        int index1 = (int) Math.floor(atlasProgression);
        int index2 = index1 < stageCount - 1 ? index1 + 1 : index1;
        this.blendStage = atlasProgression % 1;
        setTextureOffset(texOffset1, index1);
        setTextureOffset(texOffset2, index2);
    }

    private void setTextureOffset(Vector2f offset, int index) {
        int column = index % texture.getNumberOfRows();
        int row = index / texture.getNumberOfRows();
        offset.x = (float) column / texture.getNumberOfRows();
        offset.y = (float) row / texture.getNumberOfRows();
    }

    public Vector2f getTexOffset1() {
        return texOffset1;
    }

    public Vector2f getTexOffset2() {
        return texOffset2;
    }

    public float getBlendStage() {
        return blendStage;
    }

    public float getDistanceFromCamera() {
        return distanceFromCamera;
    }
}
