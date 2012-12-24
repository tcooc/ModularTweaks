package tco.modulartweaks;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import tco.modulartweaks.module.IModule;
import tco.modulartweaks.module.ModuleCactusProof;
import tco.modulartweaks.module.ModuleCheckId;
import tco.modulartweaks.module.ModuleDeath;
import tco.modulartweaks.module.ModuleDoubleDoors;
import tco.modulartweaks.module.ModuleSignEdit;
import tco.modulartweaks.module.ModuleStack;
import tco.modulartweaks.module.ModuleStrongGlass;
import tco.modulartweaks.module.ModuleTreeGravity;

import net.minecraftforge.common.Configuration;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.IFMLCallHook;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

//explosion drop rate
//lava flows like water, infinite lava
//death: keep toolbar, keep inv, keep armor, keep exp
//Better Villages, sparcs tweaks, bams improver, worldandgenerationtweaks (the bombzen), inventorycrafting
//endermen
//double door rs
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

	public void initialize() {
		clientModules.add(new ModuleDoubleDoors());
		commonModules.add(new ModuleCactusProof());
		commonModules.add(new ModuleCheckId()); //functional
		commonModules.add(new ModuleStrongGlass()); //functional
		commonModules.add(new ModuleTreeGravity());
		commonModules.add(new ModuleDeath()); //functional
		commonModules.add(new ModuleSignEdit()); //functional
		commonModules.add(new ModuleStack()); //functional
	}

	public final List<IModule> clientModules = new LinkedList<IModule>();
	public final List<IModule> commonModules = new LinkedList<IModule>();

	public void loadConfigs(Configuration config) {
		config.load();
		List<IModule> toRemove = new LinkedList<IModule>();
		for(IModule module : clientModules) {
			boolean enabled = config.get("Modules", module.getName(), false, module.getDescription()).getBoolean(false);
			if(enabled) {
				module.loadConfigs(config);
			} else {
				toRemove.add(module); //TODO DEBUG
			}
		}
		clientModules.removeAll(toRemove);
		toRemove.clear();
		for(IModule module : commonModules) {
			boolean enabled = config.get("Modules", module.getName(), false, module.getDescription()).getBoolean(false);
			if(enabled) {
				module.loadConfigs(config);
			} else {
				toRemove.add(module); //TODO DEBUG
			}
		}
		commonModules.removeAll(toRemove);
		config.save();
	}

	@Override
	public String[] getLibraryRequestClass() {
		return new String[] { "cpw.mods.fml.relauncher.CoreFMLLibraries" };
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
	public void injectData(Map<String, Object> data) {
	}

	@Override
	public Void call() throws Exception {
		initialize();
		return null;
	}

}
