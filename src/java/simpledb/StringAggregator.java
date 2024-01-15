package simpledb;

import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Knows how to compute some aggregate over a set of StringFields.
 */
public class StringAggregator implements Aggregator {

    private static final long serialVersionUID = 1L;
    private HashMap<Field, Integer> count;
    private int gbfield;
    private Type gbfieldtype;
    private int afield;
    private Op what;

    /**
     * Aggregate constructor
     *
     * @param gbfield the 0-based index of the group-by field in the tuple, or NO_GROUPING if there is no grouping
     * @param gbfieldtype the type of the group by field (e.g., Type.INT_TYPE), or null if there is no grouping
     * @param afield the 0-based index of the aggregate field in the tuple
     * @param what aggregation operator to use -- only supports COUNT
     * @throws IllegalArgumentException if what != COUNT
     */
    public StringAggregator(int gbfield, Type gbfieldtype, int afield, Op what) {
        if (what != Op.COUNT) {
            throw new IllegalArgumentException("Only COUNT is supported for StringAggregator");
        }
        this.gbfield = gbfield;
        this.gbfieldtype = gbfieldtype;
        this.afield = afield;
        this.what = what;
        this.count = new HashMap<>();
    }

    /**
     * Merge a new tuple into the aggregate, grouping as indicated in the constructor
     *
     * @param tup the Tuple containing an aggregate field and a group-by field
     */
    public void mergeTupleIntoGroup(Tuple tup) {
        Field groupVal = null;
        if (gbfield != Aggregator.NO_GROUPING) {
            groupVal = tup.getField(gbfield);
        }
        int currentCount = count.getOrDefault(groupVal, 0);
        count.put(groupVal, currentCount + 1);
    }

    /**
     * Create a OpIterator over group aggregate results.
     *
     * @return a OpIterator whose tuples are the pair (groupVal, aggregateVal) if using group, or a single (aggregateVal) if no grouping.
     * The aggregateVal is determined by the type of aggregate specified in the constructor.
     */
    public OpIterator iterator() {
        return new OpIterator() {
            private Iterator<HashMap.Entry<Field, Integer>> it;

            public void open() {
                it = count.entrySet().iterator();
            }

            public boolean hasNext() {
                return it.hasNext();
            }

            public Tuple next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("No more tuples");
                }

                HashMap.Entry<Field, Integer> entry = it.next();
                Tuple tuple = new Tuple(getTupleDesc());

                if (gbfield == NO_GROUPING) {
                    tuple.setField(0, new IntField(entry.getValue()));
                } else {
                    tuple.setField(0, entry.getKey());
                    tuple.setField(1, new IntField(entry.getValue()));
                }
                return tuple;
            }

            public void rewind() {
                open();
            }

            public TupleDesc getTupleDesc() {
                if (gbfield == NO_GROUPING) {
                    return new TupleDesc(new Type[]{Type.INT_TYPE});
                } else {
                    return new TupleDesc(new Type[]{gbfieldtype, Type.INT_TYPE});
                }
            }

            public void close() {
            }

        };
    }
}
