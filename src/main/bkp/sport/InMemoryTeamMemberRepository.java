import java.util.*;

public class InMemoryTeamMemberRepository implements TeamMemberRepository{
    private Map<String, Set<String>> teamMembers = new HashMap<>();
    public void addTeamMember(String teamId,String playerId){
        teamMembers.computeIfAbsent(teamId,k-> new HashSet<>()).add(playerId);
    }
    public List<String> getPlayers(String teamId){
       return new ArrayList<>(teamMembers.getOrDefault(teamId,new HashSet<>()));
    }
}
