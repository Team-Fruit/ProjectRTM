package net.teamfruit.projectrtm.rtm.entity.train;

import net.minecraft.entity.Entity;
import net.teamfruit.projectrtm.rtm.entity.vehicle.RenderVehicleBase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTrain extends RenderVehicleBase {
	public static final RenderTrain INSTANCE = new RenderTrain();

	@Override
	public void doRender(Entity par1, double par2, double par4, double par6, float par8, float par9) {
		EntityTrainBase train = (EntityTrainBase) par1;
		this.renderVehicleBase(train, par2, par4, par6, par8, par9);
	}
}