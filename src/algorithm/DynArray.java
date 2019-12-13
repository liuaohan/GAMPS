package algorithm;


import java.lang.reflect.Array;

public class DynArray<T> {
    private int MAX_LENGTH = 100;

    private T[] values;

    private int length;

    private int max_length;

    public DynArray() {

    }

    public DynArray(T t) {
        max_length = MAX_LENGTH;
        length = 0;
        values = (T[]) Array.newInstance(t.getClass(), max_length);
    }

    public DynArray(T t, DynArray<T> original) {
        max_length = MAX_LENGTH;
        length = 0;
        values = (T[]) Array.newInstance(t.getClass(), max_length);

        // Copy original values to current array
        for (int i = 0; i < original.size(); i++) {
            this.add(original.get(i));
        }
    }

    protected void expandSize() {//expand when the array is full
        // set new size
        max_length = max_length * 2;
        T[] temp_values = (T[]) Array.newInstance(values[0].getClass(), max_length);
        //copy values
        for (int i = 0; i < length; i++) {
            temp_values[i] = values[i];
        }

        values = temp_values;
    }


    public DynArray(DynArray<T> original) {

    }


    void add(T value) {
        //expand when the array is full
        if (length == max_length - 1) {
            expandSize();
        }
        values[length++] = value;
    }

    void remove() {
        assert (length > 0);
        length--;
    }

    void updateAt(int position, T newValue) {
        assert (position >= 0 && position < length);
        values[position] = newValue;
    }

    void replace(int position, T value) {
        assert (position >= 0 && position < length);
        values[position] = value;
    }

    void clear() {
        length = 0;
    }


    //reduce current array by new array with new size
    void reduceSize(int newSize) {
        T[] temp_values = (T[]) Array.newInstance(values[0].getClass(), max_length);
        length = newSize;

        //copy values
        for (int i = 0; i < newSize; i++) {
            temp_values[i] = values[i];
        }

        values = temp_values;
    }

    T get(int position) {
        assert (position < length && position >= 0);
        return values[position];
    }

    boolean contains(T value) {
        for (int i = 0; i < length; i++) {
            if (values[i] == value) {
                return true;
            }
        }
        return false;
    }

    int size() {
        return length;
    }
}
