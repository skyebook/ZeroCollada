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
package net.skyebook.zerocollada.translation;

import java.util.ArrayList;

import net.skyebook.zerocollada.ColladaManipulator;

import org.jdom.Document;
import org.jdom.Element;

/**
 * @author Skye Book
 *
 */
public abstract class Translation extends ColladaManipulator{

	/**
	 * 
	 */
	public Translation(Document collada, boolean handleX, boolean handleY, boolean handleZ) {
		super(collada);

		// Performs the transformation
		scanCollada();
	}

	/**
	 * Work the translations!
	 * @param translations
	 */
	public abstract void doTranslationAction(ArrayList<Element> translations);

	/**
	 * 
	 * @return
	 */
	protected void scanCollada(){
		ArrayList<Element> translations = new ArrayList<Element>();
		findTranslations(colladaDoc.getRootElement(), translations);
		doTranslationAction(translations);
	}

	private void findTranslations(Element e, ArrayList<Element> translations){
		if(e.getName().equals("translate")){
			translations.add(e);
		}
		else if(e.getChildren().size()>0){
			for(Object o : e.getChildren()){
				findTranslations((Element)o, translations);
			}
		}
	}
}
