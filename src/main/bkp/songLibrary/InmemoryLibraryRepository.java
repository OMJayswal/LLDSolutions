import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class InmemoryLibraryRepository implements LibraryRepository{

   private AtomicInteger libraryId = new AtomicInteger(0);
   private Map<String,Library> libraryMap = new HashMap<>();

   public Library getLibrary(String id){
       return libraryMap.getOrDefault(id,null);
   }
   public Library addLibrary(Library library) {
        String id = String.valueOf(libraryId.getAndAdd(1));
        library.setId(id);
        libraryMap.put(id, library);
        return library;
   }
}
