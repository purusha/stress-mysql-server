package it.at.script;

import static junit.framework.Assert.assertEquals;
import it.at.script.TimerConnectionAggregator;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;

public class TimerConnectionAggregatorTest {
	
	TimerConnectionAggregator instance;

	@Before
	public void setUp() throws Exception {
		instance = new TimerConnectionAggregator(1L, Sets.newHashSet(1, 2, 42));
	}

	@Test(expected = IllegalArgumentException.class)
	public void errorWhenAddLoadDataWithMissedId() {
		instance.addLoadData(3, new String[]{"a"});
	}

	@SuppressWarnings("deprecation")
	@Test
	public void returnStringInTheSameSort() {
		instance.addLoadData(1, new String[]{"a", "b", "c"});
		
		assertEquals(Optional.of("a"), instance.getLoadData(1));
		assertEquals(Optional.of("b"), instance.getLoadData(1));
		assertEquals(Optional.of("c"), instance.getLoadData(1));
	}
}
