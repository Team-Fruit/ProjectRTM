package net.teamfruit.projectrtm.rtm.entity.vehicle;

import net.minecraft.server.gui.IUpdatePlayerListBox;

public interface IUpdateVehicle extends IUpdatePlayerListBox {
	void onModelChanged();
}