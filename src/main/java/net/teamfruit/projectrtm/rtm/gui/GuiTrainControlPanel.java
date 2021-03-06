package net.teamfruit.projectrtm.rtm.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.teamfruit.projectrtm.rtm.RTMCore;
import net.teamfruit.projectrtm.rtm.entity.npc.macro.MacroRecorder;
import net.teamfruit.projectrtm.rtm.entity.train.EntityTrainBase;
import net.teamfruit.projectrtm.rtm.entity.train.util.Formation;
import net.teamfruit.projectrtm.rtm.entity.train.util.FormationEntry;
import net.teamfruit.projectrtm.rtm.entity.train.util.TrainState;
import net.teamfruit.projectrtm.rtm.entity.train.util.TrainState.TrainStateType;
import net.teamfruit.projectrtm.rtm.modelpack.cfg.TrainConfig;
import net.teamfruit.projectrtm.rtm.modelpack.modelset.ModelSetTrainClient;
import net.teamfruit.projectrtm.rtm.modelpack.modelset.ModelSetVehicleBase;
import net.teamfruit.projectrtm.rtm.modelpack.modelset.ModelSetVehicleBaseClient;
import net.teamfruit.projectrtm.rtm.network.PacketNotice;
import net.teamfruit.projectrtm.rtm.network.PacketSetTrainState;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTrainControlPanel extends InventoryEffectRenderer {
	private static final ResourceLocation tabTexture = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");

	private int selectedTabIndex = TabTrainControlPanel.TAB_Inventory.getTabIndex();
	/** Amount scrolled in Creative mode inventory (0 = top, 1 = bottom) */
	private float currentScroll;
	/** True if the scrollbar is being dragged */
	private boolean isScrolling;
	private boolean wasClicking;
	private List slotsList;
	private Slot slot;
	/**開いてるタブを保持*/
	private static int tabPage = 0;
	private int maxPages = 0;

	protected final EntityTrainBase train;
	protected final EntityPlayer player;
	protected final ModelSetVehicleBase<TrainConfig> modelset;

	private GuiButton buttonChunkLoader;
	private GuiButton buttonDestination;
	private GuiButton buttonAnnouncement;
	private GuiButton[] buttonDirection = new GuiButton[3];
	/**0:R, L:1*/
	private GuiButtonDoor[] buttonDoor = new GuiButtonDoor[2];

	public GuiTrainControlPanel(ContainerTrainControlPanel par1) {
		super(par1);
		this.train = par1.train;
		this.player = par1.player;
		this.modelset = (ModelSetTrainClient) par1.train.getModelSet();
		this.player.openContainer = this.inventorySlots;
		this.allowUserInput = true;
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
	}

	@Override
	public void initGui() {
		super.initGui();

		this.buttonList.clear();
		this.selectedTabIndex = 0;
		this.setCurrentTab(TabTrainControlPanel.tabArray[0]);//i
		int tabCount = TabTrainControlPanel.tabArray.length;
		if (tabCount>12) {
			this.buttonList.add(new GuiButton(101, this.guiLeft, this.guiTop-50, 20, 20, "<"));
			this.buttonList.add(new GuiButton(102, this.guiLeft+this.xSize-20, this.guiTop-50, 20, 20, ">"));
			this.maxPages = ((tabCount-12)/10)+1;
		}
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
	}

	private void setCurrentTab(TabTrainControlPanel tab) {
		if (tab==null) {
			return;
		}

		this.selectedTabIndex = tab.getTabIndex();
		ContainerTrainControlPanel containerTrain = (ContainerTrainControlPanel) this.inventorySlots;
		this.field_147008_s.clear();

		if (this.slotsList==null) {
			this.slotsList = containerTrain.inventorySlots;//ぬるぽ回避
		}

		if (tab==TabTrainControlPanel.TAB_Inventory) {
			;
			containerTrain.inventorySlots = this.slotsList;

			this.buttonList.clear();
		} else if (tab==TabTrainControlPanel.TAB_Setting) {
			//this.slotsList = containerTrain.inventorySlots;
			containerTrain.inventorySlots = new ArrayList();
			for (int i = 0; i<9; ++i) {
				Slot slot = new Slot(this.player.inventory, i, 8+i*18, 142);
				slot.slotNumber = containerTrain.inventorySlots.size();
				containerTrain.inventorySlots.add(slot);
			}

			this.buttonList.clear();
			//wMax:168
			TrainStateType t0 = TrainStateType.State_InteriorLight;
			this.buttonList.add(new GuiButton(124, this.guiLeft+4, this.guiTop+4, 82, 20, this.getFormattedText(t0.id, this.train.getTrainStateData(t0.id))));
			this.buttonList.add(new GuiButton(125, this.guiLeft+90, this.guiTop+4, 82, 20, this.getFormattedText(5, this.train.getTrainStateData(5))));
			this.buttonList.add(new GuiButton(126, this.guiLeft+4, this.guiTop+28, 82, 20, this.getFormattedText(6, this.train.getTrainStateData(6))));

			int i0 = this.train.getTrainStateData(TrainStateType.State_Direction.id);
			for (int j = 0; j<3; ++j) {
				this.buttonDirection[j] = new GuiButton(140+j, this.guiLeft+91+27*j, this.guiTop+28, 27, 20, this.getFormattedText(TrainStateType.State_Direction.id, (byte) j));
				this.buttonList.add(this.buttonDirection[j]);
				if (j==i0) {
					this.buttonDirection[j].enabled = false;
				}
			}
			/*this.buttonList.add(new GuiButton(140, this.guiLeft + 91, this.guiTop + 28, 27, 20, this.getFormattedText(EnumTrainStateType.State_Direction.id, (byte)0)));
			this.buttonList.add(new GuiButton(141, this.guiLeft + 118, this.guiTop + 28, 27, 20, this.getFormattedText(EnumTrainStateType.State_Direction.id, (byte)1)));
			this.buttonList.add(new GuiButton(142, this.guiLeft + 145, this.guiTop + 28, 27, 20, this.getFormattedText(EnumTrainStateType.State_Direction.id, (byte)2)));*/

			this.buttonChunkLoader = new GuiButton(127, this.guiLeft+28, this.guiTop+52, 120, 20, this.getFormattedText(7, this.train.getTrainStateData(7)));
			this.buttonList.add(this.buttonChunkLoader);
			this.buttonList.add(new GuiButton(110, this.guiLeft+4, this.guiTop+52, 20, 20, "<"));
			this.buttonList.add(new GuiButton(111, this.guiLeft+152, this.guiTop+52, 20, 20, ">"));

			if (((ModelSetVehicleBaseClient) this.modelset).rollsignTexture!=null) {
				this.buttonDestination = new GuiButton(128, this.guiLeft+28, this.guiTop+76, 120, 20, this.getFormattedText(8, this.train.getTrainStateData(8)));
				this.buttonList.add(this.buttonDestination);
				this.buttonList.add(new GuiButton(112, this.guiLeft+4, this.guiTop+76, 20, 20, "<"));
				this.buttonList.add(new GuiButton(113, this.guiLeft+152, this.guiTop+76, 20, 20, ">"));
			}

			this.buttonAnnouncement = new GuiButton(129, this.guiLeft+28, this.guiTop+100, 120, 20, this.getFormattedText(9, this.train.getTrainStateData(9)));
			this.buttonList.add(this.buttonAnnouncement);
			this.buttonList.add(new GuiButton(114, this.guiLeft+4, this.guiTop+100, 20, 20, "<"));
			this.buttonList.add(new GuiButton(115, this.guiLeft+152, this.guiTop+100, 20, 20, ">"));
		} else if (tab==TabTrainControlPanel.TAB_Formation) {
			containerTrain.inventorySlots = new ArrayList();
			for (int i = 0; i<9; ++i) {
				Slot slot = new Slot(this.player.inventory, i, 8+i*18, 142);
				slot.slotNumber = containerTrain.inventorySlots.size();
				containerTrain.inventorySlots.add(slot);
			}

			this.buttonList.clear();

			Formation formation = this.train.getFormation();
			if (formation!=null) {
				for (int i = 0; i<formation.size(); ++i) {
					FormationEntry entry = formation.get(i);
					if (entry==null) {
						continue;
					}
					int v = i==0 ? 0 : (i==formation.size()-1 ? 2 : 1);
					int x = this.guiLeft+8+(i%5)*32;
					int y = this.guiTop+25+(i/5)*32;
					this.buttonList.add(new GuiButtonFormation(200+i, entry, x, y, v));
				}
			}
			//Screen:162*128
		}

		this.buttonDoor[0] = new GuiButtonDoor(300, this.guiLeft+this.xSize+20, this.guiTop+20, 64, 80);
		this.buttonDoor[1] = new GuiButtonDoor(301, this.guiLeft-84, this.guiTop+20, 64, 80);
		int state = this.train.getTrainStateData(TrainStateType.State_Door.id);
		boolean r = (state&1)==1;
		boolean l = (state&2)==2;
		boolean dir = this.train.getTrainDirection()==0;
		this.buttonDoor[0].opened = dir ? l : r;
		this.buttonDoor[1].opened = dir ? r : l;
		this.buttonList.add(this.buttonDoor[0]);
		this.buttonList.add(this.buttonDoor[1]);

		this.currentScroll = 0.0F;
		this.sendTabPacket(this.selectedTabIndex);
	}

	private void sendTabPacket(int tabIndex) {
		String s = "setTrainTab,"+tabIndex;
		RTMCore.NETWORK_WRAPPER.sendToServer(new PacketNotice(PacketNotice.Side_SERVER, this.player.getEntityId(), 0, 0, s));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
		TabTrainControlPanel tab = TabTrainControlPanel.tabArray[this.selectedTabIndex];
		if (tab!=null) {
			GL11.glDisable(GL11.GL_BLEND);
			//インベントリ名表示
			//this.fontRendererObj.drawString(I18n.format(tab.getTranslatedTabLabel(), new Object[0]), 8, 6, 4210752);

			/*if(tab == TabTrainControlPanel.tabTrain)
			{
				this.fontRendererObj.drawString(this.getFormattedText(8, this.train.getTrainStateData(8)), 8, 18, 4210752);
				this.fontRendererObj.drawString(this.getFormattedText(9, this.train.getTrainStateData(9)), 8, 42, 4210752);
			}*/
		}
	}

	@Override
	protected void mouseClicked(int par1, int par2, int par3) {
		if (par3==0) {
			int l = par1-this.guiLeft;
			int i1 = par2-this.guiTop;
			TabTrainControlPanel[] tabs = TabTrainControlPanel.tabArray;
			for (int i = 0; i<tabs.length; ++i) {
				TabTrainControlPanel tab = tabs[i];
				if (tab!=null&&this.func_147049_a(tab, l, i1)) {
					return;
				}
			}
		}

		super.mouseClicked(par1, par2, par3);
	}

	@Override
	protected void mouseMovedOrUp(int par1, int par2, int par3) {
		if (par3==0) {
			int l = par1-this.guiLeft;
			int i1 = par2-this.guiTop;
			TabTrainControlPanel[] tabs = TabTrainControlPanel.tabArray;
			for (int i = 0; i<tabs.length; ++i) {
				TabTrainControlPanel tab = tabs[i];
				if (tab!=null&&this.func_147049_a(tab, l, i1)) {
					this.setCurrentTab(tab);
					return;
				}
			}
		}

		super.mouseMovedOrUp(par1, par2, par3);
	}

	private boolean needsScrollBars() {
		if (TabTrainControlPanel.tabArray[this.selectedTabIndex]==null)
			return false;
		//return selectedTabIndex != TabTrainControlPanel.tabInventory.getTabIndex() && TabTrainControlPanel.tabArray[selectedTabIndex].shouldHidePlayerInventory() && ((GuiTrainControlPanel.ContainerCreative)this.inventorySlots).func_148328_e();
		return false;
	}

	@Override
	public void handleMouseInput() {
		super.handleMouseInput();
		int i = Mouse.getEventDWheel();

		if (i!=0&&this.needsScrollBars()) {
			int j = 0;//((ContainerTrainControlPanel)this.inventorySlots).itemList.size() / 9 - 5 + 1;

			if (i>0) {
				i = 1;
			} else if (i<0) {
				i = -1;
			}

			this.currentScroll = (float) ((double) this.currentScroll-(double) i/(double) j);

			if (this.currentScroll<0.0F) {
				this.currentScroll = 0.0F;
			} else if (this.currentScroll>1.0F) {
				this.currentScroll = 1.0F;
			}
			//((ContainerTrainControlPanel)this.inventorySlots).scrollTo(this.currentScroll);
		}
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		boolean flag = Mouse.isButtonDown(0);
		int i1 = this.guiLeft+175;
		int j1 = this.guiTop+18;
		int k1 = i1+14;
		int l1 = j1+112;

		if (!this.wasClicking&&flag&&par1>=i1&&par2>=j1&&par1<k1&&par2<l1) {
			this.isScrolling = this.needsScrollBars();
		}

		if (!flag) {
			this.isScrolling = false;
		}

		this.wasClicking = flag;

		if (this.isScrolling) {
			this.currentScroll = ((float) (par2-j1)-7.5F)/((float) (l1-j1)-15.0F);

			if (this.currentScroll<0.0F) {
				this.currentScroll = 0.0F;
			} else if (this.currentScroll>1.0F) {
				this.currentScroll = 1.0F;
			}

			//((ContainerTrainControlPanel)this.inventorySlots).scrollTo(this.currentScroll);
		}

		super.drawScreen(par1, par2, par3);

		TabTrainControlPanel[] tabs = TabTrainControlPanel.tabArray;
		int start = tabPage*10;
		int i2 = Math.min(tabs.length, ((tabPage+1)*10)+2);
		if (tabPage!=0) {
			start += 2;
		}
		boolean rendered = false;

		for (int j2 = start; j2<i2; ++j2) {
			TabTrainControlPanel TabTrainControlPanel = tabs[j2];

			if (TabTrainControlPanel==null) {
				continue;
			}

			if (this.renderCreativeInventoryHoveringText(TabTrainControlPanel, par1, par2)) {
				rendered = true;
				break;
			}
		}

		if (!rendered)// && renderCreativeInventoryHoveringText(TabTrainControlPanel.tabAllSearch, par1, par2))
		{
			this.renderCreativeInventoryHoveringText(TabTrainControlPanel.TAB_Inventory, par1, par2);
		}

		if (this.slot!=null&&this.selectedTabIndex==TabTrainControlPanel.TAB_Inventory.getTabIndex()&&this.func_146978_c(this.slot.xDisplayPosition, this.slot.yDisplayPosition, 16, 16, par1, par2)) {
			this.drawCreativeTabHoveringText(I18n.format("inventory.binSlot", new Object[0]), par1, par2);
		}

		if (this.maxPages!=0) {
			String page = String.format("%d / %d", tabPage+1, maxPages+1);
			int width = fontRendererObj.getStringWidth(page);
			GL11.glDisable(GL11.GL_LIGHTING);
			this.zLevel = 300.0F;
			itemRender.zLevel = 300.0F;
			fontRendererObj.drawString(page, guiLeft+(xSize/2)-(width/2), guiTop-44, -1);
			this.zLevel = 0.0F;
			itemRender.zLevel = 0.0F;
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_LIGHTING);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderHelper.enableGUIStandardItemLighting();
		TabTrainControlPanel tab = TabTrainControlPanel.tabArray[this.selectedTabIndex];
		TabTrainControlPanel[] tabs = TabTrainControlPanel.tabArray;

		int start = tabPage*10;
		int k = Math.min(tabs.length, ((tabPage+1)*10+2));
		if (tabPage!=0)
			start += 2;

		for (int l = start; l<k; ++l) {
			TabTrainControlPanel tab1 = tabs[l];
			this.mc.getTextureManager().bindTexture(tabTexture);

			if (tab1==null) {
				continue;
			}

			if (tab1.getTabIndex()!=this.selectedTabIndex) {
				this.renderTabItem(tab1);
			}
		}

		if (tabPage!=0&&tab!=TabTrainControlPanel.TAB_Inventory) {
			this.mc.getTextureManager().bindTexture(tabTexture);
			this.renderTabItem(TabTrainControlPanel.TAB_Inventory);
		}

		this.mc.getTextureManager().bindTexture(tab.getTexture());

		this.drawTexturedModalRect(this.guiLeft-1, this.guiTop-1, 0, 0, this.xSize, this.ySize);//ズレ修正x-1,y-1
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		//int i1 = this.guiLeft + 175;
		//k = this.guiTop + 18;
		//int l = k + 112;
		this.mc.getTextureManager().bindTexture(tabTexture);

		/*if(TabTrainControlPanel.shouldHidePlayerInventory())
		{
		    this.drawTexturedModalRect(i1, k + (int)((float)(l - k - 17) * this.currentScroll), 232 + (this.needsScrollBars() ? 0 : 12), 0, 12, 15);
		}*/

		if (tab==null||tab.getTabPage()!=tabPage) {
			if (tab!=TabTrainControlPanel.TAB_Inventory) {
				return;
			}
		}

		this.renderTabItem(tab);

		if (tab==TabTrainControlPanel.TAB_Inventory) {
			GuiInventory.func_147046_a(this.guiLeft+51, this.guiTop+75, 30, (float) (this.guiLeft+51-par2), (float) (this.guiTop+75-50-par3), this.mc.thePlayer);
		}
	}

	protected boolean func_147049_a(TabTrainControlPanel tab, int x, int y) {
		if (tab.getTabPage()!=tabPage) {
			if (tab!=TabTrainControlPanel.TAB_Inventory) {
				return false;
			}
		}

		int k = tab.getTabColumn();
		int l = 28*k;

		if (k==5) {
			l = this.xSize-28+2;
		} else if (k>0) {
			l += k;
		}

		int i1 = tab.isTabInFirstRow() ? -32 : this.ySize;

		return x>=l&&x<=l+28&&y>=i1&&y<=i1+32;
	}

	/**
	 * Renders the creative inventory hovering text if mouse is over it. Returns true if did render or false otherwise.
	 * Params: current creative tab to be checked, current mouse x position, current mouse y position.
	 */
	protected boolean renderCreativeInventoryHoveringText(TabTrainControlPanel tab, int par2, int par3) {
		int k = tab.getTabColumn();
		int l = 28*k;

		if (k==5) {
			l = this.xSize-28+2;
		} else if (k>0) {
			l += k;
		}

		int i1 = tab.isTabInFirstRow() ? -32 : this.ySize;

		if (this.func_146978_c(l+3, i1+3, 23, 27, par2, par3)) {
			this.drawCreativeTabHoveringText(I18n.format(tab.getTranslatedTabLabel(), new Object[0]), par2, par3);
			return true;
		} else {
			return false;
		}
	}

	protected void renderTabItem(TabTrainControlPanel tab) {
		boolean flag = tab.getTabIndex()==this.selectedTabIndex;
		boolean flag1 = tab.isTabInFirstRow();
		int i = tab.getTabColumn();
		int j = i*28;
		int k = 0;
		int l = this.guiLeft+28*i;
		int i1 = this.guiTop;
		byte b0 = 32;

		if (flag) {
			k += 32;
		}

		if (i==5) {
			l = this.guiLeft+this.xSize-28;
		} else if (i>0) {
			l += i;
		}

		if (flag1) {
			i1 -= 28;
		} else {
			k += 64;
			i1 += this.ySize-4;
		}

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glColor3f(1F, 1F, 1F); //Forge: Reset color in case Items change it.
		GL11.glEnable(GL11.GL_BLEND); //Forge: Make sure blend is enabled else tabs show a white border.
		this.drawTexturedModalRect(l, i1, j, k, 28, b0);
		this.zLevel = 100.0F;
		itemRender.zLevel = 100.0F;
		l += 6;
		i1 += 8+(flag1 ? 1 : -1);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		ItemStack itemstack = tab.getIconItemStack();
		itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), itemstack, l, i1);
		itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), itemstack, l, i1);
		GL11.glDisable(GL11.GL_LIGHTING);
		itemRender.zLevel = 0.0F;
		this.zLevel = 0.0F;
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id==0) {
			this.mc.displayGuiScreen(new GuiAchievements(this, this.mc.thePlayer.getStatFileWriter()));
		}

		if (button.id==1) {
			this.mc.displayGuiScreen(new GuiStats(this, this.mc.thePlayer.getStatFileWriter()));
		}

		if (button.id==101) {
			tabPage = Math.max(tabPage-1, 0);
		}

		if (button.id==102) {
			tabPage = Math.min(tabPage+1, maxPages);
		}

		if ((button.id>=110&&button.id<=115)||(button.id>=124&&button.id<=129)||(button.id>=140&&button.id<=142)) {
			int i0 = 0;
			int i1 = 0;

			if (button.id==110)//ChunkLoader
			{
				i0 = 7;
				i1 = this.train.getTrainStateData(i0)-1;
			} else if (button.id==111)//ChunkLoader
			{
				i0 = 7;
				i1 = this.train.getTrainStateData(i0)+1;
			} else if (button.id==112)//行先
			{
				i0 = 8;
				i1 = this.train.getTrainStateData(i0)-1;
				if (i1<0) {
					i1 = this.modelset.getConfig().rollsignNames.length-1;
				}
			} else if (button.id==113)//行先
			{
				i0 = 8;
				i1 = this.train.getTrainStateData(i0)+1;
				if (i1>=this.modelset.getConfig().rollsignNames.length) {
					i1 = 0;
				}
			} else if (button.id==114)//車内放送
			{
				i0 = 9;
				i1 = this.train.getTrainStateData(i0)-1;
			} else if (button.id==115)//車内放送
			{
				i0 = 9;
				i1 = this.train.getTrainStateData(i0)+1;
			} else if (button.id==128) {
				return;
			} else if (button.id==129) {
				int index = this.train.getTrainStateData(9);
				String[][] sa0 = this.modelset.getConfig().sound_Announcement;
				if (sa0!=null&&index<sa0.length) {
					String[] sa1 = sa0[index][1].split(":");
					RTMCore.proxy.playSound(this.train, new ResourceLocation(sa1[0], sa1[1]), 1.0F, 1.0F);
				}
				return;
			} else if (button.id>=124&&button.id<=129) {
				i0 = button.id-120;
				if (button.id==124) {
					i0 = TrainStateType.State_InteriorLight.id;
				}
				i1 = this.train.getTrainStateData(i0)+1;
			} else if (button.id>=140&&button.id<=142) {
				i0 = TrainStateType.State_Direction.id;
				i1 = button.id-140;
				for (int i = 0; i<3; ++i) {
					if (i==i1) {
						this.buttonDirection[i].enabled = false;
					} else {
						this.buttonDirection[i].enabled = true;
					}
				}
			}

			TrainStateType stateType = TrainState.getStateType(i0);
			int i2 = i1<stateType.min ? stateType.max : (i1>stateType.max ? stateType.min : i1);
			this.sendTrainState(i0, (byte) i2);

			if (button.id==110||button.id==111) {
				this.buttonChunkLoader.displayString = this.getFormattedText(i0, (byte) i2);
			} else if (button.id==112||button.id==113) {
				this.buttonDestination.displayString = this.getFormattedText(i0, (byte) i2);
			} else if (button.id==114||button.id==115) {
				this.buttonAnnouncement.displayString = this.getFormattedText(i0, (byte) i2);
			} else {
				button.displayString = this.getFormattedText(i0, (byte) i2);
			}
		}

		if (button.id==300||button.id==301) {
			((GuiButtonDoor) button).opened ^= true;
			int r = (this.buttonDoor[0].opened ? 1 : 0);
			int l = (this.buttonDoor[1].opened ? 1 : 0);
			//boolean dir = this.train.getTrainDirection() == 0;
			int state = (r<<1|l);
			this.train.setTrainStateData(TrainStateType.State_Door.id, (byte) state);
			this.sendTrainState(TrainStateType.State_Door.id, (byte) state);
			TrainState type = TrainState.Door_Close;
			switch (state) {
				case 0:
					type = TrainState.Door_Close;
					break;
				case 1:
					type = TrainState.Door_OpenRight;
					break;
				case 2:
					type = TrainState.Door_OpenLeft;
					break;
				case 3:
					type = TrainState.Door_OpenAll;
					break;
			}
			MacroRecorder.INSTANCE.recDoor(this.train.worldObj, type);
		}
	}

	private void sendTrainState(int p1, byte p2) {
		RTMCore.NETWORK_WRAPPER.sendToServer(new PacketSetTrainState(this.train, p1, p2));
	}

	protected String getFormattedText(int par1, byte par2) {
		TrainStateType stateType = TrainState.getStateType(par1);
		if (stateType==TrainStateType.State_ChunkLoader) {
			String s = "state."+stateType.stateName;
			return I18n.format(s, new Object[0])+par2;
		} else if (stateType==TrainStateType.State_Destination) {
			if (par2>=this.modelset.getConfig().rollsignNames.length) {
				par2 = (byte) (this.modelset.getConfig().rollsignNames.length-1);
			}
			String s = "state."+stateType.stateName;
			return I18n.format(s, new Object[0])+" "+this.modelset.getConfig().rollsignNames[par2];
		} else if (stateType==TrainStateType.State_Announcement) {
			String s = "state."+stateType.stateName;
			String[][] sa = this.modelset.getConfig().sound_Announcement;
			if (sa!=null&&par2<sa.length) {
				return I18n.format(s, new Object[0])+" "+sa[par2][0];
			}
			return I18n.format(s, new Object[0])+" null";
		} else {
			String s = "state."+stateType.stateName+"."+TrainState.getState(par1, par2).stateName;
			return I18n.format(s, new Object[0]);
		}
	}

	private class GuiButtonFormation extends GuiButton {
		private FormationEntry car;
		private int v;

		public GuiButtonFormation(int id, FormationEntry entry, int posX, int posY, int posV) {
			super(id, posX, posY, 32, 16, String.valueOf(entry.entryId+1));
			this.car = entry;
			this.v = posV;
		}

		@Override
		public void drawButton(Minecraft mc, int x, int y) {
			if (!this.visible) {
				return;
			}

			mc.getTextureManager().bindTexture(TabTrainControlPanel.TAB_Formation.getTexture());
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			int u = this.car.train.isControlCar() ? 1 : 0;
			this.field_146123_n = x>=this.xPosition&&y>=this.yPosition&&x<this.xPosition+this.width&&y<this.yPosition+this.height;
			this.drawTexturedModalRect(this.xPosition, this.yPosition, 192+u*32, this.v*16, this.width, this.height);
			this.mouseDragged(mc, x, y);

			//プレーヤー位置の矢印
			if (this.car.train.riddenByEntity==mc.thePlayer) {
				this.drawTexturedModalRect(this.xPosition+12, this.yPosition-16, 180, 0, 10, 16);
			}

			this.drawCenteredString(mc.fontRenderer, this.displayString, this.xPosition+this.width/2, this.yPosition+2, 0x000000);
		}

		@Override
		public boolean mousePressed(Minecraft mx, int x, int y) {
			if (super.mousePressed(mc, x, y)) {
				if (y-this.yPosition<12) {
					;
				} else {
					if (x-this.xPosition<12) {
						;//台車クリックで連結解除
					} else {
						;
					}
				}
				return true;
			}
			return false;
		}
	}
}