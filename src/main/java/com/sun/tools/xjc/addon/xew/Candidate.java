package com.sun.tools.xjc.addon.xew;

import static com.sun.tools.xjc.addon.xew.CommonUtils.generableToString;
import static com.sun.tools.xjc.addon.xew.CommonUtils.getAnnotation;
import static com.sun.tools.xjc.addon.xew.CommonUtils.getAnnotationMemberExpression;

import java.util.HashMap;
import java.util.Map;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CPropertyInfo;

/**
 * Describes the collection container class - a candidate for removal. This class class has only one field - collection
 * of objects.
 */
public final class Candidate {
	private final JDefinedClass					 candidateClass;

	private final JFieldVar						 field;

	private final CPropertyInfo					 fieldPropertyInfo;

	private final String						 fieldTargetNamespace;

	private final JDefinedClass					 fieldParametrisationClass;

	private final JDefinedClass					 fieldParametrisationImpl;

	private final JDefinedClass					 objectFactoryClass;

	private final JDefinedClass					 valueObjectFactoryClass;

	private final Map<String, ScopedElementInfo> scopedElementInfos	= new HashMap<String, ScopedElementInfo>();

	/**
	 * By default the candidate is marked for removal unless something prevents it from being removed.
	 */
	private boolean								 markedForRemoval	= true;

	/**
	 * Number of times this candidate has been substituted in the model.
	 */
	private int									 substitutionsCount;

	Candidate(JDefinedClass candidateClass, CClassInfo candidateClassInfo, JFieldVar field, String fieldTargetNamespace,
	            JDefinedClass fieldParametrizationClass, JDefinedClass fieldParametrisationImpl,
	            JDefinedClass objectFactoryClass, JDefinedClass valueObjectFactoryClass,
	            JClass xmlElementDeclModelClass) {
		this.candidateClass = candidateClass;
		this.field = field;
		this.fieldPropertyInfo = candidateClassInfo.getProperty(field.name());
		this.fieldTargetNamespace = fieldTargetNamespace;
		this.fieldParametrisationClass = fieldParametrizationClass;
		this.fieldParametrisationImpl = fieldParametrisationImpl;
		this.objectFactoryClass = objectFactoryClass;
		this.valueObjectFactoryClass = valueObjectFactoryClass;

		String dotClazz = candidateClass.fullName() + ".class";

		for (JMethod method : valueObjectFactoryClass.methods()) {
			JAnnotationUse xmlElementDeclAnnotation = getAnnotation(method, xmlElementDeclModelClass);
			JExpression scope = getAnnotationMemberExpression(xmlElementDeclAnnotation, "scope");

			if (scope == null || !dotClazz.equals(generableToString(scope))) {
				continue;
			}

			scopedElementInfos.put(method.name(),
			            new ScopedElementInfo(getAnnotationMemberExpression(xmlElementDeclAnnotation, "name"),
			                        getAnnotationMemberExpression(xmlElementDeclAnnotation, "namespace"),
			                        method.params().get(0).type()));
		}
	}

	/**
	 * Container class
	 */
	public JDefinedClass getClazz() {
		return candidateClass;
	}

	/**
	 * Container class name
	 */
	public String getClassName() {
		return candidateClass.fullName();
	}

	/**
	 * The only field in container class (collection property).
	 */
	public JFieldVar getField() {
		return field;
	}

	/**
	 * The name of the only field in container class.
	 */
	public String getFieldName() {
		return field.name();
	}

	/**
	 * The class of the only field in container class (collection interface or concrete implementation).
	 */
	public JClass getFieldClass() {
		return (JClass) field.type();
	}

	/**
	 * The corresponding property info of the only field in container class.
	 */
	public CPropertyInfo getFieldPropertyInfo() {
		return fieldPropertyInfo;
	}

	/**
	 * The XSD namespace of the property associated with a field.
	 */
	public String getFieldTargetNamespace() {
		return fieldTargetNamespace;
	}

	/**
	 * The only parametrisation class of the field (collection type). In case of basic parametrisation like {@link List
	 * <String>} this property is {@code null}.
	 */
	public JDefinedClass getFieldParametrisationClass() {
		return fieldParametrisationClass;
	}

	/**
	 * If {@link #getFieldParametrisationClass()} is an interface, then this holds the same value. Otherwise it holds
	 * the implementation (value object) of {@link #getFieldParametrisationClass()}. In case of basic parametrisation
	 * like {@code List<String>} this property is {@code null}.
	 */
	public JDefinedClass getFieldParametrisationImpl() {
		return fieldParametrisationImpl;
	}

	/**
	 * Return information about scoped elements, that have this candidate as a scope.
	 * 
	 * @return object factory method name -to- element info map
	 */
	public Map<String, ScopedElementInfo> getScopedElementInfos() {
		return scopedElementInfos;
	}

	/**
	 * Object Factory class for interface classes. It's usually located in {@code impl.} subpackage relative to
	 * {@code valueObjectFactoryClass} package. May be {@code null}.
	 */
	public JDefinedClass getObjectFactoryClass() {
		return objectFactoryClass;
	}

	/**
	 * Object Factory class for value (implementation) classes. Is not {@code null}.
	 */
	public JDefinedClass getValueObjectFactoryClass() {
		return valueObjectFactoryClass;
	}

	/**
	 * Returns {@code true} if the setting {@code <jaxb:globalBindings generateValueClass="false">} is active.
	 */
	public boolean isValueObjectDisabled() {
		return objectFactoryClass != null;
	}

	/**
	 * Has given candidate green light to be removed?
	 */
	public boolean canBeRemoved() {
		return markedForRemoval && substitutionsCount > 0;
	}

	/**
	 * Increments number of substitutions for this candidate.
	 */
	public void incrementSubstitutions() {
		substitutionsCount++;
	}

	/**
	 * Signal that this candidate should not be removed from model on some reason.
	 */
	public void unmarkForRemoval() {
		this.markedForRemoval = false;
	}

	@Override
	public String toString() {
		return "Candidate[" + getClassName() + " field " + getFieldClass().name() + " " + getFieldName() + "]";
	}
}