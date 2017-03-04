package net.teamfruit.projectrtm.rtm.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.teamfruit.projectrtm.rtm.RTMItem;
import net.teamfruit.projectrtm.rtm.block.tileentity.TileEntityCrossingGate;

public class BlockCrossingGate extends BlockMachineBase {
	public BlockCrossingGate() {
		super(Material.rock);
		this.setStepSound(soundTypeGlass);
		this.setLightOpacity(0);
		this.setBlockBounds(0.125F, 0.0F, 0.125F, 0.875F, 3.0F, 0.875F);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int par2) {
		return new TileEntityCrossingGate();
	}

	@Override
	public void dropBlockAsItemWithChance(World world, int x, int y, int z, int par5, float par6, int par7) {
		if (!world.isRemote) {
			this.dropBlockAsItem(world, x, y, z, new ItemStack(RTMItem.installedObject, 1, 5));
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		super.onNeighborBlockChange(world, x, y, z, block);
		TileEntityCrossingGate tile = (TileEntityCrossingGate) world.getTileEntity(x, y, z);
		tile.isGettingPower = world.isBlockIndirectlyGettingPowered(x, y, z);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);
		TileEntityCrossingGate tile = (TileEntityCrossingGate) world.getTileEntity(x, y, z);
		tile.isGettingPower = world.isBlockIndirectlyGettingPowered(x, y, z);
	}
}