import java.util.concurrent.atomic.AtomicReferenceArray;

public class ThreadSafeTT {
    private final AtomicReferenceArray<TransPosEntry> entries;
    private int size;

    public ThreadSafeTT(int size){
        if ((size & (size - 1)) != 0) {
            throw new IllegalArgumentException("Size must be a power of 2.");
        }
        this.size = size;
        this.entries = new AtomicReferenceArray<>(size);
    }

    public void put(long zobKey, TransPosEntry entry){
        
        int idx = (int)(zobKey & (size - 1)); //Instead of modulo operator
        TransPosEntry cE = entries.get(idx);
        if(cE != null && cE.zobKey == entry.zobKey && cE.depth >= entry.depth) return;
        entries.set(idx, entry);
    }

    public TransPosEntry get(long zobKey){
        int idx = (int)(zobKey & (size - 1)); //Instead of modulo operator
        TransPosEntry curEntry = entries.get(idx);

        if(curEntry != null && curEntry.zobKey == zobKey){
            return curEntry;
        }else{
            return null;
        }
    }

    public void clear(){
        for(int i = 0; i < size; i++){
            entries.set(i, null);
        }
    }
}
