package net.teamfruit.projectrtm.rtm.entity.npc;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.teamfruit.projectrtm.ngtlib.math.NGTMath;
import net.teamfruit.projectrtm.ngtlib.util.NGTUtil;
import net.teamfruit.projectrtm.rtm.RTMCore;
import net.teamfruit.projectrtm.rtm.RTMItem;
import net.teamfruit.projectrtm.rtm.entity.EntityBullet;
import net.teamfruit.projectrtm.rtm.item.ItemGun;
import net.teamfruit.projectrtm.rtm.item.ItemGun.GunType;
import net.teamfruit.projectrtm.rtm.modelpack.IModelSelector;
import net.teamfruit.projectrtm.rtm.modelpack.ModelPackManager;
import net.teamfruit.projectrtm.rtm.modelpack.modelset.ModelSetNPC;
import net.teamfruit.projectrtm.rtm.util.PathNavigateCustom;

public class EntityNPC extends EntityTameable implements IModelSelector, IRangedAttackMob {
	public static final float SPEED = 0.45F;
	public static final float ATTACK_POWER = 1.0F;

	private ModelSetNPC myModelSet;
	private Role myRole = Role.MANNEQUIN;
	private EntityDummyPlayer playerDummy;

	protected int useItemCount;

	public InventoryNPC inventory = new InventoryNPC(this);

	public EntityNPC(final World world) {
		super(world);
		setSize(0.6F, 1.8F);
		setNavigator(new PathNavigateCustom(this, world));
		getNavigator().setAvoidsWater(true);
		this.playerDummy = new EntityDummyPlayer(world, this);
	}

	public EntityNPC(final World world, final EntityPlayer player) {
		this(world);
		func_152115_b(player.getUniqueID().toString());
	}

	protected void setNavigator(final PathNavigate navigator) {
		NGTUtil.setValueToField(EntityLiving.class, this, navigator, new String[] { "navigator", "field_70699_by" });
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(40.0D);
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(64.0D);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(SPEED);
		//EntityMobじゃないので新たに追加
		getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(ATTACK_POWER);
	}

	@Override
	public void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(20, "MannequinNGT01");
		this.dataWatcher.addObject(21, Byte.valueOf((byte) 0));
	}

	@Override
	public void writeEntityToNBT(final NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);

		nbt.setString("ModelName", getModelName());
		nbt.setTag("Inventory", this.inventory.writeToNBT(new NBTTagList()));
	}

	@Override
	public void readEntityFromNBT(final NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);

		setModelName(nbt.getString("ModelName"));
		final NBTTagList nbttaglist = nbt.getTagList("Inventory", 10);
		this.inventory.readFromNBT(nbttaglist);

		onInventoryChanged();
	}

	@Override
	public boolean isAIEnabled() {
		return true;
	}

	@Override
	public EntityAgeable createChild(final EntityAgeable entity) {
		return null;
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	protected int getExperiencePoints(final EntityPlayer player) {
		return 0;
	}

	@Override
	protected Item getDropItem() {
		return RTMItem.itemMotorman;
	}

	@Override
	protected void dropFewItems(final boolean par1, final int par2) {
		;
	}

	protected void dropEntity() {
		final int damage = this instanceof EntityMotorman ? 0 : 1;
		entityDropItem(new ItemStack(getDropItem(), 1, damage), 0.5F);
	}

	@Override
	public void onUpdate() {
		//インベントリ開いてる間は止まらせる
		if (this.inventory.isOpening)
			return;

		super.onUpdate();

		this.playerDummy.setPosition(this.posX, this.posY, this.posZ);
		this.playerDummy.rotationYaw = this.rotationYaw;
		this.playerDummy.rotationPitch = this.rotationPitch;

		if (isUsingItem()) {
			final ItemStack item = getHeldItem();
			final boolean hasGun = item!=null&&item.getItem() instanceof ItemGun;

			if (!(item!=null&&hasGun)||this.useItemCount>item.getMaxItemUseDuration()) {
				if (!this.worldObj.isRemote) {
					if (item!=null&&hasGun)
						item.onPlayerStoppedUsing(this.worldObj, this.playerDummy, this.useItemCount);
					setUseItem(false);
				}
				this.useItemCount = 0;
			} else if (!this.worldObj.isRemote)
				item.getItem().onUsingTick(item, this.playerDummy, this.useItemCount);

			++this.useItemCount;
		} else
			this.useItemCount = 0;
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();

		if (!this.worldObj.isRemote)
			healNPC();
	}

	@Override
	protected void updateEntityActionState() {
		if (this.myRole!=Role.MANNEQUIN)
			super.updateEntityActionState();
	}

	protected void healNPC() {
		if (this.ticksExisted%3==0&&getHealth()<getMaxHealth()) {
			final int index = this.inventory.hasItem(ItemFood.class);
			if (index>=0) {
				final ItemStack stack = this.inventory.getStackInSlot(index);
				heal(((ItemFood) stack.getItem()).func_150905_g(stack));
				--stack.stackSize;
				if (stack.stackSize<=0)
					this.inventory.setInventorySlotContents(index, null);
			}
		}
	}

	@Override
	public void onDeath(final DamageSource source) {
		super.onDeath(source);
		if (!this.worldObj.isRemote) {
			this.inventory.dropAllItems();
			if (source.getEntity() instanceof EntityPlayer&&!((EntityPlayer) source.getEntity()).capabilities.isCreativeMode)
				dropEntity();
		}
	}

	@Override
	public boolean attackEntityFrom(final DamageSource damageSource, float par2) {
		final Entity attacker = damageSource.getEntity();
		if (attacker instanceof EntityPlayer&&attacker.equals(getOwner())) {
			if (this.myRole==Role.MANNEQUIN&&!((EntityPlayer) damageSource.getEntity()).capabilities.isCreativeMode)
				return false;
			par2 = 10000.0F;
		}

		return super.attackEntityFrom(damageSource, par2);
	}

	@Override
	public boolean attackEntityAsMob(final Entity target) {
		float power = (float) getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
		int knockback = 0;
		final ItemStack stack = getHeldItem();

		if (target instanceof EntityLivingBase) {
			power += EnchantmentHelper.getEnchantmentModifierLiving(this, (EntityLivingBase) target);
			knockback += EnchantmentHelper.getKnockbackModifier(this, (EntityLivingBase) target);
		}

		final boolean flag = target.attackEntityFrom(DamageSource.causeMobDamage(this), power);

		if (flag) {
			if (knockback>0) {
				final double vx = -MathHelper.sin(NGTMath.toRadians(this.rotationYaw))*knockback*0.5F;
				final double vz = MathHelper.cos(NGTMath.toRadians(this.rotationYaw))*knockback*0.5F;
				target.addVelocity(vx, 0.1D, vz);
				this.motionX *= 0.6D;
				this.motionZ *= 0.6D;
			}

			final int j = EnchantmentHelper.getFireAspectModifier(this);

			if (j>0)
				target.setFire(j*4);

			if (target instanceof EntityLivingBase)
				EnchantmentHelper.func_151384_a((EntityLivingBase) target, this);

			EnchantmentHelper.func_151385_b(this, target);
		}

		return flag;
	}

	@Override
	public void attackEntityWithRangedAttack(final EntityLivingBase target, final float strength) {
		if (!isUsingItem()) {
			final ItemStack item = getHeldItem();
			if (item!=null&&item.getItem() instanceof ItemGun) {
				item.useItemRightClick(this.worldObj, this.playerDummy);
				setUseItem(true);
			}
		}
	}

	@Override
	public boolean interact(final EntityPlayer player) {
		if (!this.worldObj.isRemote)
			player.openGui(RTMCore.instance, RTMCore.guiIdNPC, this.worldObj, getEntityId(), 0, 0);
		return true;
	}

	public boolean isUsingItem() {
		return this.dataWatcher.getWatchableObjectByte(21)==1;
	}

	public void setUseItem(final boolean par1) {
		this.dataWatcher.updateObject(21, Byte.valueOf((byte) (par1 ? 1 : 0)));
	}

	public int getItemUseCount() {
		return this.useItemCount;
	}

	@Override
	public double getYOffset() {
		return this.yOffset-0.5F;
	}

	@Override
	protected void damageArmor(final float damage) {
		this.inventory.damageArmor(this, damage);
	}

	@Override
	public int getTotalArmorValue() {
		return this.inventory.getTotalArmorValue();
	}

	@Override
	public ItemStack getEquipmentInSlot(final int index) {
		return index==0 ? this.inventory.mainInventory[0] : this.inventory.armorInventory[index-1];
	}

	@Override
	public ItemStack getHeldItem() {
		return this.inventory.mainInventory[0];
	}

	@Override
	public void setCurrentItemOrArmor(final int index, final ItemStack item) {
		if (index==0)
			this.inventory.mainInventory[0] = item;
		else
			this.inventory.armorInventory[index-1] = item;
	}

	@Override
	public ItemStack[] getLastActiveItems() {
		return this.inventory.armorInventory;
	}

	@Override
	public ItemStack func_130225_q(final int index) {
		return this.inventory.armorInventory[3-index];//RenderBiped.shouldRenderPass()
	}

	@Override
	public String getModelType() {
		return "ModelNPC";
	}

	@Override
	public String getModelName() {
		return this.dataWatcher.getWatchableObjectString(20);
	}

	@Override
	public void setModelName(final String name) {
		this.dataWatcher.updateObject(20, new String(name));//ミニチュアでDWは非同期のため
		if (!this.worldObj.isRemote)
			onInventoryChanged();//初期化
	}

	public ModelSetNPC getModelSet() {
		if (this.myModelSet==null||this.myModelSet.isDummy()||!this.myModelSet.getConfig().getName().equals(getModelName())) {
			this.myModelSet = ModelPackManager.INSTANCE.getModelSet(getModelType(), getModelName());
			this.myRole = Role.getRole(this.myModelSet.getConfig().role);
			this.myRole.init(this);
		}
		return this.myModelSet;
	}

	@Override
	public int[] getPos() {
		return new int[] { getEntityId(), -1, 0 };
	}

	@Override
	public boolean closeGui(final String par1) {
		return true;
	}

	public boolean isMotorman() {
		return false;
	}

	public void onInventoryChanged() {
		getModelSet();
		this.myRole.onInventoryChanged(this);
	}

	public EntityBullet getBullet(final GunType type) {
		if (getAttackTarget()==null)
			return new EntityBullet(this.worldObj, this, type.speed, type.bulletType);
		return new EntityBullet(this.worldObj, this, getAttackTarget(), type.speed, type.bulletType);
	}
}