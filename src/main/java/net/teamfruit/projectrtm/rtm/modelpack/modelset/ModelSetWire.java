package net.teamfruit.projectrtm.rtm.modelpack.modelset;

import net.teamfruit.projectrtm.rtm.modelpack.cfg.WireConfig;

public class ModelSetWire extends ModelSetBase<WireConfig> {
	public ModelSetWire() {
		super();
	}

	public ModelSetWire(WireConfig cfg) {
		super(cfg);
	}

	@Override
	public WireConfig getDummyConfig() {
		return WireConfig.getDummy();
	}
}