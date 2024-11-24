public class Move {
    int from;
    int to;

    MoveType mType;

    boolean promotion;

    int moveValue; //Captured piece - piece

    char promoteTo;

    public Move(int from, int to){
        this.from = from;
        this.to = to;
        this.mType = null;
        this.promotion = false;
        this.moveValue = 0;
    }

    public Move(int from, int to, MoveType mType){
        this.from = from;
        this.to = to;
        this.mType = mType;
        this.promotion = false;
        this.moveValue = Integer.MIN_VALUE;
    }

    public Move(int from, int to, MoveType mType, int moveValue){
        this.from = from;
        this.to = to;
        this.mType = mType;
        this.promotion = false;
        this.moveValue = moveValue;
    }

    public Move(int from, int to, MoveType mType, boolean promotion){
        this.from = from;
        this.to = to;
        this.mType = mType;
        this.promotion = promotion;
        if(promotion){
            this.moveValue = Constants.queenVal;
        }else{
            this.moveValue = Integer.MIN_VALUE;
        }
        this.promoteTo = 'Q'; //Auto promote to queen
    }
    
    public Move(int from, int to, MoveType mType, boolean promotion, int moveValue){
        this.from = from;
        this.to = to;
        this.mType = mType;
        this.promotion = promotion;
        if(promotion){
            moveValue += Constants.queenVal-Constants.pawnVal; //You lose a pawn but gain a queen
        }
        this.moveValue = moveValue;
        this.promoteTo = 'Q'; //Auto promote to queen
    }


    @Override
    public String toString() {
        return Integer.toString(from) + " " + Integer.toString(to);
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        if(((Move)obj).from == this.from && ((Move)obj).to == this.to){
            return true;
        }
        return false;
    }
}
