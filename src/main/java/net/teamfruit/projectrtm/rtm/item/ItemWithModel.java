package net.teamfruit.projectrtm.rtm.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.teamfruit.projectrtm.ngtlib.util.NGTUtil;
import net.teamfruit.projectrtm.rtm.RTMCore;
import net.teamfruit.projectrtm.rtm.modelpack.IModelSelectorWithType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class ItemWithModel extends Item implements IModelSelectorWithType {
	/**モデル選択時の読み書きに使用*/
	@SideOnly(Side.CLIENT)
	private ItemStack selectedItem;
	@SideOnly(Side.CLIENT)
	private EntityPlayer selectedPlayer;

	public ItemWithModel() {
		super();
		this.setHasSubtypes(true);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
		if (world.isRemote) {
			if (!this.getModelType(itemStack).isEmpty()) {
				this.selectedItem = itemStack;
				this.selectedPlayer = player;
				player.openGui(RTMCore.instance, RTMCore.guiIdSelectItemModel, player.worldObj, 0, 0, 0);
			}
		}
		return itemStack;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
		list.add(EnumChatFormatting.GRAY+getModelName(itemStack));
	}

	protected abstract String getModelType(ItemStack itemStack);

	protected abstract String getDefaultModelName(ItemStack itemStack);

	public String getSubType(ItemStack itemStack) {
		return "";
	}

	public String getModelName(ItemStack itemStack) {
		if (itemStack.hasTagCompound()) {
			return itemStack.getTagCompound().getString("ModelName");
		} else {
			ItemWithModel item = (ItemWithModel) itemStack.getItem();
			return item.getDefaultModelName(itemStack);
		}
	}

	public void setModelName(ItemStack itemStack, String name) {
		if (!itemStack.hasTagCompound()) {
			itemStack.setTagCompound(new NBTTagCompound());
		}
		itemStack.getTagCompound().setString("ModelName", name);
	}

	@Override
	public String getModelType() {
		return this.getModelType(this.selectedItem);
	}

	@Override
	public String getModelName() {
		return getModelName(this.selectedItem);
	}

	@Override
	public void setModelName(String par1) {
		this.setModelName(this.selectedItem, par1);
		NGTUtil.sendPacketToServer(this.selectedPlayer, this.selectedItem);
	}

	@Override
	public int[] getPos() {
		return new int[3];
	}

	@Override
	public boolean closeGui(String par1) {
		this.setModelName(par1);
		return true;
	}

	@Override
	public String getSubType() {
		return this.getSubType(this.selectedItem);
	}
}