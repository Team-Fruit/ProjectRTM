package net.teamfruit.projectrtm.rtm.modelpack.modelset;

import net.minecraft.util.ResourceLocation;
import net.teamfruit.projectrtm.rtm.modelpack.cfg.MachineConfig;

public class ModelSetMachine extends ModelSetBase<MachineConfig> {
	public final ResourceLocation sound_OnActivate;
	public final ResourceLocation sound_Running;

	public ModelSetMachine() {
		super();
		this.sound_OnActivate = null;
		this.sound_Running = null;
	}

	public ModelSetMachine(MachineConfig par1) {
		super(par1);
		this.sound_OnActivate = this.getSoundResource(par1.sound_OnActivate);
		this.sound_Running = this.getSoundResource(par1.sound_Running);
	}

	@Override
	public MachineConfig getDummyConfig() {
		return MachineConfig.getDummy();
	}
}