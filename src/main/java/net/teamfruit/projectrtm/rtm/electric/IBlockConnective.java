package net.teamfruit.projectrtm.rtm.electric;

import net.minecraft.world.World;

/**コネクタを挿せるブロック*/
public interface IBlockConnective {
	boolean canConnect(World world, int x, int y, int z);
}