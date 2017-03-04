package net.teamfruit.projectrtm.rtm.sound;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.teamfruit.projectrtm.ngtlib.util.NGTUtil;
import net.teamfruit.projectrtm.ngtlib.util.NGTUtilClient;

public class SoundPlayer {
	private SoundPlayer() {
	}

	public void playSound(TileEntity tile, ResourceLocation src, boolean repeat) {
	}

	public void stopSound() {
	}

	public boolean isPlaying() {
		return false;
	}

	public static SoundPlayer create() {
		return NGTUtil.isServer() ? new SoundPlayer() : new SoundPlayerClient();
	}

	private static class SoundPlayerClient extends SoundPlayer {
		private MovingSoundTileEntity sound;

		@Override
		public void playSound(TileEntity tile, ResourceLocation src, boolean repeat) {
			if (this.sound!=null) {
				this.stopSound();
			}
			this.sound = new MovingSoundTileEntity(tile, src, repeat);
			this.sound.setVolume(10.0F);
			NGTUtilClient.playSound(this.sound);
		}

		@Override
		public void stopSound() {
			if (this.sound!=null) {
				this.sound.stop();
				this.sound = null;
			}
		}

		@Override
		public boolean isPlaying() {
			return this.sound!=null;
		}
	}
}