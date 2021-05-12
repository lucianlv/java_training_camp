package pers.cocoadel.cache;

import pers.cocoadel.cache.serialize.*;

import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import java.io.Serializable;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 可配置序列化的缓存模板类
 */
public abstract class AbstractConfigurableSerializerCache<K extends Serializable, V extends Serializable> extends AbstractCache<K, V> {

    private final SerializerFactory defaultSerializerFactory = new JacksonSerializerFactory();

    private Serializer<K> keySerializer;

    private Serializer<V> valueSerializer;

    protected AbstractConfigurableSerializerCache(CacheManager cacheManager,
                                                  String cacheName,
                                                  Configuration<K, V> configuration) {
        super(cacheManager, cacheName, configuration);
        SerializerFactory factory = getSerializerFactory(cacheManager);
        keySerializer = factory.getKeySerializer(configuration.getKeyType());
        valueSerializer = factory.getValueSerializer(configuration.getValueType());
    }

    /**
     * 获取配置的序列化工厂类名
     */
    protected String getSerializerFactoryClassName(CacheManager cacheManager) {
        String cacheSchema = cacheManager.getURI().getScheme();
        String classNameKey = ConfigurableCachingProvider.CACHE_MANAGER_MAPPINGS_PROPERTY_PREFIX + "."
                + cacheSchema +".SerializerFactory";
        Properties properties = cacheManager.getProperties();
        return properties.getProperty(classNameKey, JacksonSerializerFactory.class.getName());
    }

    /**
     * 加载序列化工厂
     */
    protected SerializerFactory getSerializerFactory(CacheManager cacheManager) {
        String className = getSerializerFactoryClassName(cacheManager);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            Class<?> clazz = classLoader.loadClass(className);
            return (SerializerFactory) clazz.newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return defaultSerializerFactory;
    }



    @Override
    protected boolean containsEntry(K key) throws CacheException, ClassCastException {
        return containsEntry(serializeKey(key));
    }

    @Override
    protected ExpirableEntry<K, V> getEntry(K key) throws CacheException, ClassCastException {
        byte[] data = getSerializedData(serializeKey(key));
        V v = parseValue(data);
        return ExpirableEntry.of(key, v);
    }

    @Override
    protected void putEntry(ExpirableEntry<K, V> entry) throws CacheException, ClassCastException {
        byte[] keyBytes = serializeKey(entry.getKey());
        byte[] valueBytes = serializeValue(entry.getValue());
        putSerializedData(keyBytes, valueBytes);
    }

    @Override
    protected ExpirableEntry<K, V> removeEntry(K key) throws CacheException, ClassCastException {
        byte[] keyBytes = serializeKey(key);
        byte[] valueBytes = removeSerializedData(keyBytes);
        V value = parseValue(valueBytes);
        return ExpirableEntry.of(key, value);
    }

    @Override
    protected Set<K> keySet() {
        Set<byte[]> bytes = serializedKeySet();
        if (bytes == null || bytes.size() == 0) {
            return Collections.emptySet();
        }
        return serializedKeySet()
                .stream()
                .map(keySerializer::parse)
                .collect(Collectors.toSet());
    }

    protected byte[] serializeKey(K k) {
        return keySerializer.serialize(k);
    }

    protected byte[] serializeValue(V value) {
        return valueSerializer.serialize(value);
    }

    protected V parseValue(byte[] bytes) {
        return valueSerializer.parse(bytes);
    }

    public void setKeySerializer(Serializer<K> keySerializer) {
        this.keySerializer = keySerializer;
    }

    public void setValueSerializer(Serializer<V> valueSerializer) {
        this.valueSerializer = valueSerializer;
    }

    protected abstract boolean containsEntry(byte[] key) throws CacheException, ClassCastException;

    protected abstract byte[] getSerializedData(byte[] key) throws CacheException, ClassCastException;

    protected abstract void putSerializedData(byte[] key, byte[] data);

    protected abstract byte[] removeSerializedData(byte[] key) throws CacheException, ClassCastException;

    protected abstract Set<byte[]> serializedKeySet();
}
