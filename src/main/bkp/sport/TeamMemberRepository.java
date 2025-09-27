import java.util.List;

public interface TeamMemberRepository {
    public void addTeamMember(String teamId,String playerId);
    public List<String> getPlayers(String teamId);
}
