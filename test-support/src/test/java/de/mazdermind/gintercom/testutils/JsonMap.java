package de.mazdermind.gintercom.testutils;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings("NullableProblems")
public class JsonMap implements Map<String, Object> {
	private final Map<String, Object> m;

	public JsonMap() {
		this(Collections.emptyMap());
	}

	public JsonMap(Map<String, Object> m) {
		this.m = m;
	}

	public JsonMap getObject(String key) {
		//noinspection unchecked
		return new JsonMap((Map<String, Object>) get(key));
	}

	@Override
	public int size() {
		return m.size();
	}

	@Override
	public boolean isEmpty() {
		return m.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return m.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return m.containsValue(value);
	}

	@Override
	public Object get(Object key) {
		return m.get(key);
	}

	@Override
	public Object put(String key, Object value) {
		return m.put(key, value);
	}

	@Override
	public Object remove(Object key) {
		return m.remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ?> m) {
		this.m.putAll(m);
	}

	@Override
	public void clear() {
		m.clear();
	}

	@Override
	public Set<String> keySet() {
		return m.keySet();
	}

	@Override
	public Collection<Object> values() {
		return m.values();
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		return m.entrySet();
	}

	@Override
	public Object getOrDefault(Object key, Object defaultValue) {
		return m.getOrDefault(key, defaultValue);
	}

	@Override
	public void forEach(BiConsumer<? super String, ? super Object> action) {
		m.forEach(action);
	}

	@Override
	public void replaceAll(BiFunction<? super String, ? super Object, ?> function) {
		m.replaceAll(function);
	}

	@Override
	public Object putIfAbsent(String key, Object value) {
		return m.putIfAbsent(key, value);
	}

	@Override
	public boolean remove(Object key, Object value) {
		return m.remove(key, value);
	}

	@Override
	public boolean replace(String key, Object oldValue, Object newValue) {
		return m.replace(key, oldValue, newValue);
	}

	@Override
	public Object replace(String key, Object value) {
		return m.replace(key, value);
	}

	@Override
	public Object computeIfAbsent(String key, Function<? super String, ?> mappingFunction) {
		return m.computeIfAbsent(key, mappingFunction);
	}

	@Override
	public Object computeIfPresent(String key, BiFunction<? super String, ? super Object, ?> remappingFunction) {
		return m.computeIfPresent(key, remappingFunction);
	}

	@Override
	public Object compute(String key, BiFunction<? super String, ? super Object, ?> remappingFunction) {
		return m.compute(key, remappingFunction);
	}

	@Override
	public Object merge(String key, Object value, BiFunction<? super Object, ? super Object, ?> remappingFunction) {
		return m.merge(key, value, remappingFunction);
	}

	@Override
	public int hashCode() {
		return m.hashCode();
	}

	@Override
	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	public boolean equals(Object o) {
		return m.equals(o);
	}
}
