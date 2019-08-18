import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

//erste Uebung (elementare Bilderzeugung)

public class GLDM_U1_S0569735 implements PlugIn {
	
	final static String[] choices = {
		"Schwarzes Bild",
		"Gelbes Bild",
		"Schwarz Weiss Verlauf",
		"Horiz. Schwarz/Rot vert. Schwarz/Blau Verlauf",
		"Französische Fahne",
		"Tschechische Fahne",
		"Bangladeschs Fahne",
			"EU Flagge",
			"Klausur"
	};
	
	private String choice;
	
	public static void main(String args[]) {
		ImageJ ij = new ImageJ(); // neue ImageJ Instanz starten und anzeigen 
		ij.exitWhenQuitting(true);
		
		GLDM_U1_S0569735 imageGeneration = new GLDM_U1_S0569735();
		imageGeneration.run("");
	}
	
	public void run(String arg) {
		
		int width  = 600;  // Breite
		int height = 400;  // Hoehe
		
		// RGB-Bild erzeugen
		ImagePlus imagePlus = NewImage.createRGBImage("GLDM_U1_S0569735", width, height, 1, NewImage.FILL_BLACK);
		ImageProcessor ip = imagePlus.getProcessor();
		
		// Arrays fuer den Zugriff auf die Pixelwerte
		int[] pixels = (int[])ip.getPixels();
		
		dialog();
		
		////////////////////////////////////////////////////////////////
		// Hier bitte Ihre Aenderungen / Erweiterungen
		
		if ( choice.equals("Schwarzes Bild") ) {
			generateBlackImage(width, height, pixels);
		}
		if ( choice.equals("Gelbes Bild") ) {
			generateYellowImage(width, height, pixels);
		}
		if ( choice.equals("Französische Fahne") ) {
			generateFrance(width, height, pixels);
		}
		if ( choice.equals("Schwarz Weiss Verlauf") ) {
			generateBWB(width, height, pixels);
		}
		if ( choice.equals("Horiz. Schwarz/Rot vert. Schwarz/Blau Verlauf") ) {
			generateBlackRedBlue(width, height, pixels);
		}
		if ( choice.equals("Tschechische Fahne") ) {
			generateTschech(width, height, pixels);
		}
		if ( choice.equals("Bangladeschs Fahne") ) {
			generateBangla(width, height, pixels);
		}
		if ( choice.equals("EU Flagge") ) {
			generateEU(width, height, pixels);
		}
		if ( choice.equals("Klausur") ) {
			generateKlausur(width, height, pixels);
		}
		////////////////////////////////////////////////////////////////////
		
		// neues Bild anzeigen
		imagePlus.show();
		imagePlus.updateAndDraw();
	}

	private void generateBlackImage(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte
		for (int y=0; y<height; y++) {
			// Schleife ueber die x-Werte
			for (int x=0; x<width; x++) {
				int pos = y*width + x; // Arrayposition bestimmen
				
				int r = 0;
				int g = 0;
				int b = 0;
				
				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
			}
		}
	}


	private void generateYellowImage(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte
		for (int y=0; y<height; y++) {
			// Schleife ueber die x-Werte
			for (int x=0; x<width; x++) {
				int pos = y*width + x; // Arrayposition bestimmen

				int r = 255;
				int g = 255;
				int b = 0;

				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
			}
		}
	}

	private void generateFrance(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte
		for (int y=0; y<height; y++) {
			// Schleife ueber die x-Werte
			for (int x=0; x<width; x++) {
				int pos = y*width + x; // Arrayposition bestimmen
				int r;
				int g;
				int b;

				if(x <width/3) {
					r = 0;
					g = 0;
					b = 255;
				}

				else if(x >=width/3 && x<2*width/3) {
					r = 255;
					g = 255;
					b = 255;
				}

				else{
					r = 255;
					g = 0;
					b = 0;
				}
				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
			}

		}
	}

	private void generateBWB(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte
		for (int y=0; y<height; y++) {
			// Schleife ueber die x-Werte
			for (int x=0; x<width; x++) {
				int pos = y*width + x; // Arrayposition bestimmen
				int r;
				int g;
				int b;
				if(x< width/2) {
					r = 2 * x * 255 / width;
					g = 2 * x * 255 / width;
					b = 2 * x * 255 / width;
				}
				else{
					r = 2 * (width-x) * 255 / width;
					g = 2 * (width-x) * 255 / width;
					b = 2 * (width-x) * 255 / width;
				}
				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
			}
		}
	}

	private void generateBlackRedBlue(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte
		for (int y=0; y<height; y++) {
			// Schleife ueber die x-Werte
			for (int x=0; x<width; x++) {
				int pos = y*width + x; // Arrayposition bestimmen
				int r;
				int g;
				int b;
				r = x*255/width;
				g = 0;
				b = y*255/height;

				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
			}
		}
	}

	private void generateTschech(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte
		for (int y=0; y<height; y++) {
			// Schleife ueber die x-Werte
			for (int x=0; x<width; x++) {
				int pos = y*width + x; // Arrayposition bestimmen
				int r=255;
				int g=255;
				int b=255;

				if(y>=height/2){
					r = 255;
					g = 0;
					b = 0;
				}
				if( x< width/2 && y> height*x/width && y< (width-x)*height/width){
					r = 0;
					g = 0;
					b = 255;
				}

				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
			}
		}
	}

	private void generateBangla(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte
		for (int y=0; y<height; y++) {
			// Schleife ueber die x-Werte
			for (int x=0; x<width; x++) {
				int pos = y*width + x; // Arrayposition bestimmen
				int r=0;
				int g=106;
				int b=78;

				if((x - 9*width/20)*(x - 9*width/20) + (y-height/2)*(y-height/2)  <= height*height/9){
					r = 244;
					g = 42;
					b = 65;
				}
				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
			}
		}
	}

	private void generateEU(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte
		for (int y=0; y<height; y++) {
			// Schleife ueber die x-Werte
			for (int x=0; x<width; x++) {
				int pos = y*width + x; // Arrayposition bestimmen
				int r=0;
				int g=51;
				int b=153;

				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
			}
		}
		for(int i =0; i<360;i+=30){
			double ir= Math.toRadians(i);
			generateStar(width,height, width/2 - height*Math.cos(ir)/3 ,height/2 + Math.sin(ir)*height/3, pixels);
		}

	}

	private void generateStar(int width, int height, double xm, double ym, int[] pixels) {
		double idk162=Math.toRadians(162.0);
		double idk36 = Math.toRadians(36.0);
		double idk72 = Math.toRadians(72.0);
		// Schleife ueber die y-Werte
		for (int y=0; y<height; y++) {
			// Schleife ueber die x-Werte
			for (int x=0; x<width; x++) {
				int pos = y*width + x; // Arrayposition bestimmen
				int r;
				int g;
				int b;

				if(y<=(Math.tan(idk36)*x+(ym - Math.tan(idk162)*height/(18*Math.cos(idk162))-Math.tan(idk36)*((xm)+(height/(18*Math.cos(idk162))))))
					&& y>=-Math.tan(idk72)*x + ym - height/18 + Math.tan(idk72)*xm
					&& y>= Math.tan(idk72)*x + ym - height/18 - Math.tan(idk72)*xm){
					r = 255;
					g = 204;
					b = 0;
					pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
				}

				if(y>(ym - Math.tan(idk162)*height/(18*Math.cos(idk162)))
					&& y<=(Math.tan(idk36)*x+(ym - Math.tan(idk162)*height/(18*Math.cos(idk162))-Math.tan(idk36)*((xm)+(height/(18*Math.cos(idk162))))))
					&& y<=(-Math.tan(idk36)*x+(ym - Math.tan(idk162)*height/(18*Math.cos(idk162)) + Math.tan(idk36)*((xm)- (height/(18*Math.cos(idk162))))))){
					r = 255;
					g = 204;
					b = 0;
					pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
				}

				if(y>(ym - Math.tan(idk162)*height/(18*Math.cos(idk162)))
						&& y>=-Math.tan(idk72)*x + ym - height/18 + Math.tan(idk72)*xm
						&& y<=(-Math.tan(idk36)*x+(ym - Math.tan(idk162)*height/(18*Math.cos(idk162)) + Math.tan(idk36)*((xm)- (height/(18*Math.cos(idk162))))))){
					r = 255;
					g = 204;
					b = 0;
					pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
				}

			}
		}
	}

	private void generateKlausur(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte
		for (int y=0; y<height; y++) {
			// Schleife ueber die x-Werte
			for (int x=0; x<width; x++) {
				int pos = y*width + x; // Arrayposition bestimmen
				int r=255*(width-1-x)/(width-1);
				int g=255*y/(height-1);
				int b=0;

				pixels[pos] = (0x80 <<24) | (r << 16) | (g << 8) |  b;
			}
		}


	}


	private void dialog() {
		// Dialog fuer Auswahl der Bilderzeugung
		GenericDialog gd = new GenericDialog("Bildart");
		
		gd.addChoice("Bildtyp", choices, choices[0]);
		
		
		gd.showDialog();	// generiere Eingabefenster
		
		choice = gd.getNextChoice(); // Auswahl uebernehmen
		
		if (gd.wasCanceled())
			System.exit(0);
	}
}

