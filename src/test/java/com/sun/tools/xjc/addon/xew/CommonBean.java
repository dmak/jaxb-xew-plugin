package com.sun.tools.xjc.addon.xew;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Dummy parent class for some beans.
 */
public class CommonBean {

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
