package net.teamfruit.projectrtm.rtm.gui;

import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.teamfruit.projectrtm.ngtlib.gui.GuiScreenCustom;
import net.teamfruit.projectrtm.rtm.RTMCore;
import net.teamfruit.projectrtm.rtm.modelpack.texture.ITextureHolder;
import net.teamfruit.projectrtm.rtm.modelpack.texture.TextureManager;
import net.teamfruit.projectrtm.rtm.modelpack.texture.TextureProperty;
import net.teamfruit.projectrtm.rtm.network.PacketTextureHolder;

import org.lwjgl.input.Mouse;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiSelectTexture extends GuiScreenCustom {
	public final ITextureHolder holder;
	private List<TextureProperty> properties;
	private int currentScroll;
	private int prevScroll;
	private int uCount, vCount;

	public GuiSelectTexture(ITextureHolder par1) {
		this.holder = par1;
		this.properties = TextureManager.INSTANCE.getTextureList(par1.getType());
	}

	@Override
	public void initGui() {
		this.uCount = !this.properties.isEmpty() ? this.properties.get(0).getUCountInGui() : 1;
		this.vCount = !this.properties.isEmpty() ? this.properties.get(0).getVCountInGui() : 1;
		int x = this.width/this.uCount;
		int y = this.height/vCount;
		this.buttonList.clear();

		int yCount = (this.properties.size()/this.uCount)+1;

		for (int v = 0; v<yCount; ++v) {
			for (int u = 0; u<this.uCount; ++u) {
				int index = v*this.uCount+u;
				if (index>=this.properties.size()) {
					break;
				}
				TextureProperty prop = this.properties.get(index);
				float f0 = 1.0F;
				if (prop.width>prop.height) {
					f0 = (float) x/prop.width;
				} else {
					f0 = (float) y/prop.height;
				}

				int w = (int) (prop.width*f0);
				int h = (int) (prop.height*f0);
				int xPos = x*u+((x-w)/2);
				int yPos = y*v+((y-h)/2);
				this.buttonList.add(new GuiButtonSelectTexture(index, xPos, yPos, w, h, prop));
			}
		}
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		this.drawDefaultBackground();
		super.drawScreen(par1, par2, par3);
	}

	@Override
	protected void actionPerformed(GuiButton guibutton) {
		if (guibutton.id==256) {
			this.mc.displayGuiScreen(null);
		}

		if (guibutton.id<this.properties.size()) {
			String name = ((GuiButtonSelectTexture) guibutton).property.texture;
			RTMCore.NETWORK_WRAPPER.sendToServer(new PacketTextureHolder(name, this.holder));
			this.mc.displayGuiScreen(null);//close
		}
	}

	@Override
	public void handleMouseInput() {
		super.handleMouseInput();
		int scroll = Mouse.getEventDWheel();

		if (scroll!=0) {
			this.prevScroll = this.currentScroll;
			scroll = scroll>0 ? 1 : (scroll<0 ? -1 : 0);
			this.currentScroll -= scroll;

			if (this.currentScroll<0) {
				this.currentScroll = 0;
			}

			int size2 = this.properties.size()/this.uCount;

			if (this.currentScroll>=size2) {
				this.currentScroll = size2-1;
			}

			this.renewButton(this.currentScroll);
		}
	}

	protected void renewButton(int scroll) {
		if (this.currentScroll!=this.prevScroll) {
			int y = this.height/this.vCount;
			if (this.prevScroll>this.currentScroll) {
				y = -y;
			}

			for (int i = 0; i<this.buttonList.size(); ++i) {
				((GuiButtonSelectTexture) this.buttonList.get(i)).moveButton(y);
			}
		}
	}
}