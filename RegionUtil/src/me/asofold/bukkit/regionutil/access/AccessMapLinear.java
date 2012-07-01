package me.asofold.bukkit.regionutil.access;

import java.util.LinkedList;
import java.util.List;

/**
 * Class for one dimensional search for point entries vs. an interval, mapping to some type T.
 * Uses int.
 * 
 * The intent is faster access rather than adding.
 * @author mc_dev
 *
 * @param <T>
 */
public class AccessMapLinear<T> {
	private static final int defaultCapacity = 10; 
	int[] keys;
	Object[] values;
	int size = 0;
	
	public AccessMapLinear(int initialCapacity){
		size = 0;
		keys = new int[initialCapacity];
		values = new Object[initialCapacity];
	}
	
	public AccessMapLinear(){
		size = 0;
		keys = new int[defaultCapacity];
		values = new Object[defaultCapacity];
	}
	
	/**
	 * Find the "closest" index to match the key.
	 * This is not the smallest difference, but should return either the index if matched, or one left or one right of where the index would belong.
	 * return -1 if not valid (should be on empty). 
	 * @param key
	 */
	private final int closestIndex(final int key){
		// TODO: implement fastest algorithm
		// Here some binary search
		if (size == 0) return -1;
		int min = 0;
		int max = size-1;
		int n = 0;
		while ( true){
			int i = (max+min)/2;
			n++;// TODO: REMOVE DEBUG CODE
			if (n > size*2) throw new RuntimeException("Too many iterations: "+n+" , min="+min+" , max="+max+" , i="+i);
			int ref = keys[i];
			if ( ref == key ) return i;
			else if (min==max) return i; // TODO: maybe redundant !
			else if ( ref > key){
				if ( i == min) return i;
				max = i;
			} 
			else { // if ( ref< key){
				if ( i == min) return max;
				min = i;
			}
		}
	}
	
	/**
	 * 
	 * @param center
	 * @param distance
	 * @return
	 */
	public final List<T> get(final int key, final int distance){
		List<T> out = new LinkedList<T>();
		final int i = closestIndex(key);
		if ( i == -1 ) return out;
		int ref = keys[i];
		final int low = key-distance;
		final int high = key + distance;
		// check the returned element:
		if ((low<=ref) && (high>= ref)) addTo( out, values[i]);
		// check below:
		if ( i>0 ){
			int k = i-1;
			ref = keys[k];
			while(ref >= low){
				addTo(out, values[k]);
				k--;
				if ( k==-1) break;
				ref = keys[k];
			}
		}
		// check above:
		if ( i<size-1 ){
			int k = i+1;
			ref = keys[k];
			while(ref <= high){
				addTo(out, values[k]);
				k++;
				if ( k==size) break;
				ref = keys[k];
			}
		}
		return out;
	}
	
	/**
	 * Might replace a value.
	 * VERY EXPENSIVE
	 * @param key
	 * @param obj
	 */
	public final void add( final int key, final T value){
		if ( size == 0 ){
			if (keys.length >0){
				keys[0] = key;
				values[0] = value;
			} else{
				keys = new int[]{key};
				values = new Object[]{value};
			}
			size = 1;
			return;
		}
		// Find index where to put it:
		final int index = closestIndex(key);
		int ref = keys[index];
		if ( ref == key ){
			values[index] = value;
			// size stays.
			return;
		}
		// TODO: (below) more efficient (contract).
		else if ( ref > key){
			int k = index -1;
			while (k>=0){
				if (keys[k] <key ) break;
				ref = keys[k];
				k--;
			}
			// k should be one less than the index to add to
			addAtIndex( k+1, key, value);
		}
		else{
			// ref < key
			int k = index + 1;
			while (k<size){
				if (keys[k] >key ) break;
				ref = keys[k];
				k++;
			}
			// k should be one greater than the index to add to
			addAtIndex( k, key, value);
		}
	}
	
	private final void  addAtIndex(final int k, final int key, final T value){
		final int[] nKeys;
		final Object[] nValues;
		if ( size < keys.length ){
			nKeys = keys;
			nValues = values;
		} else{
			// must increase size !
			nKeys = new int[size+1];
			nValues = new Object[size+1];
			// copy part below of k+1:
			for ( int i = 0; i< k; i++){
				nKeys[i] = keys[i];
				nValues[i] = values[i];
			}
		}
		
		if ( k == size){
			// do nothing.
		} else{
			// Copy the above part and set the element
			for ( int i = size; i>k; i--){
				nKeys[i] = keys[i-1];
				nValues[i] = values[i-1];
			}
		}
		nValues[k] = value;
		nKeys[k] = key;
		size ++;
		keys = nKeys;
		values = nValues;
	}

	public final T get(final int key){
		final int i = closestIndex(key);
		if (i == -1) return null;
		if (keys[i] == key) return getIndex(i);
		return null;
	}
	
	public final T remove(final int key){
		final int i = closestIndex(key);
		if ( i == -1 ) return null;
		if ( keys[i] == key) return removeIndex(i);
		return null;
	}
	
	/**
	 * 
	 * @param value
	 * @return if the value was found.
	 */
	public final boolean remove( final Object value){
		final int index = getValueIndex(value);
		if (index == -1) return false;
		else{
			removeIndex(index);
			return true;
		}
	}
	
	public final boolean containsValue(final Object value){
		final int index = getValueIndex(value);
		return index != -1;
	}
	
	/**
	 * Get the index at which the value is found FIRST.
	 * @param value
	 * @return -1 if not found.
	 */
	private final int getValueIndex(Object value) {
		if ( value == null){
			// TODO: policy !
			for ( int i = 0; i<size; i++){
				if ( values[i] == null) return i;
			}
			return -1;
		}
		for ( int i = 0; i<size; i++){
			Object ref = values[i];
			if ( ref == value) return i;
			else if (value.equals(ref)) return i;
		}
		return -1;
	}

	public final boolean containsKey(final int key){
		final int i = closestIndex(key);
		if ( i == -1) return false;
		return keys[i]==key;
	}
	
	// TODO: maybe remove(key, distance).
	
	/**
	 * Just clears values and sets size to 0.
	 */
	public final void clear(){
		if (size == 0) return;
		size = 0;
		for ( int i = 0; i < keys.length; i++){
			values[i] = null;
		}
	}
	
	/**
	 * Set to length zero with new key and value arrays of initial capacity.
	 * @param initialCapacity
	 */
	public final void clear( int initialCapacity){
		if ( initialCapacity == keys.length){
			clear();
			return;
		}
		size = 0;
		keys = new int[initialCapacity];
		values = new Object[initialCapacity];
	}
	
	@SuppressWarnings("unchecked")
	public final List<T> values(){
		final List<T> out = new LinkedList<T>();
		for ( int i=0; i<size; i++){
			out.add((T) values[i]);
		}
		return out;
	}
	
	public final boolean isEmpty(){
		return size == 0;
	}
	
	public final int size(){
		return size;
	}	
	
	/**
	 * For suppressing the type warning.
	 * @param t
	 * @param f
	 */
	@SuppressWarnings("unchecked")
	private static final <T> void addTo(final List<T> t, final Object o){
		// if (f == null) return; // TODO: maybe remove by contract
		t.add((T) o);
	}
	
	/**
	 * To suppress warning.
	 * @param index
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private final T getIndex(final int index){
		return (T) values[index];
	}
	
	private final T removeIndex( final int i){
		@SuppressWarnings("unchecked")
		final T out = (T) values[i];
		for (int k = i; k<size-1; k++){
			keys[k]  = keys[k+1];
			values[k] = values[k+1];
		}
		size --;
		return out;
	}
	
	/**
	 * Get the internal key array. Mind size().
	 * FOR TESTING
	 * @return
	 *
	 */
	public final int[] getKeyArray(){
		return keys;
	}
	
	/**
	 * Internal value array. Mind size().
	 * FOR TESTING
	 * @return
	 */
	public final Object[] getValueArray(){
		return values;
	}
	
}
