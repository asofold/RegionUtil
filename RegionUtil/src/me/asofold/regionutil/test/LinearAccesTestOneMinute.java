package me.asofold.regionutil.test;

import org.junit.Test;

public class LinearAccesTestOneMinute {

	@Test
	public void test() {
		long tsStart = System.currentTimeMillis();
		while ( System.currentTimeMillis()-tsStart<60000){
			new LinearAccessTest().testConsistency();
		}
	}

}
