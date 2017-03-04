package net.teamfruit.projectrtm.ngtlib.renderer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelFormatException;
import net.teamfruit.projectrtm.ngtlib.io.FileType;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Metasequoiaのモデルデータ
 */
@SideOnly(Side.CLIENT)
public final class MqoModel extends PolygonModel {
	private static Pattern vertexPattern = Pattern.compile("((\\-){0,1}\\d+(\\.\\d+){0,1}(\\s){0,1}){2,3}");
	private static Pattern groupObjectPattern = Pattern.compile("\"(.+?)\"");
	//private static Pattern facePattern = Pattern.compile("\\d V\\((.+?)\\) (.+?) UV\\((.+?)\\)");
	//M(n)要素がなくてもクラッシュしない&未マッピング面無視
	private static Pattern facePattern = Pattern.compile("\\d V\\((.+?)\\)(.+?)");
	private static Pattern vertexIndexPattern = Pattern.compile("V\\((.+?)\\)");
	private static Pattern uvPattern = Pattern.compile("UV\\((.+?)\\)");
	private static Pattern materialPattern = Pattern.compile("M\\((.+?)\\)");

	private static final byte Type_Object = 0;
	private static final byte Type_Vertex = 1;
	private static final byte Type_Face = 2;
	private static final byte Type_Material = 3;
	private static final byte Type_Thumbnail = 4;

	public Map<String, Material> materials;
	private ArrayList<Vertex> currentVertices;

	private byte currentType = -1;
	private byte mirrorType = -1;

	protected MqoModel(ResourceLocation resource, VecAccuracy par2) throws ModelFormatException {
		super(resource, GL11.GL_TRIANGLES, par2);
	}

	protected MqoModel(ResourceLocation resource, int mode, VecAccuracy par3) throws ModelFormatException {
		super(resource, mode, par3);
	}

	@Override
	protected void init(ResourceLocation resource) throws ModelFormatException {
		this.materials = new HashMap<String, Material>();
		this.currentVertices = new ArrayList<Vertex>();

		super.init(resource);
	}

	@Override
	protected void parseLine(String currentLine, int lineCount) {
		if (currentLine.length()==0) {
			return;
		}

		currentLine = currentLine.trim();//Tab取る

		if (this.currentType>=0) {
			if (currentLine.startsWith("}")) {
				this.currentType = -1;
			} else if (this.currentType==Type_Face) {
				if (this.currentGroupObject==null) {
					this.currentGroupObject = new GroupObject("Default", this.drawMode);
				}

				Face face = this.parseFace(currentLine, lineCount);

				if (face!=null) {
					this.currentGroupObject.faces.add(face);
					if (this.mirrorType>=0) {
						Face mirror = face.getMirror(this.mirrorType, this.vertices, this.accuracy);
						this.currentGroupObject.faces.add(mirror);
					}
				}
			} else if (this.currentType==Type_Vertex) {
				Vertex vertex = this.parseVertex(currentLine, lineCount);
				if (vertex!=null) {
					this.currentVertices.add(vertex);
				}
			} else if (this.currentType==Type_Thumbnail) {
				return;
			} else if (this.currentType==Type_Material) {
				this.parseMaterial(currentLine, lineCount);
			}
		} else {
			if (currentLine.startsWith("vertex ")) {
				this.currentType = Type_Vertex;
				this.vertices.addAll(this.currentVertices);
				this.currentVertices.clear();
			} else if (currentLine.startsWith("face ")) {
				this.currentType = Type_Face;
			} else if (currentLine.startsWith("Material ")) {
				this.currentType = Type_Material;
			} else if (currentLine.startsWith("Object ")) {
				GroupObject group = this.parseGroupObject(currentLine, lineCount);

				if (group!=null) {
					if (this.currentGroupObject!=null) {
						this.groupObjects.add(this.currentGroupObject);
					}
				}

				this.currentGroupObject = group;
				this.mirrorType = -1;
			} else if (currentLine.startsWith("mirror_axis "))//{x,y,z}={1,2,4}
			{
				String[] sa = currentLine.split(" ");
				int axis = Integer.parseInt(sa[1]);
				this.mirrorType = (byte) (axis==1 ? 0 : (axis==2 ? 1 : 2));
			} else if (currentLine.startsWith("facet ")) {
				String[] sa = currentLine.split(" ");
				float angle = Float.parseFloat(sa[1]);
				this.currentGroupObject.smoothingAngle = angle;
			} else if (currentLine.startsWith("Thumbnail ")) {
				this.currentType = Type_Thumbnail;
			}
		}
	}

	@Override
	protected void postInit() {
		this.groupObjects.add(this.currentGroupObject);
		this.vertices.addAll(this.currentVertices);
	}

	private void parseMaterial(String line, int lineCount) throws ModelFormatException {
		String[] tokens = line.split(" ");
		if (tokens.length>1) {
			String matName = tokens[0].replaceAll("\"", "");
			Material material = new Material((byte) this.materials.size(), null);
			this.materials.put(matName, material);
		} else {
			throw new ModelFormatException("Error parsing material ('"+line+"'"+", line "+lineCount+") in file '"+fileName+"'");
		}
	}

	private Vertex parseVertex(String line, int lineCount) throws ModelFormatException {
		if (this.isValidVertexLine(line)) {
			String[] tokens = line.split(" ");

			try {
				if (tokens.length==2) {
					return Vertex.create(this.getCorrectValue(tokens[0]), this.getCorrectValue(tokens[1]), 0.0F, this.accuracy);
				} else if (tokens.length==3) {
					return Vertex.create(this.getCorrectValue(tokens[0]), this.getCorrectValue(tokens[1]), this.getCorrectValue(tokens[2]), this.accuracy);
				}
			} catch (NumberFormatException e) {
				throw new ModelFormatException(String.format("Number formatting error at line %d", lineCount), e);
			}
		} else {
			throw new ModelFormatException("Error parsing vertex ('"+line+"'"+", line "+lineCount+") in file '"+this.fileName+"'");
		}
		return null;
	}

	/**MQOでは単位がcmなのでmに変換*/
	private float getCorrectValue(String s) {
		return Float.parseFloat(s)*0.01F;
		//return (float)(Double.parseDouble(s) * 0.01D);
	}

	private Face parseFace(String line, int lineCount) throws ModelFormatException {
		if (this.isValidFaceLine(line)) {
			String[] tokens = line.split(" ");
			String mat = this.getMaterial(line);
			int matId = mat.length()==0 ? 0 : Integer.parseInt(mat);

			/*if(tokens[0].equals("3"))
			{
			    return this.parseFaceTriangles(line, (byte)matId, lineCount);
			}*/

			if (tokens[0].equals("4")) {
				return this.parseFaceQuads(line, (byte) matId, lineCount);
			} else {
				int vertexCount = Integer.parseInt(tokens[0]);
				if (vertexCount==2) {
					return null;
				}
				return this.parsePolygon(line, (byte) matId, lineCount, vertexCount);
			}
		} else {
			throw new ModelFormatException("Error parsing face ('"+line+"'"+", line "+lineCount+") in file '"+this.fileName+"'");
		}
	}

	/**三角ポリゴンの生成*/
	/*private Face parseFaceTriangles(String line, byte matId, int lineCount)
	{
		Face face = new Face(matId);
	
		String vertexIndex = this.getVertexIndex(line);
		String[] vertexes = vertexIndex.split(" ");
		face.vertices = new Vertex[3];
	
		String uv = this.getUV(line);
		String[] uvs = uv.split(" ");
		face.textureCoordinates = new TextureCoordinate[3];
	
		for(int i = 0; i < 3; ++i)
	    {
			//メタセコは面の張り方が逆
			face.vertices[2 - i] = this.currentVertices.get(Integer.parseInt(vertexes[i]));
	
			float u = Float.parseFloat(uvs[i * 2]);
			float v = Float.parseFloat(uvs[(i * 2) + 1]);
			face.textureCoordinates[2 - i] = TextureCoordinate.create(u, v, this.accuracy);
	    }
	
		face.calculateFaceNormal(this.accuracy);
		return face;
	}*/

	/**四角ポリゴンの生成*/
	private Face parseFaceQuads(String line, byte matId, int lineCount) {
		Face face = new Face(matId);

		String vertexIndex = this.getVertexIndex(line);
		String[] vertexes = vertexIndex.split(" ");
		face.vertices = new Vertex[this.drawMode==GL11.GL_TRIANGLES ? 6 : 4];

		String uv = this.getUV(line);
		String[] uvs = (uv.length()==0) ? null : uv.split(" ");
		face.textureCoordinates = new TextureCoordinate[this.drawMode==GL11.GL_TRIANGLES ? 6 : 4];

		if (this.drawMode==GL11.GL_TRIANGLES) {
			for (int i = 0; i<6; ++i) {
				int i0 = i<3 ? i : (i<5 ? i-1 : 0);
				face.vertices[5-i] = this.currentVertices.get(Integer.parseInt(vertexes[i0]));
				float u = (uvs==null) ? 0.0F : Float.parseFloat(uvs[i0*2]);
				float v = (uvs==null) ? 0.0F : Float.parseFloat(uvs[(i0*2)+1]);
				face.textureCoordinates[5-i] = TextureCoordinate.create(u, v, this.accuracy);
			}
		} else {
			for (int i = 0; i<4; ++i) {
				face.vertices[3-i] = this.currentVertices.get(Integer.parseInt(vertexes[i]));
				float u = (uvs==null) ? 0.0F : Float.parseFloat(uvs[i*2]);
				float v = (uvs==null) ? 0.0F : Float.parseFloat(uvs[(i*2)+1]);
				face.textureCoordinates[3-i] = TextureCoordinate.create(u, v, this.accuracy);
			}
		}

		face.calculateFaceNormal(this.accuracy);
		return face;
	}

	private Face parsePolygon(String line, byte matId, int lineCount, int vertexCount) {
		Face face = new Face(matId);
		int size = (vertexCount-2)*3;//面数->全頂点数

		String vertexIndex = this.getVertexIndex(line);
		String[] vertexes = vertexIndex.split(" ");
		face.vertices = new Vertex[size];

		String uv = this.getUV(line);
		String[] uvs = (uv.length()==0) ? null : uv.split(" ");
		face.textureCoordinates = new TextureCoordinate[size];

		if (this.drawMode==GL11.GL_TRIANGLES) {
			for (int i = 0; i<size; ++i) {
				int index = (i%3==0) ? 0 : (i/3)+(i%3);
				int i2 = size-1-i;
				//メタセコは面の張り方が逆
				face.vertices[i2] = this.currentVertices.get(Integer.parseInt(vertexes[index]));
				float u = (uvs==null) ? 0.0F : Float.parseFloat(uvs[index*2]);
				float v = (uvs==null) ? 0.0F : Float.parseFloat(uvs[(index*2)+1]);
				face.textureCoordinates[i2] = TextureCoordinate.create(u, v, this.accuracy);
			}
		} else {
			throw new ModelFormatException("Error parsing face ('"+line+"'"+", line "+lineCount+") in file '"+this.fileName+"'");
		}

		face.calculateFaceNormal(this.accuracy);
		return face;
	}

	private GroupObject parseGroupObject(String line, int lineCount) throws ModelFormatException {
		String s = this.getGroupObjectName(line);
		if (s!=null&&s.length()>0) {
			return new GroupObject(s, this.drawMode);
		} else {
			throw new ModelFormatException("Error parsing object ('"+line+"'"+", line "+lineCount+") in file '"+fileName+"'");
		}
	}

	/**この文字列が頂点かどうか*/
	private boolean isValidVertexLine(String line) {
		return vertexPattern.matcher(line).matches();
	}

	/**この文字列が面かどうか*/
	private boolean isValidFaceLine(String line) {
		return facePattern.matcher(line).matches();
	}

	private String getVertexIndex(String line) {
		return this.getMatchedString(vertexIndexPattern.matcher(line));
	}

	private String getUV(String line) {
		return this.getMatchedString(uvPattern.matcher(line));
	}

	private String getMaterial(String line) {
		return this.getMatchedString(materialPattern.matcher(line));
	}

	/**オブジェクト名を取得*/
	private String getGroupObjectName(String line) {
		return this.getMatchedString(groupObjectPattern.matcher(line));
	}

	private String getMatchedString(Matcher matcher) {
		try {
			matcher.find();
			return matcher.group(1);
		} catch (IllegalStateException e) {
			//e.printStackTrace();
			return "";
		}
	}

	@Override
	public FileType getType() {
		return FileType.MQO;
	}

	@Override
	public Map<String, Material> getMaterials() {
		return this.materials;
	}
}