import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryTeamRepository implements TeamReposiotry {
    AtomicInteger tId = new AtomicInteger(0);
    private Map<String,Team> teams = new HashMap<>();
    public Team getTeam(String teamId){
        return teams.getOrDefault(teamId,null);
    }
    public Team addTeam(Team team){
        team.setId(String.valueOf(tId.addAndGet(1)));
        teams.put(team.getId(),team);
        return team;
    }
}
