package tco.modulartweaks;

import net.minecraftforge.common.Configuration;

import org.objectweb.asm.util.ASMifier;

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
		metadata.description = "";
	}

	@Override
	public boolean registerBus(EventBus bus, LoadController controller) {
		proxy = FMLCommonHandler.instance().getSide().isClient() ? new ClientProxy() : new CommonProxy();
		bus.register(this);
		return true;
	}
	
	@Subscribe
	public void preInit(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		ModularTweaks.instance.loadConfigs(config);
	}
	
	@Subscribe
	public void init(FMLInitializationEvent event) {
		ModularTweaks.logger.info("Loading tweaks: ");
		proxy.init();
		ModularTweaks.logger.info("Done loading tweaks.");
		try {
			//ASMifier.main(new String[]{"-debug", "tco.modulartweaks.TestModule"});
		} catch (Exception e) {
			e.printStackTrace();
		}
		//new net.minecraft.block.Block(1, net.minecraft.block.material.Material.anvil);
	}

}
