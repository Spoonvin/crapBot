public class TranspositionTable {
    private TransPosEntry[] entries;
    private int size;

    public TranspositionTable(int size){
        if ((size & (size - 1)) != 0) {
            throw new IllegalArgumentException("Size must be a power of 2.");
        }
        this.size = size;
        this.entries = new TransPosEntry[size];
    }

    public void put(long zobKey, TransPosEntry entry){
        
        int idx = (int)(zobKey & (size - 1)); //Instead of modulo operator
        TransPosEntry cE = entries[idx];
        if(cE != null && cE.depth >= entry.depth){
            return;
        }else{
            entries[idx] = entry;
        }
    }

    public TransPosEntry get(long zobKey){
        
        if(entries != null){
            int idx = (int)(zobKey & (size - 1)); //Instead of modulo operator
            TransPosEntry curEntry = entries[idx];

            if(curEntry != null && curEntry.zobKey == zobKey){
                return curEntry;
            }else{
                return null;
            }
        }else{
            return null;
        }
    }

    public void clear(){
        for(int i = 0; i < size; i++){
            entries[i] = null;
        }
    }
}
