package net.teamfruit.projectrtm.ngtlib.io;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class NGTLog {
	private static final Logger logger = LogManager.getLogger("NGT");

	public static void debug(String par1) {
		debug(par1, new Object[0]);
	}

	public static void debug(String par1, Object... par2) {
		if (par2==null||par2.length==0) {
			logger.log(Level.INFO, par1);
		} else {
			logger.log(Level.INFO, String.format(par1, par2));
		}
	}

	/*
	player.sendChatToPlayer(
		ChatMessageComponent.createFromTranslationWithSubstitutions(
			"commands.message.display.outgoing",
			new Object[]{player.getCommandSenderName(), message}
		).setColor(EnumChatFormatting.GRAY).setItalic(Boolean.valueOf(true))
	);
	*/

	/**フォーマットはこちらで行う*/
	public static void sendChatMessage(EntityPlayer player, String message, Object... objects)//ServerCommandManager
	{
		player.addChatMessage(new ChatComponentTranslation(message, objects));
	}

	/**フォーマットはこちらで行う*/
	public static void sendChatMessageToAll(String message, Object... objects) {
		if (MinecraftServer.getServer()==null) {
			debug("[NGTLog] Can't send message. This is client.");
		} else {
			MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation(message, objects));
		}
	}
}