package sc.fiji.bdvpg.sourceandconverter.importer;

import bdv.util.RandomAccessibleIntervalSource;
import bdv.viewer.Source;
import bdv.viewer.SourceAndConverter;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.util.Util;
import sc.fiji.bdvpg.sourceandconverter.SourceAndConverterUtils;

import java.util.function.Supplier;

/**
 * Action which generates a new Source which samples and spans a space region
 * defined by either :
 * - an affine transform and a number of voxels
 * - or a model source and voxel sizes
 *
 * Mipmap unsupported
 * TimePoint 0 supported only TODO : improve timepoint support
 */

public class EmptySourceAndConverterCreator implements Runnable, Supplier<SourceAndConverter> {

    AffineTransform3D at3D = new AffineTransform3D();

    long nx, ny, nz;

    String name;

    ImgFactory imgfactory;

    /**
     * Simple constructor
     * @param name
     * @param at3D
     * @param nx
     * @param ny
     * @param nz
     * @param imgfactory
     */
    public EmptySourceAndConverterCreator(
            String name,
            AffineTransform3D at3D,
            long nx, long ny, long nz,
            ImgFactory imgfactory
            ) {
        this.nx = nx;
        this.ny = ny;
        this.nz = nz;
        this.at3D = at3D;
        this.name = name;
        this.imgfactory = imgfactory;
    }

    /**
     * Constructor where the region and sampling is defined by a model source
     * This constructor translates information from the model source into
     * an affine transform and a number of voxels
     * @param name
     * @param model
     * @param timePoint
     * @param voxSizeX
     * @param voxSizeY
     * @param voxSizeZ
     * @param imgfactory
     */
    public EmptySourceAndConverterCreator(
            String name,
            SourceAndConverter model,
            int timePoint,
            double voxSizeX, double voxSizeY, double voxSizeZ,
            ImgFactory imgfactory
    ) {
        // Gets model RAI
        RandomAccessibleInterval rai = model.getSpimSource().getSource(timePoint,0);

        long nPixModelX = rai.dimension(0);
        long nPixModelY = rai.dimension(1);
        long nPixModelZ = rai.dimension(2);

        // Gets transform of model RAI
        AffineTransform3D at3Dorigin = new AffineTransform3D();

        model.getSpimSource().getSourceTransform(timePoint,0,at3Dorigin);

        // Computes Voxel Size of model source (x, y, z)
        // And how it should be resampled to match the specified voxsize into the constructor
        // Origin
        double[] x0 = new double[3];
        at3Dorigin.apply(new double[]{0,0,0}, x0);

        // xMax
        double[] pt = new double[3];
        double dist;

        at3Dorigin.apply(new double[]{1,0,0},pt);

        dist = (pt[0]-x0[0])*(pt[0]-x0[0]) +
                (pt[1]-x0[1])*(pt[1]-x0[1]) +
                (pt[2]-x0[2])*(pt[2]-x0[2]);

        double distx =  Math.sqrt(dist);

        dist = Math.sqrt(dist)*nPixModelX;

        double nPixX = dist/voxSizeX;

        at3Dorigin.apply(new double[]{0,1,0},pt);

        dist = (pt[0]-x0[0])*(pt[0]-x0[0]) +
                (pt[1]-x0[1])*(pt[1]-x0[1]) +
                (pt[2]-x0[2])*(pt[2]-x0[2]);

        double disty =  Math.sqrt(dist);

        dist = Math.sqrt(dist)*nPixModelY;
        double nPixY = dist/voxSizeY;

        at3Dorigin.apply(new double[]{0,0,1},pt);

        dist = (pt[0]-x0[0])*(pt[0]-x0[0]) +
                (pt[1]-x0[1])*(pt[1]-x0[1]) +
                (pt[2]-x0[2])*(pt[2]-x0[2]);

        double distz =  Math.sqrt(dist);

        dist = Math.sqrt(dist)*nPixModelZ;
        double nPixZ = dist/voxSizeZ;

        // Gets original affine transform and rescales it accordingly
        double[] m = at3Dorigin.getRowPackedCopy();

        m[0] = m[0]/distx * voxSizeX;
        m[4] = m[4]/distx * voxSizeX;
        m[8] = m[8]/distx * voxSizeX;

        m[1] = m[1]/disty * voxSizeY;
        m[5] = m[5]/disty * voxSizeY;
        m[9] = m[9]/disty * voxSizeY;

        m[2] = m[2]/distz * voxSizeZ;
        m[6] = m[6]/distz * voxSizeZ;
        m[10] = m[10]/distz * voxSizeZ;

        at3Dorigin.set(m);

        this.name = name;
        this.at3D = at3Dorigin;
        this.nx = (long) nPixX;
        this.ny = (long) nPixY;
        this.nz = (long) nPixZ;
        this.imgfactory = imgfactory;

    }

    @Override
    public void run() {

    }

    @Override
    public SourceAndConverter get() {

        Img img = imgfactory.create(nx,ny,nz);

        Source src = new RandomAccessibleIntervalSource(img, Util.getTypeFromInterval(img), at3D, name);

        SourceAndConverter sac;

        sac = SourceAndConverterUtils.createSourceAndConverter(src);

        return sac;
    }
}
