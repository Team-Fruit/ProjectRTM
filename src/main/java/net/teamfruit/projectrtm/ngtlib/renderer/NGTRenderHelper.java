package net.teamfruit.projectrtm.ngtlib.renderer;

import java.nio.FloatBuffer;
import java.util.List;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.teamfruit.projectrtm.ngtlib.renderer.model.Face;
import net.teamfruit.projectrtm.ngtlib.renderer.model.GroupObject;
import net.teamfruit.projectrtm.ngtlib.renderer.model.IModelNGT;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class NGTRenderHelper {
	/**
	 * 移動
	 * @param buffer : 元の変換行列
	 */
	public static FloatBuffer translate(FloatBuffer buffer, float moveX, float moveY, float moveZ) {
		float[][] fa = { { 1.0F, 0.0F, 0.0F, 0.0F }, { 0.0F, 1.0F, 0.0F, 0.0F }, { 0.0F, 0.0F, 1.0F, 0.0F }, { moveX, moveY, moveZ, 1.0F } };
		return multiplｙMatrix(buffer, fa);
	}

	/**
	 * 回転
	 * @param buffer : 元の変換行列
	 * @param angle : ラジアン
	 * @param coordinate : 座標軸（'X', 'Y', 'Z'）
	 */
	public static FloatBuffer rotate(FloatBuffer buffer, float angle, char coordinate) {
		switch (coordinate) {
			case 'X':
				float[][] fa0 = { { 1.0F, 0.0F, 0.0F, 0.0F }, { 0.0F, MathHelper.cos(angle), MathHelper.sin(angle), 0.0F }, { 0.0F, -MathHelper.sin(angle), MathHelper.cos(angle), 0.0F }, { 0.0F, 0.0F, 0.0F, 1.0F } };
				return multiplｙMatrix(buffer, fa0);
			case 'Y':
				float[][] fa1 = { { MathHelper.cos(angle), 0.0F, -MathHelper.sin(angle), 0.0F }, { 0.0F, 1.0F, 0.0F, 0.0F }, { MathHelper.sin(angle), 0.0F, MathHelper.cos(angle), 0.0F }, { 0.0F, 0.0F, 0.0F, 1.0F } };
				return multiplｙMatrix(buffer, fa1);
			case 'Z':
				float[][] fa2 = { { MathHelper.cos(angle), MathHelper.sin(angle), 0.0F, 0.0F }, { -MathHelper.sin(angle), MathHelper.cos(angle), 0.0F, 0.0F }, { 0.0F, 0.0F, 1.0F, 0.0F }, { 0.0F, 0.0F, 0.0F, 1.0F } };
				return multiplｙMatrix(buffer, fa2);
		}
		return buffer;
	}

	private static FloatBuffer multiplｙMatrix(FloatBuffer fb, float[][] fa) {
		FloatBuffer buffer = FloatBuffer.allocate(16);
		for (int i = 0; i<4; ++i) {
			for (int j = 0; j<4; ++j) {
				float f = fb.get(j)*fa[i][0]+fb.get(4+j)*fa[i][1]+fb.get(8+j)*fa[i][2]+fb.get(12+j)*fa[i][3];
				buffer.put(i*4+j, f);
			}
		}
		return buffer;
	}

	/**NGTTessellatorを使用*/
	public static void renderCustomModelAll(IModelNGT model, byte matId, boolean smoothing) {
		renderCustomModelEveryParts(model, matId, false, smoothing);
	}

	public static void renderCustomModel(IModelNGT model, byte matId, boolean smoothing, String... parts) {
		renderCustomModelEveryParts(model, matId, false, smoothing, parts);
	}

	public static void renderCustomModelExcept(IModelNGT model, byte matId, boolean smoothing, String... parts) {
		renderCustomModelEveryParts(model, matId, true, smoothing, parts);
	}

	public static void renderCustomModelEveryParts(IModelNGT model, byte matId, boolean except, boolean smoothing, String... parts) {
		renderCustomModelEveryParts(model, matId, except, smoothing, GL11.GL_TRIANGLES, parts);
	}

	public static void renderCustomModelEveryParts(IModelNGT model, byte matId, boolean except, boolean smoothing, int mode, String... parts) {
		IRenderer tessellator = PolygonRenderer.INSTANCE;
		tessellator.startDrawing(mode);
		List<GroupObject> list = model.getGroupObjects();
		for (int j = 0; j<list.size(); ++j) {
			GroupObject group = list.get(j);
			boolean b = false;
			if (parts==null||parts.length==0) {
				b = true;
			} else {
				b = except;
				for (int k = 0; k<parts.length; ++k) {
					if (group.name.equals(parts[k])) {
						b = !except;
						break;
					}
				}
			}

			if (b) {
				for (int k = 0; k<group.faces.size(); ++k) {
					Face face = group.faces.get(k);
					if (face.materialId==matId) {
						addFace(face, tessellator, smoothing);
					}
				}
			}
		}
		tessellator.draw();
	}

	public static void addFace(Face face, Tessellator tessellator, boolean smoothing) {
		if (!smoothing) {
			tessellator.setNormal(face.faceNormal.getX(), face.faceNormal.getY(), face.faceNormal.getZ());
		}

		for (int i = 0; i<face.vertices.length; ++i) {
			if (smoothing) {
				tessellator.setNormal(face.vertexNormals[i].getX(), face.vertexNormals[i].getY(), face.vertexNormals[i].getZ());
			}

			if ((face.textureCoordinates!=null)&&(face.textureCoordinates.length>0)) {
				tessellator.addVertexWithUV(face.vertices[i].getX(), face.vertices[i].getY(), face.vertices[i].getZ(), face.textureCoordinates[i].getU(), face.textureCoordinates[i].getV());
			} else {
				tessellator.addVertexWithUV(face.vertices[i].getX(), face.vertices[i].getY(), face.vertices[i].getZ(), 0.0F, 0.0F);
			}
		}
	}

	/**NGTTessellatorを使用*/
	public static void addFace(Face face, IRenderer tessellator, boolean smoothing) {
		addFaceWithMatrix(face, tessellator, null, -1, smoothing);
	}

	/**NGTTessellatorを使用*/
	public static void addFaceWithMatrix(Face face, IRenderer tessellator, FloatBuffer matrix, int index, boolean smoothing) {
		if (!smoothing) {
			tessellator.setNormal(face.faceNormal.getX(), face.faceNormal.getY(), face.faceNormal.getZ());
		}

		for (int i = 0; i<face.vertices.length; ++i) {
			if (smoothing) {
				tessellator.setNormal(face.vertexNormals[i].getX(), face.vertexNormals[i].getY(), face.vertexNormals[i].getZ());
			}

			if ((face.textureCoordinates!=null)&&(face.textureCoordinates.length>0)) {
				if (matrix==null) {
					tessellator.addVertexWithUV(face.vertices[i].getX(), face.vertices[i].getY(), face.vertices[i].getZ(), face.textureCoordinates[i].getU(), face.textureCoordinates[i].getV());
				} else {
					addVertexWithMatrix(face.vertices[i].getX(), face.vertices[i].getY(), face.vertices[i].getZ(), face.textureCoordinates[i].getU(), face.textureCoordinates[i].getV(), tessellator, matrix, index);
				}
			} else {
				if (matrix==null) {
					tessellator.addVertexWithUV(face.vertices[i].getX(), face.vertices[i].getY(), face.vertices[i].getZ(), 0.0F, 0.0F);
				} else {
					addVertexWithMatrix(face.vertices[i].getX(), face.vertices[i].getY(), face.vertices[i].getZ(), 0.0F, 0.0F, tessellator, matrix, index);
				}
			}
		}
	}

	private static void addVertexWithMatrix(float x, float y, float z, float u, float v, IRenderer tessellator, FloatBuffer matrix, int index) {
		int i = index<<4;
		float x0 = x*matrix.get(i+0)+y*matrix.get(i+4)+z*matrix.get(i+8)+matrix.get(i+12);
		float y0 = x*matrix.get(i+1)+y*matrix.get(i+5)+z*matrix.get(i+9)+matrix.get(i+13);
		float z0 = x*matrix.get(i+2)+y*matrix.get(i+6)+z*matrix.get(i+10)+matrix.get(i+14);
		tessellator.addVertexWithUV(x0, y0, z0, u, v);
	}
}