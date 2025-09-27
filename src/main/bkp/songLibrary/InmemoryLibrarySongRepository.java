import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class InmemoryLibrarySongRepository implements LibrarySongRepository{

   private AtomicInteger songId = new AtomicInteger(0);
   private Map<String,List<String>> librarySongMap = new HashMap<>();

   public void addSong(String songId,String libraryId){
       librarySongMap.computeIfAbsent(libraryId,k -> new ArrayList<>()).add(songId);
   }
   public List<String> getSongs(String libraryId) {
       return librarySongMap.getOrDefault(libraryId, new ArrayList<>());
   }
}
