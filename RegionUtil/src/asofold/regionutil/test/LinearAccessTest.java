package asofold.regionutil.test;

import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import asofold.regionutil.access.AccessMapLinear;

public class LinearAccessTest {

	/**
	 * Tests with random elements
	 */
	@Test
	public void testConsistency() {
		Set<Integer> keys = TestUtil.getIntSet(1000, -1000, 1000);
		AccessMapLinear<Integer> access = new AccessMapLinear<Integer>(1000);
		int n = 0;
		// Adding elements:
		for ( Integer key : keys){
			n++;
			access.add(key, key);
			if ( n != access.size() ) fail("inconsistent size: got "+access.size()+" , expected "+n+" instead.");
		}
		// check internal values consistency:
		int[] keyArray = access.getKeyArray();
		Object[] valueArray = access.getValueArray();
		for ( int i = 0; i < access.size(); i++){
			if (!new Integer(keyArray[i]).equals(valueArray[i])) fail("Inconsistent internal state: got value "+valueArray[i]+" for key "+keyArray[i]+" .");
		}
		// Check get values:
		for ( Integer i : keys){
			if ( !i.equals(access.get(i))) fail("Inconsistent value (get): got "+access.get(i)+" , expected "+i+" instead.");
		}
		// Check ordering of keys:
		for ( int i = 1; i<access.size(); i++){
			if ( !(keyArray[i] > keyArray[i-1])) fail("Inconsistent key order at index "+i+"(size="+keys.size()+"): "+keyArray[i-1]+" before "+keyArray[i]+" .");
		}		
		// Get range test:
		for ( Integer key : keys){
			Set<Integer> set1 = new HashSet<Integer>();
			int dist = 100;
			set1.addAll(access.get(key,dist));
			int low = key - dist;
			int high = key + dist;
			Set<Integer> set2 = new HashSet<Integer>();
			
			for ( int i = 0; i<access.size();i++){ // shortcut
				Integer other = keyArray[i];
				if ((low<=other) && (high>=other)) set2.add(other);
			}
			if ( !set1.equals(set2)) fail("Inconsistent get range: (to be displayed)");
		}
		
		// TODO: remove test , detach to static methods to allow for more flexible testing.
	}
}
