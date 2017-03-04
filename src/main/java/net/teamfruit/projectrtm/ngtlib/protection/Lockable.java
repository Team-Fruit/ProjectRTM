package net.teamfruit.projectrtm.ngtlib.protection;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public interface Lockable {
	/**int[3] or TileEntity or Entity*/
	Object getTarget(World world, int x, int y, int z);

	boolean lock(EntityPlayer player, String code);

	boolean unlock(EntityPlayer player, String code);
}