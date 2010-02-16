package allrgb;

public class LabTable
{
	public static final LabTable INSTANCE = new LabTable();

	private static final float K = 24389f / 27f;
	private static final float EPS = 216f / 24389f;
	private static final float REFERENCE_WHITE_Z = 0.825211f;
	private static final float REFERENCE_WHITE_Y = 1.0f;
	private static final float REFERENCE_WHITE_X = 0.964221f;

	private final short[] table = new short[0x3000000];

	private LabTable()
	{
		System.err.println("Generating Lab table");
		for (int i = 0; i <= 0xFFFFFF; i++)
		{
			if (i % 100000 == 0 && i > 0)
				System.err.print(".");
			if (i % 1000000 == 0 && i > 0)
				System.err.println();
			genlab(i);
		}
		System.err.println("Done");
	}

	public int[] get(final int rgb, final int[] result)
	{
		int labindex = rgb * 3;
		result[0] = table[labindex++];
		result[1] = table[labindex++];
		result[2] = table[labindex++];
		return result;
	}

	private void genlab(final int rgb)
	{
		// RGB to XYZ
		float r = ((rgb & 0xFF0000) >> 16) / 255f; //R 0..1
		float g = ((rgb & 0xFF00) >> 8) / 255f; //G 0..1
		float b = (rgb & 0xFF) / 255f; //B 0..1

		// assuming sRGB (D65)
		if (r <= 0.04045)
			r = r / 12;
		else
			r = (float) Math.pow((r + 0.055) / 1.055, 2.4);

		if (g <= 0.04045)
			g = g / 12;
		else
			g = (float) Math.pow((g + 0.055) / 1.055, 2.4);

		if (b <= 0.04045)
			b = b / 12;
		else
			b = (float) Math.pow((b + 0.055) / 1.055, 2.4);

		final float X = 0.436052025f * r + 0.385081593f * g + 0.143087414f * b;
		final float Y = 0.222491598f * r + 0.71688606f * g + 0.060621486f * b;
		final float Z = 0.013929122f * r + 0.097097002f * g + 0.71418547f * b;

		// XYZ to Lab
		final float xr = X / REFERENCE_WHITE_X;
		final float yr = Y / REFERENCE_WHITE_Y;
		final float zr = Z / REFERENCE_WHITE_Z;

		final float fx, fy, fz;
		if (xr > EPS)
			fx = (float) Math.pow(xr, 1 / 3.);
		else
			fx = (float) ((K * xr + 16.) / 116.);

		if (yr > EPS)
			fy = (float) Math.pow(yr, 1 / 3.);
		else
			fy = (float) ((K * yr + 16.) / 116.);

		if (zr > EPS)
			fz = (float) Math.pow(zr, 1 / 3.);
		else
			fz = (float) ((K * zr + 16.) / 116);

		final float Ls = (116 * fy) - 16;
		final float as = 500 * (fx - fy);
		final float bs = 200 * (fy - fz);

		int labindex = rgb * 3;
		table[labindex++] = (short) (2.55 * Ls + .5);
		table[labindex++] = (short) ((as + .5) + 78);
		table[labindex++] = (short) ((bs + .5) + 111);
	}

	public static void main(final String[] args)
	{
		final LabTable labtab = new LabTable();
		final int[] min = new int[3];
		final int[] max = new int[3];
		final int[] lab = new int[3];

		for (int i = 0; i <= 0xFFFFFF; i++)
		{
			labtab.get(i, lab);
			if (lab[0] < min[0])
				min[0] = lab[0];
			if (lab[1] < min[1])
				min[1] = lab[1];
			if (lab[2] < min[2])
				min[2] = lab[2];
			if (lab[0] > max[0])
				max[0] = lab[0];
			if (lab[1] > max[1])
				max[1] = lab[1];
			if (lab[2] > max[2])
				max[2] = lab[2];
		}
		System.out.println();
		System.out.println("L: " + min[0] + "," + max[0]);
		System.out.println("a: " + min[1] + "," + max[1]);
		System.out.println("b: " + min[2] + "," + max[2]);
	}
}
