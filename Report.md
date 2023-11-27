# Lab1 SimpleDb Report
### Design decisions
For the first execice, I used Hashpmaps to store the TdItems making  the reireval field's index/name  straightforward and more efficient. I encoutered a problem with null field names, names as keys to provide their indices. The null values were problemtic in this case , so we dedicate a separate array for the null field names and their indices. However, there is  a problem still:  when the user asks for the the index of a null filed which one should we return? In our case we return the first one if it exists. Perhaps another attribute should be added to the TdItem class to be able to distiguish between  null names. . We passed all the tests in JUnit for the Tuple and TupleDesc classes.

For the seocond exercice, thereadsafety was mentionned so I used concurrent HashMaps as data structures to implement the necessary method for the class. All tests pass for the Catalog class.

For the third exercise, we don't implement an eviction policy in the BufferPool.getPage method for this lab. Instead, we just check if the current number of pages in the buffer is less than the maximum number of pages allowed. If not, we throw a DbException.

For the fourth execice, the document discusses the endianness of Java Virtual Machines.  Initially, I thought I had to use this information, but eventually, I didn't need it because shifting bits to the right doesn't depend on whether we are using big or little endian in the code I have written. This part, involving bitwise operations  was a somewhat confusing. The iterator was also one of the toughest parts of the lab, as I had to figure out how everything works together, how to use the previous functions in this custom iterator that skips empty slots. It was also difficult in the sense that I had to read and understand other classes I hadn't implemented (already implemented for us) and use their methods in my code. The JUnit  tests HeapfileReadTest and RecordIdTest were eventually sccefully passed.

For the fifth execice, the delicate part was also the iterator as well where we go over each page and use the iterator we've defined for the heapage beforehand.


For the last exercise, the iterator was a bit confusing. We just had to call the previous methods from DbFileIterator. In the lab, the teaching assistant clarified this for me and pointed me in the right direction, explaining that we should indeed just call the methods from the previous DbFileIterator.

Finally I perofmred the 3.7 A simple query test and i got the desired ouput:
```
Moaad@Moaads-MacBook-Pro Lab1-starter-code % ls
Report.md		lib			src
bin			log			test
build.xml		some_data_file.dat
dist			some_data_file.txt
Moaad@Moaads-MacBook-Pro Lab1-starter-code % ant
Buildfile: /Users/Moaad/Desktop/Lab1/Lab1-starter-code/build.xml

compile:

dist:
      [jar] Building jar: /Users/Moaad/Desktop/Lab1/Lab1-starter-code/dist/simpledb.jar

BUILD SUCCESSFUL
Total time: 1 second
Moaad@Moaads-MacBook-Pro Lab1-starter-code % java -classpath dist/simpledb.jar simpledb.test
1	1	1	5
2	2	2	6
3	4	4	7
Moaad@Moaads-MacBook-Pro Lab1-starter-code % 
```


I spent apprroximately 30 to 35 hours on the lab, working alone which turned out to be very benifitial. I started  at a fast pace once the lab was realesed during the holday week, fnishing approaximately 50% of it. Then over the flowwing weeks, I finished the rest on the weekends and occasionally during the week.
