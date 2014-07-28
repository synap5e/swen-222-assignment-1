package cluedo.util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class BoardLoader {

	public static void main(String[] args) throws IOException{
		BufferedImage i = ImageIO.read(new File("./boards/clue_layout.png"));
		BufferedImage bi = new BufferedImage(i.getWidth(), i.getHeight(), BufferedImage.TYPE_INT_RGB);
		bi.setData(i.getData());

		final int[] data = ( (DataBufferInt) bi.getRaster().getDataBuffer() ).getData();



		for (int x=0;x<i.getWidth();x++){
			for(int y=0;y<i.getHeight();y++){
				System.out.println(data[x+y*i.getWidth()]);
			}

		}
	}

}
