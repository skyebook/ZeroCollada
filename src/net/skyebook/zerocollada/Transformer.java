/**
 * 
 */
package net.skyebook.zerocollada;

import java.util.ArrayList;

import net.skyebook.zerocollada.structure.Vector3;

import org.jdom.Document;
import org.jdom.Element;

/**
 * For Transforming a COLLADA file
 * @author Skye Book
 *
 */
public abstract class Transformer {
	
	private Document rawCollada;

	/**
	 * 
	 */
	public Transformer(Document collada) {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Perform the specified transformation
	 * @return
	 */
	public abstract Document doTransformation();
	
	private ArrayList<Vector3<?>> scanCollada(){
		String positionsSourceID = findVertices(rawCollada.getRootElement());
		
		if(positionsSourceID!=null){
			Element positionsElement = findSource(rawCollada.getRootElement(), positionsSourceID);
			if(positionsElement.getChildren().size()>0){
				for(int i=0; i<positionsElement.getChildren().size(); i++){
					Element child = (Element)positionsElement.getChildren().get(i);
					// check to see if we've struck the array Element
					if(child.getName().contains("_array")){
						if(child.getName().startsWith("float")){
							
						}
						else if(child.getName().startsWith("double")){
							
						}
					}
				}
			}
		}
		
		return null;
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
