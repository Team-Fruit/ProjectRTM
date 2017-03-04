package net.teamfruit.projectrtm.rtm.sound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.teamfruit.projectrtm.ngtlib.io.ScriptUtil;
import net.teamfruit.projectrtm.rtm.entity.train.EntityTrainBase;
import net.teamfruit.projectrtm.rtm.entity.vehicle.EntityVehicleBase;
import net.teamfruit.projectrtm.rtm.entity.vehicle.IUpdateVehicle;
import net.teamfruit.projectrtm.rtm.modelpack.modelset.ModelSetVehicleBaseClient;

@SideOnly(Side.CLIENT)
public class SoundUpdaterVehicle implements IUpdateVehicle {
	protected final SoundHandler theSoundHandler;
	protected final EntityVehicleBase theVehicle;

	protected boolean silent;
	protected ResourceLocation prevSoundResource;
	protected ISound prevSound;

	protected List<MovingSoundVehicle> playingSounds = new ArrayList<MovingSoundVehicle>();
	protected Map<Integer, Object> dataMap;

	public SoundUpdaterVehicle(final SoundHandler par1, final EntityVehicleBase par2) {
		this.theSoundHandler = par1;
		this.theVehicle = par2;
	}

	@Override
	public void update() {
		final ModelSetVehicleBaseClient modelset = (ModelSetVehicleBaseClient) this.theVehicle.getModelSet();
		if (modelset.se!=null)
			//RTMUtil.doScriptFunction(modelset.se, "onUpdate", this);
			ScriptUtil.doScriptIgnoreError(modelset.se, "onUpdate", this);
		else {
			boolean flag = false;
			if (this.theVehicle.isDead) {
				if (this.prevSound!=null)
					((MovingSoundVehicle) this.prevSound).stop();
				return;
			}

			final ResourceLocation newSound = getSound(modelset);
			if (this.prevSoundResource==null||newSound==null||!newSound.equals(this.prevSoundResource)) {
				if (this.prevSound!=null)
					((MovingSoundVehicle) this.prevSound).stop();
				this.prevSoundResource = newSound;
				this.silent = true;
			}

			if (this.silent&&!this.theSoundHandler.isSoundPlaying(this.prevSound)&&this.prevSoundResource!=null) {
				final MovingSoundVehicle sound = createMovingSound(this.theVehicle, this.prevSoundResource, true, changePitch());
				this.theSoundHandler.playSound(sound);
				this.prevSound = sound;
				this.silent = false;
				flag = true;
			}
		}
	}

	protected static MovingSoundVehicle createMovingSound(final EntityVehicleBase vehicle, final ResourceLocation sound, final boolean par3, final boolean par4) {
		if (vehicle instanceof EntityTrainBase)
			return new MovingSoundTrain((EntityTrainBase) vehicle, sound, par3, par4);
		else
			return new MovingSoundVehicle(vehicle, sound, par3, par4);
	}

	protected ResourceLocation getSound(final ModelSetVehicleBaseClient modelset) {
		final float speed = this.theVehicle.getSpeed();
		if (speed>0)
			return modelset.sound_Acceleration;
		return modelset.sound_Stop;
	}

	protected boolean changePitch() {
		return true;
	}

	public float getSpeed() {
		return this.theVehicle.getSpeed()*72.0F;
	}

	public boolean inTunnel() {
		final World world = this.theVehicle.worldObj;
		final int x = MathHelper.floor_double(this.theVehicle.posX);
		final int y = MathHelper.floor_double(this.theVehicle.posY);
		final int z = MathHelper.floor_double(this.theVehicle.posZ);
		return !world.canBlockSeeTheSky(x+1, y, z+1)&&
				!world.canBlockSeeTheSky(x-1, y, z+1)&&
				!world.canBlockSeeTheSky(x+1, y, z-1)&&
				!world.canBlockSeeTheSky(x-1, y, z-1);
	}

	/**リピートあり*/
	public void playSound(final String domain, final String path, final float volume, final float pitch) {
		this.playSound(domain, path, volume, pitch, true);
	}

	public void playSound(final String domain, final String path, final float volume, final float pitch, final boolean repeat) {
		MovingSoundVehicle sound = getPlayingSound(domain, path);
		boolean flag = false;
		if (sound==null) {
			final ResourceLocation resource = new ResourceLocation(domain, path);
			sound = new MovingSoundVehicle(this.theVehicle, resource, repeat, false);
			flag = true;
		}
		sound.setVolume(volume);
		sound.setPitch(pitch);

		if (flag) {
			this.theSoundHandler.playSound(sound);
			this.playingSounds.add(sound);
		}
	}

	public void stopSound(final String domain, final String path) {
		final MovingSoundVehicle playing = getPlayingSound(domain, path);
		if (playing!=null) {
			playing.stop();
			this.playingSounds.remove(playing);
		}
	}

	public void stopAllSounds() {
		for (int i = 0; i<this.playingSounds.size(); ++i)
			this.playingSounds.get(i).stop();

		if (this.prevSound!=null)
			((MovingSoundVehicle) this.prevSound).stop();
	}

	private MovingSoundVehicle getPlayingSound(final String domain, final String path) {
		for (int i = 0; i<this.playingSounds.size(); ++i) {
			final MovingSoundVehicle sound = this.playingSounds.get(i);
			final ResourceLocation resource = sound.getPositionedSoundLocation();
			if (resource.getResourceDomain().equals(domain)&&resource.getResourcePath().equals(path))
				return sound;
		}
		return null;
	}

	@Override
	public void onModelChanged() {
		stopAllSounds();
	}

	public Object getData(final int id) {
		if (this.dataMap==null)
			this.dataMap = new HashMap<Integer, Object>();

		if (this.dataMap.containsKey(id))
			return this.dataMap.get(id);
		return 0;
	}

	public void setData(final int id, final Object value) {
		if (this.dataMap==null)
			this.dataMap = new HashMap<Integer, Object>();
		this.dataMap.put(id, value);
	}

	public Entity getEntity() {
		return this.theVehicle;
	}
}