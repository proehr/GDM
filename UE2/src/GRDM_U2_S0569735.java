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

import javax.swing.BorderFactory;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
     Opens an image window and adds a panel below the image
*/
public class GRDM_U2_S0569735 implements PlugIn {

    ImagePlus imp; // ImagePlus object
	private int[] origPixels;
	private int width;
	private int height;
	
	
    public static void main(String args[]) {
		//new ImageJ();
    	IJ.open("/Users/line/Documents/IMI/GDM/UE2/src/orchid.jpg");
    	//IJ.open("Z:/Pictures/Beispielbilder/orchid.jpg");
		
		GRDM_U2_S0569735 pw = new GRDM_U2_S0569735();
		pw.imp = IJ.getImage();
		pw.run("");
	}
    
    public void run(String arg) {
    	if (imp==null) 
    		imp = WindowManager.getCurrentImage();
        if (imp==null) {
            return;
        }
        CustomCanvas cc = new CustomCanvas(imp);
        
        storePixelValues(imp.getProcessor());
        
        new CustomWindow(imp, cc);
    }


    private void storePixelValues(ImageProcessor ip) {
    	width = ip.getWidth();
		height = ip.getHeight();
		
		origPixels = ((int []) ip.getPixels()).clone();
	}


	class CustomCanvas extends ImageCanvas {
    
        CustomCanvas(ImagePlus imp) {
            super(imp);
        }
    
    } // CustomCanvas inner class
    
    
    class CustomWindow extends ImageWindow implements ChangeListener {
         
        private JSlider jSliderBrightness;
		private JSlider jSliderContrast;
		private JSlider jSliderSaturation;
		private JSlider jSliderHue;
		private double brightness=0;
		private double contrast=1;
		private double saturation=1;
		private double hue=0;

		CustomWindow(ImagePlus imp, ImageCanvas ic) {
            super(imp, ic);
            addPanel();
        }
    
        void addPanel() {
        	//JPanel panel = new JPanel();
        	Panel panel = new Panel();

            panel.setLayout(new GridLayout(4, 1));
            jSliderBrightness = makeTitledSilder("Helligkeit", -128, 128, 0);
            jSliderContrast = makeTitledSilder("Kontrast", 0, 10, 5);
			jSliderSaturation = makeTitledSilder("Sättigung", 0, 8, 4);
			jSliderHue = makeTitledSilder("Hue", 0, 360, 0);
            panel.add(jSliderBrightness);
            panel.add(jSliderContrast);
			panel.add(jSliderSaturation);
			panel.add(jSliderHue);
            add(panel);
            
            pack();
         }
      
        private JSlider makeTitledSilder(String string, int minVal, int maxVal, int val) {
		
        	JSlider slider = new JSlider(JSlider.HORIZONTAL, minVal, maxVal, val );
        	Dimension preferredSize = new Dimension(width, 50);
        	slider.setPreferredSize(preferredSize);
			TitledBorder tb = new TitledBorder(BorderFactory.createEtchedBorder(), 
					string, TitledBorder.LEFT, TitledBorder.ABOVE_BOTTOM,
					new Font("Sans", Font.PLAIN, 11));
			slider.setBorder(tb);
			slider.setMajorTickSpacing((maxVal - minVal)/10 );
			slider.setPaintTicks(true);
			slider.addChangeListener(this);
			
			return slider;
		}
        
        private void setSliderTitle(JSlider slider, String str) {
			TitledBorder tb = new TitledBorder(BorderFactory.createEtchedBorder(),
				str, TitledBorder.LEFT, TitledBorder.ABOVE_BOTTOM,
					new Font("Sans", Font.PLAIN, 11));
			slider.setBorder(tb);
		}

		public void stateChanged( ChangeEvent e ){
			JSlider slider = (JSlider)e.getSource();

			if (slider == jSliderBrightness) {
				brightness = slider.getValue();
				String str = "Helligkeit " + brightness; 
				setSliderTitle(jSliderBrightness, str); 
			}
			
			if (slider == jSliderContrast) {
				int value = slider.getValue();
				if(value>5){
					contrast = (value-5)*2;
				}
				else{
					contrast = value/5.0;
				}
				String str = "Kontrast " + contrast;
				setSliderTitle(jSliderContrast, str);
			}
			if (slider == jSliderSaturation) {
				int value = slider.getValue();
				if (value <= 4) {
					saturation=value/4.0;
				}
				else{
					saturation=value-3;
				}
				String str = "Sättigung " + saturation;
				setSliderTitle(jSliderSaturation, str);
			}
			if (slider == jSliderHue) {
				double value = slider.getValue();
				String str = "Hue " + value;
				hue = value*Math.PI/180;
				setSliderTitle(jSliderHue, str);
			}
			
			changePixelValues(imp.getProcessor());
			
			imp.updateAndDraw();
		}

		
		private void changePixelValues(ImageProcessor ip) {
			
			// Array fuer den Zugriff auf die Pixelwerte
			int[] pixels = (int[])ip.getPixels();
			
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					int argb = origPixels[pos];  // Lesen der Originalwerte 
					
					int r = (argb >> 16) & 0xff;
					int g = (argb >>  8) & 0xff;
					int b =  argb        & 0xff;
					
					
					// anstelle dieser drei Zeilen später hier die Farbtransformation durchführen,
					// die Y Cb Cr -Werte verändern und dann wieder zurücktransformieren
					/*int rn = (int) (r + brightness);
					int gn = (int) (g + brightness);
					int bn = (int) (b + brightness);*/

					double lumi = 0.299*r + 0.587*g + 0.114*b;
					double u = (b-lumi)*0.493;
					double v = (r-lumi)*0.877;

					double lumin=contrast*(lumi-127.5)+127.5+brightness;
					double un=(u*Math.cos(hue) - v*Math.sin(hue))*saturation*contrast;
					double vn=(u*Math.sin(hue) + v*Math.cos(hue))*saturation*contrast;

					int rn = (int)(lumin + vn/0.877);
					int bn = (int)(lumin + un/0.493);
					int gn = (int)(1/0.587*lumin - 0.299/0.587*rn - 0.114/0.587*bn);
					
					// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

					rn = Math.max(0,Math.min(255,rn));
					gn = Math.max(0,Math.min(255,gn));
					bn = Math.max(0,Math.min(255,bn));

					
					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
				}
			}
		}
		
    } // CustomWindow inner class
} 
