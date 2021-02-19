package com.github.burgerguy.hudtweaks.hud.element;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.resource.language.I18n;

public class HTIdentifier {
	private final ElementType element;
	private final Namespace namespace;
	private final EntryName type;
	
	/**
	 * Uses the hudtweaks namespace and default type.
	 */
	protected HTIdentifier(ElementType element) {
		this(element, Namespace.HUDTWEAKS, EntryName.DEFAULT);
	}
	
	/**
	 * Uses the hudtweaks namespace.
	 */	
	public HTIdentifier(ElementType element, EntryName type) {
		this(element, Namespace.HUDTWEAKS, type);
	}
	
	/**
	 * Uses the default type.
	 */
	public HTIdentifier(ElementType element, Namespace namespace) {
		this(element, namespace, EntryName.DEFAULT);
	}
	
	public HTIdentifier(ElementType element, Namespace namespace, EntryName type) {
		this.namespace = namespace;
		this.element = element;
		this.type = type;
	}
	
	public ElementType getElementType() {
		return element;
	}

	public Namespace getNamespace() {
		return namespace;
	}

	public EntryName getType() {
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
	 * Used to identify the element type. For example, the health bar's
	 * identifier would be "health".
	 */
	public static class ElementType {
		private final String elementType;
		private transient final String translationKey;
		
		public ElementType(String element, @Nullable String translationKey) {
			this.elementType = element;
			this.translationKey = translationKey;
		}
		
		public boolean equals(Object obj) {
			if (!(obj instanceof ElementType)) return false;
			return this.elementType.equals(((ElementType) obj).elementType);
		}
		
		public String toString() {
			return elementType;
		}
		
		public String toTranslatedString() {
			return translationKey != null && I18n.hasTranslation(translationKey) ? I18n.translate(translationKey) : elementType;
		}
		
		public int hashCode() {
			return elementType.hashCode();
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
	 * Used to identify an entry of an element type. Should be short and
	 * simple. The default type if one isn't provided is "default". This
	 * really only should be used when one mod has multiple replacements
	 * for one element. Acceptible types could be "type1" or "type2", but
	 * it's better to give greater detail like "vertical' or "extended".
	 */
	public static class EntryName {
		public transient static final EntryName DEFAULT = new EntryName("default", "hudtweaks.element.type.default");
		private final String entryName;
		private transient final String translationKey;
		
		public EntryName(String entryName, @Nullable String translationKey) {
			this.entryName = entryName;
			this.translationKey = translationKey;
		}
		
		public boolean equals(Object obj) {
			if (!(obj instanceof EntryName)) return false;
			return this.entryName.equals(((EntryName) obj).entryName);
		}
		
		public String toString() {
			return entryName;
		}
		
		public String toTranslatedString() {
			return translationKey != null && I18n.hasTranslation(translationKey) ? I18n.translate(translationKey) : entryName;
		}
		
		public int hashCode() {
			return entryName.hashCode();
		}
	}
}
