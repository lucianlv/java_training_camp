package org.geektimes.cache.redis.lettuce;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.codec.RedisCodec;
import org.geektimes.cache.AbstractCache;
import org.geektimes.cache.ExpirableEntry;
import org.geektimes.cache.serialize.BytesSerializer;
import org.geektimes.cache.serialize.Serializer;

import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * @Auther: liuyj
 * @Date: 2021/04/13/13:57
 * @Description:
 */
public class LettuceCache<K extends Serializable, V extends Serializable> extends AbstractCache<K, V> {

    private final StatefulRedisConnection<K, V> statefulRedisConnection;
    private final Serializer<Object,byte[]> serializer;
    private final RedisCommands<K, V> commands;
    private final Map<Class, Function<String,Serializable>> decodeMap;
    private final Map<Class, Function<Serializable,String>> encodeMap;

    private String byteBufferToString(ByteBuffer bytes){
        CharBuffer charBuffer = null;
        try {
            Charset charset = Charset.forName("UTF-8");
            CharsetDecoder decoder = charset.newDecoder();
            charBuffer = decoder.decode(bytes);
            bytes.flip();
            String str = charBuffer.toString();
            return str;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private ByteBuffer stringToByteBuffer(String str){
        return ByteBuffer.wrap(str.getBytes());
    }

    protected LettuceCache(CacheManager cacheManager, String cacheName, Configuration<K, V> configuration, RedisClient lettuceClient) {
        super(cacheManager, cacheName, configuration);
        decodeMap = new HashMap<>();
        decodeMap.put(Integer.class, str -> Integer.parseInt(str));
        decodeMap.put(Double.class, str -> Double.parseDouble(str));
        decodeMap.put(String.class, str -> str);

        encodeMap = new HashMap<>();
        encodeMap.put(Integer.class, o -> String.valueOf(o));
        encodeMap.put(Double.class, o -> String.valueOf(o));
        encodeMap.put(String.class, o -> o.toString());


        this.statefulRedisConnection = lettuceClient.connect(
                new RedisCodec<K, V>() {
                    @Override
                    public K decodeKey(ByteBuffer bytes) {
                        String str = byteBufferToString(bytes);
                        Function<String,Serializable> function = decodeMap.get(configuration.getKeyType());
                        return (K) function.apply(str);
                    }
                    @Override
                    public V decodeValue(ByteBuffer bytes) {
                        String str = byteBufferToString(bytes);
                        Function<String,Serializable> function = decodeMap.get(configuration.getValueType());
                        return (V) function.apply(str);
                    }
                    @Override
                    public ByteBuffer encodeKey(K key) {
                        Function<Serializable, String> function = encodeMap.get(configuration.getKeyType());
                        String str = function.apply(key);
                        return stringToByteBuffer(str);
                    }
                    @Override
                    public ByteBuffer encodeValue(V value) {
                        Function<Serializable, String> function = encodeMap.get(configuration.getValueType());
                        String str = function.apply(value);
                        return stringToByteBuffer(str);
                    }
                }
        );
        this.serializer= new BytesSerializer();
        this.commands= statefulRedisConnection.sync();
    }


    @Override
    protected void doClose() {
        statefulRedisConnection.close();
    }

    @Override
    protected boolean containsEntry(K key) throws CacheException, ClassCastException {
        return commands.exists(key)>0;
    }

    @Override
    protected ExpirableEntry<K, V> getEntry(K key) throws CacheException, ClassCastException {
        V value = commands.get(key);
        ExpirableEntry<K, V> expirableEntry = ExpirableEntry.of(key, value);
        return expirableEntry;
    }

    @Override
    protected void putEntry(ExpirableEntry<K, V> entry) throws CacheException, ClassCastException {
        commands.set(entry.getKey(),entry.getValue());
    }

    @Override
    protected ExpirableEntry<K, V> removeEntry(K key) throws CacheException, ClassCastException {
        ExpirableEntry<K, V> entry = getEntry(key);
        commands.del(key);
        return entry;
    }

    @Override
    protected void clearEntries() throws CacheException {

    }

    @Override
    protected Set<K> keySet() {
        return null;
    }
}
