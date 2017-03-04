package net.teamfruit.projectrtm.rtm.entity.train.parts;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.teamfruit.projectrtm.ngtlib.util.NGTUtil;
import net.teamfruit.projectrtm.rtm.entity.vehicle.EntityVehicleBase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityFloor extends EntityVehiclePart {
	private int seatType;

	public EntityFloor(World par1) {
		super(par1);
		this.preventEntitySpawning = true;
		this.setSize(1.25F, 0.0625F);
	}

	public EntityFloor(World par1, EntityVehicleBase par2, float[] par3Pos, byte par4Type) {
		super(par1, par2, par3Pos);
		this.setSeatType(par4Type);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(24, Byte.valueOf((byte) 0));//type
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		this.setSeatType(nbt.getByte("seatType"));
		super.readEntityFromNBT(nbt);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setByte("seatType", this.getSeatType());
		super.writeEntityToNBT(nbt);
	}

	@Override
	public void onLoadVehicle() {
		this.getVehicle().setFloor(this);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
	}

	@Override
	public double getMountedYOffset() {
		return (double) this.height+0.25D;
	}

	@Override
	public boolean attackEntityFrom(DamageSource par1, float par2) {
		if (this.getVehicle()==null||this.getVehicle().isDead) {
			if (!this.worldObj.isRemote) {
				this.setDead();
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean interactFirst(EntityPlayer player) {
		if (this.riddenByEntity!=null) {
			if (this.riddenByEntity instanceof EntityPlayer&&this.riddenByEntity!=player) {
				return true;
			} else if (this.riddenByEntity instanceof EntityLiving) {
				this.riddenByEntity.mountEntity((Entity) null);
				return true;
			}
			return true;
		} else {
			int seatType = this.getSeatType();
			if (!this.worldObj.isRemote&&seatType!=0) {
				if (NGTUtil.isEquippedItem(player, Items.lead)) {
					double d0 = 7.0D;
					List list = this.worldObj.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getBoundingBox(this.posX-d0, this.posY-d0, this.posZ-d0, this.posX+d0, this.posY+d0, this.posZ+d0));
					if (list!=null) {
						Iterator iterator = list.iterator();
						while (iterator.hasNext()) {
							EntityLiving entity = (EntityLiving) iterator.next();
							if (entity.getLeashed()&&entity.getLeashedToEntity()==player) {
								entity.clearLeashed(true, true);
								entity.mountEntity(this);
								return true;
							}
						}
					}
				}

				if (!player.isSneaking()) {
					player.mountEntity(this);
				}
			}
			return true;
		}
	}

	public void setSeatType(byte par1) {
		this.dataWatcher.updateObject(24, Byte.valueOf(par1));
	}

	public byte getSeatType() {
		return this.dataWatcher.getWatchableObjectByte(24);
	}

	/**ブロックとの当たり判定*/
	@Override
	protected void func_145775_I() {
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getBrightnessForRender(float par1) {
		EntityVehicleBase vehicle = this.getVehicle();
		if (vehicle!=null) {
			return vehicle.getBrightnessForRender(par1);
		}
		return super.getBrightnessForRender(par1);
	}
}