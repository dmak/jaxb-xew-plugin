package com.sun.tools.xjc.addon.xew;

import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.List;

import com.sun.codemodel.JAnnotatable;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JAnnotationValue;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFormatter;
import com.sun.codemodel.JGenerable;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import com.sun.tools.xjc.model.CPropertyInfo;
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
	 * Append the given annotation to list of annotations.
	 */
	@SuppressWarnings("unchecked")
	public static void addAnnotation(JVar field, JAnnotationUse annotation) {
		((List<JAnnotationUse>) getPrivateField(field, JVar.class, "annotations")).add(annotation);
	}

	/**
	 * Remove the given annotation from the list of annotations.
	 */
	@SuppressWarnings("unchecked")
	public static void removeAnnotation(JVar field, JAnnotationUse annotation) {
		((List<JAnnotationUse>) getPrivateField(field, JVar.class, "annotations")).remove(annotation);
	}

	/**
	 * Returns the string value of annotation element. For example, for annotation
	 * <code>@XmlElementRef(name = "last-name", namespace = "http://mycompany.org/exchange", type = JAXBElement.class)</code>
	 * for member <code>name</code> the value <code>last-name</code> will be returned.
	 */
	public static final JExpression getAnnotationMemberExpression(JAnnotationUse annotation, String annotationMember) {
		if (annotation == null) {
			return null;
		}

		JAnnotationValue annotationValue = annotation.getAnnotationMembers().get(annotationMember);

		if (annotationValue == null) {
			return null;
		}

		// FIXME: Pending for https://java.net/jira/browse/JAXB-878
		return (JExpression) getPrivateField(annotationValue, "value");
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
		setPrivateField(obj, obj.getClass(), fieldName, newValue);
	}

	/**
	 * Set the {@code newValue} to private field {@code fieldName} of given object {@code obj}.
	 */
	private static void setPrivateField(Object obj, Class<?> clazz, String fieldName, Object newValue) {
		try {
			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(obj, newValue);
		}
		catch (NoSuchFieldException e) {
			if (clazz.getSuperclass() == Object.class) {
				// Field is really not found:
				throw new RuntimeException(e);
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
		return getPrivateField(obj, obj.getClass(), fieldName);
	}

	/**
	 * Get the value of private field {@code fieldName} of given object {@code obj} which is treated as class
	 * {@code clazz}.
	 */
	private static Object getPrivateField(Object obj, Class<?> clazz, String fieldName) {
		try {
			Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(obj);
		}
		catch (NoSuchFieldException e) {
			if (clazz.getSuperclass() == Object.class) {
				// Field is really not found:
				throw new RuntimeException(e);
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
