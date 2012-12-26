package tco.modulartweaks.module;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.event.FMLServerStartingEvent;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import tco.modulartweaks.ModularTweaksTransformer;
import tco.modulartweaks.ObfuscationDecoder;

public class ModuleCrafting implements IModule {

	private String craft = "craft";
	private String repair = "repair";

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

	@Override
	public void initialize() {
		enabled = true;
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Subscribe
	public void serverStarting(FMLServerStartingEvent e) {
		if(!"none".equalsIgnoreCase(craft)) {
			e.registerServerCommand(new CommandCraft());
		}
		if(!"none".equalsIgnoreCase(repair)) {
			e.registerServerCommand(new CommandRepair());
		}
	}

	@Override
	public String getName() {
		return "Crafting";
	}

	@Override
	public String getDescription() {
		return "3x3 crafting anywhere";
	}

	@Override
	public void loadConfigs(Configuration config) {
		craft = config.get(getName(), "commandCraft", craft, "Command to open crafting gui, set to none to disable").value;
		repair = config.get(getName(), "commandRepair", repair, "Command to open repair/anvil gui, set to none to disable").value;
	}

	@Override
	public void transform(ModularTweaksTransformer trans, String name) {
		try {
			if(ObfuscationDecoder.checkBoth("net.minecraft.inventory.ContainerWorkbench", name)) {
				trans.startTransform();
				MethodNode method = trans.findMethod("canInteractWith", Type.getMethodDescriptor(Type.BOOLEAN_TYPE, Type.getType(EntityPlayer.class)));
				InsnList insert = new InsnList();
				LabelNode l1 = new LabelNode();
				insert.add(new FieldInsnNode(GETSTATIC, getClass().getCanonicalName().replaceAll("\\.", "/"), "enabled", "Z"));
				insert.add(new JumpInsnNode(IFEQ, l1));
				insert.add(new InsnNode(ICONST_1));
				insert.add(new InsnNode(IRETURN));
				insert.add(l1);
				method.instructions.insert(insert);
				trans.stopTransform(); //*/
			} else if(ObfuscationDecoder.checkBoth("net.minecraft.inventory.ContainerRepair", name)) {
				trans.startTransform();
				MethodNode method = trans.findMethod("canInteractWith", Type.getMethodDescriptor(Type.BOOLEAN_TYPE, Type.getType(EntityPlayer.class)));
				InsnList insert = new InsnList();
				LabelNode l1 = new LabelNode();
				insert.add(new FieldInsnNode(GETSTATIC, getClass().getCanonicalName().replaceAll("\\.", "/"), "enabled", "Z"));
				insert.add(new JumpInsnNode(IFEQ, l1));
				insert.add(new InsnNode(ICONST_1));
				insert.add(new InsnNode(IRETURN));
				insert.add(l1);
				method.instructions.insert(insert);
				trans.stopTransform(); //*/
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
