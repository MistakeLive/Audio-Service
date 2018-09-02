package serv.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import serv.SongRepo;
import serv.entity.Song;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

@RestController
public class SongBaseController {

    private final SongRepo songRepo;

    @Autowired
    public SongBaseController(SongRepo songRepo) {
        this.songRepo = songRepo;
    }

    @RequestMapping(value = "songs", method = RequestMethod.GET)
    public Vector<String> list() {
        List<Song> list = songRepo.findAll();
        Vector v = new Vector();

        for (int i = 0; i < list.size(); i++) {
            v.add(list.get(i).getSongName());
        }
        return v;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public void addSongtoBase(@RequestParam("file") MultipartFile attachFileObj) throws IOException {
        if ((attachFileObj != null)) {
            if (!attachFileObj.getOriginalFilename().equals("")) {
                Song song = new Song();

                song.setSongName(attachFileObj.getOriginalFilename());
                song.setSongFile(attachFileObj.getBytes());

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
    public Vector list1() {
        List<Song> list = songRepo.findAll();

        Vector v = new Vector();

        for (int i = 0; i < list.size(); i++) {
            Vector v1 = new Vector();
            v1.add(list.get(i).getSongName());
            v1.add(list.get(i).getId());
            v.add(v1);
        }
        return v;
    }
}