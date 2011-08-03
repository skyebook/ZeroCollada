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
public class Vector4<T extends Number> extends Vector3<T>{
	
	public T w;

	/**
	 * 
	 */
	public Vector4(T w, T x, T y, T z) {
		super(x, y, z);
		this.w=w;
	}

	/**
	 * @return the w
	 */
	public T getW() {
		return w;
	}

	/**
	 * @param w the w to set
	 */
	public void setW(T w) {
		this.w = w;
	}
	
}
