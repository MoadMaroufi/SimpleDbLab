//package simpledb;
//
//import java.io.*;
//import java.util.ArrayList;
//
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * BufferPool manages the reading and writing of pages into memory from
// * disk. Access methods call into it to retrieve pages, and it fetches
// * pages from the appropriate location.
// * <p>
// * The BufferPool is also responsible for locking;  when a transaction fetches
// * a page, BufferPool checks that the transaction has the appropriate
// * locks to read/write the page.
// * 
// * @Threadsafe, all fields are final
// */
//public class BufferPool {
//    /** Bytes per page, including header. */
//    private static final int DEFAULT_PAGE_SIZE = 4096;
//
//    private static int pageSize = DEFAULT_PAGE_SIZE;
//    
//    /** Default number of pages passed to the constructor. This is used by
//    other classes. BufferPool should use the numPages argument to the
//    constructor instead. */
//    public static final int DEFAULT_PAGES = 50;
//
//    private  ConcurrentHashMap<PageId, Page> PageIdToPage;
//    private Integer maxPageNum;
//
//    /**
//     * Creates a BufferPool that caches up to numPages pages.
//     *
//     * @param numPages maximum number of pages in this buffer pool.
//     */
//    public BufferPool(int numPages) {
//        this.PageIdToPage= new ConcurrentHashMap<PageId, Page>();
//        this.maxPageNum=numPages;
//
//    }
//    
//    public static int getPageSize() {
//      return pageSize;
//    }
//    
//    // THIS FUNCTION SHOULD ONLY BE USED FOR TESTING!!
//    public static void setPageSize(int pageSize) {
//    	BufferPool.pageSize = pageSize;
//    }
//    
//    // THIS FUNCTION SHOULD ONLY BE USED FOR TESTING!!
//    public static void resetPageSize() {
//    	BufferPool.pageSize = DEFAULT_PAGE_SIZE;
//    }
//
//    /**
//     * Retrieve the specified page with the associated permissions.
//     * Will acquire a lock and may block if that lock is held by another
//     * transaction.
//     * <p>
//     * The retrieved page should be looked up in the buffer pool.  If it
//     * is present, it should be returned.  If it is not present, it should
//     * be added to the buffer pool and returned.  If there is insufficient
//     * space in the buffer pool, a page should be evicted and the new page
//     * should be added in its place.
//     *
//     * @param tid the ID of the transaction requesting the page
//     * @param pid the ID of the requested page
//     * @param perm the requested permissions on the page
//     */
//    public  Page getPage(TransactionId tid, PageId pid, Permissions perm)
//        throws TransactionAbortedException, DbException {
//        Page page=PageIdToPage.get(pid);
//        if (page!=null){
//            return page;
//        }
//        if (this.PageIdToPage.size()<this.maxPageNum){
//            //Read Page from disk
//            //get catalog
//            Catalog catalog= Database.getCatalog();
//            //get table id
//            int tableId=pid.getTableId();
//            //get corresponding DbFile
//            DbFile  dbFile=catalog.getDatabaseFile(tableId);
//            //Read the page
//            page=dbFile.readPage(pid);
//            this.PageIdToPage.put(pid,page);
//            return page;
//        }
//
//        throw new DbException("BufferPool is full, cannot retrieve more pages");//No eviction policy for the moment
//    }
//
//    /**
//     * Releases the lock on a page.
//     * Calling this is very risky, and may result in wrong behavior. Think hard
//     * about who needs to call this and why, and why they can run the risk of
//     * calling it.
//     *
//     * @param tid the ID of the transaction requesting the unlock
//     * @param pid the ID of the page to unlock
//     */
//    public  void releasePage(TransactionId tid, PageId pid) {
//        // some code goes here
//        // not necessary for lab1|lab2
//    }
//
//    /**
//     * Release all locks associated with a given transaction.
//     *
//     * @param tid the ID of the transaction requesting the unlock
//     */
//    public void transactionComplete(TransactionId tid) throws IOException {
//        // some code goes here
//        // not necessary for lab1|lab2
//    }
//
//    /** Return true if the specified transaction has a lock on the specified page */
//    public boolean holdsLock(TransactionId tid, PageId p) {
//        // some code goes here
//        // not necessary for lab1|lab2
//        return false;
//    }
//
//    /**
//     * Commit or abort a given transaction; release all locks associated to
//     * the transaction.
//     *
//     * @param tid the ID of the transaction requesting the unlock
//     * @param commit a flag indicating whether we should commit or abort
//     */
//    public void transactionComplete(TransactionId tid, boolean commit)
//        throws IOException {
//        // some code goes here
//        // not necessary for lab1|lab2
//    }
//
//    /**
//     * Add a tuple to the specified table on behalf of transaction tid.  Will
//     * acquire a write lock on the page the tuple is added to and any other 
//     * pages that are updated (Lock acquisition is not needed for lab2). 
//     * May block if the lock(s) cannot be acquired.
//     * 
//     * Marks any pages that were dirtied by the operation as dirty by calling
//     * their markDirty bit, and adds versions of any pages that have 
//     * been dirtied to the cache (replacing any existing versions of those pages) so 
//     * that future requests see up-to-date pages. 
//     *
//     * @param tid the transaction adding the tuple
//     * @param tableId the table to add the tuple to
//     * @param t the tuple to add
//     */
//    public void insertTuple(TransactionId tid, int tableId, Tuple t)
//            throws DbException, IOException, TransactionAbortedException {
//            HeapFile heapFile = (HeapFile) Database.getCatalog().getDatabaseFile(tableId);
//            ArrayList<Page> pagesToAdd = heapFile.insertTuple(tid, t);
//            for (Page page : pagesToAdd)
//        	{
//        		
//            	PageIdToPage.put(page.getId(), page);
//        	}
//
//        }
//
//        /**
//         * Remove the specified tuple from the buffer pool.
//         * Will acquire a write lock on the page the tuple is removed from and any
//         * other pages that are updated. May block if the lock(s) cannot be acquired.
//         *
//         * Marks any pages that were dirtied by the operation as dirty by calling
//         * their markDirty bit, and adds versions of any pages that have 
//         * been dirtied to the cache (replacing any existing versions of those pages) so 
//         * that future requests see up-to-date pages. 
//         *
//         * @param tid the transaction deleting the tuple.
//         * @param t the tuple to delete
//         */
//        public  void deleteTuple(TransactionId tid, Tuple t)
//            throws DbException, IOException, TransactionAbortedException {
//            RecordId rid = t.getRecordId();
//
//            PageId pid = rid.getPageId();
//            HeapFile heapPage = (HeapFile) Database.getCatalog().getDatabaseFile(pid.getTableId());
//            ArrayList<Page> pagesToAdd = heapPage.deleteTuple(tid, t);
//            for (Page page : pagesToAdd)
//        	{
//        		
//            	PageIdToPage.put(page.getId(), page);
//        	}
//
//        }
//
//    /**
//     * Flush all dirty pages to disk.
//     * NB: Be careful using this routine -- it writes dirty data to disk so will
//     *     break simpledb if running in NO STEAL mode.
//     */
//    public synchronized void flushAllPages() throws IOException {
//        // some code goes here
//        // not necessary for lab1
//
//    }
//
//    /** Remove the specific page id from the buffer pool.
//        Needed by the recovery manager to ensure that the
//        buffer pool doesn't keep a rolled back page in its
//        cache.
//        
//        Also used by B+ tree files to ensure that deleted pages
//        are removed from the cache so they can be reused safely
//    */
//    public synchronized void discardPage(PageId pid) {
//        // some code goes here
//        // not necessary for lab1
//    }
//
//    /**
//     * Flushes a certain page to disk
//     * @param pid an ID indicating the page to flush
//     */
//    private synchronized  void flushPage(PageId pid) throws IOException {
//        // some code goes here
//        // not necessary for lab1
//    }
//
//    /** Write all pages of the specified transaction to disk.
//     */
//    public synchronized  void flushPages(TransactionId tid) throws IOException {
//        // some code goes here
//        // not necessary for lab1|lab2
//    }
//
//    /**
//     * Discards a page from the buffer pool.
//     * Flushes the page to disk to ensure dirty pages are updated on disk.
//     */
//    private synchronized  void evictPage() throws DbException {
//        // some code goes here
//        // not necessary for lab1
//    }
//
//}

package simpledb;

import java.io.*;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * BufferPool manages the reading and writing of pages into memory from
 * disk. Access methods call into it to retrieve pages, and it fetches
 * pages from the appropriate location.
 * <p>
 * The BufferPool is also responsible for locking;  when a transaction fetches
 * a page, BufferPool checks that the transaction has the appropriate
 * locks to read/write the page.
 * 
 * @Threadsafe, all fields are final
 */
public class BufferPool {
    /** Bytes per page, including header. */
    private static final int DEFAULT_PAGE_SIZE = 4096;

    private static int pageSize = DEFAULT_PAGE_SIZE;
    
    /** Default number of pages passed to the constructor. This is used by
    other classes. BufferPool should use the numPages argument to the
    constructor instead. */
    public static final int DEFAULT_PAGES = 50;

    private  ConcurrentHashMap<PageId, Page> PageIdToPage;
    private Integer maxPageNum;

    private final Map<PageId, LinkedList<Long>> accessHistory;
    private final int K=5; // the "K" in LRU-K

    /**
     * Creates a BufferPool that caches up to numPages pages.
     *
     * @param numPages maximum number of pages in this buffer pool.
     */
    public BufferPool(int numPages) {
        this.PageIdToPage= new ConcurrentHashMap<PageId, Page>();
        this.maxPageNum=numPages;
        this.accessHistory = new HashMap<>();


    }
    
    public static int getPageSize() {
      return pageSize;
    }
    
    // THIS FUNCTION SHOULD ONLY BE USED FOR TESTING!!
    public static void setPageSize(int pageSize) {
    	BufferPool.pageSize = pageSize;
    }
    
    // THIS FUNCTION SHOULD ONLY BE USED FOR TESTING!!
    public static void resetPageSize() {
    	BufferPool.pageSize = DEFAULT_PAGE_SIZE;
    }

    /**
     * Retrieve the specified page with the associated permissions.
     * Will acquire a lock and may block if that lock is held by another
     * transaction.
     * <p>
     * The retrieved page should be looked up in the buffer pool.  If it
     * is present, it should be returned.  If it is not present, it should
     * be added to the buffer pool and returned.  If there is insufficient
     * space in the buffer pool, a page should be evicted and the new page
     * should be added in its place.
     *
     * @param tid the ID of the transaction requesting the page
     * @param pid the ID of the requested page
     * @param perm the requested permissions on the page
     */
    public Page getPage(TransactionId tid, PageId pid, Permissions perm)
            throws TransactionAbortedException, DbException {
        synchronized (this) {
            Page page = PageIdToPage.get(pid);
            if (page != null) {
                updateAccessHistory(pid);
                return page;
            }

            if (PageIdToPage.size() >= maxPageNum) {
                evictPage();
            }

            // Read Page from disk
            Catalog catalog = Database.getCatalog();
            int tableId = pid.getTableId();
            DbFile dbFile = catalog.getDatabaseFile(tableId);
            page = dbFile.readPage(pid);

            PageIdToPage.put(pid, page);
            updateAccessHistory(pid);
            return page;
        }
    }


    /**
     * Releases the lock on a page.
     * Calling this is very risky, and may result in wrong behavior. Think hard
     * about who needs to call this and why, and why they can run the risk of
     * calling it.
     *
     * @param tid the ID of the transaction requesting the unlock
     * @param pid the ID of the page to unlock
     */
    public  void releasePage(TransactionId tid, PageId pid) {
        // some code goes here
        // not necessary for lab1|lab2
    }

    /**
     * Release all locks associated with a given transaction.
     *
     * @param tid the ID of the transaction requesting the unlock
     */
    public void transactionComplete(TransactionId tid) throws IOException {
        // some code goes here
        // not necessary for lab1|lab2
    }

    /** Return true if the specified transaction has a lock on the specified page */
    public boolean holdsLock(TransactionId tid, PageId p) {
        // some code goes here
        // not necessary for lab1|lab2
        return false;
    }

    /**
     * Commit or abort a given transaction; release all locks associated to
     * the transaction.
     *
     * @param tid the ID of the transaction requesting the unlock
     * @param commit a flag indicating whether we should commit or abort
     */
    public void transactionComplete(TransactionId tid, boolean commit)
        throws IOException {
        // some code goes here
        // not necessary for lab1|lab2
    }

    /**
     * Add a tuple to the specified table on behalf of transaction tid.  Will
     * acquire a write lock on the page the tuple is added to and any other 
     * pages that are updated (Lock acquisition is not needed for lab2). 
     * May block if the lock(s) cannot be acquired.
     * 
     * Marks any pages that were dirtied by the operation as dirty by calling
     * their markDirty bit, and adds versions of any pages that have 
     * been dirtied to the cache (replacing any existing versions of those pages) so 
     * that future requests see up-to-date pages. 
     *
     * @param tid the transaction adding the tuple
     * @param tableId the table to add the tuple to
     * @param t the tuple to add
     */
    public void insertTuple(TransactionId tid, int tableId, Tuple t)
            throws DbException, IOException, TransactionAbortedException {
        HeapFile heapFile = (HeapFile) Database.getCatalog().getDatabaseFile(tableId);
        ArrayList<Page> pagesToAdd = heapFile.insertTuple(tid, t);
        for (Page page : pagesToAdd)
        {

            PageIdToPage.put(page.getId(), page);
        }

    }

    /**
     * Remove the specified tuple from the buffer pool.
     * Will acquire a write lock on the page the tuple is removed from and any
     * other pages that are updated. May block if the lock(s) cannot be acquired.
     *
     * Marks any pages that were dirtied by the operation as dirty by calling
     * their markDirty bit, and adds versions of any pages that have
     * been dirtied to the cache (replacing any existing versions of those pages) so
     * that future requests see up-to-date pages.
     *
     * @param tid the transaction deleting the tuple.
     * @param t the tuple to delete
     */
    public  void deleteTuple(TransactionId tid, Tuple t)
            throws DbException, IOException, TransactionAbortedException {
        RecordId rid = t.getRecordId();

        PageId pid = rid.getPageId();
        HeapFile heapPage = (HeapFile) Database.getCatalog().getDatabaseFile(pid.getTableId());
        ArrayList<Page> pagesToAdd = heapPage.deleteTuple(tid, t);
        for (Page page : pagesToAdd)
        {

            PageIdToPage.put(page.getId(), page);
        }

    }

    /**
     * Flush all dirty pages to disk.
     * NB: Be careful using this routine -- it writes dirty data to disk so will
     *     break simpledb if running in NO STEAL mode.
     */
    public synchronized void flushAllPages() throws IOException {
        for (PageId pid : PageIdToPage.keySet()) {
            flushPage(pid);
        }

    }

    /** Remove the specific page id from the buffer pool.
        Needed by the recovery manager to ensure that the
        buffer pool doesn't keep a rolled back page in its
        cache.
        
        Also used by B+ tree files to ensure that deleted pages
        are removed from the cache so they can be reused safely
    */
    public synchronized void discardPage(PageId pid) {
        if (this.PageIdToPage.containsKey(pid)) {
            this.PageIdToPage.remove(pid);
        }

        
        if (this.accessHistory != null && this.accessHistory.containsKey(pid)) {
            this.accessHistory.remove(pid);
        }
    }

    /**
     * Flushes a certain page to disk
     * @param pid an ID indicating the page to flush
     */
    private synchronized  void flushPage(PageId pid) throws IOException {
        Page page = PageIdToPage.get(pid);
        if (page == null) {
            return;
        }
        if (page.isDirty()!=null){
            DbFile dbFile = Database.getCatalog().getDatabaseFile(pid.getTableId());
            dbFile.writePage(page);
            page.markDirty(false, null);
        }
    }


    /** Write all pages of the specified transaction to disk.
     */
    public synchronized  void flushPages(TransactionId tid) throws IOException {
        // some code goes here
        // not necessary for lab1|lab2
    }

    private void updateAccessHistory(PageId pid) {
        LinkedList<Long> history = accessHistory.getOrDefault(pid, new LinkedList<>());
        long currentTime = System.currentTimeMillis();
        history.addFirst(currentTime);

        if (history.size() > K) {
            history.removeLast();
        }

        accessHistory.put(pid, history);
    }
    /**
     * Discards a page from the buffer pool.
     * Flushes the page to disk to ensure dirty pages are updated on disk.
     */
    private void evictPage() throws DbException {
        PageId pageToEvict = null;
        long oldestAccessTime = Long.MAX_VALUE;

        for (Map.Entry<PageId, LinkedList<Long>> entry : accessHistory.entrySet()) {
            LinkedList<Long> times = entry.getValue();

            long oldestTimeForThisPage = (times.isEmpty()) ? Long.MAX_VALUE : times.getLast();
            if (oldestTimeForThisPage < oldestAccessTime) {
                oldestAccessTime = oldestTimeForThisPage;
                pageToEvict = entry.getKey();
            }
        }

        if (pageToEvict != null) {
            // Additional checks can be performed here, e.g., dirty page handling
            PageIdToPage.remove(pageToEvict);
            accessHistory.remove(pageToEvict);
        } else {
            throw new DbException("No page to evict");
        }
    }


}

