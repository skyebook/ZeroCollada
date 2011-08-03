/**  
*  Zero Collada - In place operations on COLLADA markup
*  Copyright (C) 2011 Skye Book
*  
*  This program is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation, either version 3 of the License, or
*  (at your option) any later version.
*  
*  This program is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*  
*  You should have received a copy of the GNU General Public License
*  along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package net.skyebook.zerocollada.structure;

/**
 * @author Skye Book
 *
 */
public class Vector3<T extends Number>{
	
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
	
}
