package net.teamfruit.projectrtm.rtm.entity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.teamfruit.projectrtm.rtm.RTMItem;
import net.teamfruit.projectrtm.rtm.electric.EntityElectricalWiring;
import net.teamfruit.projectrtm.rtm.electric.SignalLevel;
import net.teamfruit.projectrtm.rtm.item.ItemInstalledObject.IstlObjType;
import net.teamfruit.projectrtm.rtm.rail.TileEntityLargeRailBase;

public class EntityTrainDetector extends EntityElectricalWiring {
	private boolean findTrain;

	public EntityTrainDetector(World world) {
		super(world);
		this.setSize(1.0F, 0.0625F);
		this.ignoreFrustumCheck = true;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
	}

	@Override
	public void onUpdate() {
		if (!this.worldObj.isRemote) {
			this.findTrain = false;
			for (int i = 0; i<8; ++i) {
				TileEntity tile = this.worldObj.getTileEntity(this.tileEW.xCoord, this.tileEW.yCoord-i, this.tileEW.zCoord);
				if (tile!=null&&tile instanceof TileEntityLargeRailBase) {
					this.findTrain = ((TileEntityLargeRailBase) tile).isTrainOnRail();
					break;
				}
			}
		}

		super.onUpdate();
	}

	@Override
	public int getElectricity() {
		return this.findTrain ? SignalLevel.STOP.level : SignalLevel.PROCEED.level;
	}

	@Override
	public void setElectricity(int par1) {
		;
	}

	@Override
	protected void dropItems() {
		this.entityDropItem(new ItemStack(RTMItem.installedObject, 1, IstlObjType.TRAIN_DETECTOR.id), 0.0F);
	}

	@Override
	public String getSubType() {
		return "Antenna_Receive";
	}

	@Override
	protected String getDefaultName() {
		return "TrainDetector_01";
	}
}