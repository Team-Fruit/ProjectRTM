package net.teamfruit.projectrtm.rtm.modelpack.modelset;

import net.minecraft.util.ResourceLocation;
import net.teamfruit.projectrtm.rtm.modelpack.cfg.VehicleBaseConfig;

public abstract class ModelSetVehicleBase<T extends VehicleBaseConfig> extends ModelSetBase<T> {
	public final ResourceLocation sound_doorOpen;
	public final ResourceLocation sound_doorClose;

	public ModelSetVehicleBase() {
		super();
		this.sound_doorOpen = null;
		this.sound_doorClose = null;
	}

	public ModelSetVehicleBase(VehicleBaseConfig par1) {
		super((T) par1);
		this.sound_doorOpen = this.getSoundResource(par1.sound_DoorOpen);
		this.sound_doorClose = this.getSoundResource(par1.sound_DoorClose);
	}

	@Override
	public T getConfig() {
		return this.cfg;
	}
}