===================================
          + ZeroCollada +
===================================

Intro
		Hi there, thanks for having a look at ZeroCollada.  I created this
	utility to eliminate a bit of the haziness surrounding the myriad
	importers and exporters available for different DCC tools.  If you
	just want to do some operations on your files, you've come to the
	right place.  My initial impetus was as a sort of "undo" for Maya's
	unequivocally evil "Freeze Transformations" function.
		That said, its my hope that that can become a solid collection of
	CLI based tools that can manipulate COLLADA markup without too much
	frustration.
	
Usage
	java -jar ZeroCollada.jar [options] /full/path/to/file.dae
	
	Examples:
		// Transform the model closer to the origin in the amount of the furthest distance {x,z} from the origin
		// RESULT: /full/path/to/filex_{X_CHANGE}z_{Z_CHANGE}.dae
		java -jar ZeroCollada.jar -txz /full/path/to/file.dae
		
		// Transform the model closer to the origin in the amount of the furthest distance {x,y,z} from the origin
		// RESULT: /full/path/to/filex_{X_CHANGE}y_{Y_CHANGE}z_{Z_CHANGE}.dae
		java -jar ZeroCollada.jar -txyz /full/path/to/file.dae

Building
	ANT script to come.  For now, Eclipse users can do Export->Runnable JAR File.