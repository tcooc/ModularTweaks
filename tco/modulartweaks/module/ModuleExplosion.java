package tco.modulartweaks.module;

import static org.objectweb.asm.Opcodes.DSTORE;
import static org.objectweb.asm.Opcodes.FLOAD;
import static org.objectweb.asm.Opcodes.FMUL;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.LDC;
import static org.objectweb.asm.Opcodes.RETURN;
import net.minecraft.world.Explosion;
import net.minecraftforge.common.Property;
import net.minecraftforge.common.Property.Type;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

import tco.modulartweaks.ModularTweaksTransformer;
import tco.modulartweaks.ObfuscationDecoder;

public class ModuleExplosion extends ModuleImpl {

	public static boolean disableExplosions = false;
	public static float sizeMultiplier = 1.0f;
	public static boolean doDamage = true;
	public static float dropChance = 0.3f;

	@Override
	public String getDescription() {
		return "Changes explosion properties";
	}

	@Override
	public String getName() {
		return "Explosion";
	}

	@Override
	public void initialize() {
		Explosion.class.getName(); //load Explosion class on load
	}

	@Override
	public Property[] getConfig() {
		Property[] config = new Property[4];
		config[0] = new Property("disableExplosions", String.valueOf(disableExplosions), Type.BOOLEAN);
		config[0].comment = "Wipe explosions off the face of the world.";
		config[1] = new Property("sizeMultiplier", String.valueOf(sizeMultiplier), Type.DOUBLE);
		config[1].comment = "Modifies the size of explosions (DANGEROUS))";
		config[2] = new Property("doDamage", String.valueOf(doDamage), Type.BOOLEAN);
		config[2].comment = "Explosions do not damage entities";
		config[3] = new Property("dropChance", String.valueOf(dropChance), Type.DOUBLE);
		config[3].comment = "1.0=100%, 0.0=0%";
		return config;
	}

	@Override
	public boolean setConfig(String key, String value) {
		if("disableExplosions".equals(key)) {
			disableExplosions = Boolean.valueOf(value);
			return true;
		}
		if("sizeMultiplier".equals(key)) {
			sizeMultiplier = (float) (Double.valueOf(value) + 0);
			sizeMultiplier = sizeMultiplier < 0 ? 0 : sizeMultiplier;
			return true;
		}
		if("doDamage".equals(key)) {
			doDamage = Boolean.valueOf(value);
			return true;
		}
		if("dropChance".equals(key)) {
			dropChance = (float) (Double.valueOf(value) + 0);
			dropChance = dropChance < 0 ? 0 : (dropChance > 1 ? 1 : dropChance);
			return true;
		}
		return false;
	}

	@Override
	public void transform(ModularTweaksTransformer trans, String name) {
		if(ObfuscationDecoder.checkBoth("net.minecraft.world.Explosion", name)) {
			trans.startTransform();
			/* Add
			 * this.explosionSize = par9 * ModuleExplosion.sizeMultiplier;
			 */
			String signature = "(L" +
					ObfuscationDecoder.getCorrectDesc("net.minecraft.world.World") + ";L" +
					ObfuscationDecoder.getCorrectDesc("net.minecraft.entity.Entity") + ";DDDF)V";
			MethodNode method = trans.findMethod("<init>",  signature);
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
			method = trans.findMethod("doExplosionA", "()V");
			LabelNode l1 = new LabelNode();
			method.instructions.insert(l1);
			method.instructions.insert(new InsnNode(RETURN));
			method.instructions.insert(new JumpInsnNode(IFEQ, l1));
			method.instructions.insert(new FieldInsnNode(GETSTATIC, getClass().getCanonicalName().replace('.', '/'), "disableExplosions", "Z"));

			//set node to correct one
			node = method.instructions.getFirst();
			String descriptor = "L" +
					ObfuscationDecoder.getCorrectDesc("net.minecraft.util.DamageSource") +  ";";
			while(node != null) {
				if(node.getOpcode() == GETSTATIC && ((FieldInsnNode) node).desc.equals(descriptor)) {
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
			method = trans.findMethod("doExplosionB", "(Z)V");
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
	}
}
