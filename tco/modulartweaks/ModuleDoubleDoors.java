package tco.modulartweaks;

import static net.minecraftforge.common.ForgeDirection.EAST;
import static net.minecraftforge.common.ForgeDirection.NORTH;
import static net.minecraftforge.common.ForgeDirection.SOUTH;
import static net.minecraftforge.common.ForgeDirection.UNKNOWN;
import static net.minecraftforge.common.ForgeDirection.WEST;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import cpw.mods.fml.client.FMLClientHandler;

public class ModuleDoubleDoors implements Module {
	private static final ForgeDirection[] SIDES = { NORTH, EAST, SOUTH, WEST };
	private BlockDoor door;
	private BlockFenceGate gate;
	private BlockTrapDoor trapdoor;
	private Minecraft client;

	@Override
	public void initialize() {
		door = (BlockDoor) Block.doorWood;
		gate = (BlockFenceGate) Block.fenceGate;
		trapdoor = (BlockTrapDoor) Block.trapdoor;
		client = FMLClientHandler.instance().getClient();
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@Override
	public String getName() {
		return "Double Doors";
	}

	@ForgeSubscribe
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(!event.entity.worldObj.isRemote) return;
		if(event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
			World world = event.entity.worldObj;
			if(isBlock(event, UNKNOWN, door)) {
				boolean open = !doorOpen(world, event.x, event.y, event.z);
				for(ForgeDirection dir : SIDES) {
					if(isBlock(event, dir, door)) {
						int x = event.x + dir.offsetX, y = event.y + dir.offsetY, z = event.z + dir.offsetZ;
						boolean isOpen = doorOpen(world, x, y, z);
						updateState(event, x, y, z, open, isOpen);
					}
				}
			} else if(isBlock(event, UNKNOWN, gate)) {
				boolean open = !gateOpen(world, event.x, event.y, event.z);
				for(ForgeDirection dir : SIDES) {
					if(isBlock(event, dir, gate)) {
						int x = event.x + dir.offsetX, y = event.y + dir.offsetY, z = event.z + dir.offsetZ;
						boolean isOpen = gateOpen(world, x, y, z);
						updateState(event, x, y, z, open, isOpen);
					}
				}
			} else if(isBlock(event, UNKNOWN, trapdoor)) {
				boolean open = !trapdoorOpen(world, event.x, event.y, event.z);
				for(ForgeDirection dir : SIDES) {
					if(isBlock(event, dir, trapdoor)) {
						int x = event.x + dir.offsetX, y = event.y + dir.offsetY, z = event.z + dir.offsetZ;
						boolean isOpen = trapdoorOpen(world, x, y, z);
						updateState(event, x, y, z, open, isOpen);
					}
				}
			}
		}
	}

	private void updateState(PlayerInteractEvent e, int x, int y, int z, boolean open, boolean isOpen) {
		if(open != isOpen) {
			client.playerController
			.onPlayerRightClick(e.entityPlayer, e.entity.worldObj, e.entityPlayer.inventory.getCurrentItem(),
					x, y, z, e.face, client.objectMouseOver.hitVec);
		}
	}

	private boolean isBlock(PlayerInteractEvent e, ForgeDirection dir, Block block) {
		return e.entity.worldObj.getBlockId(e.x + dir.offsetX, e.y + dir.offsetY, e.z + dir.offsetZ) == block.blockID;
	}

	private boolean doorOpen(World world, int x, int y, int z) {
		return door.isDoorOpen(world, x, y, z);
	}

	private boolean gateOpen(World world, int x, int y, int z) {
		return BlockFenceGate.isFenceGateOpen(world.getBlockMetadata(x, y, z));
	}

	private boolean trapdoorOpen(World world, int x, int y, int z) {
		return BlockTrapDoor.isTrapdoorOpen(world.getBlockMetadata(x, y, z));
	}

}
