package me.asofold.regionutil.test;
import org.junit.Test;



public class AccessMap3dTestOneMinute {

	@Test
	public void test() {
		long tsStart = System.currentTimeMillis();
		while ( System.currentTimeMillis()-tsStart<60000){
			new AccessMap3dTest().testConsistency();
		}
	}

}
