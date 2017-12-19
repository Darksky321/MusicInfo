package deng.pojo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.AbstractID3v2;
import org.farng.mp3.id3.ID3v1;

public class MusicFile {
	private File file;

	private static final int CMP4TAGATOM_ERROR = 0; // 初始化值
	private static final int CMP4TAGATOM_ALBUM = 1; // 专辑
	private static final int CMP4TAGATOM_ARTIST = 2; // 艺术家
	private static final int CMP4TAGATOM_NAME = 3; // 名称
	private static final int CMP4TAGATOM_DATE = 4; // 日期
	private static final int CMP4TAGATOM_GENRE = 5; // 流派
	private static final int CMP4TAGATOM_COVER = 6; // 封面
	private static final int CMP4TAGATOM_TRACK = 7; // 音轨序号

	public MusicFile(String path) {
		super();
		this.file = new File(path);
	}

	public MusicFile(File file) {
		super();
		this.file = file;
	}

	public boolean exists() {
		return this.file.exists();
	}

	public MusicInfo getMusicInfo() {
		MusicInfo info = null;
		if (this.file.getName().endsWith(".m4a") || this.file.getName().endsWith(".M4A")) {
			info = getM4AInfo();
		} else if (this.file.getName().endsWith(".mp3") || this.file.getName().endsWith(".MP3")) {
			info = getID3Info();
		}
		return info;
	}

	private MusicInfo getID3Info() {
		MusicInfo info = null;
		MP3File mp3File;
		try {
			mp3File = new MP3File(this.file);
			AbstractID3v2 id3v2 = mp3File.getID3v2Tag();
			ID3v1 id3v1 = mp3File.getID3v1Tag();
			if (id3v2 != null) {
				info = new MusicInfo();
				info.setAlbum(id3v2.getAlbumTitle());
				info.setArtist(id3v2.getLeadArtist());
				info.setTitle(id3v2.getSongTitle());
				info.setGenre(id3v2.getSongGenre());
				info.setYear(id3v2.getYearReleased());
				info.setTrack(id3v2.getTrackNumberOnAlbum());
			} else if (id3v1 != null) {
				info = new MusicInfo();
				info.setAlbum(id3v1.getAlbumTitle());
				info.setArtist(id3v1.getLeadArtist());
				info.setTitle(id3v1.getSongTitle());
				info.setGenre(id3v1.getSongGenre());
				info.setYear(id3v1.getYearReleased());
				info.setTrack(id3v1.getTrackNumberOnAlbum());
			} else {
				return null;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (TagException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return info;
	}

	private static boolean isInArray(Object[] arr, Object target) {
		for (int i = 0; i < arr.length; i++) {
			if (target.equals(arr[i])) {
				return true;
			}
		}
		return false;
	}

	private MusicInfo getM4AInfo() {
		MusicInfo info = null;
		RandomAccessFile f;
		try {
			f = new RandomAccessFile(this.file, "r");
			if (this.file.exists()) {
				info = new MusicInfo();
			}
			int tagSize, lenSize, currentAtom, lRealBytes;
			byte[] cBuf = new byte[4];
			do {
				tagSize = f.readInt();
				f.readFully(cBuf);
				String tag = new String(cBuf);

				String ignores[] = { "ftyp", "mvhd", "trak" };// 不需要解析的标记，跳过整个块
				if (isInArray(ignores, tag)) {
					f.skipBytes(tagSize - 8);
					continue;
				}
				String parents[] = { "moov", "udta", "ilst" };
				if (isInArray(parents, tag)) {// 只跳过标记
					continue;
				}
				if ("meta".equals(tag)) {
					f.skipBytes(4);
					continue;
				}
				if ("mdat".equals(tag)) {
					break;
				}

				currentAtom = CMP4TAGATOM_ERROR;
				if (cBuf[0] == (byte) 0xA9) {// 解析专辑、艺术家、名称、年份日期，这些第一个字节值为0xA9
					if (cBuf[1] == 'a' && cBuf[2] == 'l' && cBuf[3] == 'b') {
						currentAtom = CMP4TAGATOM_ALBUM;
					} else if (cBuf[1] == 'A' && cBuf[2] == 'R' && cBuf[3] == 'T') {
						currentAtom = CMP4TAGATOM_ARTIST;
					} else if (cBuf[1] == 'n' && cBuf[2] == 'a' && cBuf[3] == 'm') {
						currentAtom = CMP4TAGATOM_NAME;
					} else if (cBuf[1] == 'd' && cBuf[2] == 'a' && cBuf[3] == 'y') {
						currentAtom = CMP4TAGATOM_DATE;
					}
				} else if ("gnre".equals(tag)) { // 解析流派
					currentAtom = CMP4TAGATOM_GENRE;
				} else if ("covr".equals(tag)) { // 解析封面图片
					currentAtom = CMP4TAGATOM_COVER;
				} else if ("trkn".equals(tag)) { //
					currentAtom = CMP4TAGATOM_TRACK;
				}

				if (currentAtom != CMP4TAGATOM_ERROR) {
					lenSize = f.readInt();
					f.readFully(cBuf);

					lRealBytes = lenSize - 16;// 计算实际数据长度
					// 判断长度及标识符是否正确
					if (lenSize + 8 == tagSize && cBuf[0] == 'd' && cBuf[1] == 'a' && cBuf[2] == 't' && cBuf[3] == 'a'
							&& lRealBytes > 0) {
						f.skipBytes(8);// 当前文件指针位于ver开始处，向后移动8个字节到实际数据处
						byte[] pRealBuf = new byte[lRealBytes];
						f.readFully(pRealBuf);// 读取实际数据
						// 根据ATOM类型解析实际读取的数据
						switch (currentAtom) {
						case CMP4TAGATOM_ALBUM: // 专辑
							info.setAlbum(new String(pRealBuf));
							break;
						case CMP4TAGATOM_ARTIST: // 艺术家
							info.setArtist(new String(pRealBuf));
							break;
						case CMP4TAGATOM_NAME: // 名称
							info.setTitle(new String(pRealBuf));
							break;
						case CMP4TAGATOM_DATE: // 日期
							info.setYear(new String(pRealBuf));
							break;
						case CMP4TAGATOM_GENRE: // 流派
							info.setGenre(new String(pRealBuf));
							break;
						case CMP4TAGATOM_TRACK: // 音轨序号
							StringBuffer sb = new StringBuffer();
							sb.append(pRealBuf[0]).append(",");
							sb.append(pRealBuf[1]).append(",");
							sb.append(pRealBuf[2]).append(",");
							sb.append(pRealBuf[3]).append(",");
							sb.append(pRealBuf[4]).append(",");
							sb.append(pRealBuf[5]).append(",");
							sb.append(pRealBuf[6]).append(",");
							sb.append(pRealBuf[7]);
							// 前四个字节记录音轨序号,后四个字节记录专辑音轨总数
							int trkn = (pRealBuf[3] & 0xff) | ((pRealBuf[2] << 8) & 0xff00)
									| ((pRealBuf[1] << 16) & 0xff0000) | (pRealBuf[0] << 24);
							/*
							 * 专辑音轨总数 int trak = (pRealBuf[7] & 0xff) | ((pRealBuf[6] << 8) & 0xff00) |
							 * ((pRealBuf[5] << 24) >>> 8) | (pRealBuf[4] << 24);
							 */
							info.setTrack(trkn + "");
							break;
						default:
							break;
						}
						continue;// 实际数据读取完成后，文件指针位于下一个ATOM的开始处
					} else {// 格式不对，移动文件指针到下一个ATOM开始的位置，基本不会发生这种情况
						f.skipBytes(tagSize - 8 - 8);
					}
				} else { // 非解析ATOM，移动文件指针到下一个ATOM的开始位置
					f.skipBytes(tagSize - 8);
				}
			} while (true);
			f.close();
			return info;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
