package org.jblocks.scratch;

public class InlineValue<T> extends SerializedObject {
	T value;

	@Override
	T getValue() {
		return value;
	}

	public InlineValue(T value) {
		this.value = value;
	}

}
