package net.teamfruit.projectrtm.rtm.network;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class PacketNotice implements IMessage {
	public static final byte Side_SERVER = 0;
	public static final byte Side_CLIENT = 1;
	/**side | hasPosData << 1*/
	public byte type;
	public String notice;
	public int x, y, z;

	public PacketNotice() {
	}

	public PacketNotice(byte par1, String par2) {
		this.type = par1;
		this.notice = par2;
	}

	public PacketNotice(byte par1, int x, int y, int z, String data) {
		this(par1, data);
		this.type |= 2;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeByte(this.type);
		ByteBufUtils.writeUTF8String(buffer, this.notice);
		if ((this.type&2)==2) {
			buffer.writeInt(this.x);
			buffer.writeInt(this.y);
			buffer.writeInt(this.z);
		}
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		this.type = buffer.readByte();
		this.notice = ByteBufUtils.readUTF8String(buffer);
		if ((this.type&2)==2) {
			this.x = buffer.readInt();
			this.y = buffer.readInt();
			this.z = buffer.readInt();
		}
	}
}