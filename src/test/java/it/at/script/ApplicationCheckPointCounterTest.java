package it.at.script;

import static org.junit.Assert.*;
import it.at.script.ApplicationCheckPointCounter;

import org.junit.Test;

@SuppressWarnings("unused")
public class ApplicationCheckPointCounterTest {

	@Test
	public void test() {
		final ApplicationCheckPointCounter count = new ApplicationCheckPointCounter();		
		count.totalNumber(100);
		
		count.partialNumber(15);
		System.err.println(count.build());
		
		count.partialNumber(30);
		System.err.println(count.build());
	}

}
