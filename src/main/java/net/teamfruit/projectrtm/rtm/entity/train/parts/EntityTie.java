package net.teamfruit.projectrtm.rtm.entity.train.parts;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.teamfruit.projectrtm.ngtlib.io.NGTLog;
import net.teamfruit.projectrtm.rtm.entity.EntityInstalledObject;
import net.teamfruit.projectrtm.rtm.entity.train.EntityBogie;
import net.teamfruit.projectrtm.rtm.entity.vehicle.EntityVehicleBase;

public class EntityTie extends EntityCargo {
	public EntityTie(World world) {
		super(world);
		this.setSize(3.0F, 0.125F);
	}

	public EntityTie(World world, ItemStack itemStack, int x, int y, int z) {
		super(world, itemStack, x, y, z);
	}

	public EntityTie(World world, EntityVehicleBase vehicle, ItemStack itemStack, float[] par4Pos, byte id) {
		super(world, vehicle, itemStack, par4Pos, id);
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
	}

	@Override
	protected void readCargoFromNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("riderUUID_Most", 4)&&nbt.hasKey("riderUUID_Least", 4)) {
			long l0 = nbt.getLong("riderUUID_Most");
			long l1 = nbt.getLong("riderUUID_Least");
			if (l0!=0L&&l1!=0L) {
				UUID uuid = new UUID(l0, l1);
				for (int j = 0; j<this.worldObj.loadedEntityList.size(); ++j) {
					Entity entity = (Entity) this.worldObj.loadedEntityList.get(j);
					if (uuid.equals(entity.getUniqueID())) {
						entity.mountEntity(this);
					}
				}
			}
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
	}

	@Override
	protected void writeCargoToNBT(NBTTagCompound nbt) {
		if (this.riddenByEntity!=null) {
			long l0 = 0L;
			long l1 = 0L;
			UUID uuid = this.riddenByEntity.getUniqueID();
			if (uuid!=null) {
				l0 = uuid.getMostSignificantBits();
				l1 = uuid.getLeastSignificantBits();
			}
			nbt.setLong("riderUUID_Most", l0);
			nbt.setLong("riderUUID_Least", l1);
		}
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (this.riddenByEntity!=null&&!(this.riddenByEntity instanceof EntityLivingBase)) {
			this.riddenByEntity.rotationYaw = this.rotationYaw;
			this.riddenByEntity.rotationPitch = this.rotationPitch;
		}
	}

	@Override
	public boolean interactFirst(EntityPlayer player) {
		if (this.riddenByEntity==null) {
			if (this.worldObj.isRemote) {
				return true;
			} else {
				double d0 = 1.5D;
				List list = this.worldObj.selectEntitiesWithinAABB(Entity.class,
						AxisAlignedBB.getBoundingBox(this.posX-d0, this.posY-0.5D, this.posZ-d0, this.posX+d0, this.posY+4.5D, this.posZ+d0),
						new IEntitySelector() {
							@Override
							public boolean isEntityApplicable(Entity entity) {
								if (entity instanceof EntityVehiclePart||entity instanceof EntityBogie||entity instanceof EntityInstalledObject) {
									return false;
								} else if (entity instanceof EntityVehicleBase) {
									return EntityTie.this.getVehicle()!=entity;
								}
								return true;
							}
						});

				if (!list.isEmpty()) {
					Iterator iterator = list.iterator();
					while (iterator.hasNext()) {
						Entity entity = (Entity) iterator.next();
						entity.mountEntity(this);
						NGTLog.sendChatMessage(player, entity.toString()+" was fixed.");
						return true;
					}
				}
				NGTLog.sendChatMessage(player, "Fixable entity not found.");
				return false;
			}
		} else {
			this.riddenByEntity.mountEntity((Entity) null);
			return true;
		}
	}
}