package net.teamfruit.projectrtm.rtm.block.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.teamfruit.projectrtm.rtm.RTMBlock;
import net.teamfruit.projectrtm.rtm.block.BlockSlot;

public class TileEntitySlot extends TileEntity {
	private int count;

	@Override
	public void updateEntity() {
		super.updateEntity();

		++count;
		if (count>4) {
			count = 0;
		}

		if (count==0&&this.worldObj.isBlockIndirectlyGettingPowered(this.xCoord, this.yCoord, this.zCoord)) {
			if (!this.worldObj.isRemote) {
				((BlockSlot) RTMBlock.slot).inhaleLiquid(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
			}
		}
	}
}