package org.geektimes.cache.serialize;

/**
 * @Auther: liuyj
 * @Date: 2021/04/13/11:52
 * @Description:
 */
public interface Serializer<S ,T> {
    //序列化
    T serialize(S source);
    //反序列化
    S deserialize(T target);
}
