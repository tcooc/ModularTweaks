package tco.modulartweaks;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.Configuration;

public class ModuleStack implements IModule {
	private static final int[] blacklist = {282, 373, 386, 387, 403, 2256, 2257, 2258, 2259, 2260, 2261, 2262, 2263, 2264, 2265, 2266, 2267};

	private final Map<Integer, Integer> defaultStackSizes = new TreeMap<Integer, Integer>();
	private final Map<Integer, Integer> editedStackSizes = new TreeMap<Integer, Integer>();

	@Override
	public void initialize() {
		for(int id : editedStackSizes.keySet()) {
			Item.itemsList[id].setMaxStackSize(editedStackSizes.get(id));
		}
	}

	@Override
	public String getName() {
		return "Stack";
	}

	@Override
	public String getDescription() {
		return "Change max stack sizes of many items";
	}

	@Override
	public void loadConfigs(Configuration config) {
		for(Item item : Item.itemsList) {
			if(item != null) {
				int stack = item.getItemStackLimit();
				if(stack != 64 && item.getContainerItem() == null && !item.isRepairable()
						&& Arrays.binarySearch(blacklist, item.shiftedIndex) < 0) {
					defaultStackSizes.put(item.shiftedIndex, stack);
				}
			}
		}

		for(int id : defaultStackSizes.keySet()) {
			int defaultSize = defaultStackSizes.get(id);
			int newSize = config.get(getName(), "stackSizeOf" + id, defaultSize, Item.itemsList[id].getItemName()).getInt(defaultSize);
			newSize = MathHelper.clamp_int(newSize, 1, 64);
			if(defaultSize != newSize) {
				editedStackSizes.put(id, newSize);
			}
		}
	}

	@Override
	public void transform(ModularTweaksTransformer transformer, String name) {
	}

}
