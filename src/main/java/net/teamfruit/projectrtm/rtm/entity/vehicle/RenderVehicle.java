package net.teamfruit.projectrtm.rtm.entity.vehicle;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderVehicle extends RenderVehicleBase {
	public static final RenderVehicle INSTANCE = new RenderVehicle();

	private RenderVehicle() {
	}
}