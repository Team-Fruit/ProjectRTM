package net.teamfruit.projectrtm.rtm.entity.vehicle;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.teamfruit.projectrtm.rtm.RTMItem;

public class EntityCar extends EntityVehicle {
	public EntityCar(World world) {
		super(world);
		this.stepHeight = 2.0F;
	}

	@Override
	public String getDefaultName() {
		return "CV33";
	}

	@Override
	protected ItemStack getVehicleItem() {
		return new ItemStack(RTMItem.itemVehicle, 1, 0);
	}
}