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
package net.skyebook.zerocollada.geom;

import java.util.ArrayList;

import net.skyebook.zerocollada.structure.Vector3;

import org.jdom.Document;
import org.jdom.Element;

/**
 * @author Skye Book
 *
 */
public class ClosestToOriginTransformer extends GeometryTransformer {

	private Number xMax;
	private Number yMax;
	private Number zMax;

	/**
	 * @param collada
	 */
	public ClosestToOriginTransformer(Document collada, boolean handleX, boolean handleY, boolean handleZ) {
		super(collada, handleX, handleY, handleZ);
	}

	/*
	 * (non-Javadoc)
	 * @see net.skyebook.zerocollada.Transformer#doTransformation(java.util.ArrayList, org.jdom.Element, org.jdom.Element)
	 */
	@SuppressWarnings("unchecked")
	public void doTransformation(ArrayList<Vector3> vertices, Element positionsElement, Element arrayElement) {

		boolean firstRun=true;

		for(Vector3<?> v : vertices){
			// if one is NaN, all are NaN.. initialize them here
			if(firstRun){
				if(handleX) xMax = v.x;
				if(handleY) yMax = v.y;
				if(handleZ) zMax = v.z;

				System.out.println("Numbers initialized");
				firstRun=false;
				continue;
			}

			// check for floats
			if(v.x instanceof Float){
				if(handleX) if(((Float)v.x)>((Float)xMax)) xMax=v.x;
				if(handleY) if(((Float)v.y)>((Float)yMax)) yMax=v.y;
				if(handleZ) if(((Float)v.z)>((Float)zMax)) zMax=v.z;
			}

			// check for doubles
			if(v.x instanceof Double){
				if(handleX) if(((Double)v.x)>((Double)xMax)) xMax=v.x;
				if(handleY) if(((Double)v.y)>((Double)yMax)) yMax=v.y;
				if(handleZ) if(((Double)v.z)>((Double)zMax)) zMax=v.z;
			}
		}

		for(int i=0; i<vertices.size(); i++){
			Vector3<?> o = vertices.get(i);

			// check for floats
			if(((Vector3<?>)o).x instanceof Float){
				Vector3<Float> v = (Vector3<Float>) o;

				if(handleX) v.x = ((Float)v.x)-((Float)xMax);
				if(handleY) v.y = ((Float)v.y)-((Float)yMax);
				if(handleZ) v.z = ((Float)v.z)-((Float)zMax);
			}

			// check for doubles
			else if(((Vector3<?>)o).x instanceof Double){
				Vector3<Double> v = (Vector3<Double>) o;

				if(handleX) v.x = ((Double)v.x)-((Double)xMax);
				if(handleY) v.y = ((Double)v.y)-((Double)yMax);
				if(handleZ) v.z = ((Double)v.z)-((Double)zMax);
			}
		}

		rewriteArray(vertices, positionsElement, arrayElement);

		//System.out.println("Offset " + xMax +", "+yMax+", "+zMax);
	}

	/* (non-Javadoc)
	 * @see net.skyebook.zerocollada.Transformer#newFileName()
	 */
	@Override
	public String newFileNameSuffix() {
		StringBuilder suffix = new StringBuilder();
		suffix.append("x_");
		suffix.append(handleX ? xMax : 0);
		suffix.append("y_");
		suffix.append(handleY ? yMax : 0);
		suffix.append("z_");
		suffix.append(handleZ ? zMax : 0);
		return suffix.toString();
	}

}
