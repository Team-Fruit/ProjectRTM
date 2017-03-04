package net.teamfruit.projectrtm.ngtlib.io;

public class FileType {
	public static final FileType OBJ = new FileType(".obj");
	public static final FileType MQO = new FileType(".mqo");
	public static final FileType NGTO = new FileType(".ngto");
	public static final FileType CLASS = new FileType(".class");
	public static final FileType ZIP = new FileType(".zip");
	public static final FileType JAR = new FileType(".jar");
	public static final FileType NGTZ = new FileType(".ngtz");

	private final String extension;

	public FileType(String par1) {
		this.extension = par1;
	}

	public boolean match(String fileName) {
		return fileName.endsWith(this.extension);
	}
}