import ij.*;
import ij.io.*;
import ij.process.*;
import ij.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.filter.*;


public class GRDM_U4 implements PlugInFilter {

	protected ImagePlus imp;
	final static String[] choices = {"Wischen", "Weiche Blende", "Overlay 1", "Overlay 2", "Schieben", "Chroma-Key", "Star"};

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_RGB+STACK_REQUIRED;
	}
	
	public static void main(String args[]) {
		ImageJ ij = new ImageJ(); // neue ImageJ Instanz starten und anzeigen 
		ij.exitWhenQuitting(true);
		
		IJ.open("/Users/line/Documents/IMI/GDM/UE4/src/StackB.zip");
		
		GRDM_U4 sd = new GRDM_U4();
		sd.imp = IJ.getImage();
		ImageProcessor B_ip = sd.imp.getProcessor();
		sd.run(B_ip);
	}

	public void run(ImageProcessor B_ip) {
		// Film B wird uebergeben
		ImageStack stack_B = imp.getStack();
		
		int length = stack_B.getSize();
		int width  = B_ip.getWidth();
		int height = B_ip.getHeight();
		
		// ermoeglicht das Laden eines Bildes / Films
		Opener o = new Opener();
		/*OpenDialog od_A = new OpenDialog("Auswählen des 2. Filmes ...",  "");
				
		// Film A wird dazugeladen
		String dateiA = od_A.getFileName();
		if (dateiA == null) return; // Abbruch
		String pfadA = od_A.getDirectory();*/
		ImagePlus A = o.openImage("/Users/line/Documents/IMI/GDM/UE4/src","StackA.zip");
		if (A == null) return; // Abbruch

		ImageProcessor A_ip = A.getProcessor();
		ImageStack stack_A  = A.getStack();

		if (A_ip.getWidth() != width || A_ip.getHeight() != height)
		{
			IJ.showMessage("Fehler", "Bildgrößen passen nicht zusammen");
			return;
		}
		
		// Neuen Film (Stack) "Erg" mit der kleineren Laenge von beiden erzeugen
		length = Math.min(length,stack_A.getSize());

		ImagePlus Erg = NewImage.createRGBImage("Ergebnis", width, height, length, NewImage.FILL_BLACK);
		ImageStack stack_Erg  = Erg.getStack();

		// Dialog fuer Auswahl des Ueberlagerungsmodus
		GenericDialog gd = new GenericDialog("Überlagerung");
		gd.addChoice("Methode",choices,"");
		gd.showDialog();

		int methode = 0;		
		String s = gd.getNextChoice();
		if (s.equals("Wischen")) methode = 1;
		if (s.equals("Weiche Blende")) methode = 2;
		if (s.equals("Overlay 1")) methode = 3;
		if (s.equals("Overlay 2")) methode = 4;
		if (s.equals("Schieben")) methode = 5;
		if (s.equals("Chroma-Key")) methode = 6;
		if (s.equals("Star")) methode = 7;

		// Arrays fuer die einzelnen Bilder
		int[] pixels_B;
		int[] pixels_A;
		int[] pixels_Erg;

		// Schleife ueber alle Bilder
		for (int z=1; z<=length; z++)
		{
			pixels_B   = (int[]) stack_B.getPixels(z);
			pixels_A   = (int[]) stack_A.getPixels(z);
			pixels_Erg = (int[]) stack_Erg.getPixels(z);

			double idk162=Math.toRadians(162.0);
			double idk36 = Math.toRadians(36.0);
			double idk72 = Math.toRadians(72.0);
			int xm=width/2;
			int ym=height/2;

			int pos = 0;
			for (int y=0; y<height; y++)
				for (int x=0; x<width; x++, pos++)
				{
					int cA = pixels_A[pos];
					int rA = (cA & 0xff0000) >> 16;
					int gA = (cA & 0x00ff00) >> 8;
					int bA = (cA & 0x0000ff);

					int cB = pixels_B[pos];
					int rB = (cB & 0xff0000) >> 16;
					int gB = (cB & 0x00ff00) >> 8;
					int bB = (cB & 0x0000ff);

					if (methode == 1)
					{
					if (y+1 > (z-1)*(double)height/(length-1))
						pixels_Erg[pos] = pixels_B[pos];
					else
						pixels_Erg[pos] = pixels_A[pos];
					}


					if (methode == 2)
					{
					double quotient = z/(double)length;
					int r = (int)((1-quotient)*rB+quotient*rA);
					int g = (int)((1-quotient)*gB+quotient*gA);
					int b = (int)((1-quotient)*bB+quotient*bA);

					pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + ( b & 0xff);
					}

					if (methode == 3)
					{
						int r=rB;
						int g=gB;
						int b=bB;

						if(rB<=128){
							r=rB*rA/128;
						}else{
							r=255-((255-rA)*(255-rB))/128;
						}
						if(gB<=128){
							g=gB*gA/128;
						}else{
							g=255-((255-gA)*(255-gB))/128;
						}
						if(bB<=128){
							b=bB*bA/128;
						}else{
							b=255-((255-bA)*(255-bB))/128;
						}

						pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + ( b & 0xff);
					}

					if (methode == 4)
					{
						int r=rB;
						int g=gB;
						int b=bB;

						if(rA<=128){
							r = (rA * rB) / 128;
						}else{
							r = 255 - (((255 - rA) * (255 - rB)) / 128);
						}
						if(gA<=128){
							g = (gA * gB) / 128;
						}else{
							g = 255 - (((255 - gA) * (255 - gB)) / 128);
						}
						if(bA<=128){
							b = (bA * bB) / 128;
						}else{
							b = 255 - (((255 - bA) * (255 - bB)) / 128);
						}

						pixels_Erg[pos] = 0xFF000000 + ((r & 0xff) << 16) + ((g & 0xff) << 8) + ( b & 0xff);
					}

					if (methode == 5)
					{
						double quotient = (z-1)/(double)(length-1);
						if (y>=quotient*height) {
							int ynew = y - (int)(height*quotient);
							int posnew = ynew*width + x;
							pixels_Erg[pos] = pixels_B[posnew];
						}
						else {
							int ynew = y + (int)(height*(1-quotient));
							int posnew = ynew*width + x;
							pixels_Erg[pos] = pixels_A[posnew];
						}
					}

					if (methode == 6)
					{

						int distancelight = (int)Math.sqrt(Math.pow(rA-255,2)+Math.pow(gA-165,2)+Math.pow(bA,2));
						int distancedark = (int)Math.sqrt(Math.pow(rA-127,2)+Math.pow(gA-82,2)+Math.pow(bA-15,2));
						if (distancelight < 150  || distancedark<50)
							pixels_Erg[pos] = pixels_B[pos];
						else
							pixels_Erg[pos] = pixels_A[pos];


						// 255 165 0

					}

					if (methode == 7)
					{
						pixels_Erg[pos] = pixels_B[pos];


						double radius= 3/2.0*width * (z-1)/(double)(length-1);

						if(z==1){
							continue;
						}




						if(y<=(Math.tan(idk36)*x+(ym - radius*Math.tan(idk162)/Math.cos(idk162)-Math.tan(idk36)*((xm)+(radius/Math.cos(idk162)))))
								&& y>=-Math.tan(idk72)*x + ym - radius + Math.tan(idk72)*xm
								&& y>= Math.tan(idk72)*x + ym - radius - Math.tan(idk72)*xm){
							pixels_Erg[pos] = pixels_A[pos];
						}

						if(y>(ym - radius*Math.tan(idk162)/Math.cos(idk162))
								&& y<=(Math.tan(idk36)*x+(ym - radius*Math.tan(idk162)/Math.cos(idk162)-Math.tan(idk36)*((xm)+(radius/Math.cos(idk162)))))
								&& y<=(-Math.tan(idk36)*x+(ym - radius*Math.tan(idk162)/Math.cos(idk162) + Math.tan(idk36)*((xm)- radius/Math.cos(idk162))))){
							pixels_Erg[pos] = pixels_A[pos];
						}

						if(y>(ym - radius*Math.tan(idk162)/Math.cos(idk162))
								&& y>=-Math.tan(idk72)*x + ym - radius + Math.tan(idk72)*xm
								&& y<=(-Math.tan(idk36)*x+(ym - radius*Math.tan(idk162)/Math.cos(idk162) + Math.tan(idk36)*((xm)- radius/Math.cos(idk162))))){
							pixels_Erg[pos] = pixels_A[pos];
						}

					}

				}
		}

		// neues Bild anzeigen
		Erg.show();
		Erg.updateAndDraw();

	}

}

