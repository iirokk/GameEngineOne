package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;
import terrain.TerrainMap;

import javax.swing.*;

public class Camera {
    private Vector3f position = new Vector3f(0,0,0);
    private float pitch;
    private float yaw;
    private float roll;
    private Player player;
    private float distanceFromPlayer = 50;
    private float angleAroundPlayer = 50;
    private final float playerHeightOffset = 6;
    private final TerrainMap terrainMap;
    private final float minimumCameraHeight = 5f;

    public Camera(Player player, TerrainMap terrainMap) {
        this.player = player;
        this.terrainMap = terrainMap;
    }

    public void move() {
        calculateZoom();
        calculatePitch();
        calculateAngleAroundPlayer();
        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCameraPosition(horizontalDistance, verticalDistance);
        float minimumCameraY = terrainMap.getHeightOfTerrain(position.x, position.y) + minimumCameraHeight;
        if (position.y < terrainMap.getHeightOfTerrain(position.x, position.y) + minimumCameraHeight ) {
            position.y = minimumCameraY;
        }
        this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
    }

    private void calculateZoom() {
        float zoomLevel = Mouse.getDWheel();
        if (zoomLevel > 0) {
            distanceFromPlayer = distanceFromPlayer*0.95f;
        } else if (zoomLevel < 0) {
            distanceFromPlayer = distanceFromPlayer*1.05f;
        }
        if (distanceFromPlayer < 20) {
            distanceFromPlayer = 20;
        } else if (distanceFromPlayer > 200) {
            distanceFromPlayer = 200;
        }
    }

    private void calculatePitch() {
        if(Mouse.isButtonDown(1)) {
            float pitchChange = Mouse.getDY() * 0.1f;
            pitch -= pitchChange;
        }
        if (pitch > 75) {
            pitch = 75;
        } else if (pitch < -75) {
            pitch = -75;
        }
    }

    private void calculateAngleAroundPlayer() {
        if(Mouse.isButtonDown(1)) {
            float angleChange = Mouse.getDX() * 0.3f;
            angleAroundPlayer -= angleChange;
        }
    }

    private float calculateHorizontalDistance() {
        return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
    }

    private float calculateVerticalDistance() {
        return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
    }

    private void calculateCameraPosition(float horizontalDistance, float verticalDistance) {
        float theta = player.getRotY() + angleAroundPlayer;  // camera angle from origin
        float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
        position.x = player.getPosition().x - offsetX;
        position.z = player.getPosition().z - offsetZ;
        position.y = player.getPosition().y + verticalDistance + playerHeightOffset;  // add player height
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }

    public void invertPitch() {
        this.pitch = -pitch;
    }
}
