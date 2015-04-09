package ch.hsr.ogv.util;

import ch.hsr.ogv.model.Attribute;
import ch.hsr.ogv.model.ModelClass;
import ch.hsr.ogv.model.ModelObject;
import ch.hsr.ogv.model.Relation;

public class ModelUtil {

	public static boolean isClass(Object object) {
		return (object instanceof ModelClass);
	}

	public static boolean isObject(Object object) {
		return (object instanceof ModelObject);
	}

	public static boolean isRelation(Object object) {
		return (object instanceof Relation);
	}

	public static boolean isAttribute(Object object) {
		return (object instanceof Attribute);
	}

}
