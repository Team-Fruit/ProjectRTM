package net.teamfruit.projectrtm.rtm.entity.vehicle;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.teamfruit.projectrtm.ngtlib.util.NGTUtil;
import net.teamfruit.projectrtm.rtm.RTMCore;
import net.teamfruit.projectrtm.rtm.entity.train.EntityBogie;
import net.teamfruit.projectrtm.rtm.entity.train.parts.EntityFloor;
import net.teamfruit.projectrtm.rtm.entity.train.parts.EntityVehiclePart;
import net.teamfruit.projectrtm.rtm.modelpack.IModelSelectorWithType;
import net.teamfruit.projectrtm.rtm.modelpack.ModelPackManager;
import net.teamfruit.projectrtm.rtm.modelpack.cfg.VehicleBaseConfig;
import net.teamfruit.projectrtm.rtm.modelpack.modelset.ModelSetVehicleBase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class EntityVehicleBase<T extends VehicleBaseConfig> extends Entity implements IModelSelectorWithType {
	public static final int MAX_SEAT_ROTATION = 45;
	public static final int MAX_DOOR_MOVE = 60;
	public static final int MAX_PANTOGRAPH_MOVE = 40;
	public static final float TO_ANGULAR_VELOCITY = (float) (360.0D/Math.PI);

	/**直接参照は非推奨*/
	private ModelSetVehicleBase<T> myModelSet;
	protected List<EntityFloor> vehicleFloors = new ArrayList<EntityFloor>();
	protected final IUpdateVehicle soundUpdater;

	private boolean floorLoaded;
	private EntityLivingBase rider;

	public float rotationRoll;
	public float prevRotationRoll;

	@SideOnly(Side.CLIENT)
	public int seatRotation;
	@SideOnly(Side.CLIENT)
	public int rollsignAnimation;
	@SideOnly(Side.CLIENT)
	public int rollsignV;

	@SideOnly(Side.CLIENT)
	public int doorMoveL;
	@SideOnly(Side.CLIENT)
	public int doorMoveR;
	@SideOnly(Side.CLIENT)
	public int pantograph_F;
	@SideOnly(Side.CLIENT)
	public int pantograph_B;
	@SideOnly(Side.CLIENT)
	public float wheelRotationR;
	@SideOnly(Side.CLIENT)
	public float wheelRotationL;

	public EntityVehicleBase(World world) {
		super(world);
		this.preventEntitySpawning = true;
		this.ignoreFrustumCheck = true;
		this.soundUpdater = world!=null ? RTMCore.proxy.getSoundUpdater(this) : null;
	}

	@Override
	protected void entityInit() {
		this.dataWatcher.addObject(20, this.getDefaultName());
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass>=0;
	}

	@Override
	public boolean canBeCollidedWith() {
		return !this.isDead;
	}

	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox() {
		return this.boundingBox;
	}

	@Override
	public AxisAlignedBB getCollisionBox(Entity par1) {
		if (par1 instanceof EntityVehiclePart) {
			if (((EntityVehiclePart) par1).getVehicle()==this) {
				return null;
			}
		} else if (par1 instanceof EntityBogie) {
			if (((EntityBogie) par1).getTrain()==this) {
				return null;
			}
		}
		return par1.boundingBox;
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setString("ModelName", this.getModelName());
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("trainName")) {
			this.setModelName(nbt.getString("trainName"));//互換性
		} else {
			this.setModelName(nbt.getString("ModelName"));
		}
	}

	public void setFloor(EntityFloor par1)//EntityFloorから
	{
		this.floorLoaded = true;
		this.vehicleFloors.add(par1);
	}

	@Override
	public void setDead() {
		super.setDead();

		if (!this.worldObj.isRemote) {
			for (EntityFloor floor : this.vehicleFloors) {
				if (floor!=null) {
					floor.setDead();
				}
			}
		}
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		this.prevRotationRoll = this.rotationRoll;

		if (this.worldObj.isRemote) {
			if (this.soundUpdater!=null) {
				this.soundUpdater.update();
			}

			this.updateAnimation();
		} else {
			if (this.riddenByEntity!=null) {
				if (this.rider==null&&this.riddenByEntity instanceof EntityLivingBase) {
					this.rider = (EntityLivingBase) this.riddenByEntity;
				}
			} else if (this.rider!=null) {
				fixRiderPos(this.rider, this);
				this.rider = null;
			}
		}
	}

	/**降りたEntityがハマらないように位置修正*/
	public static void fixRiderPos(EntityLivingBase entity, Entity vehicle) {
		World world = entity.worldObj;
		AxisAlignedBB aabb = vehicle.getBoundingBox();
		if (entity.posX>=aabb.minX&&entity.posX<aabb.maxX&&entity.posZ>=aabb.minZ&&entity.posZ<aabb.maxZ) {
			double range = 0.5D;
			int x0 = MathHelper.floor_double(aabb.minX-range);
			int x1 = MathHelper.floor_double(aabb.maxX+range);
			int z0 = MathHelper.floor_double(aabb.minZ-range);
			int z1 = MathHelper.floor_double(aabb.maxZ+range);
			int y = MathHelper.floor_double(aabb.minY);
			for (int i = x0; i<=x1; ++i) {
				for (int j = z0; j<=z1; ++j) {
					for (int k = y-2; k<=y+2; ++k) {
						if ((i!=0||j!=0)&&(i<aabb.minX||i>=aabb.maxX||j<aabb.minZ||j>=aabb.maxZ)) {
							List list = world.getEntitiesWithinAABBExcludingEntity(entity, entity.boundingBox.getOffsetBoundingBox(i, k, j));
							if (list.isEmpty()&&World.doesBlockHaveSolidTopSurface(world, i, k, j)) {
								if (world.isAirBlock(i, k+1, j)&&world.isAirBlock(i, k+2, j)) {
									entity.setPositionAndUpdate((double) i, (double) k+1.0D, (double) j);
									//NGTLog.debug("fixPos");
									return;
								}
							}
						}
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	protected void updateAnimation() {
		float speed = this.getSpeed();
		float f0 = speed*TO_ANGULAR_VELOCITY*this.getModelSet().getConfig().wheelRotationSpeed*this.getMoveDir();

		this.wheelRotationR = (this.wheelRotationR+f0)%360.0F;
		this.wheelRotationL = (this.wheelRotationL+f0)%360.0F;
	}

	/**+1.0 or -1.0*/
	protected float getMoveDir() {
		return 1.0F;
	}

	public abstract float getSpeed();

	public ModelSetVehicleBase<T> getModelSet() {
		if (this.myModelSet==null||this.myModelSet.isDummy()||!this.myModelSet.getConfig().getName().equals(this.getModelName())) {
			this.myModelSet = ModelPackManager.INSTANCE.getModelSet(this.getModelType(), this.getModelName());
			this.onModelChanged(this.myModelSet);
		}
		return this.myModelSet;
	}

	protected void onModelChanged(ModelSetVehicleBase<T> par1) {
		if (this.worldObj.isRemote) {
			this.soundUpdater.onModelChanged();
		} else {
			if (!this.floorLoaded) {
				this.setupFloors(par1);
			}
		}
	}

	/**Server Only*/
	private void setupFloors(ModelSetVehicleBase<T> par1)//getModelSetでループしないように
	{
		for (EntityFloor entity : this.vehicleFloors) {
			if (entity!=null) {
				entity.setDead();
			}
		}

		for (int i = 0; i<par1.getConfig().getSlotPos().length; ++i) {
			float[] fa = par1.getConfig().getSlotPos()[i];
			EntityFloor floor = new EntityFloor(this.worldObj, this, new float[] { fa[0], fa[1], fa[2] }, (byte) fa[3]);
			this.worldObj.spawnEntityInWorld(floor);
			this.vehicleFloors.add(floor);
		}
		this.floorLoaded = true;
	}

	@Override
	public String getModelName() {
		return this.dataWatcher.getWatchableObjectString(20);
	}

	@Override
	public void setModelName(String name) {
		this.dataWatcher.updateObject(20, (name.isEmpty() ? this.getDefaultName() : name));//ミニチュアでDWは非同期のため
		if (!this.worldObj.isRemote) {
			this.floorLoaded = false;
		}
	}

	@Override
	public int[] getPos() {
		return new int[] { this.getEntityId(), -1, 0 };
	}

	@Override
	public boolean closeGui(String par1) {
		return true;
	}

	public abstract String getDefaultName();

	@SideOnly(Side.CLIENT)
	public boolean shouldUseInteriorLight() {
		if (((VehicleBaseConfig) this.getModelSet().getConfig()).interiorLights!=null) {
			int x = MathHelper.floor_double(this.posX);
			int y = MathHelper.floor_double(this.posY+0.5D);
			int z = MathHelper.floor_double(this.posZ);
			int v = NGTUtil.getLightValue(this.worldObj, x, y, z);
			return this.useInteriorLight()&&v<7;
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	protected boolean useInteriorLight() {
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getBrightnessForRender(float par1) {
		if (this.shouldUseInteriorLight()) {
			return 0xF000F0;
		}
		return super.getBrightnessForRender(par1);
	}
}