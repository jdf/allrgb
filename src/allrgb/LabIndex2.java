/**
 * 
 */
package allrgb;

class LabIndex2 implements ColorIndex
{
	final ColorList[] colors = new ColorList[256];
	final int[] lab = new int[3];

	public LabIndex2()
	{
		for (int c = 0; c <= 0xFFFFFF; c++)
		{
			LabTable.INSTANCE.get(c, lab);
			final int lightness = lab[0];
			if (colors[lightness] == null)
				colors[lightness] = new ColorList();
			colors[lightness].add(c);
		}
	}

	public int lookup(final int src)
	{
		LabTable.INSTANCE.get(src, lab);
		final int startL = lab[0];
		int delta = 0;
		int br = startL;
		ColorList target = colors[startL];
		while (target == null)
		{
			delta++;
			br = startL + delta;
			if (br >= 0 && br < colors.length && colors[br] != null)
			{
				target = colors[br];
				break;
			}
			br = startL - delta;
			if (br >= 0 && br < colors.length && colors[br] != null)
			{
				target = colors[br];
				break;
			}
		}

		final int result = target.findAndRemoveClosest(src);
		if (target.isEmpty())
			colors[br] = null;
		return result;
	}
}