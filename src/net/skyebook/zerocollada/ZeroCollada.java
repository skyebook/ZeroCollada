/**
 * 
 */
package net.skyebook.zerocollada;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 * @author Skye Book
 *
 */
public class ZeroCollada {
	
	private static Options options;

	/**
	 * @param args
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws ParseException {
		// Setup Apache Commons CLI
		optionsSetup();
		
		HelpFormatter help = new HelpFormatter();
		help.printHelp("zerocollada", options);
		
		CommandLineParser parser = new PosixParser();
		CommandLine cmd = parser.parse(options, args);
		
		if(cmd.hasOption(ZCOpts.transform)){
			// Do a transform that brings us as close to zero as possible
			
			File colladaFile = new File(args[args.length-1]);
			if(!colladaFile.exists()){
				System.err.println("The file located at " + colladaFile.toString() + " does not exist!  Qutting.");
				return;
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
