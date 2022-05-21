package entities;

import lombok.Getter;
import lombok.Setter;
import org.lwjgl.util.vector.Vector3f;

@Getter
@Setter
public abstract class PositionalObject {

    private Vector3f position;
    private float rotX, rotY, rotZ;

    public void increasePosition(float dx, float dy, float dz) {
        this.position.x += dx;
        this.position.y += dy;
        this.position.z += dz;
    }

    public void increaseRotation(float dx, float dy, float dz) {
        this.rotX += dx;
        this.rotY += dy;
        this.rotZ += dz;
    }
}
