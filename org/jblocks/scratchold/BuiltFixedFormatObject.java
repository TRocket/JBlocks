package org.jblocks.scratchold;

public class BuiltFixedFormatObject<T> extends BuiltObject {
	T value;
	String scratchType;

	@Override
	T getValue() {
		return value;
	}

	public BuiltFixedFormatObject(T value, String scratchType) {
		this.value = value;
		this.scratchType = scratchType;
	}

	@Override
	String getScratchType() {
		// TODO Auto-generated method stub
		return this.scratchType;
	}

}
