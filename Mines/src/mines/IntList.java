package mines;

/**
 *
 * @author Philipp
 */
public class IntList {

    private final int[] array;
    private int pointer;

    public IntList(int capacity) {
        this.array = new int[capacity];
    }

    public int size() {
        return pointer;
    }

    public int capacity() {
        return array.length;
    }

    public void push(int value) {
        array[pointer++] = value;
    }

    public int pop() {
        return array[--pointer];
    }

    public void insertAt(int index, int value) {
        System.arraycopy(array, index, array, index + 1, pointer++ - index);
        set(index, value);
    }

    public void swapInsertAt(int index, int value) {
        push(get(index));
        set(index, value);
    }

    public void removeAt(int index) {
        System.arraycopy(array, index + 1, array, index, --pointer - index);
    }

    public void swapRemoveAt(int index) {
        set(index, pop());
    }

    public int get(int index) {
        return array[index];
    }

    public void set(int index, int value) {
        array[index] = value;
    }

    public void clear() {
        pointer = 0;
    }

    public void copyFrom(IntList list) {
        System.arraycopy(list.array, 0, array, 0, array.length);
        pointer = list.pointer;
    }

    public boolean isEmpty() {
        return pointer == 0;
    }
}
