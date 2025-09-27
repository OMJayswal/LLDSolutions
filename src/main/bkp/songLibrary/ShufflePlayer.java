import java.util.List;

public class ShufflePlayer implements  Player{
    private static int counter = 0;
    public void play(List<Song> songList){
        int index = (counter++)%(songList.size());
        songList.get(index).play();
    }
}
