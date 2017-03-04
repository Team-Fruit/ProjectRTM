package net.teamfruit.projectrtm.rtm.sound;

import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import net.teamfruit.projectrtm.rtm.entity.train.EntityBogie;
import net.teamfruit.projectrtm.rtm.entity.train.EntityTrainBase;
import net.teamfruit.projectrtm.rtm.entity.train.util.EnumNotch;
import net.teamfruit.projectrtm.rtm.entity.train.util.Formation;
import net.teamfruit.projectrtm.rtm.modelpack.cfg.TrainConfig;
import net.teamfruit.projectrtm.rtm.modelpack.modelset.ModelSetVehicleBase;
import net.teamfruit.projectrtm.rtm.modelpack.modelset.ModelSetVehicleBaseClient;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SoundUpdaterTrain extends SoundUpdaterVehicle {
	private final EntityTrainBase theTrain;

	/**{sound1, bell}*/
	private MovingSoundEntity[] atsSound = new MovingSoundEntity[2];
	private int currentSignal;

	public SoundUpdaterTrain(SoundHandler par1, EntityTrainBase par2) {
		super(par1, par2);
		this.theTrain = par2;
	}

	@Override
	public void update() {
		EntityBogie bogie = this.theTrain.getBogie(this.theTrain.getTrainDirection());
		if (bogie==null) {
			return;
		}

		int signal = this.theTrain.getSignal();
		if (this.theTrain.getSpeed()>0.0F) {
			switch (signal) {
				case 1:
					if (this.currentSignal!=1) {
						if (this.atsSound[0]!=null) {
							this.atsSound[0].stop();
						}
						if (this.atsSound[1]!=null) {
							this.atsSound[1].stop();
						}

						this.atsSound[0] = new MovingSoundEntity(bogie, new ResourceLocation("rtm", "train.ats"), true);
						this.theSoundHandler.playSound(this.atsSound[0]);
						this.atsSound[1] = new MovingSoundEntity(bogie, new ResourceLocation("rtm", "train.ats_bell"), true);
						this.theSoundHandler.playSound(this.atsSound[1]);
						this.currentSignal = 1;
					}
					break;

				case -1:
					if (this.currentSignal!=-1) {
						if (this.atsSound[1]!=null) {
							this.atsSound[1].stop();
						}
						this.currentSignal = -1;
					}
					break;
			}
		} else {
			if (signal!=-1&&this.currentSignal!=0) {
				if (this.atsSound[0]!=null) {
					this.atsSound[0].stop();
				}
				if (this.atsSound[1]!=null) {
					this.atsSound[1].stop();
				}
				this.currentSignal = 0;
			}
		}

		super.update();
	}

	@Override
	protected ResourceLocation getSound(ModelSetVehicleBaseClient modelset) {
		float speed = this.theTrain.getSpeed();
		if (speed>0) {
			float acceleration = EnumNotch.getAcceleration(this.getNotch(), speed);

			if (speed<((TrainConfig) modelset.getConfig()).maxSpeed[0]) {
				return acceleration>0.0F ? modelset.sound_S_A : modelset.sound_D_S;
			} else {
				return acceleration>0.0F ? modelset.sound_Acceleration : modelset.sound_Deceleration;
			}
		}
		return modelset.sound_Stop;
	}

	@Override
	protected boolean changePitch() {
		ModelSetVehicleBase<TrainConfig> modelset = (ModelSetVehicleBase) this.theTrain.getModelSet();
		float speed = this.theTrain.getSpeed();
		return speed>0 ? (speed<modelset.getConfig().maxSpeed[0] ? false : true) : false;
	}

	public float getMaxSpeed() {
		return this.theTrain.getModelSet().getConfig().maxSpeed[4]*72.0F;
	}

	public int getNotch() {
		Formation formation = this.theTrain.getFormation();
		return formation==null ? 0 : formation.getNotch();
	}

	public byte getState(int id) {
		return this.theTrain.getTrainStateData(id);
	}

	public boolean isComplessorActive() {
		return this.theTrain.complessorActive;
	}

	public int complessorCount() {
		return this.theTrain.brakeAirCount-EntityTrainBase.MIN_AIR_COUNT;
	}
}