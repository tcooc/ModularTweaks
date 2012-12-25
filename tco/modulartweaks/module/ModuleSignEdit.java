package tco.modulartweaks.module;

import tco.modulartweaks.ModularTweaksTransformer;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class ModuleSignEdit implements IModule {

	@Override
	public void initialize() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@ForgeSubscribe
	public void onBlockActivate(PlayerInteractEvent event) {
		if(event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
			int id = event.entity.worldObj.getBlockId(event.x, event.y, event.z);
			if(id == Block.signPost.blockID || id == Block.signWall.blockID) {
				TileEntity te = event.entity.worldObj.getBlockTileEntity(event.x, event.y, event.z);
				if(te instanceof TileEntitySign) {
					event.entityPlayer.displayGUIEditSign(te);
				}
			}
		}
	}

	@Override
	public String getName() {
		return "SignEdit";
	}

	@Override
	public String getDescription() {
		return "Right click signs to edit";
	}

	@Override
	public void loadConfigs(Configuration config) {
	}

	@Override
	public void transform(ModularTweaksTransformer transformer, String name) {
	}

}
