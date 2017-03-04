package net.teamfruit.projectrtm.ngtlib.renderer.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelFormatException;

@SideOnly(Side.CLIENT)
public abstract class PolygonModel implements IModelNGT {
	protected final String fileName;
	protected final int drawMode;
	protected final VecAccuracy accuracy;

	/**全ての頂点*/
	public ArrayList<Vertex> vertices = new ArrayList<Vertex>();
	public ArrayList<GroupObject> groupObjects = new ArrayList<GroupObject>();
	protected GroupObject currentGroupObject;

	public PolygonModel(final ResourceLocation resource, final int mode, final VecAccuracy par3) throws ModelFormatException {
		this.fileName = resource.toString();
		this.drawMode = mode;
		this.accuracy = par3;
		init(resource);
		calcVertexNormals();
		this.vertices.clear();
	}

	protected void init(final ResourceLocation resource) throws ModelFormatException {
		try {
			final IResource res = Minecraft.getMinecraft().getResourceManager().getResource(resource);
			loadModel(res.getInputStream());
		} catch (final IOException e) {
			throw new ModelFormatException("IO Exception reading model", e);
		}
	}

	private void loadModel(final InputStream inputStream) throws ModelFormatException {
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new InputStreamReader(inputStream));
			String currentLine = null;
			int lineCount = 0;
			while ((currentLine = reader.readLine())!=null) {
				lineCount++;
				currentLine = currentLine.replaceAll("\\s+", " ").trim();//空白文字を置換
				parseLine(currentLine, lineCount);
			}
			postInit();
		} catch (final IOException e) {
			throw new ModelFormatException("IO Exception reading model", e);
		} finally {
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(inputStream);
		}
	}

	protected abstract void parseLine(String currentLine, int lineCount);

	/**全ての行を読み込んだ後に呼ばれる*/
	protected abstract void postInit();

	/**頂点法線ベクトルの設定*/
	private void calcVertexNormals() {
		for (final GroupObject obj : this.groupObjects)
			obj.calcVertexNormals(this.accuracy);
	}

	@Override
	public void renderAll(final boolean smoothing) {
		if (smoothing)
			GL11.glShadeModel(GL11.GL_SMOOTH);

		final Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawing(this.drawMode);
		tessellateAll(tessellator, smoothing);
		tessellator.draw();

		if (smoothing)
			GL11.glShadeModel(GL11.GL_FLAT);
	}

	public void tessellateAll(final Tessellator tessellator, final boolean smoothing) {
		for (final GroupObject groupObject : this.groupObjects)
			groupObject.render(tessellator, smoothing);
	}

	@Override
	public void renderOnly(final boolean smoothing, final String... groupNames) {
		if (smoothing)
			GL11.glShadeModel(GL11.GL_SMOOTH);

		for (final GroupObject groupObject : this.groupObjects)
			for (final String groupName : groupNames)
				if (groupName.equalsIgnoreCase(groupObject.name))
					groupObject.render(smoothing);

		if (smoothing)
			GL11.glShadeModel(GL11.GL_FLAT);
	}

	@Override
	public void renderPart(final boolean smoothing, final String partName) {
		if (smoothing)
			GL11.glShadeModel(GL11.GL_SMOOTH);

		for (final GroupObject groupObject : this.groupObjects)
			if (partName.equalsIgnoreCase(groupObject.name)) {
				groupObject.render(smoothing);
				return;
			}

		if (smoothing)
			GL11.glShadeModel(GL11.GL_FLAT);
	}

	@Override
	public int getDrawMode() {
		return this.drawMode;
	}

	@Override
	public ArrayList<GroupObject> getGroupObjects() {
		return this.groupObjects;
	}
}