/**
 * 
 */
package allrgb;

class LabIndex3 implements ColorIndex
{
	final ColorList[][] laIndex = new ColorList[256][173];
	final ColorList[][] lbIndex = new ColorList[256][205];
	final int[] lab = new int[3];

	public LabIndex3()
	{
		for (int c = 0; c <= 0xFFFFFF; c++)
		{
			LabTable.INSTANCE.get(c, lab);
			final int lightness = lab[0];
			final int a = lab[1];
			final int b = lab[2];
			if (laIndex[lightness][a] == null)
				laIndex[lightness][a] = new ColorList();
			laIndex[lightness][a].add(c);
			if (lbIndex[lightness][b] == null)
				lbIndex[lightness][b] = new ColorList();
			lbIndex[lightness][b].add(c);
		}
	}

	public int lookup(final int src)
	{
		LabTable.INSTANCE.get(src, lab);
		final int startL = lab[0];
		final int startA = lab[1];
		int delta = 0;
		int br = startL;
		ColorList[] lumTarget = laIndex[startL];
		while (lumTarget == null)
		{
			delta++;
			br = startL + delta;
			if (br >= 0 && br < laIndex.length && laIndex[br] != null)
			{
				lumTarget = laIndex[br];
				break;
			}
			br = startL - delta;
			if (br >= 0 && br < laIndex.length && laIndex[br] != null)
			{
				lumTarget = laIndex[br];
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
				laIndex[br] = null;
		}
		return result;
	}
}