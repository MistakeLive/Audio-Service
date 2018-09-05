package serv.entity;

import javax.persistence.*;

@Entity
@Table(name = "songList")
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String songName;
    private byte[] songFile;
    private String duration;

    public Song() {

    }

    public Song(String songName, byte[] songFile) {
        this.songName = songName;
        this.songFile = songFile;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public byte[] getSongFile() {
        return songFile;
    }

    public void setSongFile(byte[] songFile) {
        this.songFile = songFile;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
