package asofold.regionutil.access;

import java.util.LinkedList;
import java.util.List;

/**
 * Optimized for Minecraft (Storing/Checking order: x,z,y)
 * @author mc_dev
 *
 * @param <T>
 */
public class AccessMap3d<T>{
	
	private static final int defaultCapacity = 1; 
	private final LinearAccess<LinearAccess<LinearAccess<T>>> map;
	
	public AccessMap3d(){
		map = new LinearAccess<LinearAccess<LinearAccess<T>>>(defaultCapacity);
	}
	
	public AccessMap3d(int initialCapacity){
		map = new LinearAccess<LinearAccess<LinearAccess<T>>>(initialCapacity);
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param value
	 */
	public final void add(final int x, final int y, final int z, final T value){
		LinearAccess<LinearAccess<T>> map2 = map.get(x);
		if ( map2 == null ){
			map2 = new LinearAccess<LinearAccess<T>>(defaultCapacity);
			map.add(x, map2);
			final LinearAccess<T> map3 = new LinearAccess<T>(defaultCapacity);
			map2.add(z, map3);
			map3.add(y, value);
			return;
		}
		LinearAccess<T> map3 = map2.get(z);
		if ( map3 == null){
			map3 = new LinearAccess<T>(defaultCapacity);
			map2.add(z, map3);
			map3.add(y, value);
			return;
		}
		map3.add(y, value);
	}
	
	/**
	 * Get all associated values within distance d (orthogonal).
	 * @param x
	 * @param y
	 * @param z
	 * @param d
	 * @return
	 */
	public final List<T> get(final int x, final int y, final int z, final int d){
		final List<T> out = new LinkedList<T>();
		final List<LinearAccess<LinearAccess<T>>> maps2 = map.get(x, d);
		if  (maps2.isEmpty()) return out;
		for ( final LinearAccess<LinearAccess<T>> map2 : maps2){
			final List<LinearAccess<T>> maps3 = map2.get(z, d);
			if ( maps3.isEmpty() ) continue;
			for ( LinearAccess<T> map3 : maps3 ){
				List<T> values = map3.get(y, d);
				if ( !values.isEmpty() ) out.addAll(values);
			}
		}
		return out;
	}
}
