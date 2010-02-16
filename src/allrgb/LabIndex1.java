/**
 * 
 */
package allrgb;

class LabIndex1 implements ColorIndex
{
	final ColorList[][] colors = new ColorList[256][173];
	//		final ColorList[][] colors = new ColorList[256][205];
	final int[] lab = new int[3];

	public LabIndex1()
	{
		for (int c = 0; c <= 0xFFFFFF; c++)
		{
			LabTable.INSTANCE.get(c, lab);
			final int lightness = lab[0];
			final int a = lab[1];
			if (colors[lightness][a] == null)
				colors[lightness][a] = new ColorList();
			colors[lightness][a].add(c);
		}
	}

	public int lookup(final int src)
	{
		LabTable.INSTANCE.get(src, lab);
		final int startL = lab[0];
		final int startA = lab[1];
		int delta = 0;
		int br = startL;
		ColorList[] lumTarget = colors[startL];
		while (lumTarget == null)
		{
			delta++;
			br = startL + delta;
			if (br >= 0 && br < colors.length && colors[br] != null)
			{
				lumTarget = colors[br];
				break;
			}
			br = startL - delta;
			if (br >= 0 && br < colors.length && colors[br] != null)
			{
				lumTarget = colors[br];
				break;
			}
		}

		ColorList target = lumTarget[startA];
		delta = 0;
		int a = startA;
		while (target == null)
		{
			delta++;
			a = startA + delta;
			if (a < lumTarget.length && lumTarget[a] != null)
			{
				target = lumTarget[a];
				break;
			}
			a = startA - delta;
			if (a >= 0 && lumTarget[a] != null)
			{
				target = lumTarget[a];
				break;
			}
		}

		final int result = target.findAndRemoveClosest(src);
		if (target.isEmpty())
		{
			lumTarget[a] = null;
			boolean empty = true;
			for (ColorList element : lumTarget)
				if (element != null)
				{
					empty = false;
					break;
				}
			if (empty)
				colors[br] = null;
		}
		return result;
	}
}