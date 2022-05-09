package edu.ktu.screenshotanalyser.utils;

import org.junit.jupiter.api.Test;
import junit.framework.Assert;

public class TupleTests
{
	@Test
	void testConstructorIntIntLongValues()
	{
		var t = new Tuple<Integer, Integer, Long>(1, 2, 3l);

		Assert.assertEquals((Integer)1, t.first);
		Assert.assertEquals((Integer)2, t.second);
		Assert.assertEquals((Long)3l, t.third);
	}
}
