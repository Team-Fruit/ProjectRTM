package net.teamfruit.projectrtm.rtm.util;

import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class RTMUtil {
	public static final List<String> MESSAGELIST = new LinkedList<String>();

	@SideOnly(Side.CLIENT)
	public static void setDebugMessage(String par1) {
		MESSAGELIST.add(par1);
	}
}