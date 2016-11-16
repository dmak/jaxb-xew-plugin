/**
 * com.sun.tools.xjc.addon.xew.Configuration.java
 *
 * Copyright (c) 2007-2014 UShareSoft SAS, All rights reserved
 * @author UShareSoft
 */
package com.sun.tools.xjc.addon.xew.config;

import java.util.EnumMap;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.logging.Log;

/**
 * Configuration which is common for global and per-class usecases.
 */
public class CommonConfiguration {

	protected EnumMap<ConfigurationOption, Object> configurationValues = new EnumMap<ConfigurationOption, Object>(
	            ConfigurationOption.class);

	protected Log								   logger;

	/**
	 * Types of collection instantiation modes.
	 */
	public enum ConfigurationOption {
	    /**
	     * Control file name.
	     */
		CONTROL("control"),

		/**
		 * Summary file name.
		 */
		SUMMARY("summary"),

		/**
		 * Collection implementation class name.
		 */
		COLLECTION_IMPLEMENTATION("collection"),

		/**
		 * Collection interface class name.
		 */
		COLLECTION_INTERFACE("collectionInterface"),

		/**
		 * Instantiation mode.
		 */
		INSTANTIATION_MODE("instantiate"),

		/**
		 * Apply plural form to field names. Waiting for <a href="http://java.net/jira/browse/JAXB-883">this bug</a> to
		 * be resolved. Once fixed one should read value for this flag like this:
		 * {@code Ring.get(BIGlobalBinding.class).isSimpleMode();}.
		 */
		APPLY_PLURAL_FORM("plural"),

		/**
		 * Exclude class from being a candidate or field for being substituted.
		 */
		ANNOTATE("annotate");

		final String optionName;

		private ConfigurationOption(String optionName) {
			this.optionName = optionName;
		}

		public String optionName() {
			return optionName;
		}

		public static ConfigurationOption byOption(String optionName) {
			for (ConfigurationOption option : values()) {
				if (option.optionName().equals(optionName)) {
					return option;
				}
			}

			throw new IllegalArgumentException("Unknown option \"" + optionName + "\"");
		}
	}

	/**
	 * Types of collection instantiation modes.
	 */
	public enum InstantiationMode {
	    /**
	     * Collection is initialized as property initializer (created when class is constructed).
	     */
		EARLY,

		/**
		 * Collection is initialized in getter (created when property is accessed the first time).
		 */
		LAZY,

		/**
		 * Collection is never initialized. It's consumers responsibility to set the property to some collection
		 * instance.
		 */
		NONE
	}

	/**
	 * Types of collection instantiation modes.
	 */
	public enum ControlMode {

	    /**
	     * Given class is excluded from becoming candidate for substitution.
	     */
		EXCLUDE,

		/**
		 * Given class is not excluded from becoming candidate for substitution. Used usually in conjunction with
		 * exclude to include back a part of the exclusion space.
		 */
		INCLUDE,

		/**
		 * Given candidate class is not removed (kept) from model. Substitutions are nevertheless made.
		 */
		KEEP
	}

	public CommonConfiguration() {
		configurationValues.put(ConfigurationOption.COLLECTION_IMPLEMENTATION, java.util.ArrayList.class);
		configurationValues.put(ConfigurationOption.COLLECTION_INTERFACE, java.util.List.class);
		configurationValues.put(ConfigurationOption.INSTANTIATION_MODE, InstantiationMode.EARLY);
		configurationValues.put(ConfigurationOption.APPLY_PLURAL_FORM, Boolean.FALSE);
	}

	public CommonConfiguration(CommonConfiguration configuration) {
		this.logger = configuration.logger;
		this.configurationValues = configuration.configurationValues.clone();
	}

	public Class<?> getCollectionImplClass() {
		return (Class<?>) configurationValues.get(ConfigurationOption.COLLECTION_IMPLEMENTATION);
	}

	public void setCollectionImplClass(Class<?> collectionImplClass) {
		configurationValues.put(ConfigurationOption.COLLECTION_IMPLEMENTATION, collectionImplClass);
	}

	public Class<?> getCollectionInterfaceClass() {
		return (Class<?>) configurationValues.get(ConfigurationOption.COLLECTION_INTERFACE);
	}

	public void setCollectionInterfaceClass(Class<?> collectionInterfaceClass) {
		configurationValues.put(ConfigurationOption.COLLECTION_INTERFACE, collectionInterfaceClass);
	}

	public InstantiationMode getInstantiationMode() {
		return (InstantiationMode) configurationValues.get(ConfigurationOption.INSTANTIATION_MODE);
	}

	public void setInstantiationMode(InstantiationMode instantiationMode) {
		configurationValues.put(ConfigurationOption.INSTANTIATION_MODE, instantiationMode);
	}

	public boolean isApplyPluralForm() {
		return ((Boolean) configurationValues.get(ConfigurationOption.APPLY_PLURAL_FORM)).booleanValue();
	}

	public void setApplyPluralForm(boolean applyPluralForm) {
		configurationValues.put(ConfigurationOption.APPLY_PLURAL_FORM, Boolean.valueOf(applyPluralForm));
	}

	public void setLogger(Log logger) {
		this.logger = logger;
	}

	protected ToStringBuilder appendProperties(ToStringBuilder builder) {
		builder.append("collectionImplClass", getCollectionImplClass().getName());
		builder.append("collectionInterfaceClass", getCollectionInterfaceClass().getName());
		builder.append("instantiationMode", getInstantiationMode());
		builder.append("applyPluralForm", isApplyPluralForm());

		return builder;
	}

	@Override
	public String toString() {
		return appendProperties(new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)).toString();
	}
}
