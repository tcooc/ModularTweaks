package tco.modulartweaks.module;

import org.objectweb.asm.Opcodes;

import tco.modulartweaks.ModularTweaksTransformer;
import net.minecraftforge.common.Configuration;

//loadConfigs and initialize are called (in that order) if the module is enabled
//transform is always called (even if module is disabled)
public interface IModule extends Opcodes {
	
	public void initialize();
	//should return a string with no whitespace
	public String getName();
	//cannot return null
	public String getDescription();

	//loadConfigs loads module specific configs (optional)
	//use config.get(getName(), ... for better organization
	public void loadConfigs(Configuration config);

	public void transform(ModularTweaksTransformer transformer, String name);
}
