package br.ufes.inf.lprm.msplayer.image;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class RGBConverter{
	
	public static BufferedImage to(BufferedImage image, RGB rgb) {
        //get width and height
        int width = image.getWidth();
        int height = image.getHeight();
        
        //convert to red image
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                int p = image.getRGB(x,y);
                image.setRGB(x, y, rgb.to(p));
            }
        }
        return image;
	}	
	
    public static void main(String args[])throws IOException{
        BufferedImage img = null;
        File f = null;
        
        //read image
        try{
            f = new File("/home/jordano/Downloads/google-recebera-multa-recorde-de-rr-3-bilhoes-na-europa-diz-jornal-britanico.jpg.png");
            img = ImageIO.read(f);
        }catch(IOException e){
            System.out.println(e);
        }
        
        img = to(img, RGB.RED);
        
        //write image
        try{
            f = new File("/home/jordano/Downloads/google.png");
            ImageIO.write(img, "png", f);
        }catch(IOException e){
            System.out.println(e);
        }
    }//main() ends here
}//class ends here