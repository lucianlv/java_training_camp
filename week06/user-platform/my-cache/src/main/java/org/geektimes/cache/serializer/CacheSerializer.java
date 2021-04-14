package org.geektimes.cache.serializer;

import java.io.Serializable;
import javax.cache.CacheException;

/**
 * Serializer of cache
 *
 * @param <K> source
 * @param <V> target
 */
public interface CacheSerializer<K extends Serializable, V extends Serializable> {

    V serialize(K source) throws CacheException;

    K deserialize(V targe) throws CacheException;

}
