package entities;


import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;
import physics.Gravity;
import renderEngine.DisplayManager;
import terrain.TerrainMap;

public class PlayerPosition extends PositionalObject {

    private static final float MOVEMENT_SPEED = 50;
    private static final float TURN_SPEED = 180;
    private static final float JUMP_POWER = 40;

    private float currentForwardSpeed = 0;
    private float currentSidewaysSpeed = 0;
    private float currentTurnSpeed = 0;
    private float upwardSpeed = 0;
    private boolean isInAir = false;

    public PlayerPosition(Vector3f position, float rotX, float rotY, float rotZ) {
        super.setPosition(position);
        super.setRotX(rotX);
        super.setRotY(rotY);
        super.setRotZ(rotZ);
    }

    public void move(TerrainMap terrainMap) {
        checkInputs();
        super.increaseRotation(0, currentTurnSpeed * DisplayManager.getFrameTimeSeconds(), 0);

        // counter the addition of x and y speed
        if (Math.abs(currentForwardSpeed) + Math.abs(currentSidewaysSpeed) > MOVEMENT_SPEED) {
            currentForwardSpeed /= Math.sqrt(2);
            currentSidewaysSpeed /= Math.sqrt(2);
        }

        float forwardDistance = currentForwardSpeed * DisplayManager.getFrameTimeSeconds();
        float sidewaysDistance = currentSidewaysSpeed * DisplayManager.getFrameTimeSeconds();
        float dx = (float) (forwardDistance * Math.sin(Math.toRadians(super.getRotY())) + sidewaysDistance * Math.cos(-Math.toRadians(super.getRotY())));
        float dz = (float) (forwardDistance * Math.cos(Math.toRadians(super.getRotY())) + sidewaysDistance * Math.sin(-Math.toRadians(super.getRotY())));

        upwardSpeed += Gravity.getDeltaVerticalVelocity();
        super.increasePosition(dx, upwardSpeed * DisplayManager.getFrameTimeSeconds(), dz);

        float terrainHeight = terrainMap.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
        if (super.getPosition().y < terrainHeight) {
            upwardSpeed = 0;
            super.getPosition().y = terrainHeight;
            isInAir = false;
        }
    }

    private void jump() {
        if (!isInAir) {
            this.upwardSpeed = JUMP_POWER;
            isInAir = true;
        }
    }

    private void checkInputs() {
        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            this.currentForwardSpeed = MOVEMENT_SPEED;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
            this.currentForwardSpeed = -MOVEMENT_SPEED;
        } else {
            this.currentForwardSpeed = 0;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            this.currentSidewaysSpeed = MOVEMENT_SPEED;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            this.currentSidewaysSpeed = -MOVEMENT_SPEED;
        } else {
            this.currentSidewaysSpeed = 0;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_COMMA)) {
            this.currentTurnSpeed = -TURN_SPEED;
        } else if (Keyboard.isKeyDown(Keyboard.KEY_PERIOD)) {
            this.currentTurnSpeed = TURN_SPEED;
        } else {
            this.currentTurnSpeed = 0;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            jump();
        }
    }
}
