package deng.music;

import java.io.File;
import java.io.IOException;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import deng.pojo.MusicInfo;

public class MusicFile extends File {

	/**
	 * 
	 */
	private static final long serialVersionUID = 970152225586208967L;

	public MusicFile(String path) {
		super(path);
		// TODO Auto-generated constructor stub
	}

	public MusicInfo getMusicInfo() {
		return getAudioTag();
	}

	private MusicInfo getAudioTag() {
		try {
			MusicInfo info = new MusicInfo();
			AudioFile af = AudioFileIO.read(this);
			Tag tag = af.getTag();
			info.setAlbum(tag.getFirst(FieldKey.ALBUM));
			info.setArtist(tag.getFirst(FieldKey.ARTIST));
			info.setGenre(tag.getFirst(FieldKey.GENRE));
			info.setTitle(tag.getFirst(FieldKey.TITLE));
			info.setTrackn(tag.getFirst(FieldKey.TRACK));
			info.setYear(tag.getFirst(FieldKey.YEAR));
			info.setTrack(tag.getFirst(FieldKey.TRACK_TOTAL));
			return info;
		} catch (CannotReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (TagException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ReadOnlyFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (InvalidAudioFrameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
