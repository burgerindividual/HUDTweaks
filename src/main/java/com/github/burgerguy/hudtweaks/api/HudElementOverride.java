package com.github.burgerguy.hudtweaks.api;

import com.github.burgerguy.hudtweaks.hud.HTIdentifier;
import com.github.burgerguy.hudtweaks.hud.element.HudElement;

import java.util.function.BooleanSupplier;

public class HudElementOverride {
    private final HTIdentifier overrideTarget;
    private final HudElement override;
    private final BooleanSupplier isEnabledSupplier;

    public HudElementOverride(HTIdentifier overrideTarget, HudElement override, RenderStateUpdater updater, BooleanSupplier isEnabledSupplier) {
        this.overrideTarget = overrideTarget;
        this.override = override;
        this.isEnabledSupplier = isEnabledSupplier;
        updater.fill(ms -> override.getContainerNode().tryPushMatrix(override.getIdentifier(), ms),
                ms -> override.getContainerNode().tryPopMatrix(override.getIdentifier(), ms));
    }

    public HTIdentifier getOverrideTarget() {
        return overrideTarget;
    }

    public HudElement getElement() {
        return override;
    }

    public boolean isEnabled() {
        return isEnabledSupplier.getAsBoolean();
    }
}
