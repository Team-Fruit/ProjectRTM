package net.teamfruit.projectrtm.ngtlib.world;

import net.minecraft.world.IBlockAccess;
import net.teamfruit.projectrtm.ngtlib.block.BlockSet;

public interface IBlockAccessNGT extends IBlockAccess {
	BlockSet getBlockSet(int x, int y, int z);
}