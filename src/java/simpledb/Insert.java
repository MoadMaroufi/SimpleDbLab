package simpledb;

import java.io.IOException;

/**
 * Inserts tuples read from the child operator into the tableId specified in the
 * constructor
 */
public class Insert extends Operator {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     *
     * @param t
     * The transaction running the insert.
     * @param child
     * The child operator from which to read tuples to be inserted.
     * @param tableId
     * The table in which to insert tuples.
     * @throws DbException
     * if TupleDesc of child differs from table into which we are to
     * insert.
     */
    private TransactionId t;
    private OpIterator child;
    private int tableId;
    private boolean firstCall=true;

    public Insert(TransactionId t, OpIterator child, int tableId)
            throws DbException {
        this.t = t;
        this.child = child;
        this.tableId = tableId;
        
    }

    public TupleDesc getTupleDesc() {
    	 return new TupleDesc(new Type[]{Type.INT_TYPE});
        	
    }

    public void open() throws DbException, TransactionAbortedException {
        super.open();
        child.open();
    }

    public void close() {
        super.close();
        child.close();
        firstCall = true;
    }

    public void rewind() throws DbException, TransactionAbortedException {
        child.rewind();
        firstCall = true;
    }

    /**
     * Inserts tuples read from child into the tableId specified by the
     * constructor. It returns a one field tuple containing the number of
     * inserted records. Inserts should be passed through BufferPool. An
     * instances of BufferPool is available via Database.getBufferPool(). Note
     * that insert DOES NOT need check to see if a particular tuple is a
     * duplicate before inserting it.
     *
     * @return A 1-field tuple containing the number of inserted records, or
     * null if called more than once.
     * @see Database#getBufferPool
     * @see BufferPool#insertTuple
     */
    protected Tuple fetchNext() throws TransactionAbortedException, DbException {
    	if (firstCall){
        int count =0;
        while(child.hasNext()){
            try {
            	Database.getBufferPool().insertTuple(this.t,this.tableId,child.next());
                count++;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        
            TupleDesc myTupleDesc= this.getTupleDesc();
            Tuple tuple = new Tuple(myTupleDesc);
            tuple.setField(0, new IntField(count));
            firstCall=false;
            return tuple;
        
    	}
    	
        return null;
    }



    @Override
    public OpIterator[] getChildren() {
        return new OpIterator[]{this.child};
    }

    @Override
    public void setChildren(OpIterator[] children) {
        if (children.length > 0) {
            this.child = children[0];
        }
    }
}





