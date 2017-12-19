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

	private static final int CMP4TAGATOM_ERROR = 0; // ��ʼ��ֵ
	private static final int CMP4TAGATOM_ALBUM = 1; // ר��
	private static final int CMP4TAGATOM_ARTIST = 2; // ������
	private static final int CMP4TAGATOM_NAME = 3; // ����
	private static final int CMP4TAGATOM_DATE = 4; // ����
	private static final int CMP4TAGATOM_GENRE = 5; // ����
	private static final int CMP4TAGATOM_COVER = 6; // ����
	private static final int CMP4TAGATOM_TRACK = 7; // �������

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

				String ignores[] = { "ftyp", "mvhd", "trak" };// ����Ҫ�����ı�ǣ�����������
				if (isInArray(ignores, tag)) {
					f.skipBytes(tagSize - 8);
					continue;
				}
				String parents[] = { "moov", "udta", "ilst" };
				if (isInArray(parents, tag)) {// ֻ�������
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
				if (cBuf[0] == (byte) 0xA9) {// ����ר���������ҡ����ơ�������ڣ���Щ��һ���ֽ�ֵΪ0xA9
					if (cBuf[1] == 'a' && cBuf[2] == 'l' && cBuf[3] == 'b') {
						currentAtom = CMP4TAGATOM_ALBUM;
					} else if (cBuf[1] == 'A' && cBuf[2] == 'R' && cBuf[3] == 'T') {
						currentAtom = CMP4TAGATOM_ARTIST;
					} else if (cBuf[1] == 'n' && cBuf[2] == 'a' && cBuf[3] == 'm') {
						currentAtom = CMP4TAGATOM_NAME;
					} else if (cBuf[1] == 'd' && cBuf[2] == 'a' && cBuf[3] == 'y') {
						currentAtom = CMP4TAGATOM_DATE;
					}
				} else if ("gnre".equals(tag)) { // ��������
					currentAtom = CMP4TAGATOM_GENRE;
				} else if ("covr".equals(tag)) { // ��������ͼƬ
					currentAtom = CMP4TAGATOM_COVER;
				} else if ("trkn".equals(tag)) { //
					currentAtom = CMP4TAGATOM_TRACK;
				}

				if (currentAtom != CMP4TAGATOM_ERROR) {
					lenSize = f.readInt();
					f.readFully(cBuf);

					lRealBytes = lenSize - 16;// ����ʵ�����ݳ���
					// �жϳ��ȼ���ʶ���Ƿ���ȷ
					if (lenSize + 8 == tagSize && cBuf[0] == 'd' && cBuf[1] == 'a' && cBuf[2] == 't' && cBuf[3] == 'a'
							&& lRealBytes > 0) {
						f.skipBytes(8);// ��ǰ�ļ�ָ��λ��ver��ʼ��������ƶ�8���ֽڵ�ʵ�����ݴ�
						byte[] pRealBuf = new byte[lRealBytes];
						f.readFully(pRealBuf);// ��ȡʵ������
						// ����ATOM���ͽ���ʵ�ʶ�ȡ������
						switch (currentAtom) {
						case CMP4TAGATOM_ALBUM: // ר��
							info.setAlbum(new String(pRealBuf));
							break;
						case CMP4TAGATOM_ARTIST: // ������
							info.setArtist(new String(pRealBuf));
							break;
						case CMP4TAGATOM_NAME: // ����
							info.setTitle(new String(pRealBuf));
							break;
						case CMP4TAGATOM_DATE: // ����
							info.setYear(new String(pRealBuf));
							break;
						case CMP4TAGATOM_GENRE: // ����
							info.setGenre(new String(pRealBuf));
							break;
						case CMP4TAGATOM_TRACK: // �������
							StringBuffer sb = new StringBuffer();
							sb.append(pRealBuf[0]).append(",");
							sb.append(pRealBuf[1]).append(",");
							sb.append(pRealBuf[2]).append(",");
							sb.append(pRealBuf[3]).append(",");
							sb.append(pRealBuf[4]).append(",");
							sb.append(pRealBuf[5]).append(",");
							sb.append(pRealBuf[6]).append(",");
							sb.append(pRealBuf[7]);
							// ǰ�ĸ��ֽڼ�¼�������,���ĸ��ֽڼ�¼ר����������
							int trkn = (pRealBuf[3] & 0xff) | ((pRealBuf[2] << 8) & 0xff00)
									| ((pRealBuf[1] << 16) & 0xff0000) | (pRealBuf[0] << 24);
							/*
							 * ר���������� int trak = (pRealBuf[7] & 0xff) | ((pRealBuf[6] << 8) & 0xff00) |
							 * ((pRealBuf[5] << 24) >>> 8) | (pRealBuf[4] << 24);
							 */
							info.setTrack(trkn + "");
							break;
						default:
							break;
						}
						continue;// ʵ�����ݶ�ȡ��ɺ��ļ�ָ��λ����һ��ATOM�Ŀ�ʼ��
					} else {// ��ʽ���ԣ��ƶ��ļ�ָ�뵽��һ��ATOM��ʼ��λ�ã��������ᷢ���������
						f.skipBytes(tagSize - 8 - 8);
					}
				} else { // �ǽ���ATOM���ƶ��ļ�ָ�뵽��һ��ATOM�Ŀ�ʼλ��
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
