import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Sport {
    private String name;
    private String id;
    private List<String> requiredPlayerAttributes = new ArrayList<>();
    private List<String> requiredTeamAttributes = new ArrayList<>();

    public Sport(String name,List<String> requiredPlayerAttributes,List<String> requiredTeamAttributes){
        this.name = name;
        this.requiredPlayerAttributes = requiredPlayerAttributes;
        this.requiredTeamAttributes  = requiredTeamAttributes;
    }

}
