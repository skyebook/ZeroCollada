/**
 * 
 */
package net.skyebook.zerocollada.structure;

/**
 * @author Skye Book
 *
 */
public abstract class Vector3<T>{
	
	public T x;
	public T y;
	public T z;

	/**
	 * 
	 */
	public Vector3(T x, T y, T z) {
		this.x=x;
		this.y=y;
		this.z=z;
	}

	/**
	 * @return the x
	 */
	public T getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(T x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public T getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(T y) {
		this.y = y;
	}

	/**
	 * @return the z
	 */
	public T getZ() {
		return z;
	}

	/**
	 * @param z the z to set
	 */
	public void setZ(T z) {
		this.z = z;
	}
	
	public abstract Vector3<T> subtract(Vector3<T> top, Vector3<T> bottom);
}
