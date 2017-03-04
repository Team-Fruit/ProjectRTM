package net.teamfruit.projectrtm.rtm.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.teamfruit.projectrtm.rtm.RTMAchievement;
import net.teamfruit.projectrtm.rtm.block.BlockConverter;
import net.teamfruit.projectrtm.rtm.rail.TileEntityLargeRailBase;

public class ItemCrowbar extends ItemSword {
	public ItemCrowbar() {
		super(ToolMaterial.IRON);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
		return itemStack;
	}

	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int par4, int par5, int par6, int par7, float par8, float par9, float par10) {
		if (!world.isRemote) {
			if (world.getBlock(par4, par5, par6)==Blocks.cobblestone) {
				byte b0 = BlockConverter.shouldCreateConverter(world, par4, par5, par6);
				if (b0>=0) {
					BlockConverter.createConverter(world, par4, par5, par6, b0, false);
					player.addStat(RTMAchievement.buildConverter, 1);
					return true;
				}
			} else {
				for (int i = 0; i<64; ++i) {
					for (int j = 0; j<64; ++j) {
						int x = par4+i-32;
						int y = par5;
						int z = par6+j-32;
						TileEntity tile0 = world.getTileEntity(x, y, z);
						if (tile0 instanceof TileEntityLargeRailBase&&((TileEntityLargeRailBase) tile0).getRailCore()==null) {
							world.setBlockToAir(x, y, z);
						}
					}
				}
			}
		}
		return true;
	}
}