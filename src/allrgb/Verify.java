package allrgb;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.BitSet;

import javax.imageio.ImageIO;

public class Verify
{
	public static void main(final String[] args) throws Exception
	{
		final BitSet colors = new BitSet(0xFFFFFF + 1);
		final BufferedImage img = ImageIO.read(new File("out.tif"));
		final int height = img.getHeight();
		final int width = img.getWidth();
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
				colors.set(0xFFFFFF & img.getRGB(x, y));
		System.err.println(colors.cardinality() == 0x1000000);
	}
}
