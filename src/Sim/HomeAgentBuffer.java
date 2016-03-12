package Sim;


import java.util.LinkedList;

/**
 * Created by erikuusitalo on 05/03/16.
 */
public class HomeAgentBuffer<T> {

    /**
     * Creates empty Linked list
     */
    LinkedList<BufferEntry> buffer = new LinkedList<>();

    int bufferSize       = 0;
    int TTL              = 0;

    /**
     * Constructor
     * @param bufferSize is the fixed size of the buffer
     * @param TTL is the fixed time to live for each entry
     */
    public HomeAgentBuffer(int bufferSize, int TTL) {
        this.bufferSize = bufferSize;
        this.TTL        = TTL;
    }

    /**
     * Add entry to the buffer if not full
     * @param t element to insert into buffer
     * @return false if buffer is full
     */
    public boolean offer(T t){
        if(isFull())
            return false;
        else
           return buffer.add(new BufferEntry<T>(t, SimEngine.getTime()));
    }

    /**
     * Returnes the first entry of the buffer
     * @return first entry else null
     */
    public T poll(){
        if(buffer.isEmpty())
            return null;
        else
            return (T) buffer.removeFirst().t;
    }

    /**
     * Removes expired entries in the buffer
     * @return number of removed entries
     */
    public int checkTTL(){
        int removedEntries = 0;
        for(int i = 0; i < buffer.size(); i++){
            if(SimEngine.getTime() > buffer.get(i).getEntryTime() + TTL){
                buffer.remove(i);
                removedEntries++;
            }
        }
        return removedEntries;
    }

    /**
     * Checks if buffer is full
     * @return true if buffer is full
     */
    public boolean isFull(){
        if(buffer.size() > bufferSize)
            return true;
        return false;
    }

    /**
     * Check if buffer is empty
     * @return true if buffer is empty
     */
    public boolean isEmpty(){
        if(buffer.size() == 0)
            return true;
        return false;
    }

    /**
     * Private class for buffer entries
     * @param <T>
     */
    private class BufferEntry<T> {

        private T t;
        private double entryTime;

        /**
         * @param t is type of event
         * @param entryTime is the time the entry was instantiated
         */
        public BufferEntry(T t, double entryTime) {
            this.t = t;
            this.entryTime = entryTime;
        }

        /**
         * @return t
         */
        public T getT() {
            return t;
        }

        /**
         * @return entryTime
         */
        public double getEntryTime() {
            return entryTime;
        }
    }

}
