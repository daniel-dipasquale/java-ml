package com.dipasquale.synchronization.dual.mode.data.structure.map;

import com.dipasquale.common.factory.data.structure.map.MapFactory;
import com.dipasquale.synchronization.dual.mode.DualModeObject;
import com.dipasquale.synchronization.dual.profile.factory.data.structure.map.MapFactoryProfile;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class DualModeMap<TKey, TValue> implements Map<TKey, TValue>, DualModeObject, Serializable {
    @Serial
    private static final long serialVersionUID = 6719007368363187752L;
    private final MapFactoryProfile mapFactoryProfile;
    private transient Map<TKey, TValue> map;

    private DualModeMap(final MapFactoryProfile mapFactoryProfile, final Map<TKey, TValue> map) {
        this.mapFactoryProfile = mapFactoryProfile;
        this.map = mapFactoryProfile.getObject().create(map);
    }

    private DualModeMap(final MapFactoryProfile mapFactoryProfile) {
        this(mapFactoryProfile, null);
    }

    public DualModeMap(final boolean concurrent, final MapFactory concurrentMapFactory, final MapFactory defaultMapFactory, final Map<TKey, TValue> map) {
        this(new MapFactoryProfile(concurrent, concurrentMapFactory, defaultMapFactory), map);
    }

    public DualModeMap(final boolean concurrent, final MapFactory concurrentMapFactory, final MapFactory defaultMapFactory) {
        this(concurrent, concurrentMapFactory, defaultMapFactory, null);
    }

    public DualModeMap(final boolean concurrent, final int numberOfThreads, final int initialCapacity, final Map<TKey, TValue> map) {
        this(MapFactoryProfile.createHash(concurrent, numberOfThreads, initialCapacity), map);
    }

    public DualModeMap(final boolean concurrent, final int numberOfThreads, final int initialCapacity) {
        this(concurrent, numberOfThreads, initialCapacity, null);
    }

    public DualModeMap(final boolean concurrent, final int numberOfThreads, final Map<TKey, TValue> map) {
        this(concurrent, numberOfThreads, 16, map);
    }

    public DualModeMap(final boolean concurrent, final int numberOfThreads) {
        this(concurrent, numberOfThreads, null);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(final Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return map.containsValue(value);
    }

    @Override
    public TValue get(final Object key) {
        return map.get(key);
    }

    @Override
    public TValue getOrDefault(final Object key, final TValue defaultValue) {
        return map.getOrDefault(key, defaultValue);
    }

    @Override
    public TValue put(final TKey key, final TValue value) {
        return map.put(key, value);
    }

    @Override
    public TValue putIfAbsent(final TKey key, final TValue value) {
        return map.putIfAbsent(key, value);
    }

    @Override
    public TValue compute(final TKey key, final BiFunction<? super TKey, ? super TValue, ? extends TValue> remappingFunction) {
        return map.compute(key, remappingFunction);
    }

    @Override
    public TValue computeIfAbsent(final TKey key, final Function<? super TKey, ? extends TValue> mappingFunction) {
        return map.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public TValue computeIfPresent(final TKey key, BiFunction<? super TKey, ? super TValue, ? extends TValue> remappingFunction) {
        return map.computeIfPresent(key, remappingFunction);
    }

    @Override
    public void putAll(final Map<? extends TKey, ? extends TValue> map) {
        this.map.putAll(map);
    }

    @Override
    public TValue remove(final Object key) {
        return map.remove(key);
    }

    @Override
    public boolean remove(final Object key, final Object value) {
        return map.remove(key, value);
    }

    @Override
    public boolean replace(final TKey key, final TValue oldValue, final TValue newValue) {
        return map.replace(key, oldValue, newValue);
    }

    @Override
    public TValue replace(final TKey key, final TValue value) {
        return map.replace(key, value);
    }

    @Override
    public void replaceAll(final BiFunction<? super TKey, ? super TValue, ? extends TValue> function) {
        map.replaceAll(function);
    }

    @Override
    public TValue merge(final TKey key, final TValue value, final BiFunction<? super TValue, ? super TValue, ? extends TValue> remappingFunction) {
        return map.merge(key, value, remappingFunction);
    }

    @Override
    public Set<TKey> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<TValue> values() {
        return map.values();
    }

    @Override
    public Set<Entry<TKey, TValue>> entrySet() {
        return map.entrySet();
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public void forEach(final BiConsumer<? super TKey, ? super TValue> action) {
        map.forEach(action);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return map.equals(obj);
    }

    @Override
    public String toString() {
        return map.toString();
    }

    @Override
    public void switchMode(final boolean concurrent) {
        mapFactoryProfile.switchProfile(concurrent);
        map = mapFactoryProfile.getObject().create(map);
    }

    @Serial
    private void readObject(final ObjectInputStream objectInputStream)
            throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        map = mapFactoryProfile.getObject().create((Map<TKey, TValue>) objectInputStream.readObject());
    }

    @Serial
    private void writeObject(final ObjectOutputStream objectOutputStream)
            throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(new HashMap<>(map));
    }
}
