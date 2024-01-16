package simpledb;

import java.util.*;

/**
 * Filter is an operator that implements a relational select.
 */
public class Filter extends Operator {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor accepts a predicate to apply and a child operator to read
     * tuples to filter from.
     * 
     * @param p
     *            The predicate to filter tuples with
     * @param child
     *            The child operator
     */

    private final Predicate p;
    private  OpIterator child;
    public Filter(Predicate p, OpIterator child) {
        this.p=p;
        this.child=child;
    }

    public Predicate getPredicate() {
        return this.p;
    }

    public TupleDesc getTupleDesc() {
        return this.child.getTupleDesc();
    }

    public void open() throws DbException, NoSuchElementException,
            TransactionAbortedException {
        super.open();//Proper Initialization and Cleanup by the superclass operator thats why we have to use super
        child.open();
    }

    public void close() {
        child.close();
        super.close();
    }

    public void rewind() throws DbException, TransactionAbortedException {
        child.rewind();
    }

    /**
     * AbstractDbIterator.readNext implementation. Iterates over tuples from the
     * child operator, applying the predicate to them and returning those that
     * pass the predicate (i.e. for which the Predicate.filter() returns true.)
     * 
     * @return The next tuple that passes the filter, or null if there are no
     *         more tuples
     * @see Predicate#filter
     */
    protected Tuple fetchNext() throws NoSuchElementException,
            TransactionAbortedException, DbException {
        while(child.hasNext()){
            Tuple currTuple=child.next();
            if (this.p.filter(currTuple)){
                return currTuple;
            }
        }
        return null;
    }
    @Override
    public OpIterator[] getChildren() {
        return new OpIterator[]{child};//filter has only one child because we just get the data and filter and then pass on
    }
    @Override
    public void setChildren(OpIterator[] children) {
            child = children[0];
    }
}