package tco.modulartweaks.module;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class ModuleSignEdit extends ModuleImpl {

	@Override
	public String getDescription() {
		return "Right click signs to edit";
	}

	@Override
	public String getName() {
		return "SignEdit";
	}

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

}
