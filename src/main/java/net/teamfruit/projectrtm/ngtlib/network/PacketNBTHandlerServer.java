package net.teamfruit.projectrtm.ngtlib.network;

import net.minecraft.world.World;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketNBTHandlerServer implements IMessageHandler<PacketNBT, IMessage> {
	@Override
	public IMessage onMessage(PacketNBT message, MessageContext ctx) {
		if (!message.nbtData.getBoolean("ToClient")) {
			World world = ctx.getServerHandler().playerEntity.worldObj;
			message.onGetPacket(world);
		}
		return null;
	}
}