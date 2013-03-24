/*
 * A utility class for loading, saving, and converting image formats
 * Matt McDaniel
 * Spring 2012
 */


import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.PixelGrabber;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFrame;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/* A class with various image utility functions
 * such as conversion between various formats
 */
public class imageutil {

	

	
	//loads an image from a file into BufferedImage
	//yes, it is loading into a regular image and then drawing to a buffered image
	//yes, that is stupid, but there doesn't seem to exist a better way to do it since ImageIO.read doesn't work reliably
	public static BufferedImage loadimage(String imagename){
		try{
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Image image = toolkit.getImage(imagename);
			MediaTracker mediaTracker = new MediaTracker(new JFrame());
			mediaTracker.addImage(image, 0);
			mediaTracker.waitForID(0);

			BufferedImage buf = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
			Graphics2D bufImageGraphics = buf.createGraphics();
			bufImageGraphics.drawImage(image, 0, 0, null);

			return buf ;
		}catch(Exception e){
			System.out.println("Image Load Failed: " + e ) ;
			e.printStackTrace() ;
		}

		return null ;
	}
	
	

	//returns a measure of the brightness of an rgb pixel
	//there are several candidates for this function, it's largely arbitrary
	//note that the range should match the expected integers in the gsolve function
	public static int luminance(int c[]){
		return (c[0] *2 + c[1] * 3 + c[2])/6  ;
	}
	
	public static int luminance(byte c[]){
		return ((c[0]&0xff) *2 + (c[1]&0xff) * 3 + (c[2]&0xff))/6  ;
	}
	
	public static double luminance(double c[]){
		return (c[0] *2 + c[1] * 3 + c[2])/6  ;
	}
	
	
	
	//converts a bufferedimage image to 3 integer arrays
	//first index is x, then y, then channel
	public static byte[][][] convertimage(BufferedImage img) {
		int w= img.getWidth(null), h = img.getHeight(null) ;
		int[] pixels = new int[ w*h ];
		PixelGrabber pg = new PixelGrabber(img, 0, 0, w, h, pixels, 0, w);
		try {
		    pg.grabPixels();
		} catch (InterruptedException e) {
		    System.err.println("interrupted waiting for pixels!");
		}
		byte data[][][] = new byte[w][h][3];
		for(int x=0;x<w;x++){
			for(int y=0;y<h;y++){
				int k = x+y*w;
				data[x][y][0] =  (byte)((pixels[k]>>16)&0xff) ;
				data[x][y][1] =  (byte)((pixels[k]>>8)&0xff) ;
				data[x][y][2] =  (byte)((pixels[k])&0xff) ;
			}
		}
		
		return data;
	}
	
	//converts a set of 3 integer arrays back ito a bufferedimage
	public static BufferedImage convertimage(int i[][][]){
		int w = i.length,h=i[0].length ;
		int p[] = new int[w*h] ;
		for(int x=0;x<w;x++){
			for(int y=0;y<h;y++){
				p[x+y*w] = (i[x][y][0]&0xff)<<16 | (i[x][y][1]&0xff) << 8 | (i[x][y][2]&0xff) ;
				
			}
		}
		return convertpixelstoimage(p,w);
	}
	
	//converts a set of 3 byte arrays back into a bufferedimage
	public static BufferedImage convertimage(byte i[][][]){
		int w = i.length,h=i[0].length ;
		int p[] = new int[w*h] ;
		for(int x=0;x<w;x++){
			for(int y=0;y<h;y++){
				p[x+y*w] = (i[x][y][0]&0xff)<<16 | (i[x][y][1]&0xff) << 8 | (i[x][y][2]&0xff) ;
				
			}
		}
		return convertpixelstoimage(p,w);
	}
	
	//takes an ARGB int array and converts to a buffered image
	public static BufferedImage convertpixelstoimage(int p[], int w){
		BufferedImage image = new BufferedImage(w, p.length/w, BufferedImage.TYPE_INT_RGB);
		DataBufferInt databuffer = (DataBufferInt) image.getRaster().getDataBuffer();
		int[] ipix = databuffer.getData();
		for(int k=0;k<p.length;k++){
			ipix[k] = p[k] ;
		}
		return image ;
	}
	
	
	//returns a one dimensional gaussian kernel
	public static double[] gaussian(int radius){
		double g[] = new double[radius*2+1] ;
		double theta = radius/3.0 ;
		double oneovertwothetasquared = 1/(2*theta*theta);
		double a = 1/ (theta * Math.sqrt(2*Math.PI) );
		for(int x=-radius;x<=radius;x++){
			g[x+radius] = a*Math.exp(-oneovertwothetasquared*x*x) ;
			
		}
		return g ;
	}
	
	//aplies a one dimensional kernel in both directions
	public static float[][][] applykernel(float[][][] i, double kernel[]){
		float[][][] i2 = new float[i.length][i[0].length][i[0][0].length];
		int radius = (kernel.length - 1 )/2 ;
		for(int x=radius;x<i.length-radius;x++){
			for(int y=radius;y<i[0].length-radius;y++){
				for(int c = 0 ; c < i[0][0].length; c++){
					double v = 0;
					for(int k=-radius;k<=radius;k++){
						//for(int j=-radius;j<=radius;j++){
							
								v+=i[x+k][y][c]*kernel[k+radius];
						//}
					}
					i2[x][y][c] = (float)v ;
				}
			}
		
		}
		
		float[][][] i3 = new float[i.length][i[0].length][i[0][0].length];
		for(int x=radius;x<i.length-radius;x++){
			for(int y=radius;y<i[0].length-radius;y++){
				for(int c = 0 ; c < i[0][0].length; c++){
					double v = 0;
					//for(int k=-radius;k<=radius;k++){
						for(int j=-radius;j<=radius;j++){
							
								v+=i2[x][y+j][c]*kernel[j+radius];
						}
					//}
					i3[x][y][c] = (float)v ;
				}
			}
		
		}
		
		return i3 ;
	}
	
	
	//aplies a one dimensional kernel in both directions
	public static float[][] applykernel(float[][] i, double kernel[]){
		float[][] i2 = new float[i.length][i[0].length];
		int radius = (kernel.length - 1 )/2 ;
		for(int x=radius;x<i.length-radius;x++){
			for(int y=radius;y<i[0].length-radius;y++){
				double v = 0;
					for(int k=-radius;k<=radius;k++){
						//for(int j=-radius;j<=radius;j++){
							
								v+=i[x+k][y]*kernel[k+radius];
						//}
					}
					i2[x][y] = (float)v ;
				
			}
		
		}
		
		float[][] i3 = new float[i.length][i[0].length];
		for(int x=radius;x<i.length-radius;x++){
			for(int y=radius;y<i[0].length-radius;y++){
					double v = 0;
					//for(int k=-radius;k<=radius;k++){
						for(int j=-radius;j<=radius;j++){
							
								v+=i2[x][y+j]*kernel[j+radius];
						}
					//}
					i3[x][y] = (float)v ;
			}
		
		}
		
		return i3 ;
	}
	
	
	//Uses bilinear interpolation scale an image to the given resolution
	public static byte[][][] scaleto(byte i[][][], int w, int h){
		byte i2[][][] = new byte[w][h][3] ;
		int iw = i.length, ih = i[0].length ;
		double xd,yd ;
		int x2,y2 ;
		for(int x =0; x < w; x++){
			for(int y = 0 ;y < h;y++){
				xd = (iw*x)/(double)w ;//map this point into the old image
				yd = (ih*y)/(double)h ;//map this point into the old image
				x2 = (int)xd ;
				y2 = (int)yd ;
				if(x2 + 1 < iw && y2+ 1 < ih){//if not on edge do subpixel scaling
					double t = xd - x2 ;
					double s = yd - y2 ;
					
					double a = (1-t)*(1-s), b = t*(1-s), c = (1-t)*s, d = t*s ;
					
					i2[x][y][0] = (byte)(a*(i[x2][y2][0]&0xff)+b*(i[x2+1][y2][0]&0xff) + c*(i[x2][y2+1][0]&0xff) + d*(i[x2+1][y2+1][0]&0xff)) ;
					i2[x][y][1] = (byte)(a*(i[x2][y2][1]&0xff)+b*(i[x2+1][y2][1]&0xff) + c*(i[x2][y2+1][1]&0xff) + d*(i[x2+1][y2+1][1]&0xff)) ;
					i2[x][y][2] = (byte)(a*(i[x2][y2][2]&0xff)+b*(i[x2+1][y2][2]&0xff) + c*(i[x2][y2+1][2]&0xff) + d*(i[x2+1][y2+1][2]&0xff)) ;
					
				}else if(x2 >= 0 && y2>=0){
				
					i2[x][y][0] = i[x2][y2][0] ;
					i2[x][y][1] = i[x2][y2][1] ;
					i2[x][y][2] = i[x2][y2][2] ;
				}
			}
		}
		return i2 ;
		
	}
	
	//averages sets of four pixels to perfectly scale an image to halfsize
	public static byte[][][] halfsize(byte image[][][]){
		byte i2[][][] = new byte[image.length/2][image[0].length/2][3] ;
		for(int x =0; x < image.length-1; x+=2){
			for(int y = 0 ;y < image[0].length-1;y+=2){
				for(int c = 0 ; c < 3 ; c++){
					i2[x/2][y/2][c] = (byte)(((image[x][y][c]&0xff) + (image[x+1][y][c]&0xff) +  (image[x][y+1][c]&0xff) +  (image[x+1][y+1][c]&0xff) ) *0.25) ;
				}
			}
		}
		return i2 ;
	}
	
	//returns an image with the same color as the input images
	//but with the brightness of each pixel scaled to match the luminance given
	public static byte[][][] setluminance(byte[][][] image, float luminance[][]){
		int iw = image.length, ih = image[0].length ;
		byte i2[][][] = new byte[iw][ih][3] ;
		for(int x =0; x < iw; x++){
			for(int y = 0 ;y < ih;y++){
				double l = luminance(image[x][y]) ;
				if( l > 5 ){
					double scale = luminance[x][y]/l ;
					
					i2[x][y][0] = (byte)Math.min(Math.max((image[x][y][0]&0xff)*scale,0),255) ;
					i2[x][y][1] = (byte)Math.min(Math.max((image[x][y][1]&0xff)*scale,0),255) ;
					i2[x][y][2] = (byte)Math.min(Math.max((image[x][y][2]&0xff)*scale,0),255) ;
				}
					
			}
		}
		return i2 ;
	}
	
	
	
	//returns the average value of an axis aligned bounding box on this texture
	//uses no interpolation (see video modeling 2 / thumbnailmaker.texture for additional related methods and details)
	//AABB is an axis aligned bounding box of the form{ minx,miny,maxx,maxy}
	public static double sample(double AABB[], float i[][]){
		int width = i.length ;
		int height = i[0].length ;
		int minx = (int)(AABB[0]) ;
		int maxx = (int)(AABB[2]+1) ;
		int miny = (int)(AABB[1]) ;
		int maxy = (int)(AABB[3]+1) ;
		//make sure AABB doesn't try to read outside of image
		if(minx<0)minx = 0 ;
		if(miny<0)miny = 0;
		if(maxx>width)maxx = width ;
		if(maxy>height)maxy = height ;
		
		//area*value
		double valuearea = 0 ;
		double area =0;
		for(int x=minx;x<maxx;x++){
			for(int y=miny;y<maxy;y++){
				double[] texel = new double[]{x,y,x+1,y+1};//the AABB of this pixel
				double intersect[] = AABBintersect(texel,AABB) ;
				if(intersect[2] > intersect[0] && intersect[3] > intersect[1]){//if the AABBs intersect
					double intersectarea = (intersect[2]-intersect[0])*(intersect[3]-intersect[1]);
					if(intersectarea>0.000001){
						area += intersectarea ;
						valuearea+= i[x][y]*intersectarea ;//sum up values weighted by intersection area
					}
				}
			}
		}
		return valuearea/area;
				
		
	}
	
	
	//returns an AABB representing the intersection of two AABBs 
	//where AABBs given as xmin,ymin,xmax,ymax
	public static double[] AABBintersect(double[] a, double[] b){
		return new double[]{Math.max(a[0], b[0]),Math.max(a[1], b[1]), Math.min(a[2], b[2]),Math.min(a[3], b[3])};
	}
	
	
	//Saves a buffered image to disk as jpeg with the given percentage quality
	public static void saveImage(BufferedImage bi, File file, int quality)
    {
       
        
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try
        {
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(bos);
            JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bi);
            quality = Math.max(0, Math.min(quality, 100));
            param.setQuality((float) quality / 100.0f, false);
            encoder.setJPEGEncodeParam(param);
            encoder.encode(bi);
        }
        catch (FileNotFoundException fnfe)
        {
            fnfe.printStackTrace();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        finally
        {
            if (bos != null)
            {
                try
                {
                    bos.close();
                }
                catch (IOException ioe)
                {
                    ioe.printStackTrace();
                }
            }
            if (fos != null)
            {
                try
                {
                    fos.close();
                }
                catch (IOException ioe)
                {
                    ioe.printStackTrace();
                }
            }
        }
    }
	
	

	
}
