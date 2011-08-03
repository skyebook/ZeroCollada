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
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * @author Skye Book
 *
 */
public class ZeroCollada {

	private static Options options;

	/**
	 * @param args
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public static void main(String[] args) throws ParseException, JDOMException, IOException {
		// Setup Apache Commons CLI
		optionsSetup();

		HelpFormatter help = new HelpFormatter();
		help.printHelp("zerocollada", options);

		CommandLineParser parser = new PosixParser();
		CommandLine cmd = parser.parse(options, args);

		if(cmd.hasOption(ZCOpts.transform)){
			File colladaFile = new File(args[args.length-1]);
			if(!colladaFile.exists()){
				System.err.println("The file located at " + colladaFile.toString() + " does not exist!  Qutting.");
				return;
			}
			// Do a transform that brings us as close to zero as possible
			SAXBuilder builder = new SAXBuilder();
			Document dom = builder.build(colladaFile);
			ClosestToOriginTransformer ct = new ClosestToOriginTransformer(dom);
			ct.writeColladaToFile(new File(colladaFile.toString().substring(0, colladaFile.toString().lastIndexOf("."))+ct.newFileNameSuffix()+".dae"));
		}
		else{
			// Then why are we here?
			if(cmd.hasOption(ZCOpts.includeY)){
				System.err.println("You have used the " + ZCOpts.includeY + " option but have not asked for a transform (t)");
			}
			else if(cmd.hasOption(ZCOpts.anchorCenter)){
				System.err.println("You have used the " + ZCOpts.includeY + " option but have not asked for a transform (t)");
			}
		}

	}

	public static void optionsSetup(){
		options = new Options();

		Option transform = new Option(ZCOpts.transform, "Perform a transform.  Defaults to a center anchor without consideration for the Y-axis");		
		Option includeY = new Option(ZCOpts.includeY, "Include the Y-Axis in this transform calculation");
		Option anchorCenter = new Option(ZCOpts.anchorCenter, "Anchors the transformation to the middle of the *range* of coordinates (not the average)");

		options.addOption(transform);
		options.addOption(includeY);
		options.addOption(anchorCenter);
	}

}
