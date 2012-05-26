package org.jblocks.scratch;

public class InlineValue<T> extends SerializedObject {
	T value;
	String scratchType;

	@Override
	T getValue() {
		return value;
	}

	public InlineValue(T value, String scratchType) {
		this.value = value;
		this.scratchType = scratchType;
	}

	@Override
	String getScratchType() {
		// TODO Auto-generated method stub
		return this.scratchType;
	}


}
