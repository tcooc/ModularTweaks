package tco.modulartweaks.module;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.achievement.GuiAchievement;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.Configuration;
import tco.modulartweaks.ModularTweaksTransformer;
import tco.modulartweaks.ObfuscationDecoder;

public class ModuleAchievement implements IModule {

	private static final class DummyGuiAchievement extends GuiAchievement {
		public DummyGuiAchievement(Minecraft mc) {
			super(mc);
		}
		@Override
		public void queueTakenAchievement(Achievement par1Achievement) {}
		@Override
		public void queueAchievementInformation(Achievement par1Achievement) {}
		@Override
		public void updateAchievementWindow() {}
	}

	@Override
	public void initialize() {
		Minecraft mc = FMLClientHandler.instance().getClient();
		ObfuscationReflectionHelper.setPrivateValue(
				Minecraft.class, mc, new DummyGuiAchievement(mc),
				"guiAchievement", ObfuscationDecoder.getObf("guiAchievement"));
	}

	@Override
	public String getName() {
		return "Achievement";
	}

	@Override
	public String getDescription() {
		return "Disable achievement popups (while keeping achievements).";
	}

	@Override
	public void loadConfigs(Configuration config) {
	}

	@Override
	public void transform(ModularTweaksTransformer transformer, String name) {
	}

}
