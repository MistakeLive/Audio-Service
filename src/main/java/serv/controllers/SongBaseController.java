package serv.controllers;


import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import serv.SongRepo;
import serv.entity.Song;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;



@RestController
public class SongBaseController {

    private final SongRepo songRepo;

    @Autowired
    public SongBaseController(SongRepo songRepo) {
        this.songRepo = songRepo;
    }

    @RequestMapping(value = "songs", method = RequestMethod.GET)
    public List<String> returningSongsList() {
        List<Song> list = songRepo.findAll();
        List songList = new ArrayList();

        for (int i = 0; i < list.size(); i++) {
            songList.add(list.get(i).getSongName());
        }
        return songList;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public void addSongtoBase(@RequestParam("file") MultipartFile attachFileObj) throws Exception{
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
                String songLength = Integer.toString(sl / 60) + ':' + Integer.toString(sl%60);
                tmpFile.deleteOnExit();

                Song song = new Song();

                song.setSongName(attachFileObj.getOriginalFilename());
                song.setSongFile(attachFileObj.getBytes());
                song.setDuration(songLength);
                songRepo.save(song);
            }
            System.out.println("File Is Successfully Uploaded & Saved In The Database.... Hurrey!\n");
        }
    }

    @RequestMapping(value = "songs/{name}", method = RequestMethod.GET)
    public byte[] getSong(@PathVariable String name) {

        return songRepo.findBysongName(name).get(0).getSongFile();
    }

    @RequestMapping(value = "songsAndDur", method = RequestMethod.GET)
    public List returningSongsAndDurationList() {
        List<Song> list = songRepo.findAll();

        List songList = new ArrayList();

        for (int i = 0; i < list.size(); i++) {
            List tempList = new ArrayList();
            tempList.add(list.get(i).getSongName());
            tempList.add(list.get(i).getDuration());
            songList.add(tempList);
        }
        return songList;
    }
}