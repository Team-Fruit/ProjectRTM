package net.teamfruit.projectrtm.rtm.block.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.teamfruit.projectrtm.rtm.electric.MachineType;
import net.teamfruit.projectrtm.rtm.render.MachinePartsRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityLight extends TileEntityMachineBase {
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		boolean b = this.worldObj.isBlockIndirectlyGettingPowered(this.xCoord, this.yCoord, this.zCoord);
		if (this.isGettingPower^b) {
			this.isGettingPower = b;
			this.worldObj.func_147451_t(this.xCoord, this.yCoord, this.zCoord);//明るさ更新
		}
	}

	@Override
	public Vec3 getNormal(float x, float y, float z, float pitch, float yaw) {
		if (this.normal==null) {
			this.normal = Vec3.createVectorHelper(x, y, z);
			MachinePartsRenderer.rotateVec(this.normal,
					this.getBlockMetadata(), pitch, yaw);
		}
		return this.normal;
	}

	@Override
	public MachineType getMachinleType() {
		return MachineType.Light;
	}

	@Override
	protected String getDefaultName() {
		return "SearchLight01";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}
}