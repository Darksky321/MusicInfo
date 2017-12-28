package deng.music;

import java.io.File;

public class FileTools {
	public static String getSuffix(File file) {
		if (file.exists()) {
			String name = file.getName();
			if (name.contains("."))
				return name.substring(name.lastIndexOf(".") + 1);
			else
				return "";
		} else
			return "";
	}

	public static String getSuffix(String path) {
		if (path.contains("."))
			return path.substring(path.lastIndexOf(".") + 1);
		else
			return "";
	}
}
