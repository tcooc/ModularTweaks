package tco.modulartweaks;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import tco.modulartweaks.module.IModule;
import tco.modulartweaks.module.ModuleAchievement;
import tco.modulartweaks.module.ModuleCactusProof;
import tco.modulartweaks.module.ModuleCheckId;
import tco.modulartweaks.module.ModuleCrafting;
import tco.modulartweaks.module.ModuleDeath;
import tco.modulartweaks.module.ModuleDoubleDoors;
import tco.modulartweaks.module.ModuleEntityLimit;
import tco.modulartweaks.module.ModuleExplosion;
import tco.modulartweaks.module.ModuleSignEdit;
import tco.modulartweaks.module.ModuleStack;
import tco.modulartweaks.module.ModuleStrongGlass;
import tco.modulartweaks.module.ModuleTreeFall;
import tco.modulartweaks.module.mjirc.ModuleMJIrc;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.IFMLCallHook;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;
//lava decay, infinite lava
//double door rs
@TransformerExclusions(value={"tco.modulartweaks"})
public class ModularTweaks implements IFMLLoadingPlugin, IFMLCallHook {
	public static final String ID = "ModularTweaks";
	public static final String VERSION = "1.0";

	static final boolean DEBUG = true;

	public static ModularTweaks instance;
	public static Logger logger;

	public Configuration configuration;

	public final List<IModule> modules = new LinkedList<IModule>();
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
		addModule(new ModuleDoubleDoors(), true);
		addModule(new ModuleAchievement(), true);
		addModule(new ModuleMJIrc(), true);
		addModule(new ModuleCactusProof(), false);
		addModule(new ModuleCheckId(), false);
		addModule(new ModuleStrongGlass(), false);
		addModule(new ModuleTreeFall(), false);
		addModule(new ModuleDeath(), false);
		addModule(new ModuleSignEdit(), false);
		addModule(new ModuleStack(), false);
		addModule(new ModuleExplosion(), false);
		addModule(new ModuleCrafting(), false);
		addModule(new ModuleEntityLimit(), false);
	}

	public void addModule(IModule module, boolean client) {
		modules.add(module);
		if(client) {
			clientModules.add(module);
		} else {
			commonModules.add(module);
		}
	}


	public void initializeActivatedModules(List<IModule> modules) {
		for(IModule module : modules) {
			if(DEBUG || configuration.get("Modules", module.getName(), false, module.getDescription()).getBoolean(false)) {
				logger.info(module.getName());
				module.initialize();
			}
		}
	}

	public void loadConfigs() {
		configuration.load();
		for(IModule module : modules) {
			boolean enabled = DEBUG || configuration.get("Modules", module.getName(), false, module.getDescription()).getBoolean(false);
			Property[] props = module.getConfig();
			for(int i = 0; i < props.length; i++) {
				Property prop = props[i];
				props[i] = configuration.get(module.getName(), prop.getName(), prop.value, prop.comment, prop.getType());
			}
			if(enabled) {
				for(Property prop : props) {
					module.setConfig(prop.getName(), prop.value);
				}
			}
		}
		configuration.save();
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
