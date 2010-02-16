package allrgb;

class ColorList
{
	private int[] colors = new int[100];
	private int size;

	void add(final int c)
	{
		if (size == colors.length)
		{
			final int[] tmp = new int[colors.length * 2];
			System.arraycopy(colors, 0, tmp, 0, colors.length);
			colors = tmp;
		}
		colors[size++] = c;
	}

	boolean isEmpty()
	{
		return size == 0;
	}

	final int[] aLab = new int[3];
	final int[] bLab = new int[3];

	double distanceSquared(final int a, final int b)
	{
		LabTable.INSTANCE.get(a, aLab);
		LabTable.INSTANCE.get(b, bLab);
		final double dr = aLab[0] - bLab[0];
		final double dg = aLab[1] - bLab[1];
		final double db = aLab[2] - bLab[2];
		return dr * dr + dg * dg + db * db;
	}

	int findAndRemoveClosest(final int target)
	{
		int bestIndex = -1;
		double bestDistanceSquared = Integer.MAX_VALUE;
		int numberSeen = 0;
		int index = 0;
		while (numberSeen < size)
		{
			while (index < colors.length && colors[index] == -1)
				index++;
			if (index >= colors.length)
				throw new IllegalStateException("How?");
			final double distanceSquared = distanceSquared(target, colors[index]);
			if (distanceSquared < bestDistanceSquared)
			{
				bestDistanceSquared = distanceSquared;
				bestIndex = index;
			}
			numberSeen++;
			index++;
		}
		int color = colors[bestIndex];
		colors[bestIndex] = -1;
		size--;
		return color;
	}
}