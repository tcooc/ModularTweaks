package tco.modulartweaks.module;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import net.minecraft.item.Item;
import net.minecraftforge.common.Property;
import net.minecraftforge.common.Property.Type;

public class ModuleStack extends ModuleImpl {
	private static final int[] blacklist = {282, 373, 386, 387, 403, 2256, 2257, 2258, 2259, 2260, 2261, 2262, 2263, 2264, 2265, 2266, 2267};

	private final Map<Integer, Integer> defaultStackSizes = new TreeMap<Integer, Integer>();
	private final Map<Integer, Integer> editedStackSizes = new TreeMap<Integer, Integer>();

	@Override
	public String getDescription() {
		return "Change max stack sizes of many items";
	}

	@Override
	public String getName() {
		return "Stack";
	}

	@Override
	public void initialize() {
		for(int id : editedStackSizes.keySet()) {
			Item.itemsList[id].setMaxStackSize(editedStackSizes.get(id));
		}
	}

	@Override
	public Property[] getConfig() {
		LinkedList<Property> config = new LinkedList<Property>();
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
			Property prop = new Property("stackSizeOf" + id, String.valueOf(defaultSize), Type.INTEGER);
			prop.comment = Item.itemsList[id].getItemName();
			config.add(prop);
		}
		return (Property[]) config.toArray();
	}

	@Override
	public boolean setConfig(String key, String value) {
		if(key.indexOf("stackSizeOf") >= 0) {
			int id = Integer.parseInt(key.substring(11));
			if(defaultStackSizes.containsKey(id)) {
				int stackSize = Integer.parseInt(value);
				stackSize = stackSize < 1 ? 1 : stackSize > 64 ? 64 : stackSize;
				editedStackSizes.put(id, stackSize);
				Item.itemsList[id].setMaxStackSize(stackSize);
				return true;
			}
		}
		return false;
	}

}
