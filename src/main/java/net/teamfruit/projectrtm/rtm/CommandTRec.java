package net.teamfruit.projectrtm.rtm;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.teamfruit.projectrtm.rtm.network.PacketNotice;

public class CommandTRec extends CommandBase {
	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public String getCommandName() {
		return "trec";
	}

	@Override
	public String getCommandUsage(ICommandSender commandSender) {
		return "commands.trec.usage";
	}

	@Override
	public void processCommand(ICommandSender commandSender, String[] s) {
		EntityPlayerMP player = getCommandSenderAsPlayer(commandSender);
		RTMCore.NETWORK_WRAPPER.sendTo(new PacketNotice(PacketNotice.Side_CLIENT, "TRec"), player);
	}
}