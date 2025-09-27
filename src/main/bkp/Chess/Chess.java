package com.test;
import lombok.Data;
import lombok.Getter;

@Data
class Piece{
    private char color,type;
    Move[] moves;
    public Piece(char color, char type) {
        this.color = color;
        this.type = type;
    }
    public boolean canMove(Piece[][] board,int sx,int sy,int ex,int ey){
        for(Move move:moves){
            boolean canMove = move.canMove(board,sx,sy,ex,ey);
            if(canMove)
                return canMove;
        }
        return false;
    }
    public String getName(){
        return String.valueOf(this.getColor())+String.valueOf(this.getType());
    }
}

class Queen extends Piece{
    public Queen(char color,char type){
        super(color,type);
        super.moves = new Move[2];
        moves[0] = new StraighMove();
        moves[1] = new DiagonalMove();
    }
}

class King extends Piece{
    public King(char color,char type){
        super(color,type);
        super.moves = new Move[1];
        moves[0] = new KingMove();
    }
}

class Bishop extends Piece{
    public Bishop(char color,char type){
        super(color,type);
        super.moves = new Move[1];
        moves[0] = new DiagonalMove();
    }
}

class Rook extends Piece{
    public Rook(char color,char type){
        super(color,type);
        super.moves = new Move[1];
        moves[0] = new StraighMove();
    }
}


class Horse extends Piece{
    public Horse(char color,char type){
        super(color,type);
        super.moves = new Move[1];
        moves[0] = new HorseMove();
    }
}

class Pawn extends Piece{
    public Pawn(char color,char type){
        super(color,type);
        super.moves = new Move[1];
        moves[0] = new PawnMove();
    }
}

class ChessFactory{
    public static Piece createPiece(char color,char type){
        return switch (type) {
            case 'Q' -> new Queen(color, 'Q');
            case 'K' -> new King(color, 'K');
            case 'B' -> new Bishop(color, 'B');
            case 'R' -> new Rook(color, 'R');
            case 'H' -> new Horse(color, 'H');
            case 'P' -> new Pawn(color, 'P');
            default -> {
                System.out.println("Invalid Piece type: " + color + " :" + type);
                yield null;
            }
        };
    }
}

interface Move{
    public boolean canMove(Piece[][] board,int sx,int sy,int ex,int ey);
}

class DiagonalMove implements  Move{
    public boolean canMove(Piece[][] board,int sx,int sy,int ex,int ey){
        int xDelta = ex-sx;
        int yDelta = ey-sy;
        if(Math.abs(xDelta)!=Math.abs(yDelta))
            return false;
        int xMove = xDelta>0 ? 1 : -1;
        int yMove = yDelta>0 ? 1 : -1;
        while(sx+xMove>ex && sy+yMove> ey){
            if(board[sx+xMove][sy+yMove]!=null)
                return false;
            sx = sx+xMove;
            sy = sy+yMove;
        }
        return true;
    }
}

class StraighMove implements  Move{
    public boolean canMove(Piece[][] board,int sx,int sy,int ex,int ey){
        // implement
        int xDelta = ex-sx;
        int yDelta = ey-sy;
        int xMove,yMove;
        if(xDelta!=0 && yDelta!=0)
            return false;
        if(xDelta ==0){
            xMove = 0;
            yMove = yDelta > 0 ? 1:-1;
        }else{
            yMove = 0;
            xMove = xDelta > 0 ? 1:-1;
        }
        while(sx+xDelta>ex || sy+yDelta>ey){
            if(board[sx+xMove][sy+yMove]!=null)
                return false;
            sx = sx+xMove;
            sy = sy+yMove;
        }
        return true;
    }
}

class HorseMove implements  Move{
    public boolean canMove(Piece[][] board,int sx,int sy,int ex,int ey){
        // implement
        int xDelta = ex-sx;
        int yDelta = ey-sy;
        return (Math.abs(xDelta) == 2 && Math.abs(yDelta) == 1) || (Math.abs(xDelta) == 1 && Math.abs(yDelta) == 2);
    }
}

class KingMove implements  Move{
    public boolean canMove(Piece[][] board,int sx,int sy,int ex,int ey){
        int xDelta = ex-sx;
        int yDelta = ey-sy;
        if(Math.abs(xDelta) ==1 || Math.abs(yDelta) ==1){
            return true;
        }
        return false;
    }
}

class PawnMove implements  Move{
    public boolean canMove(Piece[][] board,int sx,int sy,int ex,int ey){
        Piece fPiece = board[sx][sy];
        int xDelta = ex-sx;
        int yDelta = ey-sy;
        if(fPiece.getColor() == 'B' && xDelta == -1){
            if(Math.abs(yDelta) == 1 && board[ex][ey]!=null && board[ex][ey].getColor()=='W'){
                return true;
            }
            return yDelta == 0 && board[ex][ey] == null;
        }else if(fPiece.getColor() == 'W' && xDelta == 1){
            if(Math.abs(yDelta) == 1 && board[ex][ey]!=null && board[ex][ey].getColor()=='B'){
                return true;
            }
            return yDelta == 0 && board[ex][ey] == null;
        }
        return false;
    }
}


class ChessGame{
   private Piece [][] board;
   int gameStatus;
   int nextTurn;
   int dimension;
   public void init(String[][] b) {
       int r = b.length;
       int c = b[0].length;
       this.dimension = r;
       board = new Piece[r][c];
       for (int i = 0; i < r; i++) {
           for (int j = 0; j < c; j++) {
               if (!b[i][j].isEmpty()) {
                   char color = b[i][j].charAt(0);
                   char type = b[i][j].charAt(1);
                   board[i][j] = ChessFactory.createPiece(color, type);
               }
           }
       }
   }

   public String move(int sx,int sy,int ex,int ey){
       String result = "invalid";
       if(gameStatus!=0)
           return result;
       if(isInBoundary(sx,sy,ex,ey) && !isInSamePosition(sx,sy,ex,ey)) {
           Piece piece = board[sx][sy];
           if(piece!=null && piece.canMove(board,sx,sy,ex,ey)){
               Piece destPiece = board[ex][ey];
               if(destPiece == null){
                   board[ex][ey] = piece;
                   board[sx][sy] = null;
                   setTurn();
                   result = "";
               }else if(isOpposite(destPiece,piece)){
                   board[ex][ey] = piece;
                   board[sx][sy] = null;
                   setGameStatus(destPiece);
                   setTurn();
                   result = destPiece.getName();
               }
           }
       }
       System.out.println("Move from: "+sx+","+sy+" to "+ex+","+ey+" resulted in: "+result);
       return result;
   }

   private void setGameStatus(Piece piece){
       if(piece.getType() == 'K'){
           this.gameStatus = piece.getColor() == 'B' ? 1 : 2;
           char winner =  piece.getColor() == 'B' ? 'W' : 'B';
           System.out.println("Game over: "+winner+" has won");
       }
   }

   private boolean isOpposite(Piece x,Piece y){
       return x.getColor()!=y.getColor();
   }

   private void setTurn(){
       if(this.gameStatus!=0){
           this.nextTurn = -1;
           return;
       }
       this.nextTurn = this.nextTurn == 0 ? 1  :0;
   }

   private boolean isInBoundary(int sx,int sy,int ex,int ey){
       return sx>=0 && sx<dimension && sy>=0 & sy<dimension && ex>=0 && ex<dimension && ey>=0 && ey<dimension;
   }

   private boolean isInSamePosition(int sx,int sy,int ex,int ey){
       return sx==ex && sy==ey;
   }

   public void getNextTurn(){
       System.out.println("Next turn: "+nextTurn);
   }

    public void getGameStatus(){
        System.out.println("Game status: "+gameStatus);
    }
}
public class Chess {
    public static void main(String []args){
      ChessGame chessGame = new ChessGame();
      String[][] board = {
                {"WR","WH","WB","WQ","WK","WB","WH","WR"},
                {"WP","WP","WP","WP","WP","WP","WP","WP"},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"","","","","","","",""},
                {"BP","BP","BP","BP","BP","BP","BP","BP"},
                {"BR","BH","BB","BQ","BK","BB","BH","BR"}
        };
        chessGame.init(board);
        chessGame.move(1, 5, 2, 5);
        chessGame.getNextTurn();
        chessGame.getGameStatus();

        chessGame.move(6, 6, 5, 6);
        chessGame.getNextTurn();
        chessGame.getGameStatus();

        chessGame.move(2, 5, 3, 5);
        chessGame.getNextTurn();
        chessGame.getGameStatus();

        chessGame.move(6, 2, 5, 2);
        chessGame.getNextTurn();
        chessGame.getGameStatus();

        chessGame.move(0, 1, 2, 2);
        chessGame.getNextTurn();
        chessGame.getGameStatus();

        chessGame.move(6, 4, 5, 4);
        chessGame.getNextTurn();
        chessGame.getGameStatus();

        chessGame.move(1, 7, 2, 7);
        chessGame.getNextTurn();
        chessGame.getGameStatus();

        chessGame.move(7, 6, 5, 7);
        chessGame.getNextTurn();
        chessGame.getGameStatus();

        chessGame.move(2, 2, 3, 4);
        chessGame.getNextTurn();
        chessGame.getGameStatus();

        chessGame.move(6, 5, 5, 5);
        chessGame.getNextTurn();
        chessGame.getGameStatus();

        chessGame.move(3, 4, 5, 5);
        chessGame.getNextTurn();
        chessGame.getGameStatus();

        chessGame.move(6, 0, 5, 0);
        chessGame.getNextTurn();
        chessGame.getGameStatus();

        chessGame.move(5, 5, 7, 4);
        chessGame.getNextTurn();
        chessGame.getGameStatus();

    }
}
