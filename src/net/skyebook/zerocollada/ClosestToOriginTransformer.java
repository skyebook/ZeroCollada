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
package net.skyebook.zerocollada;

import java.util.ArrayList;

import net.skyebook.zerocollada.structure.Vector3;

import org.jdom.Document;
import org.jdom.Element;

/**
 * @author Skye Book
 *
 */
public class ClosestToOriginTransformer extends Transformer {
	
	private Number xMax = null;
	private Number yMax = null;
	private Number zMax = null;

	/**
	 * @param collada
	 */
	public ClosestToOriginTransformer(Document collada) {
		super(collada);
	}

	/*
	 * (non-Javadoc)
	 * @see net.skyebook.zerocollada.Transformer#doTransformation(java.util.ArrayList, org.jdom.Element, org.jdom.Element)
	 */
	@SuppressWarnings("unchecked")
	public Document doTransformation(ArrayList<Vector3> vertices, Element positionsElement, Element arrayElement) {

		for(Vector3<?> v : vertices){
			// if one is NaN, all are NaN.. initialize them here
			if(xMax==null){
				xMax = v.x;
				yMax = v.y;
				zMax = v.z;
				continue;
			}

			// check for floats
			if(v.x instanceof Float){
				if(((Float)v.x)>((Float)xMax)) xMax=v.x;
				if(((Float)v.y)>((Float)yMax)) yMax=v.y;
				if(((Float)v.z)>((Float)zMax)) zMax=v.z;
			}
			
			// check for doubles
			if(v.x instanceof Float){
				if(((Double)v.x)>((Double)xMax)) xMax=v.x;
				if(((Double)v.y)>((Double)yMax)) yMax=v.y;
				if(((Double)v.z)>((Double)zMax)) zMax=v.z;
			}
		}

		for(int i=0; i<vertices.size(); i++){
			Object o = vertices.get(i).x;
			
			// check for floats
			if(((Vector3<?>)o).x instanceof Float){
				Vector3<Float> v = (Vector3<Float>) o;
				
				v.x = ((Float)v.x)-((Float)xMax);
				v.y = ((Float)v.y)-((Float)yMax);
				v.z = ((Float)v.z)-((Float)zMax);
			}
			
			// check for doubles
			else if(((Vector3<?>)o).x instanceof Double){
				Vector3<Double> v = (Vector3<Double>) o;
				
				v.x = ((Double)v.x)-((Double)xMax);
				v.y = ((Double)v.y)-((Double)yMax);
				v.z = ((Double)v.z)-((Double)zMax);
			}
		}

		rewriteArray(vertices, positionsElement, arrayElement);

		System.out.println("Offset " + xMax +", "+yMax+", "+zMax);
		/*
		if(vertices.get(0) instanceof Vector3f){
			float xMax = Float.NaN;
			float yMax = Float.NaN;
			float zMax = Float.NaN;

			for(Vector3<?> v : vertices){
				// if one is NaN, all are NaN.. initialize them here
				if(Float.isNaN(xMax)){
					xMax = (Float)v.x;
					yMax = (Float)v.y;
					zMax = (Float)v.z;
					continue;
				}

				if((Float)v.x>xMax) xMax=(Float)v.x;
				if((Float)v.y>yMax) yMax=(Float)v.y;
				if((Float)v.z>zMax) zMax=(Float)v.z;
			}

			for(int i=0; i<vertices.size(); i++){
				Vector3f v = (Vector3f)vertices.get(i);
				v.x = v.x-xMax;
				v.y = v.y-yMax;
				v.z = v.z-zMax;
			}

			rewriteArray(vertices, positionsElement, arrayElement);

			System.out.println("Offset " + xMax +", "+yMax+", "+zMax);

			// leave the loop now
			break;
		}
		else if(vertices.get(0) instanceof Vector3f){
			double xMax = Double.NaN;
			double yMax = Double.NaN;
			double zMax = Double.NaN;

			for(Vector3<?> v : vertices){
				// if one is NaN, all are NaN.. initialize them here
				if(Double.isNaN(xMax)){
					xMax = v.x;
					yMax = v.y;
					zMax = v.z;
					continue;
				}

				if(v.x>xMax) xMax=v.x;
				if(v.y>yMax) yMax=v.y;
				if(v.z>zMax) zMax=v.z;
			}

			for(Vector3d v : vertices){
				v.x = v.x-xMax;
				v.y = v.y-yMax;
				v.z = v.z-zMax;
			}

			rewriteDoubleArray(vertices, positionsElement, child);

			System.out.println("Offset " + xMax +", "+yMax+", "+zMax);

			// leave the loop now
			break;
		}
		 */
		return null;
	}

	/* (non-Javadoc)
	 * @see net.skyebook.zerocollada.Transformer#newFileName()
	 */
	@Override
	public String newFileNameSuffix() {
		return("x_"+xMax.toString()+"y_"+yMax.toString()+"z_"+zMax.toString());
	}

}
