# Lab1 SimpleDb Report
### Design decisions
For the first execice we use Hashpmaps to store the Tditems and to get the indeces of teh fiels ad stuff ,so the reireval is simple when we need a fieldtindex name or somehtinh like its easy. We encoutered a problem withe null field names as our hashmaps takes names as keys and give stehir index the null value was problemtic , so we dedicate a sperate array to the null field nems and their index. There is  a problem still because when the use asks for the the index of a null filed which one should we return. I our case we return the first one if it exists. Maybe another attribute should be added to the TdItem class to be able to distiguish between the null names. We passed all teh test in Junit for teh tupe and TupleDesc classes

For the seocond exercice, it was mentionned thereadafe so we used concurrent hashmaps as data strcures to perform the necessary operations, these hahsmaps made teh reireival easier and we didnt need to scan through tsome arrays or smth. We passed all the tests in junit for the Catalog class

For the third execice, we don't impelment an eviction policy in the Bufferpool.getpage method for this lab , so we just check if the current # of pages in the buffer is smaller than the maximum number of pages allowed if not w ethrow a dbExcetion.

For the fourth execice, the document mentions the endianness of java virtual macines at first i thought i had to use this somnmewhere but eventually i didnt need it becaus eshifting bits to the right doesnt matter if we are using biog or littl endian n the code I have written. This part of using buitwise operation and conversion from bytes to bits and indeces was a bit confusing. The iterator salso was i thinkfor me on the hardest parts in this as i had to figure how everthing works out togteher how i should use the previous fucntions in this iterator , which a custum iterator that skips the empty slots. It was also difficult in the sens that had to read other classes that i havent't imeplented and understand how they work and use their methods in my code. Our code evntually passes all teh tests in Junit HeapfileReadtest and RecordIdTest.

for the fifth execice, the delicate part was the iterator as well where we go over each page and use the iterator we define for the heapage beforehand.


for the last execice, in the iterator we just use the methods of the  dbFileIterator,as it didn't make sens to rienplement the same iterator with sma emethods but int he lab the TA Jean flavien explained it to me and pointed me to the right direction.

Finally i perofmred the 3.7 A simple query and i got the desired ouput:
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


I spent apprroximately 30 to 35 hours on the lab, I worked alone which turned out to be very benifitial . I started with a very fast peace once the lab was realesed on the holday week i fnished approaximately 50% of the laba nd then during the flowwing weeks finished the rest on teh weekends and sometimes during the week. 
