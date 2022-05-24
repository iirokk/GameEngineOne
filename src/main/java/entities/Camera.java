package entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;
import terrain.TerrainMap;

@Getter
@AllArgsConstructor
public class Camera {
    private Vector3f position = new Vector3f(0, 0, 0);
    private float pitch;
    private float yaw;
    private float roll;
    private PlayerPosition playerPosition;
    private float distanceFromPlayer = 50;
    private final TerrainMap terrainMap;
    private final float minimumCameraHeight = 10f;
    private final float maxCameraDistance = 500f;
    private final float minCameraDistance = 100f;

    public Camera(PlayerPosition playerPosition, TerrainMap terrainMap) {
        this.playerPosition = playerPosition;
        this.terrainMap = terrainMap;
    }

    public void move() {
        playerPosition.setMovementSpeed((float) Math.pow(distanceFromPlayer * 0.2f, 1.5));
        calculateZoom();
        calculatePitch();
        calculateAngleAroundPlayer();
        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCameraPosition(horizontalDistance, verticalDistance);
        float minimumCameraY = terrainMap.getHeightOfTerrain(position.x, position.z) + minimumCameraHeight * distanceFromPlayer / 50;
        if (position.y < terrainMap.getHeightOfTerrain(position.x, position.z) + minimumCameraHeight * distanceFromPlayer / 50) {
            position.y = minimumCameraY;
        }
        this.yaw = 180 - (playerPosition.getRotY());
    }

    private void calculateZoom() {
        float zoomLevel = Mouse.getDWheel();
        float zoomSpeed = 0.05f;  // TODO: distance based zoom speed, zoom smoothing
        if (zoomLevel > 0) {
            distanceFromPlayer = distanceFromPlayer * (1f - zoomSpeed);
        } else if (zoomLevel < 0) {
            distanceFromPlayer = distanceFromPlayer * (1f + zoomSpeed);
        }
        if (distanceFromPlayer < minCameraDistance) {
            distanceFromPlayer = minCameraDistance;
        } else if (distanceFromPlayer > maxCameraDistance) {
            distanceFromPlayer = maxCameraDistance;
        }
    }

    private void calculatePitch() {
        if (Mouse.isButtonDown(1)) {
            float pitchChange = Mouse.getDY() * 0.1f;
            pitch -= pitchChange;
        }
        if (pitch > 75) {
            pitch = 75;
        } else if (pitch < 0) {
            pitch = -0;
        }
    }

    private void calculateAngleAroundPlayer() {
        if (Mouse.isButtonDown(1)) {
            float angleChange = Mouse.getDX() * 0.3f;
            playerPosition.setRotY(playerPosition.getRotY() - angleChange);
        }
    }

    private float calculateHorizontalDistance() {
        return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
    }

    private float calculateVerticalDistance() {
        return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
    }

    private void calculateCameraPosition(float horizontalDistance, float verticalDistance) {
        float theta = playerPosition.getRotY();  // camera angle from origin
        float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
        position.x = playerPosition.getPosition().x - offsetX;
        position.z = playerPosition.getPosition().z - offsetZ;
        position.y = playerPosition.getPosition().y + verticalDistance;
    }

    public void invertPitch() {
        this.pitch = -pitch;
    }
}
