package net.teamfruit.projectrtm.ngtlib.io;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.IOUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.teamfruit.projectrtm.ngtlib.NGTCore;

public final class NGTFileLoader {
	private static File MODS_DIR;
	private static String DEV_PATH = "ZZZZZZ";
	private static File PREV_OPENED_FOLDER;

	/**
	 * modsフォルダ以下にあるファイルを探す
	 * @param start : 接頭辞
	 * @param end : 接尾辞
	 * @param contain : 含まれる文字列
	 */
	public static List<File> findFile(final String start, final String end, final String contain) {
		return findFile(start, end, contain, null);
	}

	/**
	 * modsフォルダ以下にあるファイルを探す
	 * @param start : 接頭辞
	 * @param end : 接尾辞
	 * @param contain : 含まれる文字列
	 * @param watcher : null OK
	 */
	public static List<File> findFile(final String start, final String end, final String contain, final IProgressWatcher watcher) {
		final List<File> list = new ArrayList<File>();
		final File dir = getModsDir();
		NGTLog.debug("Set search path : "+dir.getAbsolutePath());
		list.addAll(findFileInDirectory(dir, start, end, contain, watcher));
		return list;
	}

	public static List<File> findFileInDirectory(final File dir, final String start, final String end, final String contain, final IProgressWatcher watcher) {
		final List<File> list = new ArrayList<File>();
		final String[] files = dir.list();
		int count = 0;

		if (watcher!=null)
			watcher.setMaxValue(1, files.length, "");

		for (final String path : files) {
			final File entry = new File(dir, path);
			++count;
			if (entry.isFile()) {
				final String name = entry.getName();
				if (watcher!=null)
					watcher.setValue(1, count, name);

				if (checkFileName(name, start, end, contain)) {
					list.add(entry);
					NGTLog.debug("Add file : "+name);
				} else if (FileType.ZIP.match(name)) {
					NGTLog.debug("Scan zip : "+name);
					list.addAll(findFileInZip(entry, start, end, contain));
				} else if (FileType.JAR.match(name)) {
					NGTLog.debug("Scan jar : "+name);
					list.addAll(findFileInJar(entry, start, end, contain));
				}
			} else if (entry.isDirectory())
				list.addAll(findFileInDirectory(entry, start, end, contain, watcher));
		}
		return list;
	}

	private static List<File> findFileInZip(final File dir, final String start, final String end, final String contain) {
		final List<File> list = new ArrayList<File>();
		try {
			final ZipFile zip = new ZipFile(dir.getAbsolutePath());
			final Enumeration<? extends ZipEntry> enu = zip.entries();
			while (enu.hasMoreElements()) {
				final ZipEntry ze = enu.nextElement();
				if (!ze.isDirectory()) {
					final File file = new File(dir.getAbsolutePath(), ze.getName());
					if (checkFileName(file.getName(), start, end, contain)) {
						list.add(file);
						NGTLog.debug("Add file : "+file.getName());
					}
				}
			}
			zip.close();
		} catch (final IOException e) {
			e.printStackTrace();
			NGTLog.debug("IOException:"+dir);
		} catch (final IllegalArgumentException e) {
			e.printStackTrace();
			NGTLog.debug("IllegalArgumentException:"+dir);
		}
		return list;
	}

	private static List<File> findFileInJar(final File dir, final String start, final String end, final String contain) {
		final List<File> list = new ArrayList<File>();
		try {
			final JarFile jar = new JarFile(dir.getAbsolutePath());
			final Enumeration<? extends ZipEntry> enu = jar.entries();
			while (enu.hasMoreElements()) {
				final ZipEntry ze = enu.nextElement();
				if (!ze.isDirectory()) {
					final File file = new File(dir.getAbsolutePath(), ze.getName());
					if (checkFileName(file.getName(), start, end, contain)) {
						list.add(file);
						NGTLog.debug("Add file : "+file.getName());
					}
				}
			}
			jar.close();
		} catch (final IOException e) {
			e.printStackTrace();
			NGTLog.debug("IOException:"+dir);
		} catch (final IllegalArgumentException e) {
			e.printStackTrace();
			NGTLog.debug("IllegalArgumentException:"+dir);
		}
		return list;
	}

	private static boolean checkFileName(final String filename, final String start, final String end, final String contain) {
		final boolean flag1 = start.length()==0||filename.startsWith(start);
		final boolean flag2 = end.length()==0||filename.endsWith(end);
		final boolean flag3 = contain.length()==0||filename.contains(contain);
		return flag1&&flag2&&flag3;
	}

	public static byte[] readBytes(final File par1) throws IOException {
		final InputStream is = new FileInputStream(par1);
		final ByteArrayOutputStream bout = new ByteArrayOutputStream();
		final byte[] buffer = new byte[1024];
		while (true) {
			final int len = is.read(buffer);
			if (len<0)
				break;
			bout.write(buffer, 0, len);
		}
		is.close();
		return bout.toByteArray();
	}

	/**modsフォルダの場所を取得*/
	public static File getModsDir()//cpw.mods.fml.common.Loader
	{
		if (MODS_DIR!=null)
			return MODS_DIR;
		else {
			File dir = new File(NGTCore.proxy.getMinecraftDirectory(), "mods");
			try {
				dir = dir.getCanonicalFile();
			} catch (final IOException e) {
				NGTLog.debug("failed to canonicate path :"+dir);
			}

			//			if(dir.getAbsolutePath().contains(NGTCore.developmentPathContainedText))
			//			{
			//				NGTLog.debug("Set development path to mods directory");//Eclipseでのクラスパス
			//				MODS_DIR = new File(Thread.currentThread().getContextClassLoader().getResource("").getPath());
			//				return MODS_DIR;
			//			}
			//			else
			//			{
			MODS_DIR = dir;
			return MODS_DIR;
			//			}
		}
	}

	//static int state = 0;

	private static JFileChooser getCustomChooser(final String title) {
		final JFileChooser chooser = new JFileChooser(PREV_OPENED_FOLDER) {
			@Override
			protected JDialog createDialog(final Component parent) throws HeadlessException {
				final JDialog dialog = super.createDialog(parent);
				dialog.setAlwaysOnTop(true);//常に前面に表示
				return dialog;
			}
		};

		chooser.setDialogTitle(title);
		chooser.requestFocusInWindow();
		return chooser;
	}

	//static long time;

	/**
	 * ファイル選択画面を開く
	 * @param extensions {ファイルの種類, 拡張子}
	 */
	public static synchronized File selectFile(final String[][] extensions) {
		/*long currentTime = System.currentTimeMillis();
		if(currentTime - time < 100)
		{
			return null;
		}
		time = System.currentTimeMillis();*/

		final JFileChooser chooser = getCustomChooser("Select File");
		chooser.setAcceptAllFileFilterUsed(false);//フィルタ:全てのファイル
		for (int i = 0; i<extensions.length; ++i) {
			final FileFilter filter = new FileNameExtensionFilter(extensions[i][0], extensions[i][1]);
			chooser.addChoosableFileFilter(filter);
		}

		final int state = chooser.showOpenDialog(null);
		if (state==JFileChooser.APPROVE_OPTION) {
			final File file = chooser.getSelectedFile();
			PREV_OPENED_FOLDER = file.getParentFile();
			return file;
		}

		return null;
	}

	/**
	 * ファイル保存画面を開く
	 * @param extension {ファイルの種類, 拡張子}
	 */
	public static synchronized File saveFile(final String[] extension) {
		final JFileChooser chooser = getCustomChooser("Save File");
		chooser.setAcceptAllFileFilterUsed(false);
		final FileFilter filter = new FileNameExtensionFilter(extension[0], extension[1]);
		chooser.addChoosableFileFilter(filter);

		final int state = chooser.showSaveDialog(null);
		if (state==JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			PREV_OPENED_FOLDER = file.getParentFile();
			if (!file.getName().endsWith(extension[1]))
				file = new File(file.getAbsolutePath()+"."+extension[1]);
			return file;
		}

		return null;
	}

	public static InputStream getInputStream(final ResourceLocation par1) throws IOException {
		return Minecraft.getMinecraft().getResourceManager().getResource(par1).getInputStream();
	}

	public static File createTempFile(final InputStream is, final String name) throws IOException {
		//final File tempFile = File.createTempFile(prefix, suffix);
		final File tempDir = new File(System.getProperty("java.io.tmpdir"));
		final File tempFile = new File(tempDir, name);
		tempFile.deleteOnExit();
		final FileOutputStream out = new FileOutputStream(tempFile);
		IOUtils.copy(is, out);
		return tempFile;
	}
}