package net.teamfruit.projectrtm.rtm.block.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.teamfruit.projectrtm.ngtlib.block.EnumFace;
import net.teamfruit.projectrtm.rtm.block.BlockMirror;
import net.teamfruit.projectrtm.rtm.block.BlockMirror.MirrorType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityMirror extends TileEntity {
	public MirrorType mirrorType;
	@SideOnly(Side.CLIENT)
	public MirrorComponent[] mirrors;

	@Override
	public void updateEntity() {
		if (this.worldObj.isRemote&&this.mirrors==null) {
			this.mirrorType = ((BlockMirror) this.getBlockType()).mirrorType;
			this.setupMirror();
		}
	}

	private void setupMirror() {
		boolean b = this.mirrorType==MirrorType.Mono_Panel;
		this.mirrors = new MirrorComponent[b ? 1 : 6];
		for (int i = 0; i<this.mirrors.length; ++i) {
			EnumFace face = b ? EnumFace.get(this.getBlockMetadata()) : EnumFace.get(i);
			this.mirrors[i] = new MirrorComponent(this.xCoord, this.yCoord, this.zCoord, this.mirrorType, face);
			MirrorObject.add(this.worldObj, this.mirrors[i], face, this.mirrorType);
		}
	}

	@Override
	public void onChunkUnload() {
		this.removeMirror();
	}

	@Override
	public void invalidate()//ブロック破壊時
	{
		super.invalidate();
		this.removeMirror();
	}

	private void removeMirror() {
		if (this.worldObj.isRemote&&this.mirrors!=null) {
			for (int i = 0; i<this.mirrors.length; ++i) {
				MirrorObject.remove(this.mirrors[i]);
			}
			this.mirrors = null;
		}
	}

	public int getAlpha() {
		if (this.mirrorType==MirrorType.Hexa_Cube) {
			int meta = this.getBlockMetadata();
			return (meta<<4)+meta;//*16
		} else {
			return 0xFF;
		}
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return this.mirrorType==MirrorType.Hexa_Cube ? pass==1 : pass==0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return 4096.0D;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(this.xCoord, this.yCoord, this.zCoord, this.xCoord+1, this.yCoord+1, this.zCoord+1);
	}
}