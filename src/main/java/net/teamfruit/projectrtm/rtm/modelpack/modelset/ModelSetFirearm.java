package net.teamfruit.projectrtm.rtm.modelpack.modelset;

import net.teamfruit.projectrtm.rtm.modelpack.cfg.FirearmConfig;

public class ModelSetFirearm extends ModelSetBase<FirearmConfig> {
	public ModelSetFirearm() {
		super();
	}

	public ModelSetFirearm(FirearmConfig par1) {
		super(par1);
	}

	@Override
	public FirearmConfig getDummyConfig() {
		return FirearmConfig.getDummyConfig();
	}
}