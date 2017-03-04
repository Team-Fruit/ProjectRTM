package net.teamfruit.projectrtm.ngtlib.renderer.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelFormatException;

@SideOnly(Side.CLIENT)
public class MtlParser {
	private Map<String, Material> materials = new HashMap<String, Material>();
	private Material currentMaterial;

	public MtlParser(final ResourceLocation resource) {
		try {
			final IResource res = Minecraft.getMinecraft().getResourceManager().getResource(resource);
			loadMaterial(res.getInputStream());
		} catch (final IOException e) {
			//throw new ModelFormatException("IO Exception reading .mtl file", e);
		}
	}

	private void loadMaterial(final InputStream inputStream) throws ModelFormatException {
		BufferedReader reader = null;
		String currentLine = null;
		int lineCount = 0;
		this.materials.clear();

		try {
			reader = new BufferedReader(new InputStreamReader(inputStream));

			while ((currentLine = reader.readLine())!=null) {
				lineCount++;
				currentLine = currentLine.replaceAll("\\s+", " ").trim();

				if (currentLine.length()==0||currentLine.startsWith("#"))
					continue;
				else if (currentLine.startsWith("newmtl ")) {
					final String[] sa = currentLine.split(" ");
					this.currentMaterial = new Material((byte) this.materials.size(), null);
					this.materials.put(sa[1], this.currentMaterial);
				}

				//Tr:透過
			}
		} catch (final IOException e) {
			throw new ModelFormatException("IO Exception reading model format", e);
		} finally {
			IOUtils.closeQuietly(reader);
			IOUtils.closeQuietly(inputStream);
		}
	}

	public Map<String, Material> getMaterials() {
		return this.materials;
	}
}