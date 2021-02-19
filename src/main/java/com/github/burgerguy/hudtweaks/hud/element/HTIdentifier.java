package com.github.burgerguy.hudtweaks.hud.element;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.resource.language.I18n;

public class HTIdentifier {
	private final Element element;
	private final Namespace namespace;
	private final Type type;
	
	/**
	 * Uses the hudtweaks namespace and default type.
	 */
	protected HTIdentifier(Element element) {
		this(element, Namespace.HUDTWEAKS, Type.DEFAULT);
	}
	
	/**
	 * Uses the hudtweaks namespace.
	 */	
	public HTIdentifier(Element element, Type type) {
		this(element, Namespace.HUDTWEAKS, type);
	}
	
	/**
	 * Uses the default type.
	 */
	public HTIdentifier(Element element, Namespace namespace) {
		this(element, namespace, Type.DEFAULT);
	}
	
	public HTIdentifier(Element element, Namespace namespace, Type type) {
		this.namespace = namespace;
		this.element = element;
		this.type = type;
	}
	
	public Element getElement() {
		return element;
	}

	public Namespace getNamespace() {
		return namespace;
	}

	public Type getType() {
		return type;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof HTIdentifier)) return false;
		HTIdentifier other = (HTIdentifier) obj;
		return element.equals(other.element) && namespace.equals(other.namespace) && type.equals(other.type);
	}
	
	public String toString() {
		return element.toString() + ":" + namespace.toString() + ":" + type.toString();
	}
	
	public int hashCode() {
		return Objects.hash(element.toString(), namespace.toString(), type.toString());
	}
	
	/**
	 * Used to identify the element. For example, the health bar's
	 * identifier would be "health".
	 */
	public static class Element {
		private final String element;
		private transient final String translationKey;
		
		public Element(String element, @Nullable String translationKey) {
			this.element = element;
			this.translationKey = translationKey;
		}
		
		public boolean equals(Object obj) {
			if (!(obj instanceof Element)) return false;
			return this.element.equals(((Element) obj).element);
		}
		
		public String toString() {
			return element;
		}
		
		public String toTranslatedString() {
			return translationKey != null && I18n.hasTranslation(translationKey) ? I18n.translate(translationKey) : element;
		}
		
		public int hashCode() {
			return element.hashCode();
		}
	}
	
	/**
	 * Used to identify the namespace. Treated similarly to namespaces in
	 * normal minecraft identifiers, except they are before the element.
	 * Should be the name of the mod, but anything will do.
	 */
	public static class Namespace {
		/**
		 * Reserved for elements created by hudtweaks
		 */
		protected transient static final Namespace HUDTWEAKS = new Namespace();
		private final String namespace;
		private transient final String translationKey;
		
		private Namespace() {
			this.namespace = "hudtweaks";
			this.translationKey = "hudtweaks.name";
		}
		
		public Namespace(String namespace, @Nullable String translationKey) {
			if (namespace.equals("hudtweaks")) throw new UnsupportedOperationException("namespace \"hudtweaks\" is reserved");
			this.namespace = namespace;
			this.translationKey = translationKey;
		}
		
		public boolean equals(Object obj) {
			if (!(obj instanceof Namespace)) return false;
			return this.namespace.equals(((Namespace) obj).namespace);
		}
		
		public String toString() {
			return namespace;
		}
		
		public String toTranslatedString() {
			return translationKey != null && I18n.hasTranslation(translationKey) ? I18n.translate(translationKey) : namespace;
		}
		
		public int hashCode() {
			return namespace.hashCode();
		}
	}
	
	
	/**
	 * Used to identify the type of the element. Should be short and
	 * simple. The default type if one isn't provided is "default". This
	 * really only should be used when one mod has multiple replacements
	 * for one element. Acceptible types could be "type1" or "type2", but
	 * it's better to give greater detail like "vertical' or "extended".
	 */
	public static class Type {
		public transient static final Type DEFAULT = new Type("default", "hudtweaks.element.type.default");
		private final String type;
		private transient final String translationKey;
		
		public Type(String type, @Nullable String translationKey) {
			this.type = type;
			this.translationKey = translationKey;
		}
		
		public boolean equals(Object obj) {
			if (!(obj instanceof Type)) return false;
			return this.type.equals(((Type) obj).type);
		}
		
		public String toString() {
			return type;
		}
		
		public String toTranslatedString() {
			return translationKey != null && I18n.hasTranslation(translationKey) ? I18n.translate(translationKey) : type;
		}
		
		public int hashCode() {
			return type.hashCode();
		}
	}
}
