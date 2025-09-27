import java.util.HashMap;
import java.util.Map;

public class Main{

    public static void main(String args[]) throws MissingAttributeException {
        PlayerRepository playerRepository = new InMemoryPlayerRepository();
        TeamReposiotry teamReposiotry = new InMemoryTeamRepository();
        TeamMemberRepository teamMemberRepository = new InMemoryTeamMemberRepository();
        SportRepository sportRepository = new InMemorySportRepository();

        Map<String,Object> playerAttributes = new HashMap<>();
        playerAttributes.put("handed","left-handed");
        playerAttributes.put("type","all-rounder");

        Map<String,Object> teamAttributes = new HashMap<>();
        teamAttributes.put("players",11);
        teamAttributes.put("type","outdoor");

        TeamManager teamManager = new TeamManager(sportRepository,teamReposiotry,teamMemberRepository,playerRepository);
        Sport sport = teamManager.createSport("Cricket",playerAttributes.keySet().stream().toList(),teamAttributes.keySet().stream().toList());

        Player yuvRaj = teamManager.createPlayer("Yuvraj",playerAttributes);
        Team mumbaiIndians = teamManager.createTeam("Mumbai Indians",sport.getId());
        teamManager.addPlayerToTeam(yuvRaj,mumbaiIndians);
        Player dhoni = teamManager.createPlayer("Dhoni",playerAttributes);
        teamManager.addPlayerToTeam(dhoni,mumbaiIndians);

        System.out.println(teamManager.getAllPlayers(mumbaiIndians.getId()).stream().map(Player::getName).toList());

    }
}
