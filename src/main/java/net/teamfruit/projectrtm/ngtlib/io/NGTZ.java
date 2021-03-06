package net.teamfruit.projectrtm.ngtlib.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelFormatException;
import net.teamfruit.projectrtm.ngtlib.block.NGTObject;

public class NGTZ {
	private Map<String, NGTObject> objects = new HashMap<String, NGTObject>();

	public NGTZ(ResourceLocation par1) {
		try {
			IResource res = Minecraft.getMinecraft().getResourceManager().getResource(par1);
			this.load(res.getInputStream());
		} catch (IOException e) {
			throw new ModelFormatException("IO Exception reading model", e);
		}
	}

	public void load(InputStream is) throws IOException {
		ZipInputStream zip = new ZipInputStream(is);
		ZipEntry ze;
		while ((ze = zip.getNextEntry())!=null) {
			if (!ze.isDirectory()) {
				String partsName = ze.getName().replace(".ngto", "");
				this.registerNGTO(partsName, zip);//getNextEntry()でZISをエントリのISとして扱える

			}
		}
		zip.close();
	}

	private void registerNGTO(String name, InputStream is) {
		NGTObject ngto = NGTObject.load(is);
		this.objects.put(name, ngto);
	}

	public Map<String, NGTObject> getObjects() {
		return this.objects;
	}
}