import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
class Roll{
   private final int pinsKnocked;
   public Roll(int pinsKnocked){
       this.pinsKnocked = pinsKnocked;
   }
}

class Frame{
    @Getter
    private final List<Roll> rolls = new ArrayList<>();
    @Getter
    private final boolean isTenthFrame;
    public Frame(boolean isTenthFrame){
        this.isTenthFrame = isTenthFrame;
    }
    public void addRoll(Roll roll){
        this.rolls.add(roll);
    }
    public boolean isStrike(){
        return !rolls.isEmpty() && rolls.get(0).getPinsKnocked()==10;
    }

    public boolean isSpare(){
        return rolls.size()>=2 && (rolls.get(0).getPinsKnocked()+rolls.get(1).getPinsKnocked() == 10) && !isStrike();
    }

    public boolean isComplete(){
        if(isTenthFrame){
            if(rolls.size() == 3){
                return true;
            }
            if(rolls.size() == 2){
                return !isSpare() && !isStrike();
            }
            return false;
        }
        if(isStrike())
            return true;
        return rolls.size()==2;
    }

    public int getBaseScore(){
        int score = 0;
        for(Roll roll:rolls){
            score = score+roll.getPinsKnocked();
        }
        return score;
    }
}

class Player{
    @Getter
    private final String name;
    @Getter
    private final List<Frame> frames = new ArrayList<>();
    public Player(String name){
        this.name = name;
        for(int i=0;i<9;i++){
            frames.add(new Frame(false));
        }
        frames.add(new Frame(true));
    }
    public void addFrame(Frame frame){
        this.frames.add(frame);
    }

    public int getTotalScore(){
        int totalScore = 0;
        for(Frame frame:frames){
            totalScore+=frame.getBaseScore();
            if(frame.isStrike()){
                totalScore = totalScore+10;
            }else if(frame.isSpare()){
                totalScore = totalScore+5;
            }
        }
        return totalScore;
    }

}

class Game{
    private int laneId;
    private final List<Player> players = new ArrayList<>();
    public Game(int laneId,List<Player> players){
        this.laneId = laneId;
        this.players.addAll(players);
    }

    public void playGame(){
      Random random = new Random();
      for(int i=0;i<10;i++){
          for(Player p:players){
              Frame f = p.getFrames().get(i);
              int pinsLeft = 10;
              while(!f.isComplete()) {
                  Roll roll = new Roll(random.nextInt(0, pinsLeft+1));
                  f.addRoll(roll);
                  pinsLeft = pinsLeft-roll.getPinsKnocked();
                  if(pinsLeft==0)
                      pinsLeft=10;
              }
          }
      }
    }

    public void printScoreBoard(){
        for(Player p:players){
            System.out.println(p.getName()+" score:"+p.getTotalScore());
        }
    }

    public void declareWinner(){
        int maxScore = Integer.MIN_VALUE;
        Player winner = null;
        for(Player p:players){
            int score = p.getTotalScore();
            if(score >maxScore){
                winner=p;
                maxScore = score;
            }
        }
        assert winner != null;
        System.out.println("Winner is: "+winner.getName()+" : "+maxScore);
    }
}

class BowlingAlley {
    private final List<Game> games = new ArrayList<>();
    public void playGame(Game game){
        games.add(game);
        game.playGame();
        game.printScoreBoard();
        game.declareWinner();
    }
}

public class Main {

    public static void main(String[] args){
        List<Player> players = new ArrayList<>();
        players.add(new Player("Alice"));
        players.add(new Player("Bob"));
        Game game = new Game(1,players);

        BowlingAlley bowlingAlley = new BowlingAlley();
        bowlingAlley.playGame(game);

        List<Player> players2 = new ArrayList<>();
        players2.add(new Player("Tom"));
        players2.add(new Player("Dick"));
        players2.add(new Player("Harry"));
        Game game2 = new Game(1,players2);
        game2.playGame();

        bowlingAlley.playGame(game2);

    }

}
