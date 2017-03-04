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
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class NGTJson {
	public static String readFromJson(File file) {
		StringBuilder sb = new StringBuilder();
		if (file.getAbsolutePath().contains(".zip")) {
			String path = file.getAbsolutePath();
			int index = path.indexOf(".zip");
			String zipPath = path.substring(0, index+4);
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
								sb.append(string);
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
				//BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				String string;
				while ((string = br.readLine())!=null) {
					sb.append(string);
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}

	public static void writeToJson(String json, File file) {
		try {
			//PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8")));
			pw.println(json);
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * jsonからオブジェクトを生成
	 * @throws NGTFileLoadException jsonの書式が不正な場合にスロー
	 */
	public static Object getObjectFromJson(String json, Class<?> clazz) throws NGTFileLoadException {
		try {
			return getGson().fromJson(json, clazz);
		} catch (Exception e) {
			String message = "Can't load json : "+json+" ("+e.getMessage()+")";
			throw new NGTFileLoadException(message, e);
		}
	}

	public static String getJsonFromObject(Object object) {
		return getGson().toJson(object);
	}

	private static Gson getGson() {
		return new GsonBuilder().setPrettyPrinting().create();
	}
}