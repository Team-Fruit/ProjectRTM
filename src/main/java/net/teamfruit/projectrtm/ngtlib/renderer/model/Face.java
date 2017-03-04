package net.teamfruit.projectrtm.ngtlib.renderer.model;

import java.util.List;

import net.minecraft.client.renderer.Tessellator;
import net.teamfruit.projectrtm.ngtlib.math.NGTVec;
import net.teamfruit.projectrtm.ngtlib.renderer.NGTRenderHelper;
import net.teamfruit.projectrtm.ngtlib.renderer.model.GroupObject.FaceSet;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**ポリゴンモデルの面*/
@SideOnly(Side.CLIENT)
public final class Face {
	private static float[][] MIRROR_PATTERN = {
			{ -1.0F, 1.0F, 1.0F },
			{ 1.0F, -1.0F, 1.0F },
			{ 1.0F, 1.0F, -1.0F } };

	public static final float SMOOTHING = 60.0F;

	private static final NGTVec TEMP_VEC0 = new NGTVec(0.0D, 0.0D, 0.0D);
	private static final NGTVec TEMP_VEC1 = new NGTVec(0.0D, 0.0D, 0.0D);
	private static final NGTVec TEMP_VEC2 = new NGTVec(0.0D, 0.0D, 0.0D);

	/**頂点*/
	public Vertex[] vertices;
	/**頂点の法線ベクトル*/
	public Vertex[] vertexNormals;
	/**面の法線ベクトル*/
	public Vertex faceNormal;
	/**UV*/
	public TextureCoordinate[] textureCoordinates;
	/**マテリアルの番号(0~127)*/
	public final byte materialId;

	public Face(byte material) {
		this.materialId = material;
	}

	/**
	 * スムージング角より小さいか
	 * @param normal
	 * @param angleCos =cos(toRadian(angle))
	 */
	private boolean checkSmoothing(Vertex normal, float angleCos) {
		if (normal==this.faceNormal) {
			return true;
		}
		double dx = (double) this.faceNormal.getX();
		double dy = (double) this.faceNormal.getY();
		double dz = (double) this.faceNormal.getZ();
		double d0 = dx*normal.getX()+dy*normal.getY()+dz*normal.getZ();
		return d0>=angleCos;
	}

	public void calcVertexNormals(List<FaceSet> faceList, int index, float angleCos, VecAccuracy accuracy) {
		if (this.faceNormal==null) {
			this.calculateFaceNormal(accuracy);
		}

		if (this.vertexNormals==null) {
			this.vertexNormals = new Vertex[this.vertices.length];
		}

		if (this.vertexNormals[index]==null) {
			this.vertexNormals[index] = Vertex.create(0.0F, 0.0F, 0.0F, accuracy);
		}

		for (FaceSet target : faceList) {
			if (target.face.faceNormal==null) {
				target.face.calculateFaceNormal(accuracy);
			}

			//法線ベクトル同士の角がスムージング角より小さければ足す
			if (this.checkSmoothing(target.face.faceNormal, angleCos)) {
				this.vertexNormals[index].add(target.face.faceNormal);
			}
		}
		this.vertexNormals[index].normalize();
	}

	public void addFaceForRender(Tessellator tessellator, boolean smoothing) {
		NGTRenderHelper.addFace(this, tessellator, smoothing);
	}

	/*public void calculateFaceNormal()
	{
		if(this.vertices.length == 4)
		{
			Vec3 v1 = Vec3.createVectorHelper(this.vertices[1].getX() - this.vertices[0].getX(), this.vertices[1].getY() - this.vertices[0].getY(), this.vertices[1].getZ() - this.vertices[0].getZ());
	        Vec3 v2 = Vec3.createVectorHelper(this.vertices[2].getX() - this.vertices[0].getX(), this.vertices[2].getY() - this.vertices[0].getY(), this.vertices[2].getZ() - this.vertices[0].getZ());
	        Vec3 normalVec1 = v1.crossProduct(v2).normalize();
	        v1 = Vec3.createVectorHelper(this.vertices[3].getX() - this.vertices[2].getX(), this.vertices[3].getY() - this.vertices[2].getY(), this.vertices[3].getZ() - this.vertices[2].getZ());
	        v2 = Vec3.createVectorHelper(this.vertices[0].getX() - this.vertices[2].getX(), this.vertices[0].getY() - this.vertices[2].getY(), this.vertices[0].getZ() - this.vertices[2].getZ());
	        Vec3 normalVec2 = v1.crossProduct(v2).normalize();
	        Vec3 normalVec3 = normalVec1.addVector(normalVec2.xCoord, normalVec2.yCoord, normalVec2.zCoord).normalize();
	        this.faceNormal = Vertex.create((float)normalVec3.xCoord, (float)normalVec3.yCoord, (float)normalVec3.zCoord);
		}
		else if(this.vertices.length == 6)//四角を2分割
		{
			Vec3 v1 = Vec3.createVectorHelper(this.vertices[1].getX() - this.vertices[0].getX(), this.vertices[1].getY() - this.vertices[0].getY(), this.vertices[1].getZ() - this.vertices[0].getZ());
	        Vec3 v2 = Vec3.createVectorHelper(this.vertices[2].getX() - this.vertices[0].getX(), this.vertices[2].getY() - this.vertices[0].getY(), this.vertices[2].getZ() - this.vertices[0].getZ());
	        Vec3 normalVec1 = v1.crossProduct(v2).normalize();
	        v1 = Vec3.createVectorHelper(this.vertices[4].getX() - this.vertices[3].getX(), this.vertices[4].getY() - this.vertices[3].getY(), this.vertices[4].getZ() - this.vertices[3].getZ());
	        v2 = Vec3.createVectorHelper(this.vertices[5].getX() - this.vertices[3].getX(), this.vertices[5].getY() - this.vertices[3].getY(), this.vertices[5].getZ() - this.vertices[3].getZ());
	        Vec3 normalVec2 = v1.crossProduct(v2).normalize();
	        Vec3 normalVec3 = normalVec1.addVector(normalVec2.xCoord, normalVec2.yCoord, normalVec2.zCoord).normalize();
	        this.faceNormal = Vertex.create((float)normalVec3.xCoord, (float)normalVec3.yCoord, (float)normalVec3.zCoord);
		}
		else
		{
			Vec3 v1 = Vec3.createVectorHelper(this.vertices[1].getX() - this.vertices[0].getX(), this.vertices[1].getY() - this.vertices[0].getY(), this.vertices[1].getZ() - this.vertices[0].getZ());
	        Vec3 v2 = Vec3.createVectorHelper(this.vertices[2].getX() - this.vertices[0].getX(), this.vertices[2].getY() - this.vertices[0].getY(), this.vertices[2].getZ() - this.vertices[0].getZ());
	        Vec3 normalVec = v1.crossProduct(v2).normalize();
	        this.faceNormal = Vertex.create((float)normalVec.xCoord, (float)normalVec.yCoord, (float)normalVec.zCoord);
		}
	}*/

	public void calculateFaceNormal(VecAccuracy accuracy) {
		if (this.vertices.length==4) {
			TEMP_VEC0.setValue(this.vertices[1].getX()-this.vertices[0].getX(), this.vertices[1].getY()-this.vertices[0].getY(), this.vertices[1].getZ()-this.vertices[0].getZ());
			TEMP_VEC1.setValue(this.vertices[2].getX()-this.vertices[0].getX(), this.vertices[2].getY()-this.vertices[0].getY(), this.vertices[2].getZ()-this.vertices[0].getZ());
			TEMP_VEC2.setValue(TEMP_VEC0.crossProduct(TEMP_VEC1).normalize());
			TEMP_VEC0.setValue(this.vertices[3].getX()-this.vertices[2].getX(), this.vertices[3].getY()-this.vertices[2].getY(), this.vertices[3].getZ()-this.vertices[2].getZ());
			TEMP_VEC1.setValue(this.vertices[0].getX()-this.vertices[2].getX(), this.vertices[0].getY()-this.vertices[2].getY(), this.vertices[0].getZ()-this.vertices[2].getZ());
			TEMP_VEC0.crossProduct(TEMP_VEC1).normalize();
			TEMP_VEC2.addVector(TEMP_VEC0).normalize();
			this.faceNormal = Vertex.create((float) TEMP_VEC2.xCoord, (float) TEMP_VEC2.yCoord, (float) TEMP_VEC2.zCoord, accuracy);
		} else if (this.vertices.length==6)//四角を2分割
		{
			TEMP_VEC0.setValue(this.vertices[1].getX()-this.vertices[0].getX(), this.vertices[1].getY()-this.vertices[0].getY(), this.vertices[1].getZ()-this.vertices[0].getZ());
			TEMP_VEC1.setValue(this.vertices[2].getX()-this.vertices[0].getX(), this.vertices[2].getY()-this.vertices[0].getY(), this.vertices[2].getZ()-this.vertices[0].getZ());
			TEMP_VEC2.setValue(TEMP_VEC0.crossProduct(TEMP_VEC1).normalize());
			TEMP_VEC0.setValue(this.vertices[4].getX()-this.vertices[3].getX(), this.vertices[4].getY()-this.vertices[3].getY(), this.vertices[4].getZ()-this.vertices[3].getZ());
			TEMP_VEC1.setValue(this.vertices[5].getX()-this.vertices[3].getX(), this.vertices[5].getY()-this.vertices[3].getY(), this.vertices[5].getZ()-this.vertices[3].getZ());
			TEMP_VEC0.crossProduct(TEMP_VEC1).normalize();
			TEMP_VEC2.addVector(TEMP_VEC0).normalize();
			this.faceNormal = Vertex.create((float) TEMP_VEC2.xCoord, (float) TEMP_VEC2.yCoord, (float) TEMP_VEC2.zCoord, accuracy);
		} else {
			TEMP_VEC0.setValue(this.vertices[1].getX()-this.vertices[0].getX(), this.vertices[1].getY()-this.vertices[0].getY(), this.vertices[1].getZ()-this.vertices[0].getZ());
			TEMP_VEC1.setValue(this.vertices[2].getX()-this.vertices[0].getX(), this.vertices[2].getY()-this.vertices[0].getY(), this.vertices[2].getZ()-this.vertices[0].getZ());
			TEMP_VEC0.crossProduct(TEMP_VEC1).normalize();
			this.faceNormal = Vertex.create((float) TEMP_VEC0.xCoord, (float) TEMP_VEC0.yCoord, (float) TEMP_VEC0.zCoord, accuracy);
		}
	}

	public Face getMirror(int type, List<Vertex> vertices, VecAccuracy accuracy) {
		Vertex[] va = new Vertex[this.vertices.length];
		for (int i = 0; i<va.length; ++i) {
			float x = this.vertices[i].getX()*MIRROR_PATTERN[type][0];
			float y = this.vertices[i].getY()*MIRROR_PATTERN[type][1];
			float z = this.vertices[i].getZ()*MIRROR_PATTERN[type][2];
			//精度
			/*float x = (float)((double)this.vertices[i].getX() * (double)MIRROR_PATTERN[type][0]);
			float y = (float)((double)this.vertices[i].getY() * (double)MIRROR_PATTERN[type][1]);
			float z = (float)((double)this.vertices[i].getZ() * (double)MIRROR_PATTERN[type][2]);*/
			int index = va.length-1-i;
			va[index] = Vertex.create(x, y, z, accuracy);

			//けっこう遅くなる
			/*int i2 = vertices.indexOf(va[index]);
			if(i2 >= 0)
			{
				va[index] = vertices.get(i2);
			}
			else
			{
				vertices.add(va[index]);
			}*/
		}

		Face face = new Face(this.materialId);
		face.vertices = va;
		face.textureCoordinates = new TextureCoordinate[this.textureCoordinates.length];
		for (int i = 0; i<this.textureCoordinates.length; ++i) {
			face.textureCoordinates[this.textureCoordinates.length-1-i] = this.textureCoordinates[i];
		}
		return face;
	}
}