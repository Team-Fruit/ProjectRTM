package net.teamfruit.projectrtm.rtm.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.teamfruit.projectrtm.ngtlib.math.NGTMath;
import net.teamfruit.projectrtm.rtm.entity.npc.EntityMotorman;
import net.teamfruit.projectrtm.rtm.entity.npc.EntityNPC;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemNPC extends Item {
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	public ItemNPC() {
		super();
		this.setHasSubtypes(true);
		this.setMaxStackSize(16);
	}

	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int par4, int par5, int par6, int par7, float par8, float par9, float par10) {
		if (!world.isRemote) {
			float rotationInterval = 15.0F;
			int yaw = MathHelper.floor_double(NGTMath.normalizeAngle(-player.rotationYaw+180.0D+(rotationInterval/2.0D))/(double) rotationInterval);
			float yawF = (float) yaw*rotationInterval;

			EntityNPC entity = itemStack.getItemDamage()==0 ? new EntityMotorman(world, player) : new EntityNPC(world, player);
			entity.setLocationAndAngles((double) par4+0.5D, (double) par5+1.5D, (double) par6+0.5D, yawF, 0.0F);
			entity.rotationYawHead = yawF;
			world.spawnEntityInWorld(entity);
		}
		return true;
	}

	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		int i = MathHelper.clamp_int(itemStack.getItemDamage(), 0, 1);
		return super.getUnlocalizedName()+"."+itemStack.getItemDamage();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item par1, CreativeTabs tabs, List list) {
		for (int j = 0; j<2; ++j) {
			list.add(new ItemStack(par1, 1, j));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int par1) {
		int j = MathHelper.clamp_int(par1, 0, 1);
		return this.icons[j];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		this.icons = new IIcon[2];
		this.icons[0] = register.registerIcon("rtm:itemMotorman");
		this.icons[1] = register.registerIcon("rtm:itemNPC");
	}
}