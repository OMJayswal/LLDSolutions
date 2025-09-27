import java.util.List;

public interface LibrarySongRepository {
    public void addSong(String songId,String libraryId);
    public List<String> getSongs(String libraryId);
}
