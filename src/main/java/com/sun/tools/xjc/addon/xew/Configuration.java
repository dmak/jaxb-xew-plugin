/**
 * com.sun.tools.xjc.addon.xew.Configuration.java
 *
 * Copyright (c) 2007-2014 UShareSoft SAS, All rights reserved
 * @author UShareSoft
 */
package com.sun.tools.xjc.addon.xew;

public class Configuration implements Cloneable {
	private Class<?>            collectionInterfaceClass               = java.util.List.class;
	private Class<?>            collectionImplClass                    = java.util.ArrayList.class;
	private Instantiation       instantiation                          = Instantiation.EARLY;
	private boolean             deleteCandidates                       = false;
	// Waiting for this bug to be resolved: http://java.net/jira/browse/JAXB-883
	//private boolean             applyPluralForm                        = Ring.get(BIGlobalBinding.class).isSimpleMode();
	// This is currently an experimental and not properly working feature, so keep this field set to false.
	private boolean             applyPluralForm                        = false;

	public Class<?> getCollectionInterfaceClass() {
		return collectionInterfaceClass;
	}

	public void setCollectionInterfaceClass(Class<?> collectionInterfaceClass) {
		this.collectionInterfaceClass = collectionInterfaceClass;
	}

	public Class<?> getCollectionImplClass() {
		return collectionImplClass;
	}

	public void setCollectionImplClass(Class<?> collectionImplClass) {
		this.collectionImplClass = collectionImplClass;
	}

	public Instantiation getInstantiation() {
		return instantiation;
	}

	public void setInstantiation(Instantiation instantiation) {
		this.instantiation = instantiation;
	}

	public boolean isDeleteCandidates() {
		return deleteCandidates;
	}

	public void setDeleteCandidates(boolean deleteCandidates) {
		this.deleteCandidates = deleteCandidates;
	}

	public boolean isApplyPluralForm() {
		return applyPluralForm;
	}

	public void setApplyPluralForm(boolean applyPluralForm) {
		this.applyPluralForm = applyPluralForm;
	}


	@Override
	protected Configuration clone() {
		try {
			return (Configuration) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Types of collection instantiation modes.
	 */
	enum Instantiation {
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
}
