package net.teamfruit.projectrtm.rtm.sound;

import net.minecraft.util.ResourceLocation;
import net.teamfruit.projectrtm.rtm.entity.train.EntityTrainBase;
import net.teamfruit.projectrtm.rtm.modelpack.cfg.TrainConfig;
import net.teamfruit.projectrtm.rtm.modelpack.modelset.ModelSetVehicleBase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MovingSoundTrain extends MovingSoundVehicle {
	public MovingSoundTrain(EntityTrainBase train, ResourceLocation sound, boolean par3, boolean par4) {
		super(train, sound, par3, par4);
	}

	@Override
	public void update() {
		super.update();

		if (this.changePitch) {
			EntityTrainBase train = (EntityTrainBase) this.entity;
			ModelSetVehicleBase<TrainConfig> modelset = (ModelSetVehicleBase) train.getModelSet();
			float f0 = modelset.getConfig().maxSpeed[0];
			float f1 = (train.getSpeed()-f0)/(modelset.getConfig().maxSpeed[4]-f0)+1.0F;//0.5~2.0
			this.field_147663_c = f1;
		}
	}
}