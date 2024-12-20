import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;

public class RepetitionQue {
    private Deque<Long> repetitionQue;
    private HashMap<Long, Integer> positionCount;

    public RepetitionQue() {
        this.repetitionQue = new LinkedList<>();
        this.positionCount = new HashMap<>();
    }

    public RepetitionQue(RepetitionQue orgRQ){
        this.repetitionQue = new LinkedList<>();
        for(Long i : orgRQ.getQue()){
            this.repetitionQue.add(i);
        }
        this.positionCount = (HashMap<Long, Integer>) orgRQ.getPosCount().clone();
    }

    public void add(long position) {
        this.repetitionQue.push(position);
        this.positionCount.put(position, this.positionCount.getOrDefault(position, 0) + 1);
    }

    // For unmaking moves
    public void undo() {
        long position = this.repetitionQue.pop();
        int count = this.positionCount.get(position);
        if (count == 1) {
            this.positionCount.remove(position);
        } else {
            this.positionCount.put(position, count - 1);
        }
    }

    public boolean checkRepetition(long zobKey) {
        return this.positionCount.getOrDefault(zobKey, 0) >= 3;
    }

    public Deque<Long> getQue(){
        return repetitionQue;
    }
    public HashMap<Long, Integer> getPosCount(){
        return positionCount;
    }
}