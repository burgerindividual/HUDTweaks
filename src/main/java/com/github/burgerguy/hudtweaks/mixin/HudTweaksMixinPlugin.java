package com.github.burgerguy.hudtweaks.mixin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import net.fabricmc.loader.api.FabricLoader;

public class HudTweaksMixinPlugin implements IMixinConfigPlugin {
	
	public static final Path configFile = FabricLoader.getInstance().getConfigDir().resolve("hudtweaks.conf");
	
	private final Logger logger = LogManager.getLogger("HUDTweaks");
	
	@Override
	public void onLoad(String mixinPackage) {
		if (!Files.exists(configFile)) {
			try {
				Files.createFile(configFile);
			} catch (IOException e) {
				logger.catching(e);
			}
		}
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return true;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
	}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}
	
}
