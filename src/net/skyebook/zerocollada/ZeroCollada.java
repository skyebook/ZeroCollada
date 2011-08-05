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

import net.skyebook.zerocollada.geom.ClosestToOriginTransformer;
import net.skyebook.zerocollada.translation.Translation;

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
	private static final String usage = "java -jar ZeroCollada.jar [OPTIONS] [FILE|DIRECTORY]";

	/**
	 * @param args
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public static void main(String[] args) throws ParseException, JDOMException, IOException {
		// Setup Apache Commons CLI
		optionsSetup();

		CommandLineParser parser = new PosixParser();
		CommandLine cmd = parser.parse(options, args);

		if(args.length==0 || cmd.hasOption(ZCOpts.help)){
			showHelp();
		}

		// was there a valid operation specified?
		if(hasValidOption(cmd)){
			File fileFromCommandLine = new File(args[args.length-1]);
			if(!fileFromCommandLine.exists()){
				System.err.println("The file located at " + fileFromCommandLine.toString() + " does not exist!  Qutting.");
				return;
			}
			else if(fileFromCommandLine.isFile() && fileFromCommandLine.toString().endsWith(".dae")){
				// we've been given a single file.  Act on this single file
				doRequestedAction(fileFromCommandLine, cmd);
			}
			else if(fileFromCommandLine.isDirectory()){
				// We've been given a directory of files process the ones that are .dae
				for(File file : fileFromCommandLine.listFiles()){
					if(file.isFile() && file.toString().endsWith(".dae")){
						doRequestedAction(file, cmd);
					}
					else{
						System.err.println(file.toString() + " does not seem to be a COLLADA file");
					}
				}
			}
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

	private static boolean hasValidOption(CommandLine cmd){
		return cmd.hasOption(ZCOpts.transform) || cmd.hasOption(ZCOpts.translate);
	}

	private static void doRequestedAction(File file, CommandLine cmd) throws IOException, JDOMException{
		// what is the operation?
		if(cmd.hasOption(ZCOpts.transform)){
			// Do a transform that brings us as close to zero as possible
			Document dom = createDocument(file);

			ClosestToOriginTransformer ct = new ClosestToOriginTransformer(dom, cmd.hasOption(ZCOpts.includeX), cmd.hasOption(ZCOpts.includeY), cmd.hasOption(ZCOpts.includeZ));
			file = removeOldXYZTag(file);
			ct.writeColladaToFile(newFilename(file, ct));
			// recollect the resources
			ct = null;
			dom = null;
			System.gc();
		}
		else if(cmd.hasOption(ZCOpts.translate)){
			cmd.getOptionValues(ZCOpts.translate);
			//System.out.println("Args: " + cmd.getOptionValues(ZCOpts.translate).length);
			if(cmd.getOptionValues(ZCOpts.translate).length==0){
				showHelp();
			}
			else{
				Translation translation = new Translation(createDocument(file));
				for(int i=0; i<cmd.getOptionValues(ZCOpts.translate).length; i++){
					String arg = cmd.getOptionValues(ZCOpts.translate)[i];
					if(arg.equals("zerowithname")){
						translation.zeroTranslation(cmd.getOptionValues(ZCOpts.translate)[i+1]);
					}
					else if(arg.equals("zerowithoutname")){
						translation.zeroAllTranslationsExcept(cmd.getOptionValues(ZCOpts.translate)[i+1]);
					}
				}
				translation.writeColladaToFile(newFilename(file, translation));
			}
		}
	}

	private static File newFilename(File file, ColladaManipulator manipulator){
		return new File(file.toString().substring(0, file.toString().lastIndexOf("."))+manipulator.newFileNameSuffix()+".dae");
	}

	private static Document createDocument(File file) throws JDOMException, IOException{
		SAXBuilder builder = new SAXBuilder();
		Document dom = builder.build(file);
		return dom;
	}


	private static void showHelp(){
		HelpFormatter help = new HelpFormatter();
		help.printHelp(usage, options);
	}

	private static File removeOldXYZTag(File file){
		if(file.toString().contains("x_")){
			return new File(file.toString().substring(0, file.toString().indexOf("x_"))+".dae");
		}
		else return file;
	}

	public static void optionsSetup(){
		options = new Options();

		Option help = new Option(ZCOpts.help, "Print this help menu");		
		Option transform = new Option(ZCOpts.transform, "Perform a transform.  Defaults to closet-to-origin anchor without consideration for the Y-axis");		
		Option includeX = new Option(ZCOpts.includeX, "Include the X-Axis in this transform calculation");
		Option includeY = new Option(ZCOpts.includeY, "Include the Y-Axis in this transform calculation");
		Option includeZ = new Option(ZCOpts.includeZ, "Include the Z-Axis in this transform calculation");
		Option anchorCenter = new Option(ZCOpts.anchorCenter, "NOT IMPLEMENTED YET -- Anchors the transformation to the middle of the *range* of coordinates (not the average)");

		Option translate = new Option(ZCOpts.translate, true, "Change translations");
		translate.setArgName("translation type");
		translate.setArgs(2);

		options.addOption(help);
		options.addOption(transform);
		options.addOption(includeX);
		options.addOption(includeY);
		options.addOption(includeZ);
		options.addOption(anchorCenter);
		options.addOption(translate);
	}

}
