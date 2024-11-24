public class RepetitionBuffer {
    //Used for checking repetitions. Might miss repetitions if length is not big enough. 
    
    private long[] positionBuffer;
    private int index;
    private int length;

    public RepetitionBuffer(int length){
        this.positionBuffer = new long[length];
        this.length = length;
    }

    public void add(long position){
        positionBuffer[index++] = position;
        if(index >= length) index = 0;
    }

    //For unmaking moves
    public void goBack(){
        index--;
        if(index < 0) index = length-1;
        positionBuffer[index] = 0;
    }

    public boolean checkRepetition(long zobKey){
        int count = 0;
        for(long key : positionBuffer){
            if(key == zobKey) count++;
        }
        if(count >= 3) return true;
        return false;
    }
}