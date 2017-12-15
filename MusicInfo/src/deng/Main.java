package deng;

import deng.pojo.MusicFile;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MusicFile file = new MusicFile(
				"C:\\Users\\Deng\\Desktop\\Sam+Smith+-+The+Thrill+of+It+All+(Special+Edition)\\01 Too Good at Goodbyes.m4a");
		System.out.println(file.getMusicInfo().getTitle());
	}
}
