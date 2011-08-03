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
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import net.skyebook.zerocollada.structure.Vector3;

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
	private Element modifiedElement;
	private String positionsSourceID;
	private Element positionsElement;

	protected boolean handleX=true;
	protected boolean handleY=false;
	protected boolean handleZ=true;

	/**
	 * 
	 */
	public Transformer(Document collada, boolean handleX, boolean handleY, boolean handleZ) {
		colladaDoc=collada;
		this.handleX=handleX;
		this.handleY=handleY;
		this.handleZ=handleZ;

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

		// update the date before writing the file
		updateModifiedDateTag();

		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		FileWriter writer = new FileWriter(file);
		outputter.output(colladaDoc, writer);
		writer.close();
	}

	/**
	 * Adapted from the Java forums: http://forums.sun.com/thread.jspa?messageID=768476
	 */
	private void updateModifiedDateTag(){

		/*
		 * create ISO 8601 formatter for Calendar objects
		 */
		MessageFormat iso8601 = new MessageFormat("{0,time}{1,number,+00;-00}:{2,number,00}") ;

		// need to shove a date formatter that is cognizant of the
		// calendar's time zone into the message formatter
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss") ;
		df.setTimeZone(Calendar.getInstance().getTimeZone()) ;
		iso8601.setFormat(0, df) ;

		/*
		 * calculate the time zone offset from UTC in hours and minutes at the current time
		 */
		long zoneOff = Calendar.getInstance().get(Calendar.ZONE_OFFSET) + Calendar.getInstance().get(Calendar.DST_OFFSET) ;
		zoneOff /= 60000L ;  // in minutes
		int zoneHrs = (int) (zoneOff / 60L) ;
		int zoneMins = (int) (zoneOff % 60L) ;
		if (zoneMins < 0) zoneMins = -zoneMins ;

		String iso8601Date =  (iso8601.format(new Object[] {
				Calendar.getInstance().getTime(),
				new Integer(zoneHrs),
				new Integer(zoneMins)
		}
		)) ;

		// find the date of the last modification and upate it
		findModified(colladaDoc.getRootElement());

		if(modifiedElement!=null){
			modifiedElement.setText(iso8601Date);
		}else{
			System.err.println("The COLLADA file had no <modified\\> element set.  The date could not be updated");
		}

	}

	private void findModified(Element e){
		if(e.getName().equals("modified")){
			modifiedElement=e;
			return;
		}
		else if(e.getChildren().size()>0){
			for(Object o : e.getChildren()){
				findModified((Element)o);
			}
		}
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

		// cut out the last space -> It appears that JDOM does this for us
		//arrayString.substring(0, arrayString.length()-1);
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

			Vector3<Float> vertex = new Vector3<Float>(0f,0f,0f);
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

			Vector3<Double> vertex = new Vector3<Double>(0d,0d,0d);
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
