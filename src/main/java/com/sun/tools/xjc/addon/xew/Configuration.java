/**
 * com.sun.tools.xjc.addon.xew.Configuration.java
 *
 * Copyright (c) 2007-2014 UShareSoft SAS, All rights reserved
 * @author UShareSoft
 */
package com.sun.tools.xjc.addon.xew;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;

public class Configuration implements Cloneable {

	private Class<?>           collectionImplClass      = java.util.ArrayList.class;

	private Class<?>           collectionInterfaceClass = java.util.List.class;

	private InstantiationMode  instantiationMode        = InstantiationMode.EARLY;

	private List<ControlEntry> controlList              = new ArrayList<ControlEntry>();

	// Waiting for this bug to be resolved: http://java.net/jira/browse/JAXB-883
	//private boolean             applyPluralForm                        = Ring.get(BIGlobalBinding.class).isSimpleMode();
	// This is currently an experimental and not properly working feature, so keep this field set to false.
	private boolean            applyPluralForm          = false;

	private final Log          logger;

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

	public Configuration(Log logger) {
		this.logger = logger;
	}

	public Class<?> getCollectionImplClass() {
		return collectionImplClass;
	}

	public void setCollectionImplClass(Class<?> collectionImplClass) {
		this.collectionImplClass = collectionImplClass;
	}

	public Class<?> getCollectionInterfaceClass() {
		return collectionInterfaceClass;
	}

	public void setCollectionInterfaceClass(Class<?> collectionInterfaceClass) {
		this.collectionInterfaceClass = collectionInterfaceClass;
	}

	public InstantiationMode getInstantiationMode() {
		return instantiationMode;
	}

	public void setInstantiationMode(InstantiationMode instantiationMode) {
		this.instantiationMode = instantiationMode;
	}

	public boolean isApplyPluralForm() {
		return applyPluralForm;
	}

	public void setApplyPluralForm(boolean applyPluralForm) {
		this.applyPluralForm = applyPluralForm;
	}

	private static class ControlEntry {
		Pattern     pattern;

		ControlMode controlMode;

		ControlEntry(Pattern pattern, ControlMode controlMode) {
			this.pattern = pattern;
			this.controlMode = controlMode;
		}

		@Override
		public String toString() {
			return "[ControlEntry " + pattern + "=" + controlMode + "]";
		}
	}

	/**
	 * Parse the given control file and initialize this config appropriately.
	 */
	void readControlFile(String fileName) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		controlList.clear();

		String line;
		while ((line = reader.readLine()) != null) {
			line = line.trim();

			if (line.isEmpty() || line.startsWith("#")) {
				continue;
			}

			int separatorIndex = line.indexOf('=');

			if (separatorIndex <= 0) {
				logger.warn("Control file line \"" + line + "\" is invalid as does not have '=' separator.");
				continue;
			}

			String className = line.substring(0, separatorIndex);
			ControlMode controlMode;
			try {
				controlMode = ControlMode.valueOf(line.substring(separatorIndex + 1).trim().toUpperCase());
			}
			catch (IllegalArgumentException e) {
				logger.warn("Control file line \"" + line + "\" is invalid as control mode is unknown.");
				continue;
			}

			controlList.add(new ControlEntry(className.startsWith("/") && className.endsWith("/")
			            && className.length() > 2 ? Pattern.compile(className.substring(1, className.length() - 1))
			            : Pattern.compile(className, Pattern.LITERAL), controlMode));
		}

		reader.close();
	}

	/**
	 * Returns {@code true} if given candidate class should be considered.
	 */
	public boolean isClassIncluded(String className) {
		boolean inclusionForced = false;
		boolean exclusionForced = false;

		for (ControlEntry controlEntry : controlList) {
			if (controlEntry.pattern.matcher(className).matches()) {
				switch (controlEntry.controlMode) {
				case INCLUDE:
					inclusionForced = true;
					break;
				case EXCLUDE:
					exclusionForced = true;
					break;
				default:
				}
			}
		}

		if (inclusionForced) {
			return true;
		}

		if (exclusionForced) {
			return false;
		}

		return true;
	}

	/**
	 * Returns {@code true} if given candidate class should not be removed from model.
	 */
	public boolean isClassUnmarkedForRemoval(String className) {
		for (ControlEntry controlEntry : controlList) {
			if (controlEntry.pattern.matcher(className).matches() && controlEntry.controlMode == ControlMode.KEEP) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Configuration clone() {
		try {
			return (Configuration) super.clone();
		}
		catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return "[Configuration collectionImplClass:" + collectionImplClass.getName() + " collectionInterfaceClass:"
		            + collectionInterfaceClass.getName() + " instantiationMode:" + instantiationMode + " controlList:"
		            + controlList + " applyPluralForm:" + applyPluralForm + "]";
	}
}
