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
