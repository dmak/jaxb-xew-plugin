package com.sun.tools.xjc.addon.xew;

import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import com.sun.codemodel.JAnnotatable;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JAnnotationValue;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFormatter;
import com.sun.codemodel.JGenerable;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIDeclaration;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIProperty;
import com.sun.tools.xjc.reader.xmlschema.bindinfo.BindInfo;
import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.XSComponent;
import com.sun.xml.xsom.XSDeclaration;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSTerm;

public final class CommonUtils {

	/**
	 * Returns {@code true} if given class is hidden, that is not generated & saved by XJC. These are for example
	 * instances of {@link JCodeModel.JReferencedClass} (JVM-wide classes) or instances of {@link JDefinedClass} with
	 * hidden flag (customized super-class or super-interface).
	 */
	public static boolean isHiddenClass(JClass clazz) {
		// See also https://java.net/jira/browse/JAXB-958
		return !(clazz instanceof JDefinedClass) || ((JDefinedClass) clazz).isHidden();
	}

	/**
	 * Returns <code>true</code> of the given <code>type</code> is {@link JClass} and contains <code>classToCheck</code>
	 * in the list of parametrisations.
	 */
	public static boolean isListedAsParametrisation(JClass classToCheck, JType type) {
		return type instanceof JClass && ((JClass) type).getTypeParameters().contains(classToCheck);
	}

	//
	// Annotation helpers.
	//

	/**
	 * Returns the annotation for the given field.
	 */
	public static JAnnotationUse getAnnotation(JAnnotatable annotatable, JClass annotationClass) {
		for (JAnnotationUse annotation : annotatable.annotations()) {
			if (annotation.getAnnotationClass().equals(annotationClass)) {
				return annotation;
			}
		}

		return null;
	}

	/**
	 * Returns the annotation element as {@href JAnnotationValue}.
	 */
	public static JAnnotationValue getAnnotationMember(JAnnotationUse annotation, String annotationMember) {
		if (annotation == null) {
			return null;
		}

		// FIXME: Workaround for https://java.net/jira/browse/JAXB-1040:
		Map<String, JAnnotationValue> memberValues = (Map<String, JAnnotationValue>) getPrivateField(annotation,
		            "memberValues");

		if (memberValues == null) {
			return null;
		}

		return memberValues.get(annotationMember);
	}

	/**
	 * Returns the value of annotation element as {@href JExpression}. For example, for annotation
	 * <code>@XmlElementRef(name = "last-name", namespace = "http://mycompany.org/exchange", type = JAXBElement.class)</code>
	 * for member <code>name</code> the value <code>last-name</code> will be returned.
	 */
	public static JExpression getAnnotationMemberExpression(JAnnotationUse annotation, String annotationMember) {
		JAnnotationValue annotationValue = getAnnotationMember(annotation, annotationMember);

		if (annotationValue == null) {
			return null;
		}

		// FIXME: Pending for https://java.net/jira/browse/JAXB-878
		try {
			// In most cases the value is some expression...
			return (JExpression) getPrivateField(annotationValue, annotationValue.getClass(), "value");
		}
		catch (NoSuchFieldException e) {
			// ... and in some cases (like enum) do the conversion from JGenerable to JExpression
			// (a bit unoptimal, since this expression is going to be converted back to string)
			return JExpr.lit(generableToString(annotationValue));
		}
	}

	/**
	 * Append the given annotation to list of annotations.
	 */
	@SuppressWarnings("unchecked")
	public static void addAnnotation(JVar field, JAnnotationUse annotation) {
		((List<JAnnotationUse>) getPrivateField(field, "annotations")).add(annotation);
	}

	/**
	 * Remove the given annotation from the list of annotations.
	 */
	@SuppressWarnings("unchecked")
	public static void removeAnnotation(JVar field, JAnnotationUse annotation) {
		((List<JAnnotationUse>) getPrivateField(field, "annotations")).remove(annotation);
	}

	/**
	 * Check that given field property has name customization ({@code <jaxb:property name="..." />}).
	 * 
	 * @see com.sun.xml.bind.api.impl.NameUtil
	 * @see com.sun.codemodel.JJavaName
	 * @see com.sun.tools.xjc.reader.xmlschema.bindinfo.BIProperty#getCustomization(XSComponent)
	 */
	public static boolean hasPropertyNameCustomization(CPropertyInfo fieldPropertyInfo) {
		XSAnnotation annotation = fieldPropertyInfo.getSchemaComponent().getAnnotation();

		if (annotation == null) {
			annotation = getXsdDeclaration(fieldPropertyInfo).getAnnotation();
		}

		if (annotation == null || !(annotation.getAnnotation() instanceof BindInfo)) {
			return false;
		}

		for (BIDeclaration declaration : (BindInfo) annotation.getAnnotation()) {
			if (declaration instanceof BIProperty) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns the string value of passed argument.
	 */
	public static final String generableToString(JGenerable generable) {
		// There is hardly any clean universal way to get the value from e.g. JExpression except of serializing it.
		// Compare JStringLiteral and JExp#dotclass().
		Writer w = new StringWriter();

		generable.generate(new JFormatter(w));

		// FIXME: Hopefully nobody will put quotes into annotation member value.
		return w.toString().replace("\"", "");
	}

	//
	// Reflection helpers.
	//

	public static void setPrivateField(Object obj, String fieldName, Object newValue) {
		try {
			setPrivateField(obj, obj.getClass(), fieldName, newValue);
		}
		catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Set the {@code newValue} to private field {@code fieldName} of given object {@code obj}.
	 * 
	 * @throws NoSuchFieldException
	 *             if given field was not found
	 */
	private static void setPrivateField(Object obj, Class<?> clazz, String fieldName, Object newValue)
	            throws NoSuchFieldException {
		try {
			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(obj, newValue);
		}
		catch (NoSuchFieldException e) {
			if (clazz.getSuperclass() == Object.class) {
				// Field is really not found:
				throw e;
			}

			// Try super class:
			setPrivateField(obj, clazz.getSuperclass(), fieldName, newValue);
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get the value of private field {@code fieldName} of given object {@code obj}.
	 */
	public static Object getPrivateField(Object obj, String fieldName) {
		try {
			return getPrivateField(obj, obj.getClass(), fieldName);
		}
		catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get the value of private field {@code fieldName} of given object {@code obj} which is treated as class
	 * {@code clazz}.
	 * 
	 * @throws NoSuchFieldException
	 *             if given field was not found
	 */
	private static Object getPrivateField(Object obj, Class<?> clazz, String fieldName) throws NoSuchFieldException {
		try {
			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(obj);
		}
		catch (NoSuchFieldException e) {
			if (clazz.getSuperclass() == Object.class) {
				// Field is really not found:
				throw e;
			}

			// Try super class:
			return getPrivateField(obj, clazz.getSuperclass(), fieldName);
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns XSD declaration of given property.
	 */
	public static XSDeclaration getXsdDeclaration(CPropertyInfo propertyInfo) {
		XSComponent schemaComponent = propertyInfo.getSchemaComponent();

		if (!(schemaComponent instanceof XSParticle)) {
			// XSComplexType for example:
			return null;
		}

		XSTerm term = ((XSParticle) schemaComponent).getTerm();

		if (!(term instanceof XSDeclaration)) {
			// XSModelGroup for example:
			return null;
		}

		return (XSDeclaration) term;
	}
}
