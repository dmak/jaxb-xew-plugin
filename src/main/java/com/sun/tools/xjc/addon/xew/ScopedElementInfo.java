package com.sun.tools.xjc.addon.xew;

import com.sun.codemodel.JExpression;
import com.sun.codemodel.JType;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Container for information relative to scoped elements.
 */
public final class ScopedElementInfo {
	/**
	 * Element name ("post-office").
	 */
	public JExpression name;

	/**
	 * Element namespace ("http://foo.bar").
	 */
	public JExpression namespace;

	/**
	 * Element type ({@link String}).
	 */
	public JType	   type;

	public ScopedElementInfo(JExpression name, JExpression namespace, JType type) {
		this.name = name;
		this.namespace = namespace;
		this.type = type;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}