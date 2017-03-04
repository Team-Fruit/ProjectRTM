package net.teamfruit.projectrtm.rtm.block.tileentity;

import net.minecraft.block.Block;
import net.teamfruit.projectrtm.rtm.rail.TileEntityLargeRailBase;

public class TileEntityTurnplateParts extends TileEntityLargeRailBase {
	@Override
	public void updateEntity() {
		super.updateEntity();
	}

	@Override
	public Block getBlockType() {
		if (this.blockType==null) {
			Block block = this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord);
			this.blockType = block;
		}
		return this.blockType;
	}
}