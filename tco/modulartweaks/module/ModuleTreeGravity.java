package tco.modulartweaks.module;

import java.lang.reflect.Method;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import tco.modulartweaks.ModularTweaksTransformer;
import tco.modulartweaks.ObfuscationDecoder;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;

public class ModuleTreeGravity implements IModule {
	@Override
	public void initialize() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public String getName() {
		return "Tree Gravity";
	}

	@Override
	public String getDescription() {
		return "Trees fall.";
	}

	@Override
	public void loadConfigs(Configuration config) {		
	}

	public static void onWoodBreak(World world, int x, int y,
			int z, int id, int meta) {
		if(isWood(world, x + 1, y, z, id)) {
			breakBlock(world, x + 1, y, z);
		}
		if(isWood(world, x - 1, y, z, id)) {
			breakBlock(world, x - 1, y, z);
		}
		if(isWood(world, x, y, z + 1, id)) {
			breakBlock(world, x, y, z + 1);
		}
		if(isWood(world, x, y, z - 1, id)) {
			breakBlock(world, x, y, z - 1);
		}
		if(isWood(world, x, y + 1, z, id)) {
			breakBlock(world, x, y + 1, z);
		}
	}

	private static int fillDepth = 19;
	private static boolean working = false;

	/*public static void onWoodBreak2(World world, int x, int y,
			int z, int par5, int par6) {
		if(working) return;
		working = true;
		int[] blocks = new int[fillDepth * fillDepth * fillDepth];
		for(int i = 0; i < blocks.length; i++) {
			blocks[i] = i;
		}
		for(int i = 1; i < fillDepth - 1; i++) {
			for(int j = 1; j < fillDepth - 1; j++) {
				for(int k = 1; k < fillDepth - 1; k++) {
					int x1 = x - fillDepth / 2 + i, y1 = y + j, z1 = z - fillDepth / 2 + k;
					if(isWood(world, x1, y1, z1)) {
						if(isWood(world, x1 + 1, y1, z1)) {
						} else if(isWood(world, x1 - 1, y1, z1)) {
							//blocks[k + j * fillDepth + i * fillDepth * fillDepth] = blocks[]
						}
					} else if(isWood(world, x1, y1, z1 + 1)) {
					} else if(isWood(world, x1, y1, z1 - 1)) {
					} else if(isWood(world, x1, y1 + 1, z1)) {
					} else if(isWood(world, x1, y1 - 1, z1)) {

					}
				}
			}
		}
		working = false;
	}*/

	private static void breakBlock(World world, int x, int y, int z){
		List<ItemStack> items = Block.blocksList[world.getBlockId(x, y, z)].getBlockDropped(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
		for (ItemStack item : items) {
			float var = 0.7F;
			double dx = world.rand.nextFloat() * var + (1.0F - var) * 0.5D;
			double dy = world.rand.nextFloat() * var + (1.0F - var) * 0.5D;
			double dz = world.rand.nextFloat() * var + (1.0F - var) * 0.5D;
			EntityItem entityitem = new EntityItem(world, x + dx, y + dy, z + dz, item);
			world.spawnEntityInWorld(entityitem);
		}
		world.setBlockWithNotify(x, y, z, 0);
	}

	private static boolean isWood(World world, int x, int y, int z, int id) {
		return world.getBlockId(x, y, z) == id;
	}

	@Override
	public void transform(ModularTweaksTransformer trans, String name) {
		try{
			if(ObfuscationDecoder.checkBoth("net.minecraft.block.BlockLog", name)) {
				trans.startTransform();
				/* Insert
				 * ModuleTreeGravity.onWoodBreak(world, x, y, z, i ,m);
				 */
				MethodNode method = trans.findMethod("breakBlock", Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(World.class), Type.INT_TYPE, Type.INT_TYPE, Type.INT_TYPE, Type.INT_TYPE, Type.INT_TYPE));
				InsnList insn = new InsnList();
				insn.add(new VarInsnNode(Opcodes.ALOAD, 1));
				insn.add(new VarInsnNode(Opcodes.ILOAD, 2));
				insn.add(new VarInsnNode(Opcodes.ILOAD, 3));
				insn.add(new VarInsnNode(Opcodes.ILOAD, 4));
				insn.add(new VarInsnNode(Opcodes.ILOAD, 5));
				insn.add(new VarInsnNode(Opcodes.ILOAD, 6));
				Method reflMethod = ModuleTreeGravity.class.getDeclaredMethod("onWoodBreak", World.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE);
				insn.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ModuleTreeGravity.class.getCanonicalName().replaceAll("\\.", "/"),
						reflMethod.getName(), Type.getMethodDescriptor(reflMethod)));
				method.instructions.insert(insn);
				trans.stopTransform(); //*/
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
