package deng.pojo;

public class MusicInfo {
	private String album;
	private String artist;
	private String title;
	private String genre;
	private String year;
	private String track;

	public MusicInfo() {
		super();
	}

	public MusicInfo(String album, String artist, String title, String genre, String year, String track) {
		super();
		this.album = album;
		this.artist = artist;
		this.title = title;
		this.genre = genre;
		this.year = year;
		this.track = track;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getTrack() {
		return track;
	}

	public void setTrack(String track) {
		this.track = track;
	}

}