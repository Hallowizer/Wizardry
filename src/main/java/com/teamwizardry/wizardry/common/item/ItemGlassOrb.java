package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.wizardry.common.block.fluid.ModFluids;
import com.teamwizardry.wizardry.init.ModItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;

/**
 * Created by Demoniaque on 6/20/2016.
 */
public class ItemGlassOrb extends ItemMod {

	public ItemGlassOrb() {
		super("glass_orb");
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		IBlockState state = entityItem.world.getBlockState(entityItem.getPosition());
		if (state.getBlock() == ModFluids.NACRE.getActualBlock()) {
			ItemStack newStack = new ItemStack(ModItems.PEARL_NACRE, entityItem.getItem().getCount());
			entityItem.setItem(newStack);
			newStack.getItem().onEntityItemUpdate(entityItem);
		}
		return super.onEntityItemUpdate(entityItem);
	}
}
