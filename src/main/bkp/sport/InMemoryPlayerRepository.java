import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryPlayerRepository implements PlayerRepository{
    AtomicInteger pId = new AtomicInteger(0);
    private Map<String,Player> players = new HashMap<>();
    public Player getPlayer(String playerId){
        return players.getOrDefault(playerId,null);
    }
    public Player addPlayer(Player player){
        player.setId(String.valueOf(pId.addAndGet(1)));
        players.put(player.getId(),player);
        return player;
    }

}
