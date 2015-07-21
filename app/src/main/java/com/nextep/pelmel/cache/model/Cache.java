package com.nextep.pelmel.cache.model;

/**
 * This interface represents a cache
 * 
 * @author cfondacci
 * 
 * @param <T>
 */
public interface Cache<T> {

	/**
	 * Gets a value from its key
	 * 
	 * @param key
	 *            the key to retrieve in cache
	 * @return the corresponding value or <code>null</code> if no value found in
	 *         cache
	 */
	T get(String key);

	/**
	 * Puts a value in the cache
	 * 
	 * @param key
	 *            the key to store the value in
	 * @param value
	 *            the value to store in cache
	 */
	void put(String key, T value);

	/**
	 * Clears everything in this cache
	 */
	void clear();
}
