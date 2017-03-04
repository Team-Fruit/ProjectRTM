package net.teamfruit.projectrtm.rtm.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.teamfruit.projectrtm.rtm.modelpack.IModelSelectorWithType;
import net.teamfruit.projectrtm.rtm.modelpack.ModelPackManager;
import net.teamfruit.projectrtm.rtm.modelpack.modelset.ModelSetMachine;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class EntityInstalledObject extends Entity implements IModelSelectorWithType {
	private ModelSetMachine myModelSet;

	public EntityInstalledObject(World world) {
		super(world);
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass>=0;
	}

	@Override
	protected void entityInit() {
		this.dataWatcher.addObject(20, this.getDefaultName());
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		String s = nbt.getString("ModelName");
		if (s.isEmpty())//v34互換
		{
			s = this.getDefaultName();
		}
		this.setModelName(s);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setString("ModelName", this.getModelName());
	}

	@Override
	public boolean canBePushed() {
		return false;
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
	public boolean attackEntityFrom(DamageSource par1, float par2) {
		if (this.isEntityInvulnerable()||this.isDead) {
			return false;
		} else {
			if (par1.getEntity() instanceof EntityPlayer) {
				if (!this.worldObj.isRemote) {
					this.setBeenAttacked();
					EntityPlayer entityplayer = (EntityPlayer) par1.getEntity();
					if (!entityplayer.capabilities.isCreativeMode) {
						this.dropItems();
					}
					Block block = Blocks.stone;
					this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, block.stepSound.func_150496_b(), (block.stepSound.getVolume()+1.0F)/2.0F, block.stepSound.getPitch()*0.8F);
					this.setDead();
				}
				return true;
			}
			return false;
		}
	}

	protected abstract void dropItems();

	@Override
	public void moveEntity(double par1, double par3, double par5) {
	}

	@Override
	public void addVelocity(double par1, double par3, double par5) {
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int p_70056_9_) {
		this.setPosition(x, y, z);
		this.setRotation(yaw, pitch);
	}

	/*=====================================================================================*/

	public ModelSetMachine getModelSet() {
		if (this.myModelSet==null||this.myModelSet.isDummy()) {
			this.myModelSet = ModelPackManager.INSTANCE.getModelSet("ModelMachine", this.getModelName());
		}
		return this.myModelSet;
	}

	@Override
	public String getModelType() {
		return "ModelMachine";
	}

	@Override
	public String getModelName() {
		return this.dataWatcher.getWatchableObjectString(20);
	}

	@Override
	public void setModelName(String par1) {
		this.dataWatcher.updateObject(20, par1);
		this.myModelSet = null;
	}

	@Override
	public int[] getPos() {
		return new int[] { this.getEntityId(), -1, 0 };
	}

	@Override
	public boolean closeGui(String par1) {
		this.setModelName(par1);
		return true;
	}

	protected abstract String getDefaultName();
}