import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class Team {
    private String name;
    private String sportId;
    private String id;
    private Map<String,Object> teamAttributes = new HashMap<>();
    public Team(String name,String sportId){
        this.name = name;
        this.sportId = sportId;
    }
}
