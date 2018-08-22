package serv;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import serv.Entity.Song;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

@RestController
public class SongBaseController {

    private final SongRepo songRepo;
    static Song song;

    @Autowired
    public SongBaseController(SongRepo songRepo){
        this.songRepo=songRepo;
    }

    @RequestMapping("songs")
    public Vector<String> list(){
        List<Song>  list = songRepo.findAll();
        Vector v = new Vector();

        for (int i=0; i < list.size(); i++){
            v.add(list.get(i).getSongName());
        }
        return v;
    }

    @PostMapping
    public void addSongtoBase(@RequestParam("file") MultipartFile attachFileObj) throws IOException {
        if ((attachFileObj != null)) {
                    if (!attachFileObj.getOriginalFilename().equals("")) {
                        song = new Song();

                        song.setSongName(attachFileObj.getOriginalFilename());
                        song.setSongFile(attachFileObj.getBytes());

                        songRepo.save(song);
                }
                System.out.println("File Is Successfully Uploaded & Saved In The Database.... Hurrey!\n");
            }
    }

    @GetMapping("songs/{name}")
    public byte[] getSong (@PathVariable String name){
        return songRepo.findBysongName(name).getSongFile();
    }

}
