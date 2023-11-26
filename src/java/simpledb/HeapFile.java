package simpledb;

import java.io.*;
import java.util.*;

/**
 * HeapFile is an implementation of a DbFile that stores a collection of tuples
 * in no particular order. Tuples are stored on pages, each of which is a fixed
 * size, and the file is simply a collection of those pages. HeapFile works
 * closely with HeapPage. The format of HeapPages is described in the HeapPage
 * constructor.
 * 
 * @see simpledb.HeapPage#HeapPage
 * @author Sam Madden
 */
public class HeapFile implements DbFile {

    /**
     * Constructs a heap file backed by the specified file.
     * 
     * @param f
     *            the file that stores the on-disk backing store for this heap
     *            file.
     */
    private File f;
    private TupleDesc td;
    public HeapFile(File f, TupleDesc td) {
        this.f=f;
        this.td=td;
    }

    /**
     * Returns the File backing this HeapFile on disk.
     * 
     * @return the File backing this HeapFile on disk.
     */
    public File getFile() {
        return this.f;
    }

    /**
     * Returns an ID uniquely identifying this HeapFile. Implementation note:
     * you will need to generate this tableid somewhere to ensure that each
     * HeapFile has a "unique id," and that you always return the same value for
     * a particular HeapFile. We suggest hashing the absolute file name of the
     * file underlying the heapfile, i.e. f.getAbsoluteFile().hashCode().
     * 
     * @return an ID uniquely identifying this HeapFile.
     */
    public int getId() {
        return f.getAbsoluteFile().hashCode();
    }

    /**
     * Returns the TupleDesc of the table stored in this DbFile.
     * 
     * @return TupleDesc of this DbFile.
     */
    public TupleDesc getTupleDesc() {
        return this.td;
    }

    // see DbFile.java for javadocs
    public Page readPage(PageId pid) {
        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile(getFile(), "r");
            //calculate the offset to read a page
            long offset=(long)BufferPool.getPageSize()* pid.getPageNumber();
            byte[] data = new byte[BufferPool.getPageSize()];

            // Seek to the offset of the page and read the data
            file.seek(offset);
            file.readFully(data);
            HeapPageId heapPageId=new HeapPageId(pid.getTableId(), pid.getPageNumber());
            return new HeapPage(heapPageId,data);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    // see DbFile.java for javadocs
    public void writePage(Page page) throws IOException {
        // some code goes here
        // not necessary for lab1
    }

    /**
     * Returns the number of pages in this HeapFile.
     */
    public int numPages() {
        return (int) Math.ceil((double) f.length() / BufferPool.getPageSize());

    }

    // see DbFile.java for javadocs
    public ArrayList<Page> insertTuple(TransactionId tid, Tuple t)
            throws DbException, IOException, TransactionAbortedException {
        // some code goes here
        return null;
        // not necessary for lab1
    }

    // see DbFile.java for javadocs
    public ArrayList<Page> deleteTuple(TransactionId tid, Tuple t) throws DbException,
            TransactionAbortedException {
        // some code goes here
        return null;
        // not necessary for lab1
    }

    // see DbFile.java for javadocs
    //We should use the iterator on the pages that we've defined beforehand ,
    // what we will do here is go over the pages
    //and for each page use this iterator on th page
    public DbFileIterator iterator(TransactionId tid) {
        return new DbFileIterator() {
            private int currPageNum=-1;
            private Iterator<Tuple> currPageIterator=null;

            @Override
            public void open() throws DbException, TransactionAbortedException {
                //Initialize the iterator for the first page
                currPageNum=0;
                PageId pid = new HeapPageId(getId(), currPageNum);
                Page page = Database.getBufferPool().getPage(tid, pid, Permissions.READ_ONLY);
                currPageIterator=((HeapPage) page).iterator();
            }

            @Override
            public boolean hasNext() throws TransactionAbortedException, DbException {
            	if(currPageIterator==null) {
            		return false;
            	}
                if(currPageIterator.hasNext()){
                    return true;
                }else if (currPageNum<numPages()-1){
                    currPageNum++;
                    PageId pid = new HeapPageId(getId(), currPageNum);
                    Page page = Database.getBufferPool().getPage(tid, pid, Permissions.READ_ONLY);
                    currPageIterator=((HeapPage) page).iterator();
                    return currPageIterator.hasNext();
                }
               return false;
            }

            @Override
            public Tuple next() throws TransactionAbortedException, DbException, NoSuchElementException {
                if (!hasNext()){
                    throw new NoSuchElementException("All tuples consumed!");
                }
                return  currPageIterator.next();

            }

            @Override
            public void rewind() throws DbException, TransactionAbortedException {
                close();
                open();
            }

            @Override
            public void close() {
                currPageNum=-1;
                currPageIterator=null;

            }





        };
        }
    }
