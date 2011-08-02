/**
 * 
 */
package net.skyebook.zerocollada.structure;

/**
 * A double-precision implementation of an OpenGL style vector3d
 * @author Skye Book
 *
 */
public class Vector3d extends Vector3<Double> {

	/**
	 * @param x
	 * @param y
	 * @param z
	 */
	public Vector3d(Double x, Double y, Double z) {
		super(x, y, z);
	}

	/* (non-Javadoc)
	 * @see net.skyebook.zerocollada.structure.Vector3#subtract(net.skyebook.zerocollada.structure.Vector3, net.skyebook.zerocollada.structure.Vector3)
	 */
	@Override
	public Vector3<Double> subtract(Vector3<Double> top, Vector3<Double> bottom) {
		return new Vector3d(top.x-bottom.x, top.y-bottom.y, top.z-bottom.z);
	}

}
