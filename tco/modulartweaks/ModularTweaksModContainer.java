package tco.modulartweaks;

import java.util.logging.Level;

import net.minecraftforge.common.Configuration;
import tco.modulartweaks.module.IModule;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ModularTweaksModContainer extends DummyModContainer {

	public CommonProxy proxy;

	public ModularTweaksModContainer() {
		super(new ModMetadata());
		ModMetadata metadata = getMetadata();
		metadata.modId = ModularTweaks.ID;
		metadata.version = ModularTweaks.VERSION;
		metadata.name = ModularTweaks.ID;
		metadata.description = "Vanilla gameplay changes.";
		metadata.url = "https://github.com/tcooc/ModularTweaks";
		metadata.authorList.add("tcooc");
	}

	@Override
	public boolean registerBus(EventBus bus, LoadController controller) {
		bus.register(this);
		for(IModule module : ModularTweaks.instance.modules) {
			bus.register(module);
		}
		return true;
	}

	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		proxy = FMLCommonHandler.instance().getSide().isClient() ? new ClientProxy() : new CommonProxy();
		ModularTweaks.instance.configuration = new Configuration(event.getSuggestedConfigurationFile());
		ModularTweaks.instance.loadConfigs();
	}

	@Subscribe
	public void init(FMLInitializationEvent event) {
		ModularTweaks.logger.info("Loading tweaks: ");
		try {
			proxy.init();
		} catch(Exception e) {
			ModularTweaks.logger.log(Level.SEVERE, "Error initializing modules", e);
		}
		ModularTweaks.logger.info("Done loading tweaks.");
		if(ModularTweaks.DEBUG) {
			try {
				//org.objectweb.asm.util.ASMifier.main(new String[]{"-debug", "net.minecraft.world.Explosion"});
			} catch (Exception e) {
				e.printStackTrace();
			}
			ObfuscationDecoder.dumpObfuscation();
		}
		//new net.minecraft.block.Block(1, net.minecraft.block.material.Material.anvil);
	}

}
