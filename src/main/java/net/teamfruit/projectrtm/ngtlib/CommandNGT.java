package net.teamfruit.projectrtm.ngtlib;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.teamfruit.projectrtm.ngtlib.io.NGTLog;
import net.teamfruit.projectrtm.ngtlib.network.PacketNotice;
import net.teamfruit.projectrtm.ngtlib.util.NGTCertificate;

public class CommandNGT extends CommandBase {
	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public String getCommandName() {
		return "ngt";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "commands.ngt.usage";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);

		if (args.length==1) {
			if (NGTCertificate.registerKey(player, args[0])) {
				NGTCore.NETWORK_WRAPPER.sendTo(new PacketNotice(PacketNotice.Side_CLIENT, "regKey"), player);
			}
		} else {
			NGTLog.sendChatMessage(player, "commands.ngt.invalid_command");
		}
	}
}