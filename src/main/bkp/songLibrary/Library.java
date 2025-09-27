import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Library {
    private String id;
    private String name;
    public Library(String name){
        this.name = name;
    }
}
