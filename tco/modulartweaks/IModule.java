package tco.modulartweaks;

import org.objectweb.asm.tree.ClassNode;

import net.minecraftforge.common.Configuration;

//transform is always called (even if module is disabled)
//if a module is enabled, call loadConfigs, then initialize
//loadconfigs load module specific configs (optional)
public interface IModule {
	public void initialize();
	public String getName();
	public String getDescription();
	public void loadConfigs(Configuration config);
	public void transform(ModularTweaksTransformer transformer, String name);
}
