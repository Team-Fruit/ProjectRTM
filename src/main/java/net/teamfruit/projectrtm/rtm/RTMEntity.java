package net.teamfruit.projectrtm.rtm;

import cpw.mods.fml.common.registry.EntityRegistry;
import net.teamfruit.projectrtm.rtm.entity.EntityATC;
import net.teamfruit.projectrtm.rtm.entity.EntityBullet;
import net.teamfruit.projectrtm.rtm.entity.EntityBumpingPost;
import net.teamfruit.projectrtm.rtm.entity.EntityMMBoundingBox;
import net.teamfruit.projectrtm.rtm.entity.EntityTrainDetector;
import net.teamfruit.projectrtm.rtm.entity.npc.EntityMotorman;
import net.teamfruit.projectrtm.rtm.entity.npc.EntityNPC;
import net.teamfruit.projectrtm.rtm.entity.train.EntityBogie;
import net.teamfruit.projectrtm.rtm.entity.train.EntityFreightCar;
import net.teamfruit.projectrtm.rtm.entity.train.EntityTanker;
import net.teamfruit.projectrtm.rtm.entity.train.EntityTrain;
import net.teamfruit.projectrtm.rtm.entity.train.EntityTrainBase;
import net.teamfruit.projectrtm.rtm.entity.train.parts.EntityArtillery;
import net.teamfruit.projectrtm.rtm.entity.train.parts.EntityContainer;
import net.teamfruit.projectrtm.rtm.entity.train.parts.EntityFloor;
import net.teamfruit.projectrtm.rtm.entity.train.parts.EntityTie;
import net.teamfruit.projectrtm.rtm.entity.vehicle.EntityCar;
import net.teamfruit.projectrtm.rtm.entity.vehicle.EntityPlane;
import net.teamfruit.projectrtm.rtm.entity.vehicle.EntityShip;

public final class RTMEntity {
	private static final byte FREQ_INSTALLED = 10;

	private static short nextId;
	private static final short RANGE = 1024;//trackingRange->EntityTrackerで設定される

	public static void init(Object mod) {
		EntityRegistry.registerModEntity(EntityFloor.class, "RTM.E.Floor", getNextId(), mod, RANGE, 3, false);
		EntityRegistry.registerModEntity(EntityBogie.class, "RTM.E.Bogie", getNextId(), mod, RANGE, 3, false);
		EntityRegistry.registerModEntity(EntityMotorman.class, "RTM.E.Motorman", getNextId(), mod, RANGE, 4, true);
		EntityRegistry.registerModEntity(EntityATC.class, "RTM.E.ATC", getNextId(), mod, 160, FREQ_INSTALLED, false);
		EntityRegistry.registerModEntity(EntityTrainDetector.class, "RTM.E.TrainDetector", getNextId(), mod, 160, FREQ_INSTALLED, false);
		EntityRegistry.registerModEntity(EntityContainer.class, "RTM.E.Container", getNextId(), mod, 160, 3, false);
		EntityRegistry.registerModEntity(EntityArtillery.class, "RTM.E.Artillery", getNextId(), mod, 160, 3, false);
		EntityRegistry.registerModEntity(EntityBullet.class, "RTM.E.Bullet", getNextId(), mod, 256, 3, true);
		EntityRegistry.registerModEntity(EntityBumpingPost.class, "RTM.E.BumpingPost", getNextId(), mod, 160, FREQ_INSTALLED, false);
		EntityRegistry.registerModEntity(EntityTie.class, "RTM.E.Tie", getNextId(), mod, 160, 3, false);
		EntityRegistry.registerModEntity(EntityMMBoundingBox.class, "RTM.E.MMBB", getNextId(), mod, 160, Integer.MAX_VALUE, false);
		EntityRegistry.registerModEntity(EntityCar.class, "RTM.E.Car", getNextId(), mod, 160, 3, true);
		EntityRegistry.registerModEntity(EntityShip.class, "RTM.E.Ship", getNextId(), mod, 160, 3, true);
		EntityRegistry.registerModEntity(EntityPlane.class, "RTM.E.Plane", getNextId(), mod, 160, 3, true);
		EntityRegistry.registerModEntity(EntityNPC.class, "RTM.E.NPC", getNextId(), mod, RANGE, 4, true);

		registerTrain(EntityTrain.class, "RTM.E.Train", mod);
		registerTrain(EntityFreightCar.class, "RTM.E.FreightCar", mod);
		registerTrain(EntityTanker.class, "RTM.E.Tanker", mod);
	}

	public static void registerTrain(Class<? extends EntityTrainBase> clazz, String name, Object mod) {
		EntityRegistry.registerModEntity(clazz, name, getNextId(), mod, RANGE, 3, false);
	}

	public static int getNextId() {
		return nextId++;
	}
}