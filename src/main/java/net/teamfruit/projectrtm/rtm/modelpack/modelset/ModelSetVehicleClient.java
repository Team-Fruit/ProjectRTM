package net.teamfruit.projectrtm.rtm.modelpack.modelset;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.teamfruit.projectrtm.rtm.modelpack.cfg.VehicleConfig;

@SideOnly(Side.CLIENT)
public class ModelSetVehicleClient extends ModelSetVehicleBaseClient {
	public ModelSetVehicleClient() {
		super();
	}

	public ModelSetVehicleClient(VehicleConfig par1) {
		super(par1);
	}

	@Override
	public VehicleConfig getDummyConfig() {
		return VehicleConfig.getDummy();
	}
}