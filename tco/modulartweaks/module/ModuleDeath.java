package tco.modulartweaks.module;

import tco.modulartweaks.ModularTweaks;
import tco.modulartweaks.ModularTweaksTransformer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;

public class ModuleDeath implements IModule {
	@Override
	public void initialize() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@ForgeSubscribe
	public void playerDrops(PlayerDropsEvent event) {
		ModularTweaks.logger.info("Player drop!!");
	}

	@ForgeSubscribe
	public void onLivingHurt(LivingHurtEvent event) {
		if(event.entityLiving instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.entityLiving;
			if(player.getHealth() <= event.ammount) {
				ModularTweaks.logger.info("Player death!!");
				//player death
			}
		}
	}

	@Override
	public String getName() {
		return "Death";
	}

	@Override
	public String getDescription() {
		return "Player death tweaks";
	}

	@Override
	public void loadConfigs(Configuration config) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void transform(ModularTweaksTransformer transformer, String name) {
	}
}
