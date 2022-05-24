package models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import textures.ModelTexture;

@Getter
@AllArgsConstructor
public class TexturedModel {

    private RawModel rawModel;
    private ModelTexture texture;
}
