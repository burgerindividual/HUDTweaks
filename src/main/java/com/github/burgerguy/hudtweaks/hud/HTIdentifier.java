package com.github.burgerguy.hudtweaks.hud;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.resource.language.I18n;

public class HTIdentifier {
	private final ElementType element;
	private final Namespace namespace;
	private final EntryName entryName;

	/**
	 * Uses the default entry name.
	 */
	public HTIdentifier(ElementType element, Namespace namespace) {
		this(element, namespace, EntryName.DEFAULT);
	}

	public HTIdentifier(ElementType element, Namespace namespace, EntryName entryName) {
		this.namespace = namespace;
		this.element = element;
		this.entryName = entryName;
	}

	public ElementType getElementType() {
		return element;
	}
	
	public Namespace getNamespace() {
		return namespace;
	}
	
	public EntryName getEntryName() {
		return entryName;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof HTIdentifier)) return false;
		HTIdentifier other = (HTIdentifier) obj;
		return element.equals(other.element) && namespace.equals(other.namespace) && entryName.equals(other.entryName);
	}

	@Override
	public String toString() {
		return element.toString() + ":" + namespace.toString() + ":" + entryName.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(element.toString(), namespace.toString(), entryName.toString());
	}

	/**
	 * Used to identify the element entryName. For example, the health bar's
	 * identifier would be "health".
	 */
	public static class ElementType {
		private final String elementType;
		private transient final String translationKey;

		public ElementType(String element, @Nullable String translationKey) {
			elementType = element;
			this.translationKey = translationKey;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ElementType)) return false;
			return elementType.equals(((ElementType) obj).elementType);
		}

		@Override
		public String toString() {
			return elementType;
		}

		public String toTranslatedString() {
			return translationKey != null && I18n.hasTranslation(translationKey) ? I18n.translate(translationKey) : elementType;
		}

		@Override
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
		private final String namespace;
		private transient final String translationKey;

		public Namespace(String namespace, @Nullable String translationKey) {
			this.namespace = namespace;
			this.translationKey = translationKey;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Namespace)) return false;
			return namespace.equals(((Namespace) obj).namespace);
		}

		@Override
		public String toString() {
			return namespace;
		}

		public String toTranslatedString() {
			return translationKey != null && I18n.hasTranslation(translationKey) ? I18n.translate(translationKey) : namespace;
		}

		@Override
		public int hashCode() {
			return namespace.hashCode();
		}
	}


	/**
	 * Used to identify an entry of an element entryName. Should be short and
	 * simple. The default entryName if one isn't provided is "default". This
	 * really only should be used when one mod has multiple replacements
	 * for one element. Acceptible types could be "type1" or "type2", but
	 * it's better to give greater detail like "vertical' or "extended".
	 */
	public static class EntryName {
		public transient static final EntryName DEFAULT = new EntryName("default", "hudtweaks.element.entryname.default");
		private final String entryName;
		private transient final String translationKey;

		public EntryName(String entryName, @Nullable String translationKey) {
			this.entryName = entryName;
			this.translationKey = translationKey;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof EntryName)) return false;
			return entryName.equals(((EntryName) obj).entryName);
		}

		@Override
		public String toString() {
			return entryName;
		}

		public String toTranslatedString() {
			return translationKey != null && I18n.hasTranslation(translationKey) ? I18n.translate(translationKey) : entryName;
		}

		@Override
		public int hashCode() {
			return entryName.hashCode();
		}
	}
}
