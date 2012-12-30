package tco.modulartweaks.module;

import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.IRETURN;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;
import net.minecraftforge.common.Property.Type;

import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

import tco.modulartweaks.ModularTweaksTransformer;
import tco.modulartweaks.ObfuscationDecoder;

import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class ModuleCrafting extends ModuleImpl {

	private class CommandCraft extends CommandBase {
		@Override
		public String getCommandName() {
			return craft;
		}
		@Override
		public void processCommand(ICommandSender sender, String[] args) {
			if(sender instanceof EntityPlayer) {
				EntityPlayer p = (EntityPlayer) sender;
				p.displayGUIWorkbench((int) p.posX, (int) p.posY, (int) p.posZ);
			}
		}
	}

	private class CommandRepair extends CommandBase {
		@Override
		public String getCommandName() {
			return repair;
		}
		@Override
		public void processCommand(ICommandSender sender, String[] args) {
			if(sender instanceof EntityPlayer) {
				EntityPlayer p = (EntityPlayer) sender;
				p.displayGUIAnvil((int) p.posX, (int) p.posY, (int) p.posZ);
			}
		}
	}
	public static boolean enabled = false;

	private String craft = "craft";

	private String repair = "repair";

	@Override
	public Property[] getConfig() {
		Property[] config = new Property[2];
		config[0] = new Property("commandCraft", craft, Type.STRING);
		config[0].comment = "Command to open crafting gui, set to none to disable";
		config[1] = new Property("commandRepair", repair, Type.STRING);
		config[1].comment = "Command to open repair/anvil gui, set to none to disable";
		return config;
	}

	@Override
	public String getDescription() {
		return "3x3 crafting anywhere";
	}

	@Override
	public String getName() {
		return "Crafting";
	}

	@Override
	public void initialize() {
		enabled = true;
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Subscribe
	public void serverStarting(FMLServerStartingEvent e) {
		if(!enabled) return;
		if(!"none".equalsIgnoreCase(craft)) {
			e.registerServerCommand(new CommandCraft());
		}
		if(!"none".equalsIgnoreCase(repair)) {
			e.registerServerCommand(new CommandRepair());
		}
	}

	@Override
	public boolean setConfig(String key, String value) {
		if("commandCraft".equals(key)) {
			craft = value;
			return true;
		}
		if("commandRepair".equals(key)) {
			repair = value;
			return true;
		}
		return false;
	}

	@Override
	public void transform(ModularTweaksTransformer trans, String name) {
		if(ObfuscationDecoder.checkBoth("net.minecraft.inventory.ContainerWorkbench", name)) {
			trans.startTransform();
			String signature = "(L" +
					ObfuscationDecoder.getCorrectDesc("net.minecraft.entity.player.EntityPlayer") + ";)Z";
			MethodNode method = trans.findMethod("canInteractWith", signature);
			InsnList insert = new InsnList();
			LabelNode l1 = new LabelNode();
			insert.add(new FieldInsnNode(GETSTATIC, getClass().getCanonicalName().replace('.', '/'), "enabled", "Z"));
			insert.add(new JumpInsnNode(IFEQ, l1));
			insert.add(new InsnNode(ICONST_1));
			insert.add(new InsnNode(IRETURN));
			insert.add(l1);
			method.instructions.insert(insert);
			trans.stopTransform(); //*/
		} else if(ObfuscationDecoder.checkBoth("net.minecraft.inventory.ContainerRepair", name)) {
			trans.startTransform();
			String signature = "(L" +
					ObfuscationDecoder.getCorrectDesc("net.minecraft.entity.player.EntityPlayer") + ";)Z";
			MethodNode method = trans.findMethod("canInteractWith", signature);
			InsnList insert = new InsnList();
			LabelNode l1 = new LabelNode();
			insert.add(new FieldInsnNode(GETSTATIC, getClass().getCanonicalName().replace('.', '/'), "enabled", "Z"));
			insert.add(new JumpInsnNode(IFEQ, l1));
			insert.add(new InsnNode(ICONST_1));
			insert.add(new InsnNode(IRETURN));
			insert.add(l1);
			method.instructions.insert(insert);
			trans.stopTransform(); //*/
		}
	}
}
