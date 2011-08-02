/**
 * 
 */
package net.skyebook.zerocollada.structure;

/**
 * A float-precision implementation of an OpenGL style vector3f
 * @author Skye Book
 *
 */
public class Vector3f extends Vector3<Float> {

	/**
	 * @param x
	 * @param y
	 * @param z
	 */
	public Vector3f(Float x, Float y, Float z) {
		super(x, y, z);
	}
	
	/* (non-Javadoc)
	 * @see net.skyebook.zerocollada.structure.Vector3#subtract(net.skyebook.zerocollada.structure.Vector3, net.skyebook.zerocollada.structure.Vector3)
	 */
	@Override
	public Vector3<Float> subtract(Vector3<Float> top, Vector3<Float> bottom) {
		return new Vector3f(top.x-bottom.x, top.y-bottom.y, top.z-bottom.z);
	}

}
