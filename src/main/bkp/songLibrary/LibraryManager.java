import java.util.ArrayList;
import java.util.List;

public class LibraryManager {

    private SongRepository songRepository;
    private LibraryRepository libraryRepository;
    private LibrarySongRepository librarySongRepository;

    public LibraryManager(SongRepository songRepository, LibraryRepository libraryRepository, LibrarySongRepository librarySongRepository) {
        this.songRepository = songRepository;
        this.libraryRepository = libraryRepository;
        this.librarySongRepository = librarySongRepository;
    }

    public Library createLibrary(String name){
        return libraryRepository.addLibrary(new Library(name));
    }

    public Song createSong(String name,String artist){
        return songRepository.addSong(new Song(name,artist));
    }

    public void addSongToLibrary(String songId,String libraryId){
        librarySongRepository.addSong(songId,libraryId);
    }

    public List<Song> getLibrarySong(String libraryId){
        List<String> songs = librarySongRepository.getSongs(libraryId);
        List<Song> songList = new ArrayList<>();
        for(String s: songs){
            songList.add(songRepository.getSong(s));
        }
        return songList;
    }


}
