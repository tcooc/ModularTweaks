package tco.modulartweaks;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import tco.modulartweaks.module.IModule;
import tco.modulartweaks.module.ModuleCactusProof;
import tco.modulartweaks.module.ModuleCheckId;
import tco.modulartweaks.module.ModuleCrafting;
import tco.modulartweaks.module.ModuleDeath;
import tco.modulartweaks.module.ModuleDoubleDoors;
import tco.modulartweaks.module.ModuleSignEdit;
import tco.modulartweaks.module.ModuleStack;
import tco.modulartweaks.module.ModuleStrongGlass;
import tco.modulartweaks.module.ModuleTreeFall;
import tco.modulartweaks.module.ModuleExplosion;

import net.minecraftforge.common.Configuration;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.IFMLCallHook;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

//lava decay, infinite lava
//3x3 crafting
//double door rs
@TransformerExclusions(value={"tco.modulartweaks"})
public class ModularTweaks implements IFMLLoadingPlugin, IFMLCallHook {
	public static final String ID = "ModularTweaks";
	public static final String VERSION = "1.0";

	private static final boolean DEBUG = false;

	public static ModularTweaks instance;
	public static Logger logger;
	
	public final List<IModule> clientModules = new LinkedList<IModule>();
	public final List<IModule> commonModules = new LinkedList<IModule>();

	public ModularTweaks() {
		instance = this;
		if(logger == null) {
			logger = Logger.getLogger(ID);
			logger.setParent(FMLLog.getLogger());
			if(!DEBUG) {
				logger.setLevel(Level.INFO);
			} else {
				//extend default logging handling
				ConsoleHandler handler = new ConsoleHandler();
				handler.setFormatter(new Formatter() {
					@Override
					public String format(LogRecord record) {
						if(record.getLevel().intValue() < Level.INFO.intValue()) {
							return "[" + record.getLoggerName() + "] ["
									 + record.getLevel() + "] " + record.getMessage() + "\n";
						}
						return "";
					}
				});
				handler.setLevel(Level.FINEST);
				logger.addHandler(handler);
				logger.setLevel(Level.FINEST);
			}
		}
	}

	public void initialize() {
		clientModules.add(new ModuleDoubleDoors());
		commonModules.add(new ModuleCactusProof());
		commonModules.add(new ModuleCheckId());
		commonModules.add(new ModuleStrongGlass());
		commonModules.add(new ModuleTreeFall());
		commonModules.add(new ModuleDeath());
		commonModules.add(new ModuleSignEdit());
		commonModules.add(new ModuleStack());
		commonModules.add(new ModuleExplosion());
		commonModules.add(new ModuleCrafting());
	}

	public void loadConfigs(Configuration config) {
		config.load();
		List<IModule> toRemove = new LinkedList<IModule>();
		for(IModule module : clientModules) {
			boolean enabled = DEBUG || config.get("Modules", module.getName(), false, module.getDescription()).getBoolean(false);
			if(enabled) {
				module.loadConfigs(config);
			} else {
				toRemove.add(module);
			}
		}
		clientModules.removeAll(toRemove);
		toRemove.clear();
		for(IModule module : commonModules) {
			boolean enabled = DEBUG || config.get("Modules", module.getName(), false, module.getDescription()).getBoolean(false);
			if(enabled) {
				module.loadConfigs(config);
			} else {
				toRemove.add(module);
			}
		}
		commonModules.removeAll(toRemove);
		config.save();
	}

	@Override
	public String[] getLibraryRequestClass() {
		//use default libraries that fml uses
		return new String[] { "cpw.mods.fml.relauncher.CoreFMLLibraries" };
	}

	@Override
	public String[] getASMTransformerClass() {
		//return IClassTransformer
		return new String[] { "tco.modulartweaks.ModularTweaksTransformer" };
	}

	@Override
	public String getModContainerClass() {
		//return a ModContainer
		return "tco.modulartweaks.ModularTweaksModContainer";
	}

	@Override
	public String getSetupClass() {
		//return a IFMLCallHook
		return "tco.modulartweaks.ModularTweaks";
	}

	@Override
	public void injectData(Map<String, Object> data) {
		//??
	}

	@Override
	public Void call() throws Exception {
		initialize();
		return null;
	}

}
