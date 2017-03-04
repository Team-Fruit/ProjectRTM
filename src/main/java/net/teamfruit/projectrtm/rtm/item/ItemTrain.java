package net.teamfruit.projectrtm.rtm.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.teamfruit.projectrtm.ngtlib.io.NGTLog;
import net.teamfruit.projectrtm.rtm.entity.train.EntityBogie;
import net.teamfruit.projectrtm.rtm.entity.train.EntityFreightCar;
import net.teamfruit.projectrtm.rtm.entity.train.EntityTanker;
import net.teamfruit.projectrtm.rtm.entity.train.EntityTrain;
import net.teamfruit.projectrtm.rtm.entity.train.EntityTrainBase;
import net.teamfruit.projectrtm.rtm.entity.train.parts.EntityVehiclePart;
import net.teamfruit.projectrtm.rtm.modelpack.ModelPackManager;
import net.teamfruit.projectrtm.rtm.modelpack.cfg.TrainConfig;
import net.teamfruit.projectrtm.rtm.modelpack.modelset.ModelSetVehicleBase;
import net.teamfruit.projectrtm.rtm.rail.TileEntityLargeRailBase;
import net.teamfruit.projectrtm.rtm.rail.util.RailMap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemTrain extends ItemWithModel {
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;
	@SideOnly(Side.CLIENT)
	private IIcon testIcon;

	public ItemTrain() {
		super();
	}

	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int par7, float par8, float par9, float par10) {
		if (world.isRemote) {
			return true;
		}

		RailMap rm0 = TileEntityLargeRailBase.getRailMapFromCoordinates(world, player, x, y, z);
		if (rm0==null) {
			return true;
		}

		int r = 16;
		List list = world.getEntitiesWithinAABBExcludingEntity(player, AxisAlignedBB.getBoundingBox(x-r, y-4, z-r, x+r+1, y+8, z+r+1));
		for (int i = 0; i<list.size(); ++i) {
			Entity entity = (Entity) list.get(i);
			if (entity instanceof EntityTrainBase||entity instanceof EntityBogie||entity instanceof EntityVehiclePart) {
				double distanceSq = entity.getDistanceSq(x, y, z);
				ModelSetVehicleBase<TrainConfig> modelSet = ModelPackManager.INSTANCE.getModelSet(TrainConfig.TYPE, this.getModelName(itemStack));
				float f0 = modelSet.getConfig().trainDistance+4.0F;
				RailMap rm1 = TileEntityLargeRailBase.getRailMapFromCoordinates(world, player, entity.posX, entity.posY, entity.posZ);
				if (distanceSq<f0*f0&&rm0.equals(rm1)) {
					NGTLog.sendChatMessage(player, "message.train.obstacle", new Object[] { entity.toString() });
					return true;
				}
			}
		}

		int i0 = rm0.getNearlestPoint(128, (double) x+0.5D, (double) z+0.5D);
		float yw0 = MathHelper.wrapAngleTo180_float(rm0.getRailRotation(128, i0));
		float yaw = EntityBogie.fixBogieYaw(-player.rotationYaw, yw0);
		float pitch = EntityBogie.fixBogiePitch(rm0.getRailPitch(), yw0, yaw);
		double posX = rm0.getRailPos(128, i0)[1];
		double posY = rm0.getRailHeight(128, i0)+EntityTrainBase.TRAIN_HEIGHT;
		double posZ = rm0.getRailPos(128, i0)[0];

		EntityTrainBase train;
		switch (itemStack.getItemDamage()) {
			case 1:
				train = new EntityTrain(world, "");
				break;
			case 2:
				train = new EntityFreightCar(world, "");
				break;
			case 3:
				train = new EntityTanker(world, "");
				break;
			case 127:
				train = new EntityTrain(world, "");
				break;
			default:
				train = new EntityTrain(world, "");
		}

		String model = this.getModelName(itemStack);
		train.setPositionAndRotation(posX, posY, posZ, yaw, pitch);
		train.setModelName(model);
		train.spawnTrain(world);
		--itemStack.stackSize;
		return true;
	}

	@Override
	public String getUnlocalizedName(ItemStack par1) {
		return this.getUnlocalizedName()+"."+par1.getItemDamage();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int par1) {
		if (par1==127) {
			return this.testIcon;
		}
		int j = MathHelper.clamp_int(par1, 0, 3);
		return this.icons[j];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item par1, CreativeTabs par2, List list) {
		list.add(new ItemStack(par1, 1, 0));
		list.add(new ItemStack(par1, 1, 1));
		list.add(new ItemStack(par1, 1, 2));
		list.add(new ItemStack(par1, 1, 3));
		list.add(new ItemStack(par1, 1, 127));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		this.icons = new IIcon[4];
		this.icons[0] = register.registerIcon("rtm:itemDC");
		this.icons[1] = register.registerIcon("rtm:itemEC");
		this.icons[2] = register.registerIcon("rtm:itemFC");
		this.icons[3] = register.registerIcon("rtm:itemTC");
		this.testIcon = register.registerIcon("rtm:itemEC");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
		super.addInformation(itemStack, player, list, par4);
		for (int i = 0; i<8; ++i) {
			list.add(EnumChatFormatting.GRAY+StatCollector.translateToLocal("usage.train."+i));
		}
	}

	@Override
	protected String getModelType(ItemStack itemStack) {
		return TrainConfig.TYPE;
	}

	@Override
	protected String getDefaultModelName(ItemStack itemStack) {
		switch (itemStack.getItemDamage()) {
			case 1:
				return "223h";
			case 2:
				return "koki100";
			case 3:
				return "torpedo-car";
			case 127:
				return "objTest";
			default:
				return "kiha600";
		}
	}

	@Override
	public String getSubType(ItemStack itemStack) {
		switch (itemStack.getItemDamage()) {
			case 1:
				return "EC";
			case 2:
				return "CC";
			case 3:
				return "TC";
			case 127:
				return "Test";
			default:
				return "DC";
		}
	}
}