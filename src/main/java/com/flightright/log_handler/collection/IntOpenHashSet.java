package com.flightright.log_handler.collection;

/**
 * Created by Evgeny on 13/05/2017.
 */
public class IntOpenHashSet {
    protected static final int INITIAL_CAPACITY = 16;
    protected static final float INITIAL_MUL_CAPACITY = 1.2f;
    protected static final float INITIAL_LOAD_FACTOR = .8f;

    protected int capacity;
    protected float mulCapacity;
    protected float loadFactor;
    protected int size;
    protected int maxFill;

    protected boolean containsNullKey;

    private static final int INT_PHI = 0x9E3779B9;

    protected int[] key;

    public IntOpenHashSet() {
        this.capacity = INITIAL_CAPACITY;
        this.mulCapacity = INITIAL_MUL_CAPACITY;
        this.loadFactor = INITIAL_LOAD_FACTOR;
        maxFill = maxFill(capacity, loadFactor);

        key = new int[capacity];
    }

    public IntOpenHashSet(float loadFactor, float mulCapacity) {
        this.capacity = INITIAL_CAPACITY;
        this.mulCapacity = mulCapacity;
        this.loadFactor = loadFactor;
        maxFill = maxFill(capacity, loadFactor);

        key = new int[capacity];
    }

    public IntOpenHashSet(int initialSize, float loadFactor, float mulCapacity) {
        this.capacity = (int) (initialSize / loadFactor);
        this.mulCapacity = mulCapacity;
        this.loadFactor = loadFactor;
        maxFill = maxFill(capacity, loadFactor);

        key = new int[capacity];
    }


    public boolean add(final int k) {
        final int pos = insert(k);
        return pos < 0;
    }

    public boolean contains(int k) {
        if (k == 0)
            return containsNullKey;

        int id = getId(k, capacity);

        // The starting point.
        if (key[id] == 0)
            return false;

        if (key[id] == k)
            return true;
        // There's always an unused entry.
        while (true) {
            if (key[id = (id + 1) % capacity] == 0)
                return false;
            if (key[id] == k)
                return true;
        }
    }

    private int insert(final int k) {

        if (k == 0) {
            if (containsNullKey) return capacity;

            containsNullKey = true;
            size++;
            return -1;
        }

        int id = getId(k, capacity);

        if (key[id] != 0) {
            if (key[id] == k)
                return id;

            while (key[id = (id + 1) % capacity] != 0) {
                if (key[id] == k)
                    return id;
            }
        }


        key[id] = k;
        if (size++ >= maxFill) rehash((int) (capacity * mulCapacity));
        return -1;
    }

    private void rehash(int newCapacity) {

        final int[] newKeys = new int[newCapacity];
        int i = capacity;
        for (int j = containsNullKey ? size - 1 : size; j > 0; j--) {
            while (key[--i] == 0) ;

            int oldKey = key[i];
            int id = getId(oldKey, newCapacity);
            if (newKeys[id] != 0)
                while (newKeys[id = (id + 1) % newCapacity] != 0)
                    ;
            newKeys[id] = oldKey;
        }

        this.capacity = newCapacity;
        maxFill = maxFill(capacity, loadFactor);
        this.key = newKeys;
    }

    protected static int hash(int k) {
        final int h = k * INT_PHI;
        return h ^ (h >>> 16);
    }

    protected static int getId(int k, int capacity) {
        int id = hash(k) % capacity;
        return id < 0 ? id + capacity : id;
    }

    public int size() {
        return this.size;
    }


    protected static int maxFill(final int n, final float f) {
        return Math.min((int) Math.ceil(n * f), n - 1);
    }


}
