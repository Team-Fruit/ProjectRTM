package net.teamfruit.projectrtm.rtm.block.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.util.AxisAlignedBB;
import net.teamfruit.projectrtm.ngtlib.block.TileEntityPlaceable;
import net.teamfruit.projectrtm.ngtlib.math.NGTMath;
import net.teamfruit.projectrtm.ngtlib.util.NGTUtil;
import net.teamfruit.projectrtm.rtm.modelpack.texture.FlagProperty;
import net.teamfruit.projectrtm.rtm.modelpack.texture.ITextureHolder;
import net.teamfruit.projectrtm.rtm.modelpack.texture.TextureManager;
import net.teamfruit.projectrtm.rtm.modelpack.texture.TextureManager.TexturePropertyType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityFlag extends TileEntityPlaceable implements ITextureHolder<FlagProperty> {
	private FlagProperty property;
	private String textureName = "";

	@SideOnly(Side.CLIENT)
	public int tick;
	@SideOnly(Side.CLIENT)
	public float wave;

	public TileEntityFlag() {
		if (!NGTUtil.isServer()) {
			//上で初期化するとNoSuchFieldError
			this.wave = (float) NGTMath.RANDOM.nextInt(360);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		this.setTexture(nbt.getString("TextureName"));
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setString("TextureName", this.textureName);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		if (this.worldObj.isRemote) {
			this.wave += 10.0F;
			if (this.wave>=360.0F) {
				this.wave = 0.0F;
			}

			++this.tick;
			if (this.tick>=36000) {
				this.tick = 0;
			}
		}
	}

	protected void sendPacket() {
		NGTUtil.sendPacketToClient(this);
	}

	@Override
	public Packet getDescriptionPacket() {
		if (this.worldObj==null||!this.worldObj.isRemote) {
			this.sendPacket();
		}
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return NGTUtil.getChunkLoadDistanceSq();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		//AxisAlignedBB bb = AxisAlignedBB.getBoundingBox((double)this.xCoord, (double)this.yCoord, (double)this.zCoord, (double)this.xCoord + 1.0D, (double)this.yCoord + 1.0D, (double)this.zCoord + 1.0D);
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public FlagProperty getProperty() {
		if (this.property==null||this.property==FlagProperty.DUMMY) {
			this.property = TextureManager.INSTANCE.getProperty(this.getType(), this.textureName);
			if (this.property==null) {
				this.property = FlagProperty.DUMMY;
			}
		}
		return this.property;
	}

	@Override
	public void setTexture(String name) {
		this.textureName = name;
		this.property = null;
		this.markDirty();
		this.getDescriptionPacket();
	}

	@Override
	public TexturePropertyType getType() {
		return TexturePropertyType.Flag;
	}
}