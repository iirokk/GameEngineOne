package fontRendering;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import shaders.ShaderProgram;

public class FontShader extends ShaderProgram{

	private static final String VERTEX_FILE = "src\\main\\java\\fontRendering\\fontVertex.txt";
	private static final String FRAGMENT_FILE = "src\\main\\java\\fontRendering\\fontFragment.txt";

	private int location_color;
	private int location_translation;
	private int location_transparency;
	private int location_fontWidth;
	private int location_fontEdgeSmoothing;
	private int location_borderWidth;
	private int location_borderEdgeSmoothing;
	private int location_borderColor;

	public FontShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_color = super.getUniformLocation("color");
		location_translation = super.getUniformLocation("translation");
		location_transparency = super.getUniformLocation("transparency");
		location_fontWidth = super.getUniformLocation("fontWidth");
		location_fontEdgeSmoothing = super.getUniformLocation("fontEdgeSmoothing");
		location_borderWidth = super.getUniformLocation("borderWidth");
		location_borderEdgeSmoothing = super.getUniformLocation("borderEdgeSmoothing");
		location_borderColor = super.getUniformLocation("borderColor");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}

	protected void loadColor(Vector3f color) {
		super.loadVector(location_color, color);
	}

	protected void loadTranslation(Vector2f translation) {
		super.load2DVector(location_translation, translation);
	}

	protected void loadTransparency(Float transparency) {
		super.loadFloat(location_transparency, transparency);
	}

	protected void loadFontWidth(Float fontWidth) {
		super.loadFloat(location_fontWidth, fontWidth);
	}

	protected void loadFontEdgeSmoothing(Float fontEdgeSmoothing) {
		super.loadFloat(location_fontEdgeSmoothing, fontEdgeSmoothing);
	}

	protected void loadBorderWidth(Float borderWidth) {
		super.loadFloat(location_borderWidth, borderWidth);
	}

	protected void loadBorderEdgeSmoothing(Float borderEdgeSmoothing) {
		super.loadFloat(location_borderEdgeSmoothing, borderEdgeSmoothing);
	}

	protected void loadBorderColor(Vector3f borderColor) {
		super.loadVector(location_borderColor, borderColor);
	}
}
