/**
 * 
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

	/**
	 * 
	 */
	public Transformer(Document collada) {
		// TODO Auto-generated constructor stub
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
	public abstract Document doTransformation();

	private ArrayList<Vector3<?>> scanCollada(){
		String positionsSourceID = findVertices(colladaDoc.getRootElement());

		if(positionsSourceID!=null){
			Element positionsElement = findSource(colladaDoc.getRootElement(), positionsSourceID);
			if(positionsElement.getChildren().size()>0){
				for(int i=0; i<positionsElement.getChildren().size(); i++){
					Element child = (Element)positionsElement.getChildren().get(i);
					// check to see if we've struck the array Element
					if(child.getName().contains("_array")){
						if(child.getName().startsWith("float")){
							ArrayList<Vector3f> vertices = createFloatArray(positionsElement, child);
							float xMax = Float.NaN;
							float yMax = Float.NaN;
							float zMax = Float.NaN;

							for(Vector3f v : vertices){
								// if one is NaN, all are NaN.. initialize them here
								if(Float.isNaN(xMax)){
									xMax = v.x;
									yMax = v.y;
									zMax = v.z;
									continue;
								}

								if(v.x>xMax) xMax=v.x;
								if(v.y>yMax) yMax=v.y;
								if(v.z>zMax) zMax=v.z;
							}

							for(Vector3f v : vertices){
								v.x = v.x-xMax;
								v.y = v.y-yMax;
								v.z = v.z-zMax;
							}

							rewriteFloatArray(vertices, positionsElement, child);

							System.out.println("Offset " + xMax +", "+yMax+", "+zMax);

							// leave the loop now
							break;
						}
						else if(child.getName().startsWith("double")){
							ArrayList<Vector3d> vertices = createDoubleArray(positionsElement, child);
							double xMax = Double.NaN;
							double yMax = Double.NaN;
							double zMax = Double.NaN;

							for(Vector3d v : vertices){
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
					}
				}
			}
		}

		return null;
	}

	private void rewriteFloatArray(ArrayList<Vector3f> vertices, Element positionsElement, Element arrayElement){
		ArrayList<String> order = getVertexOrder(positionsElement);

		StringBuilder sb = new StringBuilder();
		for(Vector3f v : vertices){
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

	private void rewriteDoubleArray(ArrayList<Vector3d> vertices, Element positionsElement, Element arrayElement){
		ArrayList<String> order = getVertexOrder(positionsElement);

		StringBuilder sb = new StringBuilder();
		for(Vector3d v : vertices){
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

	private ArrayList<String> getVertexOrder(Element positionsElement){
		Element accessor = positionsElement.getChild("technique_common").getChild("accessor");

		// get the x, y, z order
		ArrayList<String> order = new ArrayList<String>();
		for(Object o : accessor.getChildren()){
			order.add(((Element)o).getAttributeValue("name"));
		}
		return order;
	}


	private ArrayList<Vector3f> createFloatArray(Element positionsElement, Element arrayElement){
		ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();

		ArrayList<String> order = getVertexOrder(positionsElement);

		// read from the array
		String[] data = arrayElement.getText().split(" ");
		for(int i=0; i<data.length; i++){
			float[] set = new float[]{3};
			set[0] = Float.parseFloat(data[i]);
			i++;
			set[1] = Float.parseFloat(data[i]);
			i++;
			set[2] = Float.parseFloat(data[i]);

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

	private ArrayList<Vector3d> createDoubleArray(Element positionsElement, Element arrayElement){
		ArrayList<Vector3d> vertices = new ArrayList<Vector3d>();

		ArrayList<String> order = getVertexOrder(positionsElement);

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


	private Element findSource(Element e, String id){
		if(e.getName().equals("source")){
			if(id.contains(e.getAttributeValue("id"))) return e;
		}
		else if(e.getChildren().size()>0){
			for(Object o : e.getChildren()){
				return findSource((Element)o, id);
			}
		}
		return null;
	}

	private String findVertices(Element e){
		if(e.getName().equals("vertices")){
			for(Object o : e.getChildren()){
				if (((Element)o).getAttributeValue("semantic").equals("POSITION")){
					return ((Element)o).getAttributeValue("source");
				}
			}
			return null;
		}
		else if(e.getChildren().size()>0){
			for(Object o : e.getChildren()){
				return findVertices((Element)o);
			}
		}
		else{
			System.err.println("A vertices element could not be found!");
			return null;
		}
		return null;
	}

}
