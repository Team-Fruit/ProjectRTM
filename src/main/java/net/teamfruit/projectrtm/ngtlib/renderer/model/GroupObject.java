package net.teamfruit.projectrtm.ngtlib.renderer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.teamfruit.projectrtm.ngtlib.math.NGTMath;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class GroupObject {
	public String name;
	public byte drawMode;
	public float smoothingAngle;
	public ArrayList<Face> faces = new ArrayList<Face>();

	public GroupObject(int par1) {
		this("", par1);
	}

	public GroupObject(String par1, int par2) {
		this.name = par1;
		this.drawMode = (byte) par2;
	}

	protected void calcVertexNormals(VecAccuracy accuracy) {
		//その頂点を共有している面のリストを格納
		int size = this.faces.size()*2;
		Map<Vertex, List<FaceSet>> faceMap = new HashMap<Vertex, List<FaceSet>>(size);

		for (Face face : this.faces) {
			for (int i = 0; i<face.vertices.length; ++i) {
				Vertex vertex = face.vertices[i];
				List<FaceSet> list = faceMap.get(vertex);
				if (list==null) {
					list = new ArrayList<FaceSet>();
					faceMap.put(vertex, list);
				}
				list.add(new FaceSet(face, i));
			}
		}

		float angleCos = MathHelper.cos(NGTMath.toRadians(this.smoothingAngle));
		for (Entry<Vertex, List<FaceSet>> set : faceMap.entrySet()) {
			for (FaceSet faceSet : set.getValue()) {
				faceSet.face.calcVertexNormals(set.getValue(), faceSet.index, angleCos, accuracy);
			}
		}
	}

	public void render(boolean smoothing) {
		if (this.faces.size()>0) {
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawing(this.drawMode);
			this.render(tessellator, smoothing);
			tessellator.draw();
		}
	}

	public void render(Tessellator tessellator, boolean smoothing) {
		if (this.faces.size()>0) {
			for (Face face : faces) {
				face.addFaceForRender(tessellator, smoothing);
			}
		}
	}

	protected final class FaceSet {
		public final Face face;
		public final int index;

		public FaceSet(Face p1, int p2) {
			this.face = p1;
			this.index = p2;
		}
	}
}