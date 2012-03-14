import org.junit.Test;

import asofold.regionutil.test.AccessMap3dTest;


public class AccessMap3dTestOneMinute {

	@Test
	public void test() {
		long tsStart = System.currentTimeMillis();
		while ( System.currentTimeMillis()-tsStart<60000){
			new AccessMap3dTest().testConsistency();
		}
	}

}
