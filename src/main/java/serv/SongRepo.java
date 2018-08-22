package serv;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import serv.Entity.Song;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "Songs", path = "Songs")
public interface SongRepo extends PagingAndSortingRepository <Song, Long> {

    Song findBysongName (String name);
    List<Song> findAll();
}
