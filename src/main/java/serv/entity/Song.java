package serv.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "song")
public class Song {

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String id;
    private byte[] songFile;

    @OneToOne(fetch = FetchType.LAZY,
            cascade =  CascadeType.ALL,
            mappedBy = "song")
    private SongInfo songInfo;

    @ManyToMany(mappedBy = "songs")
    private Set<Playlist>  playlists;

    public Set<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(Set<Playlist> playlists) {
        this.playlists = playlists;
    }

    public Song() {
    }

    public Song(byte[] songFile, SongInfo songInfo) {
        this.songFile = songFile;
        this.songInfo = songInfo;
        this.id = songInfo.id;
    }


    public String getId() {
        return id;
    }

    public void setId(String  id) {
        this.id = id;
    }

    public byte[] getSongFile() {
        return songFile;
    }

    public void setSongFile(byte[] songFile) {
        this.songFile = songFile;
    }

    public SongInfo getSongInfo() {
        return songInfo;
    }

    public void setSongInfo(SongInfo songInfo) {
        this.songInfo = songInfo;
    }
}
