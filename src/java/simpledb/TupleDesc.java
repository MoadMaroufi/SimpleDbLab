package simpledb;

import java.io.Serializable;
import java.util.*;

/**
 * TupleDesc describes the schema of a tuple.
 */
public class TupleDesc implements Serializable {

    /**
     * A help class to facilitate organizing the information of each field
     */
    public static class TDItem implements Serializable {
        /**
         * Serialization is the process of converting an object
         * into a stream of bytes to be saved to disk or transmitted over a network.
         **/

        private static final long serialVersionUID = 1L;

        /**
         * The type of the field
         */
        public final Type fieldType;

        /**
         * The name of the field
         */
        public final String fieldName;

        public TDItem(Type t, String n) {
            this.fieldName = n;
            this.fieldType = t;
        }

        public String toString() {
            return fieldName + "(" + fieldType + ")";
        }
    }

    /**
     * @return An iterator which iterates over all the field TDItems
     * that are included in this TupleDesc
     */
    public Iterator<TDItem> iterator() {
        return tdiDict.values().iterator();
    }

    private static final long serialVersionUID = 1L;

    public HashMap<Integer, TDItem> tdiDict = new HashMap<>();
    public HashMap<String, Integer> fieldNameToIndex = new HashMap<>();
    public List<Integer> nullNameIndices = new ArrayList<>();
    /**
     * Create a new TupleDesc with typeAr.length fields with fields of the
     * specified types, with associated named fields.
     *
     * @param typeAr  array specifying the number of and types of fields in this
     *                TupleDesc. It must contain at least one entry.
     * @param fieldAr array specifying the names of the fields. Note that names may
     *                be null.
     */
    public TupleDesc(Type[] typeAr, String[] fieldAr) {
        if (typeAr.length == 0) {
            throw new IllegalArgumentException("typeAr must contain at least one entry.");
        }
        if (fieldAr.length != typeAr.length) {
            throw new IllegalArgumentException("fieldAr and typeAr should have the same length.");
        }
        for (int i = 0; i < typeAr.length; i++) {
            this.tdiDict.put(i, new TDItem(typeAr[i], fieldAr[i]));
            /* Insert only the first occurrence of null */
            if (fieldAr[i] != null) {
                this.fieldNameToIndex.put(fieldAr[i], i);
            }else {
            	this.nullNameIndices.add(i);
            }
        }

    }

    /**
     * Constructor. Create a new tuple desc with typeAr.length fields with
     * fields of the specified types, with anonymous (unnamed) fields.
     *
     * @param typeAr array specifying the number of and types of fields in this
     *               TupleDesc. It must contain at least one entry.
     */
    public TupleDesc(Type[] typeAr) {
        if (typeAr.length == 0) {
            throw new IllegalArgumentException("typeAr must contain at least one entry.");
        }
        this.fieldNameToIndex.put(null, 0);
        for (int i = 0; i < typeAr.length; i++) {
            this.tdiDict.put(i, new TDItem(typeAr[i], null));
        }
    }

    /**
     * @return the number of fields in this TupleDesc
     */
    public int numFields() {

        return this.tdiDict.size();
    }

    /**
     * Gets the (possibly null) field name of the ith field of this TupleDesc.
     *
     * @param i index of the field name to return. It must be a valid index.
     * @return the name of the ith field
     * @throws NoSuchElementException if i is not a valid field reference.
     */
    public String getFieldName(int i) throws NoSuchElementException {
        if (i < 0 || i >= this.numFields()) {
            throw new NoSuchElementException("The index must be valid");
        }
        return this.tdiDict.get(i).fieldName;
    }

    /**
     * Gets the type of the ith field of this TupleDesc.
     *
     * @param i The index of the field to get the type of. It must be a valid
     *          index.
     * @return the type of the ith field
     * @throws NoSuchElementException if i is not a valid field reference.
     */
    public Type getFieldType(int i) throws NoSuchElementException {
        if (i < 0 || i >= this.numFields()) {
            throw new NoSuchElementException("The index must be valid");
        }
        return this.tdiDict.get(i).fieldType;
    }

    /**
     * Find the index of the field with a given name.
     *
     * @param name name of the field.
     * @return the index of the field that is first to have the given name.
     * @throws NoSuchElementException if no field with a matching name is found.
     */
    public int fieldNameToIndex(String name) throws NoSuchElementException {
    	
    	if (name == null) {
            if (nullNameIndices.isEmpty()) {
                throw new NoSuchElementException("No field with a null name is found.");
            }
          //we return the index of the first null field name because we don't know which one the user is referencing
           // maybe a field name id should be added to the TdItem to deal with particular case
            return nullNameIndices.get(0); 
        }
        Integer filedNameIndex = fieldNameToIndex.get(name);
        if (filedNameIndex == null) {
            throw new NoSuchElementException("No field with a matching name is found.");
        }
        return filedNameIndex;
    }

    /**
     * @return The size (in bytes) of tuples corresponding to this TupleDesc.
     * Note that tuples from a given TupleDesc are of a fixed size.
     */
    public int getSize() {
        int size = 0;
        Iterator<TDItem> iteratorTDItem = this.iterator();
        while (iteratorTDItem.hasNext()) {
            TDItem it = iteratorTDItem.next();
            size += it.fieldType.getLen();
        }
        return size;
    }


    /**
     * Merge two TupleDescs into one, with td1.numFields + td2.numFields fields,
     * with the first td1.numFields coming from td1 and the remaining from td2.
     *
     * @param td1 The TupleDesc with the first fields of the new TupleDesc
     * @param td2 The TupleDesc with the last fields of the TupleDesc
     * @return the new TupleDesc
     */
    public static TupleDesc merge(TupleDesc td1, TupleDesc td2) {
        int td1NumFields = td1.numFields();
        int td2NumFields = td2.numFields();
        int td12NumFields = td1NumFields + td2NumFields;
        Type[] td12Types = new Type[td12NumFields];
        String[] td12Names = new String[td12NumFields];

        //direct indexing presents less overhead than iterators
        for (int i = 0; i < td1NumFields; i++) {
            td12Types[i] = td1.getFieldType(i);
            td12Names[i] = td1.getFieldName(i);
        }

        for (int i = 0; i < td2.numFields(); i++) {
            td12Types[i + td1NumFields] = td2.getFieldType(i);
            td12Names[i + td1NumFields] = td2.getFieldName(i);
        }
        return new TupleDesc(td12Types, td12Names);
    }

    /**
     * Compares the specified object with this TupleDesc for equality. Two
     * TupleDescs are considered equal if they have the same number of items
     * and if the i-th type in this TupleDesc is equal to the i-th type in o
     * for every i.
     *
     * @param o the Object to be compared for equality with this TupleDesc.
     * @return true if the object is equal to this TupleDesc.
     */

    public boolean equals(Object o) {
        if (!(o instanceof TupleDesc oTD)) {//pattern matching
            return false;
        }
        if (this.numFields() != oTD.numFields()) {
            return false;
        }
        for (int i = 0; i < this.numFields(); i++) {
            if (!this.getFieldType(i).equals(oTD.getFieldType(i))) {
                return false;
            }
        }
        return true;

    }

    public int hashCode() {
    	return Objects.hash(tdiDict, fieldNameToIndex);
    }

    /**
     * Returns a String describing this descriptor. It should be of the form
     * "fieldType[0](fieldName[0]), ..., fieldType[M](fieldName[M])", although
     * the exact format does not matter.
     *
     * @return String describing this descriptor.
     */
    public String toString() {
        StringBuilder TupleDescStr = new StringBuilder();

        for (int i = 0; i < numFields(); i++) {
            if (i > 0) {
                TupleDescStr.append(", ");
            }

            Type ft = getFieldType(i);
            String fn = getFieldName(i);

            TupleDescStr.append(ft)
                    .append("(")
                    .append(fn)
                    .append(")");
        }

        return TupleDescStr.toString();
    }
}

