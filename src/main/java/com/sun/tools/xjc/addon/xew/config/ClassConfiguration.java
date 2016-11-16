package com.sun.tools.xjc.addon.xew.config;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Per-class or per-field configuration.
 */
public class ClassConfiguration extends CommonConfiguration {

	public ClassConfiguration(CommonConfiguration configuration) {
		super(configuration);
	}

	public boolean isAnnotatable() {
		return ObjectUtils.defaultIfNull((Boolean) configurationValues.get(ConfigurationOption.ANNOTATE), Boolean.TRUE)
		            .booleanValue();
	}

	public void setAnnotatable(boolean annotate) {
		configurationValues.put(ConfigurationOption.ANNOTATE, Boolean.valueOf(annotate));
	}

	@Override
	protected ToStringBuilder appendProperties(ToStringBuilder builder) {
		super.appendProperties(builder);
		builder.append("excluded", isAnnotatable());

		return builder;
	}
}
