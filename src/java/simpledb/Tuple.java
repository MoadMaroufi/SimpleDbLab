package simpledb;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Tuple maintains information about the contents of a tuple. Tuples have a
 * specified schema specified by a TupleDesc object and contain Field objects
 * with the data for each field.
 */
public class Tuple implements Serializable {

    private static final long serialVersionUID = 1L;
    private TupleDesc tupleDesc;
    private Field[] tupleFields;
    private RecordId tupleRecordId;
    /**
     * Create a new tuple with the specified schema (type).
     *
     * @param td
     *            the schema of this tuple. It must be a valid TupleDesc
     *            instance with at least one field.
     */
    public Tuple(TupleDesc td) {
        if (td==null || td.numFields()<=0){
            throw new IllegalArgumentException("It must be a valid TupleDesc instance with at least one field.");
        }
        this.tupleDesc=td;
        this.tupleFields=new Field[td.numFields()];
    }

    /**
     * @return The TupleDesc representing the schema of this tuple.
     */
    public TupleDesc getTupleDesc() {

        return this.tupleDesc;
    }

    /**
     * @return The RecordId representing the location of this tuple on disk. May
     *         be null.
     */
    public RecordId getRecordId() {

        return this.tupleRecordId;
    }

    /**
     * Set the RecordId information for this tuple.
     *
     * @param rid
     *            the new RecordId for this tuple.
     */
    public void setRecordId(RecordId rid) {
        this.tupleRecordId=rid;
    }

    /**
     * Change the value of the ith field of this tuple.
     *
     * @param i
     *            index of the field to change. It must be a valid index.
     * @param f
     *            new value for the field.
     */
    public void setField(int i, Field f) {
        if (i < 0 || i >= this.getTupleDesc().numFields()) {
            throw new NoSuchElementException("The index must be valid");
        }
        this.tupleFields[i]=f;
    }

    /**
     * @return the value of the ith field, or null if it has not been set.
     *
     * @param i
     *            field index to return. Must be a valid index.
     */
    public Field getField(int i) {
        if (i < 0 || i >= this.getTupleDesc().numFields()) {
            throw new NoSuchElementException("The index must be valid");
        }
        return  this.tupleFields[i];
    }

    /**
     * Returns the contents of this Tuple as a string. Note that to pass the
     * system tests, the format needs to be as follows:
     *
     * column1\tcolumn2\tcolumn3\t...\tcolumnN
     *
     * where \t is any whitespace (except a newline)
     */
    public String toString() {
        StringBuilder columnsStr = new StringBuilder();
        int valueInt;
        String valueStr;
        int numFields = this.getTupleDesc().numFields();

        for (int i = 0; i < numFields; i++) {
            Field currField = this.getField(i);
            if (currField instanceof IntField intField) {
                valueInt = intField.getValue();
                columnsStr.append(valueInt);
            } else if (currField instanceof StringField stringField) {
                valueStr = stringField.getValue();
                columnsStr.append(valueStr);
            }
            if (i < numFields - 1) {
                columnsStr.append("\t");
            }
        }
        return columnsStr.toString();
    }


    /**
     * @return
     *        An iterator which iterates over all the fields of this tuple
     * */
    public Iterator<Field> fields()
    {
        return Arrays.stream(tupleFields).iterator();
    }

    /**
     * reset the TupleDesc of this tuple (only affecting the TupleDesc)
     * */
    public void resetTupleDesc(TupleDesc td)
    {
        this.tupleDesc = td;
    }
}

