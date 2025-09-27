import java.util.*;

public class TeamManager {
    private TeamReposiotry teamReposiotry;
    private TeamMemberRepository teamMemberRepository;
    private PlayerRepository playerRepository;
    private SportRepository sportRepository;

    public TeamManager(SportRepository sportRepository,TeamReposiotry teamReposiotry, TeamMemberRepository teamMemberRepository, PlayerRepository playerRepository) {
        this.sportRepository = sportRepository;
        this.teamReposiotry = teamReposiotry;
        this.teamMemberRepository = teamMemberRepository;
        this.playerRepository = playerRepository;
    }

    public Player createPlayer(String name,Map<String,Object> attributes){
        Player player = new Player(name,attributes);
        return playerRepository.addPlayer(player);
    }

    public Team createTeam(String name,String sportId) throws MissingAttributeException{
        Team team = new Team(name,sportId);
        Sport sport = sportRepository.getSport(sportId);
        if(!validateTeamAttributes(sport,team)){
            throw new MissingAttributeException("Sports Attributes missing");
        }
        return teamReposiotry.addTeam(team);
    }

    public void addPlayerToTeam(Player player,Team team) throws MissingAttributeException{
        if(!validatePlayerAttributes(sportRepository.getSport(team.getSportId()),player)){
            throw new MissingAttributeException("Player Attributes missing");
        }
        teamMemberRepository.addTeamMember(team.getId(),player.getId());
    }

    private boolean validatePlayerAttributes(Sport sport,Player player){
        return new HashSet<>(sport.getRequiredPlayerAttributes()).containsAll(player.getAttributes().keySet());
    }

    private boolean validateTeamAttributes(Sport sport,Team team){
        return new HashSet<>(sport.getRequiredTeamAttributes()).containsAll(team.getTeamAttributes().keySet());
    }

    public List<Player> getAllPlayers(String teamId){
        List<String> teamPlayers = teamMemberRepository.getPlayers(teamId);
        List<Player> players = new ArrayList<>();
        for(String sId: teamPlayers){
            players.add(playerRepository.getPlayer(sId));
        }
        return players;
    }

    public Sport createSport(String name, List<String> requiredPlayerAttributes,
                             List<String> requiredTeamAttributes){
        Sport sport = new Sport(name,requiredPlayerAttributes,requiredTeamAttributes);
        return sportRepository.addSport(sport);
    }
}
