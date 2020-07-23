package toolbox;

import entities.Camera;
import entities.Entity;
import org.lwjgl.util.vector.Vector3f;

import java.util.List;

public class MouseSelector {

    private final MousePicker mousePicker;
    private static final float RAY_RANGE = 600;
    private final Camera camera;

    public MouseSelector(MousePicker mousePicker, Camera camera) {
        this.mousePicker = mousePicker;
        this.camera = camera;
    }

    public void mouseSelectEntity(List<Entity> selectableEntities) {
        // loop through entity list and select nearest entity colliding with mousePicker ray
        Vector3f currentRay = mousePicker.getCurrentRay();
        if (selectableEntities.isEmpty()) {
            return;
        }
        Entity selectedEntity = null;
        float closestDistance = RAY_RANGE+1;
        for (Entity entity:selectableEntities) {
            float distanceToBoundingSphere = getObjectCollisionDistance(currentRay, entity.getPosition(), 5);
            if (distanceToBoundingSphere < closestDistance ) {
                selectedEntity = entity;
                closestDistance = distanceToBoundingSphere;
            }
        }
        if (selectedEntity != null) {
            selectedEntity.increaseRotation(1, 1, 1);
        }
    }

    private float getObjectCollisionDistance(Vector3f mouseRay, Vector3f objectPosition, float objectRadius) {
        // returns value outside RAY_RANGE if no collision is detected
        Vector3f camPos = camera.getPosition();
        Vector3f objectRay = new Vector3f(objectPosition.x - camPos.x,
                objectPosition.y - camPos.y,
                objectPosition.z - camPos.z);

        // https://www.scratchapixel.com/lessons/3d-basic-rendering/minimal-ray-tracer-rendering-simple-shapes/ray-sphere-intersection
        // Distance from camera to the point projected on vector line
        float projectionDistance = Vector3f.dot(objectRay, mouseRay);
        if (projectionDistance <0) {  // if projectionDistance <0, no ray collision is possible
            return RAY_RANGE * 2;
        }

        // The perpendicular distance from the point to the vector line
        float distanceToRay = (float) Math.sqrt(Vector3f.dot(objectRay, objectRay) - projectionDistance * projectionDistance);
        if (distanceToRay > objectRadius) {  // if distanceToRay greater than bounding sphere radius, no ray collision
            return RAY_RANGE * 2;
        }

        // distance from projected point to closest boundary
        float projectedBoundaryDistance = (float) Math.sqrt(objectRadius*objectRadius - distanceToRay*distanceToRay);
        return distanceToRay - projectedBoundaryDistance;
    }
}
