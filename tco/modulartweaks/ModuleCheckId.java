package tco.modulartweaks;

import net.minecraft.block.Block;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ICrashCallable;

public class ModuleCheckId implements Module, ICrashCallable {

	@Override
	public void initialize() {
		FMLCommonHandler.instance().registerCrashCallable(this);		
	}
	
	@Override
	public String getName() {
		return "ID Check";
	}

	public static String getAvailableIds() {
		int maxId = Block.blocksList.length;
		StringBuffer result = new StringBuffer();
		for(int i = 1; i < maxId; i++) {
			if(Block.blocksList[i] == null) {
				int start = i;
				while(i + 1 < maxId && Block.blocksList[i + 1] == null) {
					i++;
				}
				if(start != i) {
					result.append(start).append('-').append(i).append(',');
				} else {
					result.append(i).append(',');
				}
			}
		}
		result.deleteCharAt(result.length() - 1);
		return result.toString();
	}

	@Override
	public String call() {
		return "\n\n<ID Check>: Free block ids on crash: " + getAvailableIds() + "\n\n";
	}

	@Override
	public String getLabel() {
		return ModularTweaks.ID;
	}

}
