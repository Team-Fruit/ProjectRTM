package net.teamfruit.projectrtm.rtm.modelpack.modelset;

import net.teamfruit.projectrtm.rtm.modelpack.cfg.RailConfig;

public class ModelSetRail extends ModelSetBase<RailConfig> {
	public ModelSetRail() {
		super();
	}

	public ModelSetRail(RailConfig par1) {
		super(par1);
	}

	@Override
	public RailConfig getDummyConfig() {
		return RailConfig.getDummy();
	}
}