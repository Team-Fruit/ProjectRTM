package net.teamfruit.projectrtm.rtm.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.teamfruit.projectrtm.rtm.RTMCore;
import net.teamfruit.projectrtm.rtm.block.tileentity.TileEntityFlag;

public class BlockFlag extends BlockContainer {
	public BlockFlag() {
		super(Material.iron);
		this.setLightOpacity(0);
		this.setBlockBounds(0.4375F, 0.0F, 0.4375F, 0.5625F, 1.0F, 0.5625F);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityFlag();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {
		if (world.isRemote) {
			player.openGui(RTMCore.instance, RTMCore.instance.guiIdSelectTexture, world, x, y, z);
		}
		return true;
	}
}