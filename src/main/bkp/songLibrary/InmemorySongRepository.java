import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class InmemorySongRepository implements SongRepository{

   private AtomicInteger songId = new AtomicInteger(0);
   private Map<String,Song> songMap = new HashMap<>();

   public Song addSong(Song song){
       String sId = String.valueOf(songId.addAndGet(1));
       song.setSongId(sId);
       songMap.put(sId,song);
       return song;
   }
   public Song getSong(String songId) {
       return songMap.getOrDefault(songId, null);
   }
}
