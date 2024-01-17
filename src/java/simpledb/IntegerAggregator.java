package simpledb;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Knows how to compute some aggregate over a set of IntFields.
 */
public class IntegerAggregator implements Aggregator {

    private static final long serialVersionUID = 1L;
    private HashMap<Field, Integer> aggregation;
    private HashMap<Field, Integer> count;
    private int gbfield;
    private Type gbfieldtype;
    private int afield;
    private Op what;

    /**
     * Aggregate constructor
     * 
     * @param gbfield
     *            the 0-based index of the group-by field in the tuple, or
     *            NO_GROUPING if there is no grouping
     * @param gbfieldtype
     *            the type of the group by field (e.g., Type.INT_TYPE), or null
     *            if there is no grouping
     * @param afield
     *            the 0-based index of the aggregate field in the tuple
     * @param what
     *            the aggregation operator
     */

    public IntegerAggregator(int gbfield, Type gbfieldtype, int afield, Op what) {
        this.gbfield=gbfield;
        this.gbfieldtype=gbfieldtype;
        this.afield=afield;
        this.what=what;
        // this one is used eclusively for the final results that we will be returning.
        this.aggregation = new HashMap<>();
        //this one on on the other is used to keep count for the avg operator and only ofr that one, it shouldn't serve 
        //the count operator , as it is alreadys erved by aggregation.
        this.count=new HashMap<>();
    }

    /**
     * Merge a new tuple into the aggregate, grouping as indicated in the
     * constructor
     * 
     * @param tup
     *            the Tuple containing an aggregate field and a group-by field
     */
    public void mergeTupleIntoGroup(Tuple tup) {
        Field groupVal = null;
        if (gbfield != Aggregator.NO_GROUPING){
            groupVal=tup.getField(gbfield);
        }
        if (what == Op.COUNT) {
            int currentCount = aggregation.getOrDefault(groupVal, 0);
            aggregation.put(groupVal, currentCount + 1);
            return; 
        }
        
        int aggVal = ((IntField) tup.getField(afield)).getValue();
        //We initilize teh aggreate value when op=Max with -infinity and vice versa for Min and 0 for all others.
        int currentVal = aggregation.getOrDefault(groupVal, (what == Op.MIN) ? Integer.MAX_VALUE : (what == Op.MAX) ? Integer.MIN_VALUE : 0);
        int currentCount = count.getOrDefault(groupVal, 0);

        switch (what) {
            case SUM -> aggregation.put(groupVal, currentVal + aggVal);
            case AVG -> {
                aggregation.put(groupVal, currentVal + aggVal);
                count.put(groupVal, currentCount + 1);
            }
            case MIN -> aggregation.put(groupVal, Math.min(currentVal, aggVal));
            case MAX -> aggregation.put(groupVal, Math.max(currentVal, aggVal));
        }
    }

    /**
     * Create a OpIterator over group aggregate results.
     * 
     * @return a OpIterator whose tuples are the pair (groupVal, aggregateVal)
     *         if using group, or a single (aggregateVal) if no grouping. The
     *         aggregateVal is determined by the type of aggregate specified in
     *         the constructor.
     */
    public OpIterator iterator() {
        return new OpIterator() {
            private Iterator<HashMap.Entry<Field, Integer>> it;

            public void open() {
                it = aggregation.entrySet().iterator();
            }

            public boolean hasNext() {
                return it.hasNext();
            }

            public Tuple next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("All tuples have been consumed");
                }

                HashMap.Entry<Field, Integer> entry = it.next();
                Tuple tuple = new Tuple(getTupleDesc());
                int  aggregateVal =entry.getValue();

                if (what == Op.AVG) {
                    int countVal = count.get(entry.getKey());
                    aggregateVal /= countVal; // Calculate average
                }


                if (gbfield == NO_GROUPING) {
                    tuple.setField(0, new IntField( aggregateVal));
                } else {
                    tuple.setField(0, entry.getKey());
                    tuple.setField(1, new IntField( aggregateVal));
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
        };
    }




