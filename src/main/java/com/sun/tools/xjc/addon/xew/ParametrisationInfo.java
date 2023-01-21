package com.sun.tools.xjc.addon.xew;

import com.sun.codemodel.JDefinedClass;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Container for parametrisation class and corresponding implementation. If value objects are enabled, then class and
 * implementation refer the same class for simplicity.
 */
public class ParametrisationInfo {

	public final JDefinedClass parametrisationClass;

	public JDefinedClass	   parametrisationImpl;

	public ParametrisationInfo(JDefinedClass parametrisationClass) {
		this.parametrisationClass = parametrisationClass;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
