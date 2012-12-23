package tco.modulartweaks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import cpw.mods.fml.common.ObfuscationReflectionHelper;

public class ModuleStrongGlass implements Module {
	@Override
	public void initialize() {
		ObfuscationReflectionHelper.setPrivateValue(
				BlockPane.class, (BlockPane) Block.thinGlass, true,
				"canDropItself", ObfuscationHelper.get("canDropItself"));
	}

	@Override
	public String getName() {
		return "Strong Glass";
	}
	
	public static int onGlassBreak(Random rand) {
		return 1;
	}
}
