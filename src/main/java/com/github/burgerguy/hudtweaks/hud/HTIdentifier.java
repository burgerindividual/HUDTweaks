package com.github.burgerguy.hudtweaks.hud;

import com.google.gson.JsonParseException;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.resource.language.I18n;

import java.util.Objects;

public final class HTIdentifier {
	private final ModId modId;
	private final ElementId elementId;

	public HTIdentifier(ModId modId, ElementId elementId) {
		this.modId = modId;
		this.elementId = elementId;
	}

	public ModId getModId() {
		return modId;
	}

	public ElementId getElementId() {
		return elementId;
	}

	/**
	 * Should ONLY be used for profile loading/saving.
	 * This does NOT include any translation strings.
	 */
	public static HTIdentifier fromString(String string) {
		String[] split = string.split(":");
		if (split.length == 2) {
			return new HTIdentifier(new HTIdentifier.ModId(split[0], null), new HTIdentifier.ElementId(split[1], null));
		} else {
			throw new JsonParseException("Unable to parse identifier string \"" + string + "\"");
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		HTIdentifier that = (HTIdentifier) obj;
		return Objects.equals(this.modId, that.modId) &&
				Objects.equals(this.elementId, that.elementId);
	}

	@Override
	public String toString() {
		return modId.toString() + ':' + elementId.toString();
	}

	public String toTranslatedString() {
		return modId.toTranslatedString() + ':' + elementId.toTranslatedString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(modId, elementId);
	}

	public static class ModId {
		private final String modId;
		private final String translationKey;

		public ModId(String modId, @Nullable String translationKey) {
			this.modId = modId;
			this.translationKey = translationKey;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ModId)) return false;
			return modId.equals(((ModId) obj).modId);
		}

		@Override
		public String toString() {
			return modId;
		}

		public String toTranslatedString() {
			return translationKey != null && I18n.hasTranslation(translationKey) ? I18n.translate(translationKey) : modId;
		}

		@Override
		public int hashCode() {
			return modId.hashCode();
		}
	}

	/**
	 * Used to identify the element entryName. For example, the health bar's
	 * identifier would be "health".
	 */
	public static class ElementId {
		private final String elementId;
		private final String translationKey;

		public ElementId(String element, @Nullable String translationKey) {
			elementId = element;
			this.translationKey = translationKey;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ElementId)) return false;
			return elementId.equals(((ElementId) obj).elementId);
		}

		@Override
		public String toString() {
			return elementId;
		}

		public String toTranslatedString() {
			return translationKey != null && I18n.hasTranslation(translationKey) ? I18n.translate(translationKey) : elementId;
		}

		@Override
		public int hashCode() {
			return elementId.hashCode();
		}
	}
}
