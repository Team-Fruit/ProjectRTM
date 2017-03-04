package net.teamfruit.projectrtm.rtm.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.teamfruit.projectrtm.ngtlib.gui.GuiSelect;
import net.teamfruit.projectrtm.ngtlib.gui.GuiSlotCustom.SlotElement;
import net.teamfruit.projectrtm.rtm.entity.npc.EntityMotorman;
import net.teamfruit.projectrtm.rtm.entity.npc.macro.MacroRecorder;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiMotorman extends GuiSelect {
	public static GuiMotorman getGui(final EntityMotorman entity) {
		File macroFolder = MacroRecorder.INSTANCE.getMacroFolder();
		List<File> list = new ArrayList<File>();
		for (File child : macroFolder.listFiles()) {
			if (child.isFile()&&child.getName().endsWith(".txt")) {
				list.add(child);
			}
		}

		if (list.isEmpty()) {
			return null;
		}

		ItemStack icon = new ItemStack(Items.book);
		SlotElement[] elements = new SlotElement[list.size()];
		for (int i = 0; i<elements.length; ++i) {
			File file = list.get(i);
			elements[i] = new SlotElementItem<File>(new ISelector() {
				@Override
				public void select(Object par1) {
					entity.setMacro((File) par1);
				}
			}, file, file.getName(), icon);
		}
		return new GuiMotorman(entity, elements);
	}

	private GuiMotorman(EntityMotorman entity, SlotElement[] par2) {
		super(null, par2);
	}

	@Override
	public void initGui() {
		super.initGui();
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
	}

	@Override
	protected void closeScreen() {
		this.mc.displayGuiScreen(null);
	}
}