package net.teamfruit.projectrtm.ngtlib.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.teamfruit.projectrtm.ngtlib.protection.ProtectionManager;
import net.teamfruit.projectrtm.ngtlib.util.NGTUtil;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketProtection implements IMessage, IMessageHandler<PacketProtection, IMessage> {
	private String name;
	private NBTTagCompound data;

	public PacketProtection() {
	}

	public PacketProtection(String par1, NBTTagCompound par2) {
		this.name = par1;
		this.data = par2;
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		ByteBufUtils.writeUTF8String(buffer, this.name);
		ByteBufUtils.writeTag(buffer, this.data);
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		this.name = ByteBufUtils.readUTF8String(buffer);
		this.data = ByteBufUtils.readTag(buffer);
	}

	@Override
	public IMessage onMessage(PacketProtection message, MessageContext ctx) {
		World world = NGTUtil.getClientWorld();
		if (world==null) {
			return null;
		}

		ProtectionManager.INSTANCE.receivePacket(message.name, message.data);
		return null;
	}
}