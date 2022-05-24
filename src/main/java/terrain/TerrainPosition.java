package terrain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TerrainPosition {
    private final int gridX;
    private final int gridZ;

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof TerrainPosition)) return false;
        final TerrainPosition other = (TerrainPosition) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.getGridX() != other.getGridX()) return false;
        if (this.getGridZ() != other.getGridZ()) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof TerrainPosition;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getGridX();
        result = result * PRIME + this.getGridZ();
        return result;
    }
}
