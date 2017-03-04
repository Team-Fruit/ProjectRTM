package net.teamfruit.projectrtm.ngtlib.world;

import net.minecraft.world.WorldProvider;

public class WorldProviderDummy extends WorldProvider {
	@Override
	public String getDimensionName() {
		return "dummy";
	}
}