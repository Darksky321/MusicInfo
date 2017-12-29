package deng.music;

import java.io.File;

import deng.pojo.MusicInfo;

public class RenameTools {

	/**
	 * 重命名单个文件
	 * 
	 * @param file
	 *            音乐文件
	 * @param newName
	 *            重命名字符串
	 * @return 成功则为真,否则假
	 */
	public static boolean renameFile(File file, String newName) {
		MusicFile mf = new MusicFile(file.getAbsolutePath());
		MusicInfo mi = mf.getMusicInfo();
		if (mi != null) {
			String suffix = FileTools.getSuffix(mf);
			String name = File.separator + decodeFileName(mi, newName);
			System.out.println(mf.getParent() + name + "." + suffix);
			return mf.renameTo(new File(mf.getParent() + File.separator + name + "." + suffix));
		} else
			return false;
	}

	/**
	 * 重命名一个文件夹内的音乐文件
	 * 
	 * @param folder
	 *            目标文件夹
	 * @param newName
	 *            重命名字符串
	 */
	public static void renameFolder(File folder, String newName) {
		if (folder.isDirectory()) {
			File[] files = folder.listFiles();
			for (File f : files) {
				if (f.isFile()) {
					RenameTools.renameFile(f, newName);
				}
			}
		}
	}

	/**
	 * 递归重命名文件夹下的所有音乐文件
	 * 
	 * @param folder
	 *            目标文件夹
	 * @param newName
	 *            重命名字符串
	 */
	public static void renameRecursion(File folder, String newName) {
		if (folder.isDirectory()) {
			File[] files = folder.listFiles();
			for (File f : files) {
				if (f.isFile()) {
					RenameTools.renameFile(f, newName);
				} else if (f.isDirectory()) {
					renameRecursion(f, newName);
				}
			}
		}
	}

	/**
	 * 重命名字符串解释成字符串
	 * 
	 * @param mi
	 *            音乐信息
	 * @param ori
	 *            字符串
	 * @return 新文件名
	 */
	private static String decodeFileName(MusicInfo mi, String ori) {
		String name = ori.replace("@album@", mi.getAlbum()).replace("@artist@", mi.getArtist())
				.replace("@genre@", mi.getGenre()).replace("@title@", mi.getTitle()).replace("@trackn@", mi.getTrackn())
				.replace("@year@", mi.getYear());
		int length = mi.getTrack().length();
		String trkn = "";
		if (length > 0 && length > mi.getTrackn().length()) {
			trkn = String.format("%0" + length + "d", Integer.parseInt(mi.getTrackn()));
		} else {
			trkn = mi.getTrackn();
		}
		name = name.replace("@trackf@", trkn);
		String trk2 = "";
		if (mi.getTrackn().length() < 3) {
			trk2 = String.format("%02d", Integer.parseInt(mi.getTrackn()));
		}
		name = name.replace("@track2@", trk2);
		return name;
	}
}
