package com.nextep.pelmel.cache.model.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nextep.pelmel.cache.model.Cache;

public class MemorySizedTTLCacheImpl<T> implements Cache<T> {

	private final int ttlMillis;
	private final int size;

	private final Map<String, Long> ttlMap = Collections
			.synchronizedMap(new HashMap<String, Long>());
	private final List<String> allKeys = Collections
			.synchronizedList(new ArrayList<String>());
	private final Map<String, T> valuesMap = Collections
			.synchronizedMap(new HashMap<String, T>());

	public MemorySizedTTLCacheImpl(int ttlMillis, int maxSize) {
		this.ttlMillis = ttlMillis;
		this.size = maxSize;
	}

	@Override
	public T get(String key) {
		// Default return value is null
		T value = null;
		// Checking expiration
		final Long time = ttlMap.get(key);
		if (time != null) {
			// Only if before timeout
			if (System.currentTimeMillis() <= time) {
				// We assign the stored value
				value = valuesMap.get(key);
			}
		}
		// Returning our value
		return value;
	}

	@Override
	public void put(String key, T value) {
		final boolean alreadyInCache = allKeys.contains(key);
		if (!alreadyInCache) {
			final int currentSize = allKeys.size();
			if (currentSize + 1 >= size) {
				// Removing oldest key
				final String oldestKey = allKeys.get(0);
				allKeys.remove(0);
				valuesMap.remove(oldestKey);
				ttlMap.remove(oldestKey);
			}
			if (key != null) {
				allKeys.add(key);
			}
		} else {
			if (key != null) {
				// Appending key at the end of the list (freshness...)
				allKeys.remove(key);
				allKeys.add(key);
			}
		}
		// Now setting value
		final long millis = System.currentTimeMillis();
		if (key != null) {
			ttlMap.put(key, millis + ttlMillis);
			valuesMap.put(key, value);
		}
	}

	@Override
	public void clear() {
		ttlMap.clear();
		allKeys.clear();
		valuesMap.clear();
	}

}
