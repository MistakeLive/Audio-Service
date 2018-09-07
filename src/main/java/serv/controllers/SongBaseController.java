package serv.controllers;


import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import serv.SongInfoRepo;
import serv.SongRepo;
import serv.entity.Song;
import serv.entity.SongInfo;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;


@RestController
public class SongBaseController {

    private final SongRepo songRepo;
    private final SongInfoRepo songInfoRepo;

    @Autowired
    public SongBaseController(SongRepo songRepo, SongInfoRepo songInfoRepo) {
        this.songRepo = songRepo;
        this.songInfoRepo = songInfoRepo;
    }

    @RequestMapping(value = "songs", method = RequestMethod.GET)
    public List<String> returningSongsList() {
        List<Song> list = songRepo.findAll();
        List songList = new ArrayList();

        for (int i = 0; i < list.size(); i++) {
            songList.add(list.get(i).getSongInfo().getSongName());
        }
        return songList;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public String addSongtoBase(@RequestParam("file") MultipartFile attachFileObj) throws Exception{
        String ID = "";
        if ((attachFileObj != null)) {
            if (!attachFileObj.getOriginalFilename().equals("")) {

                Header h= null;
                FileInputStream file = null;

                File tmpFile = File.createTempFile("temp" , "mp3" );
                attachFileObj.transferTo(tmpFile);

                file = new FileInputStream(tmpFile);
                Bitstream bitstream = new Bitstream(file);

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
                songInfo.setSongAuthor("author");

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
    public List getSongsAndDurationList() {

        List<SongInfo> list;
        List songList =  new ArrayList();
        list = songInfoRepo.findAll();

        for (int i = 0; i < list.size(); i++) {
            List tempList = new ArrayList();
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
}