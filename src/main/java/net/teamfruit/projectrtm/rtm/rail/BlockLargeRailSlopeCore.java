package net.teamfruit.projectrtm.rtm.rail;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.teamfruit.projectrtm.rtm.RTMBlock;

public class BlockLargeRailSlopeCore extends BlockLargeRailBase {
	public BlockLargeRailSlopeCore(int par1) {
		super(par1);
	}

	@Override
	public int getRenderType() {
		return RTMBlock.renderIdBlockRail;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int par2) {
		return new TileEntityLargeRailSlopeCore();
	}

	@Override
	public boolean isCore() {
		return true;
	}
}