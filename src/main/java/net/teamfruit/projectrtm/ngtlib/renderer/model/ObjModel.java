package net.teamfruit.projectrtm.ngtlib.renderer.model;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelFormatException;
import net.teamfruit.projectrtm.ngtlib.io.FileType;

/**
 * ForgeのWavefrontObjectがマルチスレッド未対応のためRTMなどではこちらを使用<br>
 * 四角ポリゴン対応済み
 */
@SideOnly(Side.CLIENT)
public final class ObjModel extends PolygonModel {
	/**小数点含む必要あり*/
	private static Pattern vertexPattern = Pattern.compile("(v( (\\-){0,1}\\d+\\.\\d+){3,4} *\\n)|(v( (\\-){0,1}\\d+\\.\\d+){3,4} *$)");
	private static Pattern vertexNormalPattern = Pattern.compile("(vn( (\\-){0,1}\\d+\\.\\d+){3,4} *\\n)|(vn( (\\-){0,1}\\d+\\.\\d+){3,4} *$)");
	private static Pattern textureCoordinatePattern = Pattern.compile("(vt( (\\-){0,1}\\d+\\.\\d+){2,3} *\\n)|(vt( (\\-){0,1}\\d+\\.\\d+){2,3} *$)");
	private static Pattern face_V_VT_VN_Pattern = Pattern.compile("(f( \\d+/\\d+/\\d+){3,4} *\\n)|(f( \\d+/\\d+/\\d+){3,4} *$)");
	private static Pattern face_V_VT_Pattern = Pattern.compile("(f( \\d+/\\d+){3,4} *\\n)|(f( \\d+/\\d+){3,4} *$)");
	private static Pattern face_V_VN_Pattern = Pattern.compile("(f( \\d+//\\d+){3,4} *\\n)|(f( \\d+//\\d+){3,4} *$)");
	private static Pattern face_V_Pattern = Pattern.compile("(f( \\d+){3,4} *\\n)|(f( \\d+){3,4} *$)");
	private static Pattern groupObjectPattern = Pattern.compile("([go]( [\\w\\d]+) *\\n)|([go]( [\\w\\d]+) *$)");

	public ArrayList<Vertex> vertexNormals;
	public ArrayList<TextureCoordinate> textureCoordinates;
	public Map<String, Material> materials;

	private byte currentMaterial;

	protected ObjModel(final ResourceLocation resource, final VecAccuracy par2) throws ModelFormatException {
		super(resource, GL11.GL_TRIANGLES, par2);
	}

	@Override
	protected void init(final ResourceLocation resource) throws ModelFormatException {
		this.vertexNormals = new ArrayList<Vertex>();
		this.textureCoordinates = new ArrayList<TextureCoordinate>();

		final String mtlFileName = resource.getResourcePath().replaceAll(".obj", ".mtl");
		final ResourceLocation mtlFile = new ResourceLocation(resource.getResourceDomain(), mtlFileName);
		this.materials = new MtlParser(mtlFile).getMaterials();

		super.init(resource);
	}

	@Override
	protected void parseLine(final String currentLine, final int lineCount) {
		if (currentLine.length()==0)
			return;
		else if (currentLine.startsWith("f ")) {
			if (this.currentGroupObject==null) {
				this.currentGroupObject = new GroupObject("Default", GL11.GL_TRIANGLES);
				this.currentGroupObject.smoothingAngle = Face.SMOOTHING;
			}

			final Face face = parseFace(currentLine, lineCount);

			if (face!=null)
				this.currentGroupObject.faces.add(face);
		} else if (currentLine.startsWith("vt ")) {
			final TextureCoordinate textureCoordinate = parseTextureCoordinate(currentLine, lineCount);
			if (textureCoordinate!=null)
				this.textureCoordinates.add(textureCoordinate);
		} else if (currentLine.startsWith("v ")) {
			final Vertex vertex = parseVertex(currentLine, lineCount);
			if (vertex!=null)
				this.vertices.add(vertex);
		}

		else if (currentLine.startsWith("usemtl ")) {
			final String[] sa = currentLine.split(" ");
			final Material mat = this.materials.get(sa[1]);
			if (mat!=null)
				this.currentMaterial = mat.id;
		} else if (currentLine.startsWith("vn ")) {
			final Vertex vertex = parseVertexNormal(currentLine, lineCount);
			if (vertex!=null)
				this.vertexNormals.add(vertex);
		} else if (currentLine.startsWith("g ")|currentLine.startsWith("o ")) {
			final GroupObject group = parseGroupObject(currentLine, lineCount);

			if (group!=null)
				if (this.currentGroupObject!=null)
					this.groupObjects.add(this.currentGroupObject);

			this.currentGroupObject = group;
			this.currentGroupObject.smoothingAngle = Face.SMOOTHING;
		}
	}

	@Override
	protected void postInit() {
		this.groupObjects.add(this.currentGroupObject);
	}

	/**頂点生成*/
	private Vertex parseVertex(String line, final int lineCount) throws ModelFormatException {
		if (isValidVertexLine(line)) {
			line = line.substring(line.indexOf(" ")+1);
			final String[] tokens = line.split(" ");

			try {
				if (tokens.length==2)
					return Vertex.create(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]), 0.0F, this.accuracy);
				else if (tokens.length==3)
					return Vertex.create(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), this.accuracy);
			} catch (final NumberFormatException e) {
				throw new ModelFormatException(String.format("Number formatting error at line %d", lineCount), e);
			}
		} else
			throw new ModelFormatException("Error parsing entry ('"+line+"'"+", line "+lineCount+") in file '"+this.fileName+"' - Incorrect format");

		return null;
	}

	/**頂点法線生成*/
	private Vertex parseVertexNormal(String line, final int lineCount) throws ModelFormatException {
		if (isValidVertexNormalLine(line)) {
			line = line.substring(line.indexOf(" ")+1);
			final String[] tokens = line.split(" ");

			try {
				if (tokens.length==3)
					return Vertex.create(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), this.accuracy);
			} catch (final NumberFormatException e) {
				throw new ModelFormatException(String.format("Number formatting error at line %d", lineCount), e);
			}
		} else
			throw new ModelFormatException("Error parsing entry ('"+line+"'"+", line "+lineCount+") in file '"+this.fileName+"' - Incorrect format");

		return null;
	}

	private TextureCoordinate parseTextureCoordinate(String line, final int lineCount) throws ModelFormatException {
		if (isValidTextureCoordinateLine(line)) {
			line = line.substring(line.indexOf(" ")+1);
			final String[] tokens = line.split(" ");

			try {
				return TextureCoordinate.create(Float.parseFloat(tokens[0]), 1.0F-Float.parseFloat(tokens[1]), this.accuracy);
			} catch (final NumberFormatException e) {
				throw new ModelFormatException(String.format("Number formatting error at line %d", lineCount), e);
			}
		} else
			throw new ModelFormatException("Error parsing entry ('"+line+"'"+", line "+lineCount+") in file '"+this.fileName+"' - Incorrect format");
	}

	private Face parseFace(final String line, final int lineCount) throws ModelFormatException {
		if (isValidFaceLine(line)) {
			final String trimmedLine = line.substring(line.indexOf(" ")+1);
			final String[] tokens = trimmedLine.split(" ");

			if (tokens.length>2)
				return parsePolygon(line, tokens, lineCount);

			/*if(tokens.length == 3)
			{
			    return this.parseFaceTriangles(line, tokens, lineCount);
			}
			else if(tokens.length == 4)
			{
			    return this.parseFaceQuads(line, tokens, lineCount);
			}*/
		} else
			throw new ModelFormatException("Error parsing entry ('"+line+"'"+", line "+lineCount+") in file '"+this.fileName+"' - Incorrect format");
		return null;
	}

	/**三角ポリゴンの生成*/
	@Deprecated
	private Face parseFaceTriangles(final String line, final String[] tokens, final int lineCount) {
		final Face face = new Face(this.currentMaterial);
		String[] subTokens = new String[0];
		final byte type = getValidType(line);
		if (type>=0) {
			face.vertices = new Vertex[tokens.length];
			if (type==0||type==1)
				face.textureCoordinates = new TextureCoordinate[tokens.length];

			if (type==0||type==2) {
				//face.vertexNormals = new Vertex[tokens.length];
			}

			for (int i = 0; i<tokens.length; ++i)
				if (type<3) {
					if (type==0||type==1)
						subTokens = tokens[i].split("/");
					else if (type==2)
						subTokens = tokens[i].split("//");

					face.vertices[i] = this.vertices.get(Integer.parseInt(subTokens[0])-1);

					if (type==0||type==1)
						face.textureCoordinates[i] = this.textureCoordinates.get(Integer.parseInt(subTokens[1])-1);

					if (type==0||type==2) {
						//face.vertexNormals[i] = this.vertexNormals.get(Integer.parseInt(subTokens[type == 0 ? 2 : 1]) - 1);
					}
				} else
					face.vertices[i] = this.vertices.get(Integer.parseInt(tokens[i])-1);

			face.calculateFaceNormal(this.accuracy);
		} else
			throw new ModelFormatException("Error parsing entry ('"+line+"'"+", line "+lineCount+") in file '"+this.fileName+"' - Incorrect format");

		return face;
	}

	/**四角ポリゴンの生成*/
	@Deprecated
	private Face parseFaceQuads(final String line, final String[] tokens, final int lineCount) {
		final Face face = new Face(this.currentMaterial);
		String[] subTokens = new String[0];
		final byte type = getValidType(line);
		if (type>=0) {
			face.vertices = new Vertex[6];
			if (type==0||type==1)
				face.textureCoordinates = new TextureCoordinate[6];

			if (type==0||type==2) {
				//face.vertexNormals = new Vertex[6];
			}

			for (int i = 0; i<6; ++i) {
				final int i0 = i<3 ? i : i<5 ? i-1 : 0;

				if (type<3) {
					if (type==0||type==1)
						subTokens = tokens[i0].split("/");
					else if (type==2)
						subTokens = tokens[i0].split("//");

					face.vertices[i] = this.vertices.get(Integer.parseInt(subTokens[0])-1);

					if (type==0||type==1)
						face.textureCoordinates[i] = this.textureCoordinates.get(Integer.parseInt(subTokens[1])-1);

					if (type==0||type==2) {
						//face.vertexNormals[i] = this.vertexNormals.get(Integer.parseInt(subTokens[type == 0 ? 2 : 1]) - 1);
					}
				} else
					face.vertices[i] = this.vertices.get(Integer.parseInt(tokens[i0])-1);
			}

			face.calculateFaceNormal(this.accuracy);
		} else
			throw new ModelFormatException("Error parsing entry ('"+line+"'"+", line "+lineCount+") in file '"+this.fileName+"' - Incorrect format");

		return face;
	}

	private Face parsePolygon(final String line, final String[] tokens, final int lineCount) {
		final Face face = new Face(this.currentMaterial);
		String[] subTokens = new String[0];
		final byte type = getValidType(line);
		if (type>=0) {
			final int size = (tokens.length-2)*3;//面数->全頂点数
			face.vertices = new Vertex[size];

			if (type==0||type==1)
				face.textureCoordinates = new TextureCoordinate[6];

			if (type==0||type==2) {
				//face.vertexNormals = new Vertex[6];
			}

			for (int i = 0; i<size; ++i) {
				final int index = i%3==0 ? 0 : i/3+i%3;

				if (type<3) {
					if (type==0||type==1)
						subTokens = tokens[index].split("/");
					else if (type==2)
						subTokens = tokens[index].split("//");

					face.vertices[i] = this.vertices.get(Integer.parseInt(subTokens[0])-1);

					if (type==0||type==1)
						face.textureCoordinates[i] = this.textureCoordinates.get(Integer.parseInt(subTokens[1])-1);

					if (type==0||type==2) {
						//face.vertexNormals[i] = this.vertexNormals.get(Integer.parseInt(subTokens[type == 0 ? 2 : 1]) - 1);
					}
				} else
					face.vertices[i] = this.vertices.get(Integer.parseInt(tokens[index])-1);
			}

			face.calculateFaceNormal(this.accuracy);
		} else
			throw new ModelFormatException("Error parsing entry ('"+line+"'"+", line "+lineCount+") in file '"+this.fileName+"' - Incorrect format");

		return face;
	}

	private GroupObject parseGroupObject(final String line, final int lineCount) throws ModelFormatException {
		GroupObject group = null;
		if (isValidGroupObjectLine(line)) {
			final String trimmedLine = line.substring(line.indexOf(" ")+1);
			if (trimmedLine.length()>0)
				group = new GroupObject(trimmedLine, GL11.GL_TRIANGLES);
		} else
			throw new ModelFormatException("Error parsing entry ('"+line+"'"+", line "+lineCount+") in file '"+this.fileName+"' - Incorrect format");
		return group;
	}

	private static boolean isValidVertexLine(final String line) {
		return vertexPattern.matcher(line).matches();
	}

	private static boolean isValidVertexNormalLine(final String line) {
		return vertexNormalPattern.matcher(line).matches();
	}

	private static boolean isValidTextureCoordinateLine(final String line) {
		return textureCoordinatePattern.matcher(line).matches();
	}

	/***
	 * Verifies that the given line from the model file is a valid face that is described by vertices, texture coordinates, and vertex normals
	 * @param line the line being validated
	 * @return true if the line is a valid face that matches the format "f v1/vt1/vn1 ..." (with a minimum of 3 points in the face, and a maximum of 4), false otherwise
	 */
	private static boolean isValidFace_V_VT_VN_Line(final String line) {
		return face_V_VT_VN_Pattern.matcher(line).matches();
	}

	/***
	 * Verifies that the given line from the model file is a valid face that is described by vertices and texture coordinates
	 * @param line the line being validated
	 * @return true if the line is a valid face that matches the format "f v1/vt1 ..." (with a minimum of 3 points in the face, and a maximum of 4), false otherwise
	 */
	private static boolean isValidFace_V_VT_Line(final String line) {
		return face_V_VT_Pattern.matcher(line).matches();
	}

	/***
	 * Verifies that the given line from the model file is a valid face that is described by vertices and vertex normals
	 * @param line the line being validated
	 * @return true if the line is a valid face that matches the format "f v1//vn1 ..." (with a minimum of 3 points in the face, and a maximum of 4), false otherwise
	 */
	private static boolean isValidFace_V_VN_Line(final String line) {
		return face_V_VN_Pattern.matcher(line).matches();
	}

	/***
	 * Verifies that the given line from the model file is a valid face that is described by only vertices
	 * @param line the line being validated
	 * @return true if the line is a valid face that matches the format "f v1 ..." (with a minimum of 3 points in the face, and a maximum of 4), false otherwise
	 */
	private static boolean isValidFace_V_Line(final String line) {
		return face_V_Pattern.matcher(line).matches();
	}

	/***
	 * Verifies that the given line from the model file is a valid face of any of the possible face formats
	 * @param line the line being validated
	 * @return true if the line is a valid face that matches any of the valid face formats, false otherwise
	 */
	private static boolean isValidFaceLine(final String line) {
		return isValidFace_V_VT_VN_Line(line)||isValidFace_V_VT_Line(line)||isValidFace_V_VN_Line(line)||isValidFace_V_Line(line);
	}

	/**
	 * @return 0:V_VT_VN<br>
	 * 1:V_VT<br>
	 * 2:V_VN<br>
	 * 3:V<br>
	 * -1:一致せず
	 * */
	private static byte getValidType(final String line) {
		if (isValidFace_V_VT_VN_Line(line))
			return 0;
		else if (isValidFace_V_VT_Line(line))
			return 1;
		else if (isValidFace_V_VN_Line(line))
			return 2;
		else if (isValidFace_V_Line(line))
			return 3;
		return -1;
	}

	private static boolean isValidGroupObjectLine(final String line) {
		return groupObjectPattern.matcher(line).matches();
	}

	@Override
	public FileType getType() {
		return FileType.OBJ;
	}

	@Override
	public int getDrawMode() {
		return GL11.GL_TRIANGLES;
	}

	@Override
	public Map<String, Material> getMaterials() {
		return this.materials;
	}
}