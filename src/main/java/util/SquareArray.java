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

    public SquareArray(BufferedImage imageTerrain) {
        if (imageTerrain.getWidth() != imageTerrain.getHeight()) {
            throw new RuntimeException(String.format("Can't create square array freom image dimensions: %s, %s", imageTerrain.getWidth(), imageTerrain.getHeight()));
        }

        Float[][] array = new Float[imageTerrain.getHeight()][imageTerrain.getWidth()];
        for (int i = 0; i < imageTerrain.getHeight(); i++)
            for (int j = 0; j < imageTerrain.getWidth(); j++)
                array[i][j] = (float) imageTerrain.getRGB(i, j);
        setArray(array);
    }

    public int getSize() {
        return array.length;
    }

    public DoubleSummaryStatistics getStats() {
        return Arrays.stream(array).flatMap(Arrays::stream).collect(Collectors.summarizingDouble(Float::floatValue));
    }
}
