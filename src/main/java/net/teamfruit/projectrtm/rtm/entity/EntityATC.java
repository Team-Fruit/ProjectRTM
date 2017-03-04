package net.teamfruit.projectrtm.rtm.entity;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.teamfruit.projectrtm.rtm.RTMItem;
import net.teamfruit.projectrtm.rtm.electric.EntityElectricalWiring;
import net.teamfruit.projectrtm.rtm.item.ItemInstalledObject.IstlObjType;
import net.teamfruit.projectrtm.rtm.rail.TileEntityLargeRailBase;
import net.teamfruit.projectrtm.rtm.rail.TileEntityLargeRailCore;

public class EntityATC extends EntityElectricalWiring {
	private int signalLevel;

	public EntityATC(World world) {
		super(world);
		this.setSize(1.0F, 0.0625F);
		this.ignoreFrustumCheck = true;
	}

	@Override
	public boolean attackEntityFrom(DamageSource par1, float par2) {
		if (!this.worldObj.isRemote) {
			this.setSignalToRail(0);
		}
		return super.attackEntityFrom(par1, par2);
	}

	private void setSignalToRail(int signalLevel) {
		int x = MathHelper.floor_double(this.posX);
		int y = MathHelper.floor_double(this.posY);
		int z = MathHelper.floor_double(this.posZ);
		for (int i = 0; i<8; ++i) {
			TileEntity tile0 = this.worldObj.getTileEntity(x, y-i, z);
			if (tile0!=null&&tile0 instanceof TileEntityLargeRailBase) {
				TileEntityLargeRailCore tile = ((TileEntityLargeRailBase) tile0).getRailCore();
				tile.setSignal(signalLevel);
				break;
			}
		}
	}

	@Override
	public int getElectricity() {
		return -1;
	}

	@Override
	public void setElectricity(int par1) {
		this.signalLevel = par1;
		this.setSignalToRail(this.signalLevel);
	}

	@Override
	protected void dropItems() {
		this.entityDropItem(new ItemStack(RTMItem.installedObject, 1, IstlObjType.ATC.id), 0.0F);
	}

	@Override
	public String getSubType() {
		return "Antenna_Send";
	}

	@Override
	protected String getDefaultName() {
		return "ATC_01";
	}
}