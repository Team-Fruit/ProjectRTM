package net.teamfruit.projectrtm.rtm.entity.npc;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

public class EntityDummyPlayer extends EntityPlayer {
	public final EntityNPC npc;

	public EntityDummyPlayer(World world, EntityNPC par2) {
		super(world, new GameProfile(UUID.randomUUID(), "HogeHoge"));
		this.npc = par2;
		this.capabilities.isCreativeMode = true;
		this.yOffset = par2.yOffset;
	}

	@Override
	public void addChatMessage(IChatComponent chat) {
		;
	}

	@Override
	public boolean canCommandSenderUseCommand(int p_70003_1_, String p_70003_2_) {
		return false;
	}

	@Override
	public ChunkCoordinates getPlayerCoordinates() {
		return null;
	}

	@Override
	public float getEyeHeight() {
		return this.height*0.85F;
	}
}