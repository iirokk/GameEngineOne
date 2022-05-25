package postProcessing.saturation;

import shaders.ShaderProgram;

public class SaturationShader extends ShaderProgram {

	private static final String VERTEX_FILE = "src\\main\\java\\postProcessing\\saturation\\saturationVertex.txt";
	private static final String FRAGMENT_FILE = "src\\main\\java\\postProcessing\\saturation\\saturationFragment.txt";
	
	public SaturationShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {	
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
}
