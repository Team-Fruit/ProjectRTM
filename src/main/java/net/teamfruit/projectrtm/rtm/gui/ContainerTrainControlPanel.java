package net.teamfruit.projectrtm.rtm.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.Slot;
import net.teamfruit.projectrtm.rtm.entity.train.EntityTrainBase;

public class ContainerTrainControlPanel extends ContainerPlayer {
	public final EntityTrainBase train;
	public final EntityPlayer player;
	private List slotsList;

	public ContainerTrainControlPanel(EntityTrainBase par1, EntityPlayer par2) {
		super(par2.inventory, !par2.worldObj.isRemote, par2);
		this.train = par1;
		this.player = par2;
		this.slotsList = this.inventorySlots;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return player.equals(this.player);
	}

	public void setCurrentTab(int tabIndex)//Gui->PacketNotice
	{
		if (tabIndex==TabTrainControlPanel.TAB_Inventory.getTabIndex()) {
			this.inventorySlots = this.slotsList;
		} else {
			this.inventorySlots = new ArrayList();
			for (int i = 0; i<9; ++i) {
				Slot slot = new Slot(this.player.inventory, i, 8+i*18, 142);
				slot.slotNumber = this.inventorySlots.size();
				this.inventorySlots.add(slot);
			}
		}
	}
}