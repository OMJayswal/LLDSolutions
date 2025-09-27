import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Song {
    private String songId;
    private String name;
    private String artist;

    public Song(String name, String artist) {
        this.name = name;
        this.artist = artist;
    }

    public void play(){
        System.out.println("Playing song :"+songId+" artist: "+artist+" name: "+name);
    }
}
