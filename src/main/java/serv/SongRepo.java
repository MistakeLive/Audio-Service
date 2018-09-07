package serv;

import org.springframework.data.repository.CrudRepository;
import serv.entity.Song;

import java.util.List;

public interface SongRepo extends CrudRepository<Song, Long> {

    List<Song> findAll();
    Song findById(String id);
}
