package asofold.regionutil.test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class TestUtil {
	static final Random random = new Random(System.currentTimeMillis());
	
	static Set<Integer> getIntSet(int size, int min, int max){
		Set<Integer> set = new HashSet<Integer>();
		for ( int i = 0; i<size; i++){
			set.add(min+ random.nextInt(max-min));
		}
		return set;
	}
}
