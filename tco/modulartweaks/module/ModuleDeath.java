package tco.modulartweaks.module;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.registry.GameRegistry;
import tco.modulartweaks.ModularTweaksTransformer;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;

public class ModuleDeath implements IModule, IPlayerTracker {

	private Map<String, Integer> expMap = new TreeMap<String, Integer>();
	private Map<String, List<ItemStack>> itemMap = new TreeMap<String, List<ItemStack>>();

	private boolean keepExp = false,
			keepInventory = false,
			deathChest = true;

	@Override
	public void initialize() {
		MinecraftForge.EVENT_BUS.register(this);
		GameRegistry.registerPlayerTracker(this);
	}

	@ForgeSubscribe
	public void onLivingDeath(LivingDeathEvent event) {
		if(event.entityLiving instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.entityLiving;
			if(keepExp) {
				expMap.put(player.username, player.experienceTotal);
				player.experience = 0;
				player.experienceLevel = 0;
				player.experienceTotal = 0;
			}
		}
	}

	@ForgeSubscribe
	public void playerDrops(PlayerDropsEvent event) {
		if(keepInventory) {
			List<ItemStack> list = new LinkedList<ItemStack>();
			for(EntityItem e : event.drops) {
				list.add(e.func_92014_d());
			}
			itemMap.put(event.entityPlayer.username, list);
			event.drops.clear();
		} else if(deathChest) {
			fillChest(event.drops, event.entityPlayer);
			fillChest(event.drops, event.entityPlayer);
		}
	}

	private int getChest(ArrayList<EntityItem> list) {
		for(int i = 0; i < list.size(); i++) {
			ItemStack stack = list.get(i).func_92014_d();
			if(stack != null && stack.itemID == Block.chest.blockID) {
				return i;
			}
		}
		return -1;
	}

	private void fillChest(ArrayList<EntityItem> list, EntityPlayer player) {
		int chestIndex = getChest(list);
		if(chestIndex >= 0) {
			int x = (int) player.posX, y = (int) player.posY, z = (int) player.posZ;
			ForgeDirection dir = ForgeDirection.UNKNOWN;
			boolean placed = onItemUse(list, player, list.get(chestIndex).func_92014_d(), player, player.worldObj, x, y, z, 1);
			if(!placed) {
				for(int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; i++) {
					dir = ForgeDirection.VALID_DIRECTIONS[i];
					if(onItemUse(list, player, list.get(chestIndex).func_92014_d(), player, player.worldObj, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, 1)) {
						break;
					}
				}
			}
		}
	}

	private boolean onItemUse(ArrayList<EntityItem> list, EntityPlayer player, ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7)
	{
		int var11 = par3World.getBlockId(par4, par5, par6);

		if (var11 == Block.snow.blockID)
		{
			par7 = 1;
		}
		else if (var11 != Block.vine.blockID && var11 != Block.tallGrass.blockID && var11 != Block.deadBush.blockID
				&& (Block.blocksList[var11] == null || !Block.blocksList[var11].isBlockReplaceable(par3World, par4, par5, par6)))
		{
			if (par7 == 0)
			{
				--par5;
			}

			if (par7 == 1)
			{
				++par5;
			}

			if (par7 == 2)
			{
				--par6;
			}

			if (par7 == 3)
			{
				++par6;
			}

			if (par7 == 4)
			{
				--par4;
			}

			if (par7 == 5)
			{
				++par4;
			}
		}

		if (par1ItemStack.stackSize == 0)
		{
			return false;
		}
		else if (!par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack))
		{
			return false;
		}
		else if (par5 == 255 && Block.blocksList[par1ItemStack.itemID].blockMaterial.isSolid())
		{
			return false;
		}
		else if (par3World.canPlaceEntityOnSide(par1ItemStack.itemID, par4, par5, par6, false, par7, par2EntityPlayer))
		{
			Block var12 = Block.blocksList[par1ItemStack.itemID];
			int var13 = par1ItemStack.getItem().getMetadata(par1ItemStack.getItemDamage());
			int var14 = Block.blocksList[par1ItemStack.itemID].func_85104_a(par3World, par4, par5, par6, par7, 0.5f, 0.5f, 0.5f, var13);

			if (((ItemBlock) par1ItemStack.getItem()).placeBlockAt(par1ItemStack, par2EntityPlayer, par3World, par4, par5, par6, par7, 0.5f, 0.5f, 0.5f, var14))
			{
				par3World.playSoundEffect(par4 + 0.5F, par5 + 0.5F, par6 + 0.5F, var12.stepSound.getPlaceSound(), (var12.stepSound.getVolume() + 1.0F) / 2.0F, var12.stepSound.getPitch() * 0.8F);
				--par1ItemStack.stackSize;
				//start edit
				TileEntity te = player.worldObj.getBlockTileEntity(par4, par5, par6);
				if(te instanceof TileEntityChest) {
					TileEntityChest teChest = (TileEntityChest) te;
					for(int i = 0; i < teChest.getSizeInventory() && list.size() > 0;) {
						ItemStack stack = list.get(list.size() - 1).func_92014_d();
						if(stack.stackSize > 0) {
							teChest.setInventorySlotContents(i, list.get(list.size() - 1).func_92014_d());
							i++;
						}
						list.remove(list.size() - 1);
					}
				}
				//end
			}

			return true;
		}
		else
		{
			return false;
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
		keepExp = config.get(getName(), "keepExp", keepExp,
				"Keep exp").getBoolean(keepExp);
		keepInventory = config.get(getName(), "keepInventory", keepInventory,
				"Keep inventory (not including hotbar)").getBoolean(keepInventory);
		deathChest = config.get(getName(), "deathChest", deathChest,
				"When a player with a chest dies, attempt placing dropped items in the chest which is left at the death point.").getBoolean(deathChest);	
	}

	@Override
	public void transform(ModularTweaksTransformer transformer, String name) {
	}

	//IPlayerTracker

	@Override
	public void onPlayerRespawn(EntityPlayer player) {
		if(keepExp && expMap.containsKey(player.username)) {
			player.addExperience(expMap.get(player.username));
			expMap.remove(player.username);
		}
		if(keepInventory && itemMap.containsKey(player.username)) {
			List<ItemStack> list = itemMap.get(player.username);
			for(ItemStack item : list) {
				if(!player.inventory.addItemStackToInventory(item)) {
					player.dropPlayerItem(item);
				}
			}
		}
	}

	@Override
	public void onPlayerLogin(EntityPlayer player) {
	}

	@Override
	public void onPlayerLogout(EntityPlayer player) {
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {
	}

}
