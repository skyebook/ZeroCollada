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
import java.util.Calendar;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * For manipulating a COLLADA file
 * @author Skye Book
 *
 */
public abstract class ColladaManipulator {

	protected Document colladaDoc;
	private Element modifiedElement;

	/**
	 * 
	 */
	public ColladaManipulator(Document collada) {
		colladaDoc=collada;
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
	 * Generates the filename for this file
	 * @return
	 */
	public abstract String newFileNameSuffix();
}
