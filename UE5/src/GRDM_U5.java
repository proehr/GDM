import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Opens an image window and adds a panel below the image
 */
public class GRDM_U5 implements PlugIn {

    ImagePlus imp; // ImagePlus object
    private int[] origPixels;
    private int width;
    private int height;

    String[] items = {"Original", "Tiefpassfilter", "Hochpassfilter", "Verstärkte Kanten", "Verstärkte Kanten mit Rand"};


    public static void main(String args[]) {

        IJ.open("/Users/line/Documents/IMI/GDM/UE5/src/sail.jpg");

        GRDM_U5 pw = new GRDM_U5();
        pw.imp = IJ.getImage();
        pw.run("");
    }

    public void run(String arg) {
        if (imp == null)
            imp = WindowManager.getCurrentImage();
        if (imp == null) {
            return;
        }
        CustomCanvas cc = new CustomCanvas(imp);

        storePixelValues(imp.getProcessor());

        new CustomWindow(imp, cc);
    }


    private void storePixelValues(ImageProcessor ip) {
        width = ip.getWidth();
        height = ip.getHeight();

        origPixels = ((int[]) ip.getPixels()).clone();
    }


    class CustomCanvas extends ImageCanvas {

        CustomCanvas(ImagePlus imp) {
            super(imp);
        }

    } // CustomCanvas inner class


    class CustomWindow extends ImageWindow implements ItemListener {

        private String method;

        CustomWindow(ImagePlus imp, ImageCanvas ic) {
            super(imp, ic);
            addPanel();
        }

        void addPanel() {
            //JPanel panel = new JPanel();
            Panel panel = new Panel();

            JComboBox cb = new JComboBox(items);
            panel.add(cb);
            cb.addItemListener(this);

            add(panel);
            pack();
        }

        public void itemStateChanged(ItemEvent evt) {

            // Get the affected item
            Object item = evt.getItem();

            if (evt.getStateChange() == ItemEvent.SELECTED) {
                System.out.println("Selected: " + item.toString());
                method = item.toString();
                changePixelValues(imp.getProcessor());
                imp.updateAndDraw();
            }

        }


        private void changePixelValues(ImageProcessor ip) {

            // Array zum Zurückschreiben der Pixelwerte
            int[] pixels = (int[]) ip.getPixels();

            if (method.equals("Original")) {

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pos = y * width + x;

                        pixels[pos] = origPixels[pos];
                    }
                }
            }

            if (method.equals("Tiefpassfilter")) {

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {

                        int rsum = 0;
                        int gsum = 0;
                        int bsum = 0;

                        for (int i = -1; i <= 1; i++) {
                            for (int j = -1; j <= 1; j++) {
                                if (x + i < width && y + j < height && x + i >= 0 && y + j >= 0) {
                                    int pos = (y + j) * width + x + i;
                                    int argbpix = origPixels[pos];

                                    int rpix = (argbpix >> 16) & 0xff;
                                    int gpix = (argbpix >> 8) & 0xff;
                                    int bpix = argbpix & 0xff;

                                    rsum += rpix;
                                    gsum += gpix;
                                    bsum += bpix;
                                }
                            }
                        }

                        rsum /= 9;
                        gsum /= 9;
                        bsum /= 9;

                        int pos = (y) * width + x;

                        pixels[pos] = (0xFF << 24) | (rsum << 16) | (gsum << 8) | bsum;
                    }
                }
            }

            if (method.equals("Hochpassfilter")) {

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {

                        int rsum = 0;
                        int gsum = 0;
                        int bsum = 0;

                        for (int i = -1; i <= 1; i++) {
                            for (int j = -1; j <= 1; j++) {
                                if (x + i < width && y + j < height && x + i >= 0 && y + j >= 0) {
                                    int pos = (y + j) * width + x + i;
                                    int argbpix = origPixels[pos];

                                    int rpix = (argbpix >> 16) & 0xff;
                                    int gpix = (argbpix >> 8) & 0xff;
                                    int bpix = argbpix & 0xff;

                                    if (i == 0 && j == 0) {
                                        rsum += 8 * rpix;
                                        gsum += 8 * gpix;
                                        bsum += 8 * bpix;
                                    } else {
                                        rsum -= rpix;
                                        gsum -= gpix;
                                        bsum -= bpix;
                                    }
                                }
                            }
                        }

                        rsum = Math.min(255, (Math.max(0, rsum / 9 + 128)));
                        gsum = Math.min(255, (Math.max(0, gsum / 9 + 128)));
                        bsum = Math.min(255, (Math.max(0, bsum / 9 + 128)));


                        int pos = (y) * width + x;

                        pixels[pos] = (0xFF << 24) | (rsum << 16) | (gsum << 8) | bsum;
                    }
                }
            }

            if (method.equals("Verstärkte Kanten")) {

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {

                        int rsum = 0;
                        int gsum = 0;
                        int bsum = 0;

                        for (int i = -1; i <= 1; i++) {
                            for (int j = -1; j <= 1; j++) {
                                if (x + i < width && y + j < height && x + i >= 0 && y + j >= 0) {
                                    int pos = (y + j) * width + x + i;
                                    int argbpix = origPixels[pos];

                                    int rpix = (argbpix >> 16) & 0xff;
                                    int gpix = (argbpix >> 8) & 0xff;
                                    int bpix = argbpix & 0xff;

                                    if (i == 0 && j == 0) {
                                        rsum += 17 * rpix;
                                        gsum += 17 * gpix;
                                        bsum += 17 * bpix;
                                    } else {
                                        rsum -= rpix;
                                        gsum -= gpix;
                                        bsum -= bpix;
                                    }
                                }
                            }
                        }

                        rsum = Math.min(255, (Math.max(0, rsum / 9)));
                        gsum = Math.min(255, (Math.max(0, gsum / 9)));
                        bsum = Math.min(255, (Math.max(0, bsum / 9)));


                        int pos = (y) * width + x;

                        pixels[pos] = (0xFF << 24) | (rsum << 16) | (gsum << 8) | bsum;
                    }
                }
            }

            if (method.equals("Verstärkte Kanten mit Rand")) {

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {

                        int rsum = 0;
                        int gsum = 0;
                        int bsum = 0;

                        for (int i = -1; i <= 1; i++) {
                            for (int j = -1; j <= 1; j++) {
                                int yj = Math.min(height - 1, Math.max(0, y + j));
                                int xi = Math.min(width - 1, Math.max(0, x + i));

                                int pos = yj * width + xi;
                                int argbpix = origPixels[pos];

                                int rpix = (argbpix >> 16) & 0xff;
                                int gpix = (argbpix >> 8) & 0xff;
                                int bpix = argbpix & 0xff;

                                if (i == 0 && j == 0) {
                                    rsum += 17 * rpix;
                                    gsum += 17 * gpix;
                                    bsum += 17 * bpix;
                                } else {
                                    rsum -= rpix;
                                    gsum -= gpix;
                                    bsum -= bpix;
                                }

                            }

                        }

                        rsum = Math.min(255, (Math.max(0, rsum / 9)));
                        gsum = Math.min(255, (Math.max(0, gsum / 9)));
                        bsum = Math.min(255, (Math.max(0, bsum / 9)));


                        int pos = (y) * width + x;

                        pixels[pos] = (0xFF << 24) | (rsum << 16) | (gsum << 8) | bsum;
                    }
                }
            }

        }


    } // CustomWindow inner class
}
