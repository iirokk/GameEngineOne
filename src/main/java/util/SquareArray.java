package util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SquareArray {

    private Float[][] array;

    public int getSize() {
        return array.length;
    }

    public DoubleSummaryStatistics getStats() {
        return Arrays.stream(array).flatMap(Arrays::stream).collect(Collectors.summarizingDouble(Float::floatValue));
    }

    public SquareArray getSlice(int startX, int endX, int startY, int endY) {
        Float[][] slice = new Float[endX-startX][endY-startY];

        Float[][] floats = Arrays.copyOfRange(array, startX, endX);
        for (int i = 0; i < floats.length; i++) {
            slice[i] = Arrays.copyOfRange(floats[i], startY, endY);
        }
        return new SquareArray(slice);
    }

    public void flipReverse() {
        Float[][] newArray = new Float[array.length][array.length];
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array.length; j++) {
                newArray[array.length-1-j][array.length-1-i] = array[i][j];
            }
        }
        this.array = newArray;
    }
}
