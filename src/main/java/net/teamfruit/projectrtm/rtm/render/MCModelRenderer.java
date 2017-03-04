package net.teamfruit.projectrtm.rtm.render;

import net.minecraft.entity.Entity;
import net.teamfruit.projectrtm.ngtlib.renderer.model.MCModel;
import net.teamfruit.projectrtm.rtm.modelpack.modelset.ModelSetVehicleBaseClient;

public class MCModelRenderer extends VehiclePartsRenderer {
	private MCModel model;
	private boolean light;
	private boolean alphaBlend;

	public MCModelRenderer(String... par1) {
		super(par1);
	}

	@Override
	public void init(ModelSetVehicleBaseClient par1, ModelObject par2) {
		this.model = (MCModel) par2.model;
		this.light = par2.light;
		this.alphaBlend = par2.alphaBlend;
	}

	@Override
	public void render(Entity entity, int pass, float par3) {
		if ((!this.light&&pass>=2)||(!this.alphaBlend&&pass==1)) {
			return;
		}

		this.model.renderAll(false);
	}
}