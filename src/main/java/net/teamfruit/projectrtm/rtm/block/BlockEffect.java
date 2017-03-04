package net.teamfruit.projectrtm.rtm.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.teamfruit.projectrtm.rtm.block.tileentity.TileEntityEffect;

public class BlockEffect extends BlockContainer {
	public BlockEffect() {
		super(Material.grass);
		this.setLightOpacity(0);
		this.setHardness(10000.0F);
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
	public boolean hasTileEntity() {
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World par1, int par2) {
		return new TileEntityEffect();
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		return null;
	}
}