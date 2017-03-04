package net.teamfruit.projectrtm.rtm.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.teamfruit.projectrtm.ngtlib.util.NGTUtil;
import net.teamfruit.projectrtm.ngtlib.util.NGTUtilClient;
import net.teamfruit.projectrtm.rtm.RTMCore;
import net.teamfruit.projectrtm.rtm.entity.npc.macro.MacroRecorder;
import net.teamfruit.projectrtm.rtm.entity.train.EntityTrainBase;
import net.teamfruit.projectrtm.rtm.entity.train.parts.EntityArtillery;
import net.teamfruit.projectrtm.rtm.entity.vehicle.EntityPlane;
import net.teamfruit.projectrtm.rtm.entity.vehicle.EntityVehicle;
import net.teamfruit.projectrtm.rtm.modelpack.cfg.TrainConfig;
import net.teamfruit.projectrtm.rtm.modelpack.modelset.ModelSetTrainClient;
import net.teamfruit.projectrtm.rtm.network.PacketRTMKey;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class RTMKeyHandlerClient {
	private static final String CATG_RTM = "rtm.key";
	public static final RTMKeyHandlerClient INSTANCE = new RTMKeyHandlerClient();

	//public static final KeyBinding keyAccelerate = new KeyBinding("rtm.accelerate", Keyboard.KEY_O, "rtm.accelerate");
	//public static final KeyBinding keyDecelerate = new KeyBinding("rtm.decelerate", Keyboard.KEY_L, "rtm.decelerate");
	public static final KeyBinding keyHorn = new KeyBinding("rtm.horn", Keyboard.KEY_P, CATG_RTM);
	public static final KeyBinding keyChime = new KeyBinding("rtm.chime", Keyboard.KEY_I, CATG_RTM);
	public static final KeyBinding keyATS = new KeyBinding("rtm.ats", Keyboard.KEY_COMMA, CATG_RTM);

	private boolean sneaking;

	private RTMKeyHandlerClient() {
	}

	public static void init() {
		//ClientRegistry.registerKeyBinding(keyAccelerate);
		//ClientRegistry.registerKeyBinding(keyDecelerate);
		ClientRegistry.registerKeyBinding(keyHorn);
		ClientRegistry.registerKeyBinding(keyChime);
		ClientRegistry.registerKeyBinding(keyATS);
	}

	public void onTickStart() {
		/*Minecraft mc = NGTUtilClient.getMinecraft();
		Entity entity = mc.thePlayer.ridingEntity;
		if(entity != null && entity instanceof EntityVehicle)
		{
			if(mc.gameSettings.keyBindJump.getIsKeyPressed())
			{
				this.sendKeyToServer(RTMCore.KEY_JUMP, "");
			}
			else if(mc.gameSettings.keyBindSneak.getIsKeyPressed())
			{
				if(entity instanceof EntityPlane && !entity.onGround)
				{
					this.sendKeyToServer(RTMCore.KEY_SNEAK, "");
					this.unpressKey(mc.gameSettings.keyBindSneak);
				}
			}
		}*/

		Minecraft mc = NGTUtilClient.getMinecraft();
		EntityPlayer player = mc.thePlayer;
		if (Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {
			if (player.isRiding()&&player.ridingEntity instanceof EntityVehicle) {
				this.sendKeyToServer(RTMCore.KEY_JUMP, "");
			}
		} else if (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
			if (player.isRiding()&&player.ridingEntity instanceof EntityPlane) {
				if (!player.ridingEntity.onGround) {
					this.sendKeyToServer(RTMCore.KEY_SNEAK, "");
				}
			}
		}
	}

	public void onTickEnd() {
		/*Minecraft mc = NGTUtilClient.getMinecraft();
		if(this.sneaking)
		{
			KeyBinding.setKeyBindState(p_74510_0_, true);
			this.sneaking = false;
		}*/
	}

	@SubscribeEvent
	public void keyDown(KeyInputEvent event) {
		Minecraft mc = NGTUtilClient.getMinecraft();
		EntityPlayer player = mc.thePlayer;

		if (mc.gameSettings.keyBindBack.isPressed()) {
			if (player.isRiding()&&player.ridingEntity instanceof EntityTrainBase) {
				this.sendKeyToServer(RTMCore.KEY_Forward, "");
				MacroRecorder.INSTANCE.recNotch(player.worldObj, 1);
			}
		} else if (mc.gameSettings.keyBindForward.isPressed()) {
			if (player.isRiding()&&player.ridingEntity instanceof EntityTrainBase) {
				this.sendKeyToServer(RTMCore.KEY_Back, "");
				MacroRecorder.INSTANCE.recNotch(player.worldObj, -1);
			}
		}
		/*else if(mc.gameSettings.keyBindLeft.isPressed())
		{
			this.sendKeyToServer(RTMCore.KEY_LEFT, "");
		}
		else if(mc.gameSettings.keyBindRight.isPressed())
		{
			this.sendKeyToServer(RTMCore.KEY_RIGHT, "");
		}*/
		else if (mc.gameSettings.keyBindJump.getIsKeyPressed()) {
			/*if(player.isRiding() && player.ridingEntity instanceof EntityVehicle)
			{
				this.sendKeyToServer(RTMCore.KEY_JUMP, "");
			}*/
		} else if (mc.gameSettings.keyBindSneak.getIsKeyPressed()) {
			if (player.isRiding()&&player.ridingEntity instanceof EntityPlane) {
				if (((EntityPlane) player.ridingEntity).disableUnmount()) {
					//this.sendKeyToServer(RTMCore.KEY_SNEAK, "");
					this.unpressKey(mc.gameSettings.keyBindSneak);
				}
			}
		} else if (keyHorn.isPressed()) {
			if (player.isRiding()) {
				if (player.ridingEntity instanceof EntityTrainBase) {
					this.playSound(player, RTMCore.KEY_Horn);
				} else if (player.ridingEntity instanceof EntityArtillery) {
					this.sendKeyToServer(RTMCore.KEY_Fire, "");
				}
			}
		} else if (keyChime.isPressed()) {
			this.playSound(player, RTMCore.KEY_Chime);
		} else if (mc.gameSettings.keyBindInventory.getIsKeyPressed())//isPressedだとMinecraft1976が処理されない
		{
			if (player.isRiding()&&player.ridingEntity instanceof EntityTrainBase) {
				mc.gameSettings.keyBindInventory.isPressed();
				this.sendKeyToServer(RTMCore.KEY_ControlPanel, "");
			}
		} else if (keyATS.isPressed()) {
			this.sendKeyToServer(RTMCore.KEY_ATS, "");
		}
	}

	private void unpressKey(KeyBinding key) {
		NGTUtil.getMethod(KeyBinding.class, key, new String[] { "unpressKey", "func_74505_d" }, new Class[] {});
	}

	private void sendKeyToServer(byte keyCode, String sound) {
		EntityPlayer player = NGTUtilClient.getMinecraft().thePlayer;
		RTMCore.NETWORK_WRAPPER.sendToServer(new PacketRTMKey(player, keyCode, sound));
	}

	private void playSound(EntityPlayer player, byte key) {
		if (player.isRiding()&&player.ridingEntity instanceof EntityTrainBase) {
			EntityTrainBase train = (EntityTrainBase) player.ridingEntity;
			ModelSetTrainClient modelset = (ModelSetTrainClient) train.getModelSet();
			if (modelset!=null) {
				ResourceLocation sound = null;
				if (key==RTMCore.KEY_Horn) {
					//ClientProxy.playSound(player, modelset.sound_Horn);
					sound = modelset.sound_Horn;
					MacroRecorder.INSTANCE.recHorn(player.worldObj);
				} else if (key==RTMCore.KEY_Chime) {
					int index = train.getTrainStateData(9);
					String[][] sa0 = ((TrainConfig) modelset.getConfig()).sound_Announcement;
					if (sa0!=null&&index<sa0.length) {
						String[] sa1 = sa0[index][1].split(":");
						//ClientProxy.playSound(player, new ResourceLocation(sa1[0], sa1[1]));
						sound = new ResourceLocation(sa1[0], sa1[1]);
						MacroRecorder.INSTANCE.recChime(player.worldObj, sa0[index][1]);
					}
				}

				if (sound!=null) {
					this.sendKeyToServer(key, sound.getResourceDomain()+":"+sound.getResourcePath());
				}
			}
		}
	}
}