package com.taobao.tair;

import java.io.Serializable;

public class DataAddCountEntry implements Serializable {
	private static final long serialVersionUID = 1L;
	private int value = 0;
	public DataAddCountEntry(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
}
