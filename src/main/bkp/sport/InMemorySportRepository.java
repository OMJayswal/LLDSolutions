import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemorySportRepository implements SportRepository{
    AtomicInteger sId = new AtomicInteger(0);
    private Map<String,Sport> sports = new HashMap<>();
    public Sport getSport(String sportId){
        return sports.getOrDefault(sportId,null);
    }
    public Sport addSport(Sport sport){
        sport.setId(String.valueOf(sId.addAndGet(1)));
        sports.put(sport.getId(),sport);
        return sport;
    }

}
