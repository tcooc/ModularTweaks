package tco.modulartweaks.module;

import tco.modulartweaks.ModularTweaksTransformer;
import net.minecraftforge.common.Configuration;

//transform is always called (even if module is disabled)
//loadConfigs and initialize are called (in that order) if enabled
//loadconfigs loads module specific configs (optional)
//use config.get(getName(), ... for getter organization
public interface IModule {
	public void initialize();
	public String getName();
	public String getDescription();
	public void loadConfigs(Configuration config);
	public void transform(ModularTweaksTransformer transformer, String name);
}
