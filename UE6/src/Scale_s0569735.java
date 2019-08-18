import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.PlugIn;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;


public class Scale_s0569735 implements PlugInFilter {

    private ImagePlus imp;
    private int[] origPixels;
    private int width;
    private int height;

    public int setup(String arg, ImagePlus imp) {
        if (arg.equals("about")) {
            showAbout();
            return DONE;
        }
        return DOES_RGB + NO_CHANGES;
        // kann RGB-Bilder und veraendert das Original nicht
    }

    public static void main(String[] args) {

        IJ.open("/Users/line/Documents/IMI/GDM/UE6/src/component.jpg");

        Scale_s0569735 pw = new Scale_s0569735();
        pw.imp = IJ.getImage();
        ImageProcessor newImage = pw.imp.getProcessor();
        pw.run(newImage);
    }

    public void run(ImageProcessor ip) {

        String[] dropdownmenue = {"Kopie", "Pixelwiederholung", "Bilinear"};

        GenericDialog gd = new GenericDialog("scale");
        gd.addChoice("Methode", dropdownmenue, dropdownmenue[0]);
        gd.addNumericField("Hoehe:", 500, 0);
        gd.addNumericField("Breite:", 400, 0);

        gd.showDialog();

        int height_n = (int) gd.getNextNumber(); // _n fuer das neue skalierte Bild
        int width_n = (int) gd.getNextNumber();

        int width = ip.getWidth();  // Breite bestimmen
        int height = ip.getHeight(); // Hoehe bestimmen

        //height_n = height;
        //width_n  = width;

        ImagePlus neu = NewImage.createRGBImage("Skaliertes Bild",
                width_n, height_n, 1, NewImage.FILL_BLACK);

        ImageProcessor ip_n = neu.getProcessor();


        int[] pix = (int[]) ip.getPixels();
        int[] pix_n = (int[]) ip_n.getPixels();

        int methode = 0;
        String s = gd.getNextChoice();
        if (s.equals("Kopie")) methode = 1;
        if (s.equals("Pixelwiederholung")) methode = 2;
        if (s.equals("Bilinear")) methode = 3;

        // Schleife ueber das neue Bild
        if (methode == 1) {                                                //Kopie
            for (int y_n = 0; y_n < height_n; y_n++) {
                for (int x_n = 0; x_n < width_n; x_n++) {
                    int y = y_n;
                    int x = x_n;

                    if (y < height && x < width) {
                        int pos_n = y_n * width_n + x_n;
                        int pos = y * width + x;

                        pix_n[pos_n] = pix[pos];
                    }
                }
            }
        }

        if (methode == 2) {                                                //Pixelwiederholung
            for (int y_n = 0; y_n < height_n; y_n++) {
                for (int x_n = 0; x_n < width_n; x_n++) {
                    int y = y_n * (height - 1) / height_n;
                    int x = x_n * (width - 1) / width_n;

                    int pos_n = y_n * width_n + x_n;
                    int pos = y * width + x;

                    pix_n[pos_n] = pix[pos];

                }
            }
        }

        if (methode == 3) {                                            //Bilineare Transformation
            double xScale = (width - 1) / (double) (width_n - 1);
            double yScale = (height - 1) / (double) (height_n - 1);
            for (int y_n = 0; y_n < height_n; y_n++) {
                for (int x_n = 0; x_n < width_n; x_n++) {
                    double yDouble = y_n * yScale;
                    double xDouble = x_n * xScale;

                    int yOrig = (int) yDouble;
                    int xOrig = (int) xDouble;

                    double diffX = xDouble - xOrig;
                    double diffY = yDouble - yOrig;


                    int rsum = 0;
                    int gsum = 0;
                    int bsum = 0;

                    int[][] fourPixels = new int[2][2];

                    for(int i=0;i<fourPixels.length;i++){
                        for(int j = 0; j<fourPixels[i].length;j++) {

                            int pos = (Math.max(0,Math.min(yOrig + j,height-1))) * width + Math.max(0,Math.min(xOrig + i,width-1));
                            fourPixels[i][j] = pix[pos];

                            int rpix = (fourPixels[i][j] >> 16) & 0xff;
                            int gpix = (fourPixels[i][j] >> 8) & 0xff;
                            int bpix = fourPixels[i][j] & 0xff;

                            if(i==0&&j==0) {                                                //unnötig lang, aber ich hatte keine Zeit, es noch zu verbessern

                                rsum += rpix * (1 - diffX) * (1 - diffY);                   //für jeden der 4 pixel ein eignes if-statement
                                gsum += gpix * (1 - diffX) * (1 - diffY);
                                bsum += bpix * (1 - diffX) * (1 - diffY);
                            }
                            if(i==1&&j==0) {

                                rsum += rpix * diffX * (1 - diffY);
                                gsum += gpix * diffX * (1 - diffY);
                                bsum += bpix * diffX * (1 - diffY);
                            }if(i==0&&j==1) {

                                rsum += rpix * (1 - diffX) * diffY;
                                gsum += gpix * (1 - diffX) * diffY;
                                bsum += bpix * (1 - diffX) * diffY;
                            }if(i==1&&j==1) {

                                rsum += rpix * diffX * diffY;
                                gsum += gpix * diffX * diffY;
                                bsum += bpix * diffX * diffY;
                            }

                        }
                    }



                    int pos_n = y_n * width_n + x_n;
                    pix_n[pos_n] = (0xFF << 24) | (rsum << 16) | (gsum << 8) | bsum;


                }
            }
        }


        // neues Bild anzeigen
        neu.show();
        neu.updateAndDraw();
    }

    void showAbout() {
        IJ.showMessage("");
    }
}

