package tco.modulartweaks;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.minecraftforge.common.Configuration;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.IFMLCallHook;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

//explosion drop rate, cactus, lava flows like water, infinite lava
//keep toolbar, keep inv, keep armor, keep exp
//Better Villages, sparcs tweaks, bams improver, worldandgenerationtweaks (the bombzen), inventorycrafting
//glass recovery
//creepers, ghasts, endermen
@TransformerExclusions(value={"tco.modulartweaks"})
public class ModularTweaks implements IFMLLoadingPlugin, IFMLCallHook {
	public static final String ID = "ModularTweaks";
	public static final String VERSION = "1.0";

	public static ModularTweaks instance;

	public static Logger logger;

	public ModularTweaks() {
		instance = this;
		if(logger == null) {
			logger = Logger.getLogger(ID);
			logger.setParent(FMLLog.getLogger());
		}
	}

	public static final List<IModule> clientModules = new LinkedList<IModule>();
	public static final List<IModule> serverModules = new LinkedList<IModule>();

	public static void loadConfigs(Configuration config) {
		config.load();
		List<IModule> toRemove = new LinkedList<IModule>();
		for(IModule module : clientModules) {
			boolean enabled = config.get("Modules", module.getName(), false, module.getDescription()).getBoolean(false);
			//if(enabled) {
				module.loadConfigs(config);
			//} else {
				//toRemove.add(module); //TODO DEBUG
			//}
		}
		clientModules.removeAll(toRemove);
		toRemove.clear();
		for(IModule module : serverModules) {
			boolean enabled = config.get("Modules", module.getName(), false, module.getDescription()).getBoolean(false);
			//if(enabled) {
				module.loadConfigs(config);
			//} else {
				//toRemove.add(module); //TODO DEBUG
			//}
		}
		serverModules.removeAll(toRemove);
		config.save();
	}


	static{
		serverModules.add(new ModuleCactusProof());
		serverModules.add(new ModuleCheckId());
		clientModules.add(new ModuleDoubleDoors());
		serverModules.add(new ModuleStrongGlass());
		serverModules.add(new ModuleTreeGravity());
		serverModules.add(new ModuleDeath());
	}

	@Override
	public String[] getLibraryRequestClass() {
		return null;
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[] { "tco.modulartweaks.ModularTweaksTransformer" };
	}

	@Override
	public String getModContainerClass() {
		return "tco.modulartweaks.ModularTweaksModContainer";
	}

	@Override
	public String getSetupClass() {
		return "tco.modulartweaks.ModularTweaks";
	}

	@Override
	public void injectData(Map<String, Object> data) {}

	@Override
	public Void call() throws Exception {
		return null;
	}

}
