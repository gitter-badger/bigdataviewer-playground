package sc.fiji.bdvpg.scijava.command.spimdata;

import mpicbg.spim.data.generic.AbstractSpimData;
import org.scijava.ItemIO;
import org.scijava.ItemVisibility;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import sc.fiji.bdvpg.scijava.ScijavaBdvDefaults;
import sc.fiji.bdvpg.services.BdvService;
import sc.fiji.bdvpg.spimdata.importer.SpimDataImporterXML;

import java.io.File;
import java.util.ArrayList;

@Plugin( type = Command.class, menuPath = ScijavaBdvDefaults.RootMenu+"SpimDataset>Open XML/HDF5 Files" )
public class MultipleSpimDataImporterCommand implements Command {

    /**
     * Note: Due to a bug in SciJava there needs to be some
     * text above the `File[]` parameter.
     * Otherwise the UI for `File[]` is not built.
     */
    @Parameter ( visibility = ItemVisibility.MESSAGE  )
    private String message = "Please choose XML/HDF5 files:";

    @Parameter(style="extensions:xml")
    public File[] files;

    public void run() {
        for ( int i = 0; i < files.length; ++i ) {
            new SpimDataImporterXML( files[i] ).get();
        }
    }

}