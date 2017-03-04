package net.teamfruit.projectrtm.ngtlib.io;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.minecraft.util.ResourceLocation;

public final class NGTText {
	public static String getText(ResourceLocation resource, boolean indention) throws IOException {
		List<String> list = readText(resource);
		StringBuilder sb = new StringBuilder();
		for (String s : list) {
			sb.append(s);
			if (indention) {
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	public static List<String> readText(ResourceLocation resource) throws IOException {
		List<String> list = new ArrayList<String>();
		InputStream is = NGTFileLoader.getInputStream(resource);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String currentLine = null;

		while ((currentLine = reader.readLine())!=null) {
			list.add(currentLine);
		}

		is.close();
		reader.close();

		return list;
	}

	public static String readText(File file, boolean indention) {
		String[] list = readText(file);
		StringBuilder sb = new StringBuilder();
		for (String s : list) {
			sb.append(s);
			if (indention) {
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	/**失敗した場合、長さ0の配列を返す*/
	public static String[] readText(File file) {
		ArrayList<String> strings = new ArrayList<String>();
		if (file.getAbsolutePath().contains(".zip")) {
			String path = file.getAbsolutePath();
			int index = path.indexOf(".zip");
			String zipPath = path.substring(0, index+4);
			NGTLog.debug("load json form zip : "+zipPath);
			try {
				ZipFile zip = new ZipFile(zipPath);
				Enumeration<? extends ZipEntry> enu = zip.entries();
				while (enu.hasMoreElements()) {
					ZipEntry ze = enu.nextElement();
					if (!ze.isDirectory()) {
						File fileInZip = new File(zipPath, ze.getName());
						if (fileInZip.getName().equals(file.getName())) {
							InputStream is = zip.getInputStream(ze);
							BufferedInputStream bis = new BufferedInputStream(is);
							BufferedReader br = new BufferedReader(new InputStreamReader(bis));
							String string;
							while ((string = br.readLine())!=null) {
								strings.add(string);
							}
							br.close();
							break;
						}
					}
				}
				zip.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				String string;
				while ((string = br.readLine())!=null) {
					strings.add(string);
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return strings.toArray(new String[strings.size()]);
	}

	public static boolean writeToText(File file, String... texts) {
		try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8")));
			for (String s : texts) {
				pw.println(s);
			}
			pw.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}