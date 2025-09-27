import java.util.List;
import java.util.Random;

public class RandomPlayer implements  Player{
    public RandomPlayer(){
    }
    public void play(List<Song> songs){
      Random random = new Random();
      Song song = songs.get(random.nextInt(songs.size()));
      song.play();
    }
}
