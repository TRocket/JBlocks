package org.jblocks.scratchold;

public class BuiltInlineValue<T> extends BuiltObject {
	T value;
	String scratchType;

	@Override
	T getValue() {
		return value;
	}

	public BuiltInlineValue(T value, String scratchType) {
		this.value = value;
		this.scratchType = scratchType;
	}

	@Override
	String getScratchType() {
		// TODO Auto-generated method stub
		return this.scratchType;
	}


}
