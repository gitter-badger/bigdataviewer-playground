package sc.fiji.bdvpg.bdv;

import net.imagej.ImageJ;
import sc.fiji.bdvpg.services.SourceAndConverterServices;

public class BdvCreatorDemo
{
	public static void main( String[] args )
	{
		// Create the ImageJ application context with all available services; necessary for SourceAndConverterServices creation
		ImageJ ij = new ImageJ();
		ij.ui().showUI();

		// Creates a Bdv since none exists yet
		SourceAndConverterServices.getSourceAndConverterDisplayService().getActiveBdv();
	}
}
