package org.zrd.utils.extension;

/**
 * @Description TODO
 * @Author ZRD
 * @Date 2021/8/11
 */
public class Holder<T> {

    private volatile T value;

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }
}
