package me.asofold.bukkit.regionutil.access;

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
	private final AccessMapLinear<AccessMapLinear<AccessMapLinear<T>>> map;
	
	public AccessMap3d(){
		map = new AccessMapLinear<AccessMapLinear<AccessMapLinear<T>>>(defaultCapacity);
	}
	
	public AccessMap3d(int initialCapacity){
		map = new AccessMapLinear<AccessMapLinear<AccessMapLinear<T>>>(initialCapacity);
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param value
	 */
	public final void add(final int x, final int y, final int z, final T value){
		AccessMapLinear<AccessMapLinear<T>> map2 = map.get(x);
		if ( map2 == null ){
			map2 = new AccessMapLinear<AccessMapLinear<T>>(defaultCapacity);
			map.add(x, map2);
			final AccessMapLinear<T> map3 = new AccessMapLinear<T>(defaultCapacity);
			map2.add(z, map3);
			map3.add(y, value);
			return;
		}
		AccessMapLinear<T> map3 = map2.get(z);
		if ( map3 == null){
			map3 = new AccessMapLinear<T>(defaultCapacity);
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
		return get(x, y, z, d, d, d);
	}
	
	/**
	 * Get all associated values within corresponding distance dx/dy/dz (orthogonal).
	 * @param x
	 * @param y
	 * @param z
	 * @param dx
	 * @param dy
	 * @param dz
	 * @return
	 */
	public final List<T> get(final int x, final int y, final int z, final int dx, final int dy, final int dz){
		final List<T> out = new LinkedList<T>();
		final List<AccessMapLinear<AccessMapLinear<T>>> maps2 = map.get(x, dx);
		if  (maps2.isEmpty()) return out;
		for ( AccessMapLinear<AccessMapLinear<T>> map2 : maps2){
			final List<AccessMapLinear<T>> maps3 = map2.get(z, dz);
			if ( maps3.isEmpty() ) continue;
			for ( AccessMapLinear<T> map3 : maps3 ){
				final List<T> values = map3.get(y, dy);
				if ( !values.isEmpty() ) out.addAll(values);
			}
		}
		return out;
	}
	
	@SuppressWarnings("unchecked")
	public final List<T> values(){
		final List<T> out = new LinkedList<T>();
		final Object[] maps2 = map.getValueArray();
		for (int i=0; i<map.size(); i++){
			final AccessMapLinear<AccessMapLinear<T>> map2 = (AccessMapLinear<AccessMapLinear<T>>) maps2[i];
			final Object[] maps3 = map2.getValueArray();
			for (int k=0; k<map2.size(); k++){
				final AccessMapLinear<T> map3 = (AccessMapLinear<T>) maps3[k];
				final Object[] all = map3.getValueArray();
				for ( int m=0; m<map3.size(); m++){
					out.add((T) all[m]);
				}
			}
		}
		return out;
	}
	
	/**
	 * Clear and set to default capacity.
	 */
	public final void clear(){
		map.clear(defaultCapacity);
	}
	
	/**
	 * Clear contents, but set initial capacity.
	 * @param inititalCapacity
	 */
	public final void clear ( int inititalCapacity){
		map.clear(inititalCapacity);
	}
}
