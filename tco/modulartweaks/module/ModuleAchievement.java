package tco.modulartweaks.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.achievement.GuiAchievement;
import net.minecraft.stats.Achievement;
import tco.modulartweaks.ObfuscationDecoder;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;

public class ModuleAchievement extends ModuleImpl {

	private static final class DummyGuiAchievement extends GuiAchievement {
		public DummyGuiAchievement(Minecraft mc) {
			super(mc);
		}
		@Override
		public void queueAchievementInformation(Achievement par1Achievement) {}
		@Override
		public void queueTakenAchievement(Achievement par1Achievement) {}
		@Override
		public void updateAchievementWindow() {}
	}

	@Override
	public String getDescription() {
		return "Disable achievement popups (while keeping achievements).";
	}

	@Override
	public String getName() {
		return "Achievement";
	}

	@Override
	public void initialize() {
		Minecraft mc = FMLClientHandler.instance().getClient();
		try {
			ObfuscationReflectionHelper.setPrivateValue(
					Minecraft.class, mc, new DummyGuiAchievement(mc),
					"guiAchievement", ObfuscationDecoder.getObf("guiAchievement"));
		} catch(Exception e) {
			throw new RuntimeException("Obfuscation error", e);
		}
	}

}
