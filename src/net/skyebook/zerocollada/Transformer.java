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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import net.skyebook.zerocollada.structure.Vector3;
import net.skyebook.zerocollada.structure.Vector3d;
import net.skyebook.zerocollada.structure.Vector3f;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * For Transforming a COLLADA file
 * @author Skye Book
 *
 */
public abstract class Transformer {
	
	private Document colladaDoc;
	private String positionsSourceID;
	private Element positionsElement;
	
	protected boolean handleY=false;

	/**
	 * 
	 */
	public Transformer(Document collada, boolean handleY) {
		colladaDoc=collada;
		this.handleY=handleY;
		
		// Performs the transformation
		scanCollada();
		
	}

	/**
	 * Writes the COLLADA document to a file
	 * @param file The file to write the COLLADA document to
	 * @throws IOException
	 */
	public void writeColladaToFile(File file) throws IOException{
		if(!file.exists()){
			file.createNewFile();
		}
		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		FileWriter writer = new FileWriter(file);
		outputter.output(colladaDoc, writer);
		writer.close();
	}

	/**
	 * Perform the specified transformation
	 * @return
	 */
	public abstract void doTransformation(ArrayList<Vector3> vertices, Element positionsElement, Element arrayElement);

	/**
	 * Generates the filename for this file
	 * @return
	 */
	public abstract String newFileNameSuffix();

	/**
	 * 
	 * @return
	 */
	protected ArrayList<Vector3> scanCollada(){
		findVertices(colladaDoc.getRootElement());

		if(positionsSourceID!=null){
			findSource(colladaDoc.getRootElement(), positionsSourceID);
			if(positionsElement.getChildren().size()>0){
				for(int i=0; i<positionsElement.getChildren().size(); i++){
					Element child = (Element)positionsElement.getChildren().get(i);
					// check to see if we've struck the array Element
					if(child.getName().contains("_array")){
						if(child.getName().startsWith("float")){
							ArrayList<Vector3> vertices = createFloatArray(positionsElement, child);

							doTransformation(vertices, positionsElement, child);

							return vertices;
						}
						else if(child.getName().startsWith("double")){
							ArrayList<Vector3> vertices = createDoubleArray(positionsElement, child);

							doTransformation(vertices, positionsElement, child);

							return vertices;
						}
					}
				}
			}
		}

		return null;
	}

	protected void rewriteArray(ArrayList<Vector3> vertices, Element positionsElement, Element arrayElement){
		ArrayList<String> order = getVertexOrder();

		StringBuilder sb = new StringBuilder();
		for(Vector3<?> v : vertices){
			for(int i=0; i<3; i++){
				if(order.get(i).equals("X")) sb.append(v.x+" ");
				else if(order.get(i).equals("Y")) sb.append(v.y+" ");
				else if(order.get(i).equals("Z")) sb.append(v.z+" ");
			}
		}

		String arrayString = sb.toString();
		// cut out the last space
		arrayString.substring(0, arrayString.length()-1);
		arrayElement.setText(arrayString);
	}

	private ArrayList<String> getVertexOrder(){
		Element technique = null;
		for(int i=0; i<positionsElement.getChildren().size(); i++){
			if(((Element)positionsElement.getChildren().get(i)).getName().equals("technique_common")){
				technique = ((Element)positionsElement.getChildren().get(i));
				break;
			}
		}
		if(technique==null) System.out.println("Couldn't find technique");
		
		Element accessor = null;
		for(int i=0; i<technique.getChildren().size(); i++){
			if(((Element)technique.getChildren().get(i)).getName().equals("accessor")){
				accessor = ((Element)technique.getChildren().get(i));
				break;
			}
		}
		if(accessor==null) System.out.println("Couldn't find accessor");
		
		//Element accessor = positionsElement.getChild("technique_common").getChild("accessor");
		//Element accessor = ((Element)positionsElement.getParent()).getChild("technique_common").getChild("accessor");

		// get the x, y, z order
		ArrayList<String> order = new ArrayList<String>();
		for(Object o : accessor.getChildren()){
			order.add(((Element)o).getAttributeValue("name"));
		}
		return order;
	}


	private ArrayList<Vector3> createFloatArray(Element positionsElement, Element arrayElement){
		ArrayList<Vector3> vertices = new ArrayList<Vector3>();

		ArrayList<String> order = getVertexOrder();

		// read from the array
		String[] data = arrayElement.getText().split(" ");
		System.out.println("There are " + data.length + " numbers");
		for(int i=0; i<data.length; i+=3){
			float[] set = new float[3];
			set[0] = Float.parseFloat(data[i]);
			set[1] = Float.parseFloat(data[i+1]);
			set[2] = Float.parseFloat(data[i+2]);

			Vector3f vertex = new Vector3f(0f,0f,0f);
			for(int j=0; j<3; j++){
				if(order.get(j).equals("X")) vertex.x=set[j];
				else if(order.get(j).equals("Y")) vertex.y=set[j];
				else if(order.get(j).equals("Z")) vertex.z=set[j];
			}

			vertices.add(vertex);
		}
		return vertices;
	}

	private ArrayList<Vector3> createDoubleArray(Element positionsElement, Element arrayElement){
		ArrayList<Vector3> vertices = new ArrayList<Vector3>();

		ArrayList<String> order = getVertexOrder();

		// read from the array
		String[] data = arrayElement.getText().split(" ");
		for(int i=0; i<data.length; i++){
			double[] set = new double[]{3};
			set[0] = Double.parseDouble(data[i]);
			i++;
			set[1] = Double.parseDouble(data[i]);
			i++;
			set[2] = Double.parseDouble(data[i]);

			Vector3d vertex = new Vector3d(0d,0d,0d);
			for(int j=0; j<3; j++){
				if(order.get(j).equals("X")) vertex.x=set[j];
				else if(order.get(j).equals("Y")) vertex.y=set[j];
				else if(order.get(j).equals("Z")) vertex.z=set[j];
			}

			vertices.add(vertex);
		}
		return vertices;
	}


	private void findSource(Element e, String id){
		if(e.getName().equals("source")){
			if(id.contains(e.getAttributeValue("id"))) positionsElement = e;
		}
		else if(e.getChildren().size()>0){
			for(Object o : e.getChildren()){
				findSource((Element)o, id);
			}
		}
	}
	
	private void findVertices(Element e){
		/*
		Element vertices = e.getChild("COLLADA").getChild("library_geometries").getChild("geometry").getChild("mesh").getChild("vertices");
		for(Object a : vertices.getAttributes()){
			System.out.println(((Attribute)a).getName());
		}
		*/
		
		//System.out.println("Element name: " + e.getName());
		if(e.getName().equals("vertices")){
			for(Object o : e.getChildren()){
				if (((Element)o).getAttributeValue("semantic").equals("POSITION")){
					positionsSourceID = ((Element)o).getAttributeValue("source");
					System.out.println("Positions: " + positionsSourceID);
					return;
				}
			}
		}
		else if(e.getChildren().size()>0){
			for(int i=0; i<e.getChildren().size(); i++){
				Object o = e.getChildren().get(i);
				findVertices((Element)o);
			}
		}
		else{
			//System.err.println("A vertices element could not be found!");
		}
	}

}
