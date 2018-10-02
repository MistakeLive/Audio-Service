package serv.controllers;


import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import serv.entity.Playlist;
import serv.entity.Song;
import serv.entity.SongInfo;
import serv.entity.User;
import serv.repos.PlaylistRepo;
import serv.repos.SongInfoRepo;
import serv.repos.SongRepo;
import serv.repos.UserRepo;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;


@RestController
public class SongBaseController {

    private final SongRepo songRepo;
    private final SongInfoRepo songInfoRepo;
    private final PlaylistRepo playlistRepo;
    private final UserRepo userRepo;

    @Autowired
    public SongBaseController(SongRepo songRepo, SongInfoRepo songInfoRepo, PlaylistRepo playlistRepo, UserRepo userRepo) {
        this.songRepo = songRepo;
        this.songInfoRepo = songInfoRepo;
        this.playlistRepo = playlistRepo;
        this.userRepo = userRepo;
    }

    @RequestMapping(value = "songs", method = RequestMethod.GET)
    public List<String> getSongsList() {
        List<Song> list = songRepo.findAll();
        List<String> songList = new ArrayList();

        for (int i = 0; i < list.size(); i++) {
            songList.add(list.get(i).getSongInfo().getSongName());
        }
        return songList;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addSongToBase(@RequestParam("file") MultipartFile attachFileObj) throws Exception{
        String ID = "";
        if ((attachFileObj != null)) {
            if (!attachFileObj.getOriginalFilename().equals("")) {

                Header h = null;
                FileInputStream file = null;

                File tmpFile = File.createTempFile("temp" , "mp3" );
                attachFileObj.transferTo(tmpFile);

                file = new FileInputStream(tmpFile);
                Bitstream bitstream = new Bitstream(file);

                String author = "";

                Mp3File mp3File = new Mp3File(tmpFile);
                ID3v2 id3v2Tag = mp3File.getId3v2Tag();
                author = id3v2Tag.getArtist();

                h = bitstream.readFrame();

                int size = h.calculate_framesize();
                float ms_per_frame = h.ms_per_frame();
                int maxSize = h.max_number_of_frames(10000);
                float t = h.total_ms(size);
                long tn = 0;

                tn = file.getChannel().size();

                int min = h.min_number_of_frames(500);
                int sl = (int) h.total_ms((int) tn)/1000;
                String songLength = Integer.toString(sl / 60) + ':';
                if (sl%60 < 10) songLength+= '0';
                songLength += Integer.toString(sl%60);
                tmpFile.deleteOnExit();

                Song song = new Song();
                SongInfo songInfo = new SongInfo();

                songInfo.setSongName(attachFileObj.getOriginalFilename());
                songInfo.setSongDuration(songLength);
                songInfo.setSongAuthor(author);


                song.setSongFile(attachFileObj.getBytes());
                song.setSongInfo(songInfo);
                song.getSongInfo().setSong(song);
                songRepo.save(song);
                ID = song.getId();
            }
            System.out.println("File Is Successfully Uploaded & Saved In The Database.... Hurrey!\n");
        }
        return ID;
    }

    @RequestMapping(value = "songs/{id}", method = RequestMethod.GET)
    public byte[] getSong(@PathVariable String id) {
        return songRepo.findById(id).getSongFile();
    }

    @RequestMapping(value = "songsAndDur", method = RequestMethod.GET)
    public List<List<String>> getSongsInfo() {

        List<SongInfo> list;
        List<List<String>> songList =  new ArrayList();
        list = songInfoRepo.findAll();

        for (int i = 0; i < list.size(); i++) {
            List<String> tempList = new ArrayList();
            tempList.add(list.get(i).getSongName());
            tempList.add(list.get(i).getSongAuthor());
            tempList.add(list.get(i).getSongDuration());
            tempList.add(list.get(i).getSong().getId());
            songList.add(tempList);
        }
        return songList;
    }

    @RequestMapping(value = "info/{id}", method = RequestMethod.GET)
    public List<String> getSongInfoById(@PathVariable String id) {

        SongInfo songInfo = songRepo.findById(id).getSongInfo();
        List<String> tempList = new ArrayList<String>();
        tempList.add(songInfo.getSongName());
        tempList.add(songInfo.getSongAuthor());
        tempList.add(songInfo.getSongDuration());
        return tempList;
    }

    @RequestMapping(value = "playlists", method = RequestMethod.GET)
    public List getPlaylistList(){
        List<Playlist> playlists = playlistRepo.findAll();
        List res = new ArrayList();
        for (int i=0; i < playlists.size(); i++) {
            List tempList = new ArrayList();
            tempList.add(playlists.get(i).getPlaylistId());
            tempList.add(playlists.get(i).getSongId());
            tempList.add(playlists.get(i).getUserId());
            tempList.add(playlists.get(i).getPlaylistName());
            res.add(tempList);
        }
        return res;
    }

    @RequestMapping(value = "userPlaylists", method = RequestMethod.GET)
    public List getUserPlaylistList(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepo.findByUsername(username);
        Long id = user.getId();

        List<Playlist> playlists = playlistRepo.findByUserId(id);
        List res = new ArrayList();
        for (int i=0; i < playlists.size(); i++) {
            List tempList = new ArrayList();
            tempList.add(playlists.get(i).getPlaylistId());
            tempList.add(playlists.get(i).getSongId());
            tempList.add(playlists.get(i).getUserId());
            tempList.add(playlists.get(i).getPlaylistName());
            res.add(tempList);
        }
        return res;
    }

    @RequestMapping(value = "playlists/{id}", method = RequestMethod.GET)
    public List getPlaylistByid(@RequestParam Integer id){

        Playlist playlist = playlistRepo.findById(id);

            List tempList = new ArrayList();
            tempList.add(playlist.getPlaylistId());
            tempList.add(playlist.getSongId());
            tempList.add(playlist.getUserId());
            tempList.add(playlist.getPlaylistName());
        return tempList;
    }

    @RequestMapping(value = "/addPlaylist", method = RequestMethod.POST)
    public List addPlaylist(@RequestParam String name){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepo.findByUsername(username);
        Long id = user.getId();

        Playlist playlist = new Playlist(id, name);

        playlistRepo.save(playlist);

        List list = new ArrayList();
        list.add(playlist.getPlaylistId());
        list.add(playlist.getSongs());
        list.add(playlist.getUserId());
        list.add(playlist.getPlaylistName());

        return list;
    }

    @RequestMapping(value = "addSongToPlaylist", method = RequestMethod.POST)
    public void addSongToPlaylist ( @RequestParam String songId, @RequestParam Integer playlistId){
        Playlist playlist = playlistRepo.findById(playlistId);
        Song song = songRepo.findById(songId);
        playlist.addSong(song);
        playlistRepo.save(playlist);
    }

    @RequestMapping(value = "/deleteSongFromPlaylist", method = RequestMethod.POST)
    public void deleteSongFromPlaylist ( @RequestParam String songId, @RequestParam Integer playlistId){
        Playlist playlist = playlistRepo.findById(playlistId);
        Song song = songRepo.findById(songId);
        playlist.deleteSong(song);
        playlistRepo.save(playlist);
    }
}