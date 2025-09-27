import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class Player {
    private String name;
    private String id;
    private Map<String,Object> attributes;
    public Player(String name){
        this.name = name;
    }

    public Player(String name, Map<String,Object> attributes){
        this.name = name;
        this.attributes = attributes;
    }
}
