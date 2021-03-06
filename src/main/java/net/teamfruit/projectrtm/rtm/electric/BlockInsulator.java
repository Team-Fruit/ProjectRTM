package net.teamfruit.projectrtm.rtm.electric;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.teamfruit.projectrtm.rtm.RTMItem;

public class BlockInsulator extends BlockConnector {
	public BlockInsulator() {
		super();
	}

	@Override
	public TileEntity createNewTileEntity(World world, int par2) {
		return new TileEntityInsulator();
	}

	@Override
	public void dropBlockAsItemWithChance(World world, int par2, int par3, int par4, int par5, float par6, int par7) {
		if (!world.isRemote) {
			this.dropBlockAsItem(world, par2, par3, par4, new ItemStack(RTMItem.installedObject, 1, 3));
		}
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		return new ItemStack(RTMItem.installedObject, 1, 3);
	}
}