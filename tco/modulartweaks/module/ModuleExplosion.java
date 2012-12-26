package tco.modulartweaks.module;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import tco.modulartweaks.ModularTweaksTransformer;
import tco.modulartweaks.ObfuscationDecoder;

public class ModuleExplosion implements IModule {

	public static boolean disableExplosions = false;
	public static float sizeMultiplier = 1.0f;
	public static boolean doDamage = true;
	public static float dropChance = 0.3f;

	@Override
	public void initialize() {
		Explosion.class.getName();
	}

	@Override
	public String getName() {
		return "Explosion";
	}

	@Override
	public String getDescription() {
		return "Changes explosion properties";
	}

	@Override
	public void loadConfigs(Configuration config) {
		disableExplosions = config.get(getName(), "disableExplosions", disableExplosions, "Wipe explosions off the face of the world.").getBoolean(disableExplosions);
		sizeMultiplier = (float) config.get(getName(), "sizeMultiplier", sizeMultiplier, "Modifies the size of explosions (DANGEROUS))").getDouble(sizeMultiplier);
		doDamage = config.get(getName(), "doDamage", doDamage, "Explosions do not damage entities").getBoolean(doDamage);
		dropChance = (float) config.get(getName(), "dropChance", dropChance, "1.0=100%, 0.0=0%").getDouble(dropChance);
		MathHelper.clamp_float(dropChance, 0, 1.0f);
		sizeMultiplier = sizeMultiplier < 0 ? 0 : sizeMultiplier;
		dropChance = 0;
		doDamage = false;
		sizeMultiplier = 100;
	}

	@Override
	public void transform(ModularTweaksTransformer trans, String name) {
		try {
			if(ObfuscationDecoder.checkBoth("net.minecraft.world.Explosion", name)) {
				trans.startTransform();
				/* Add
				 * this.explosionSize = par9 * ModuleExplosion.sizeMultiplier;
				 */
				MethodNode method = trans.findMethod("<init>", Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(World.class), Type.getType(Entity.class), Type.DOUBLE_TYPE, Type.DOUBLE_TYPE, Type.DOUBLE_TYPE, Type.FLOAT_TYPE));
				AbstractInsnNode node = method.instructions.getFirst();
				while(node != null) {
					if(node.getOpcode() == FLOAD) {
						break;
					}
					node = node.getNext();
				}
				//backwards for insert
				method.instructions.insert(node, new InsnNode(FMUL));
				method.instructions.insert(node, new FieldInsnNode(GETSTATIC, getClass().getCanonicalName().replaceAll("\\.", "/"), "sizeMultiplier", "F"));
				/* Insert
				 * if(ModuleExplosion.disableExplosions) return;
				 * And add
				 *  if(ModuleExplosion.doDamage) {
				 *   	var32.attackEntityFrom(DamageSource.explosion, (int)((var35 * var35 + var35) / 2.0D * 8.0D * (double)this.explosionSize + 1.0D));
				 *   }
				 * into doExplosionA
				 */
				method = trans.findMethod("doExplosionA", Type.getMethodDescriptor(Type.VOID_TYPE));
				LabelNode l1 = new LabelNode();
				method.instructions.insert(l1);
				method.instructions.insert(new InsnNode(RETURN));
				method.instructions.insert(new JumpInsnNode(IFEQ, l1));
				method.instructions.insert(new FieldInsnNode(GETSTATIC, getClass().getCanonicalName().replace('.', '/'), "disableExplosions", "Z"));

				//set node to correct one
				node = method.instructions.getFirst();
				while(node != null) {
					if(node.getOpcode() == GETSTATIC && ((FieldInsnNode) node).desc.equals(Type.getDescriptor(DamageSource.class))) {
						while(node.getOpcode() != DSTORE) {
							node = node.getPrevious();
						}
						break;
					}
					node = node.getNext();
				}
				LabelNode l2 = new LabelNode();
				method.instructions.insert(node, new JumpInsnNode(IFEQ, l2)); //right before node
				method.instructions.insert(node, new FieldInsnNode(GETSTATIC, getClass().getCanonicalName().replace('.', '/'), "doDamage", "Z")); //before node
				while(node.getOpcode() != INVOKEVIRTUAL) {
					node = node.getNext();
				}
				node = node.getNext();
				method.instructions.insert(node, l2); //after node

				//trans.printMethod(method);
				/* Insert
				 * if(ModuleExplosion.disableExplosions) return;
				 * And replace
				 * var25.dropBlockAsItemWithChance(this.worldObj, var4, var5, var6, this.worldObj.getBlockMetadata(var4, var5, var6), ModuleExplosion.dropChance, 0);
				 * into doExplosionB
				 */
				method = trans.findMethod("doExplosionB", Type.getMethodDescriptor(Type.VOID_TYPE, Type.BOOLEAN_TYPE));
				LabelNode l3 = new LabelNode();
				method.instructions.insert(l3);
				method.instructions.insert(new InsnNode(RETURN));
				method.instructions.insert(new JumpInsnNode(IFEQ, l3));
				method.instructions.insert(new FieldInsnNode(GETSTATIC, getClass().getCanonicalName().replace('.', '/'), "disableExplosions", "Z"));

				node = method.instructions.getFirst();
				AbstractInsnNode lastFloatLDC = null;
				while(node != null) {
					if(node.getOpcode() == LDC && ((LdcInsnNode) node).cst instanceof Float) {
						lastFloatLDC = node;
					}
					node = node.getNext();
				}
				method.instructions.set(lastFloatLDC, new FieldInsnNode(GETSTATIC, getClass().getCanonicalName().replace('.', '/'), "dropChance", "F"));

				trans.stopTransform(); //*/
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
