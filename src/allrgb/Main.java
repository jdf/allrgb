package allrgb;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

import javax.imageio.ImageIO;

public class Main
{

	static int clamp(final int b)
	{
		if (b < 0)
			return 0;
		if (b > 255)
			return 255;
		return b;
	}

	static void addError(final BufferedImage img, final int x, final int y,
			final int redError, final int greenError, final int blueError,
			final int multiplier)
	{
		final int rgb = 0xFFFFFF & img.getRGB(x, y);
		final int srcRed = (rgb & 0xFF0000) >> 16;
		final int srcGreen = (rgb & 0xFF00) >> 8;
		final int srcBlue = rgb & 0xFF;
		final int r = clamp(srcRed + ((multiplier * redError) >> 4));
		final int g = clamp(srcGreen + ((multiplier * greenError) >> 4));
		final int b = clamp(srcBlue + ((multiplier * blueError) >> 4));
		img.setRGB(x, y, ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF) << 0));
	}

	public static void main(final String[] args) throws Exception
	{
		final boolean dither = true;

		System.err.println("Constructing index");
		final ColorIndex index = new LabIndex1();

		System.err.println("Reading src image");
		final BufferedImage src = ImageIO.read(new File("sts-107.jpg"));

		//		System.err.println("Reading src image");
		//		final BufferedImage src = new BufferedImage(4096, 4096,
		//				BufferedImage.TYPE_INT_RGB);
		//		final Graphics g = src.getGraphics();
		//		g.setColor(Color.WHITE);
		//		g.fillRect(0, 0, 2048, 4096);
		//		g.setColor(Color.BLACK);
		//		g.fillRect(2048, 0, 2048, 4096);
		//		g.dispose();
		//		ImageIO.write(src, "png", new File("src.png"));

		System.err.println("Allocating pixels");
		final int width = src.getWidth();
		final int height = src.getHeight();
		int[] pixels = new int[width * height];
		for (int i = 0; i < pixels.length; i++)
			pixels[i] = i;
		System.err.println("Shuffling pixels");
		final Random random = new Random();
		for (int i = 0; i < pixels.length; i++)
		{
			int n = random.nextInt(pixels.length);
			int tmp = pixels[n];
			pixels[n] = pixels[i];
			pixels[i] = tmp;
		}
		System.err.println("Creating destination image");
		final BufferedImage dest = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);

		int counter = 0;
		for (final int pixel : pixels)
		{
			final int x = pixel % height;
			final int y = pixel / width;
			++counter;
			if (counter % 100000 == 0)
				System.err.println(counter);

			if (counter % 5000000 == 0)
			{
				final File debug = new File("out-" + counter + ".png");
				System.err.println("Writing " + debug);
				ImageIO.write(dest, "png", debug);
			}

			final int rgb = 0xFFFFFF & src.getRGB(x, y);
			final int matched = index.lookup(rgb);
			dest.setRGB(x, y, matched);

			if (dither)
			{
				final int srcRed = (rgb & 0xFF0000) >> 16;
				final int redError = srcRed - ((matched & 0xFF0000) >> 16);
				final int srcGreen = (rgb & 0xFF00) >> 8;
				final int greenError = srcGreen - ((matched & 0xFF00) >> 8);
				final int srcBlue = rgb & 0xFF;
				final int blueError = srcBlue - (matched & 0xFF);
				if (x < width - 1)
					addError(src, x + 1, y, redError, greenError, blueError, 7);
				if (x > 0 && y < height - 1)
					addError(src, x - 1, y + 1, redError, greenError, blueError, 3);
				if (y < height - 1)
					addError(src, x, y + 1, redError, greenError, blueError, 5);
				if (x < width - 1 && y < height - 1)
					addError(src, x + 1, y + 1, redError, greenError, blueError, 1);
			}
		}
		System.err.println("Writing destination image");
		ImageIO.write(dest, "TIFF", new File("out.tif"));
	}
}
