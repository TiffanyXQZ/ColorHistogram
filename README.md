# ColorHistogram

Jan 21, 2020 Update

1. Implemented the MyMinHash class based on the original library MinHash.
		a) The purpose is that we can create attributes, expand and edit the MinHash based on our future needs.
		b) Ran test examples, got the same calculation results between our MyMinHash class and the original library MinHash class.

2. Added HistoMap class
		a) count the number of occurance of each color

3. Added ImageData_MinHash class uses MyMinHash to calculate MinHash for each image.
		a) Added attributes: e.g. color counter, required timers, ect.
		b) Added getTopNColor, can filter out and print the top "N" color indices with the number of occurance.
		c) Added getTopRangeColor, since white background is usually the top 1 or top 2 color, therefore, I added the this method to be able to filter out the first n colors, and only consider from (n+1)th to Nth top colors.

4. Edited listview coding in MainActivity.java line 209 to line 297
		a) Now the simulator device can present: 
				1)Number of pixels
				2)Number of colors
				3)Number of buckets
				4)Time of rhb_hashing
				5)Time of min_hashing
				6)Top "N" colors' color indices

5. Added WeightedJaccard class
		a) Time cost of Weighted Jaccard Similarity is 60 times faster than Real Jaccard Similarity
		b) By using the TopNColor filter:
				1)Real Jaccard does not show any improvement on recognition of same products or different products.
				2)Weighted Jaccard show exceptional improvement on recogintion of the same products and different products. But, according to our existing test data set:
						I)The bucket number need to drop from 10 to 5.
						II)Also, we need to filter out top 1 and top 2 color before calculating similarity, because the white background is generally the top 1 or top 2 color.

				3)When bucket number changed from 10 to 5, minhash and Real Jaccard lost their accuracy.


6. Future Research:
		a) Study the weighted MinHash and implement it (there is no existing Java lib, but I can implement based on the existing python lib of WeightedMinhash)
		b) Test the trend of how bucket number of RGB can influence the Weighted Jaccard Similarity accuracy.
		c) Use Tengpeng's image dataset from his previous paper to test the WeightedJaccard and WeightedMinhash 



Printing Result: Bucket Number = 5, N = 10, Range = {2,10}

I/System.out: The average time used for calculating minhash similarity is: 6537 nanoseconds 
I/System.out: The average time used for calculating Jaccard similarity is: 88913406 nanoseconds
I/System.out: The average time used for calculating weighted Jaccard is: 1464350 nanoseconds 
    -----------------
I/System.out: Minhash similarity, real Jaccad similarity and weighted Jaccard similarity of a111111 to a3160402803 are: 0.875 and 0.855072463768116 and 0.5852432

I/System.out: Minhash similarity, and Top 10 color for real Jaccad similarity and weighted Jaccard similarity of a111111 to a3160402803 are: 0.875 and 0.8181818181818182 and 0.6277528

I/System.out: Minhash similarity, and Range from [2, 10] color for real Jaccad similarity and weighted Jaccard similarity of a111111 to a3160402803 are: 0.875 and 0.6 and 0.81472695
	-----------------
    -----------------
I/System.out: Minhash similarity, real Jaccad similarity and weighted Jaccard similarity of a1650052227 to a3160402803 are: 0.75 and 0.6071428571428571 and 0.34087792

I/System.out: Minhash similarity, and Top 10 color for real Jaccad similarity and weighted Jaccard similarity of a1650052227 to a3160402803 are: 0.75 and 0.05263157894736842 and 0.9349175

I/System.out: Minhash similarity, and Range from [2, 10] color for real Jaccad similarity and weighted Jaccard similarity of a1650052227 to a3160402803 are: 0.75 and 0.0 and NaN
    -----------------
    -----------------
I/System.out: Minhash similarity, real Jaccad similarity and weighted Jaccard similarity of a222222 to a3160402803 are: 0.9166666666666666 and 0.8695652173913043 and 0.59735084

I/System.out: Minhash similarity, and Top 10 color for real Jaccad similarity and weighted Jaccard similarity of a222222 to a3160402803 are: 0.9166666666666666 and 0.8181818181818182 and 0.670917

I/System.out: Minhash similarity, and Range from [2, 10] color for real Jaccad similarity and weighted Jaccard similarity of a222222 to a3160402803 are: 0.9166666666666666 and 0.7777777777777778 and 0.69835585
    -----------------
    -----------------
I/System.out: Minhash similarity, real Jaccad similarity and weighted Jaccard similarity of a3160402803 to a3160402803 are: 1.0 and 1.0 and 1.0

I/System.out: Minhash similarity, and Top 10 color for real Jaccad similarity and weighted Jaccard similarity of a3160402803 to a3160402803 are: 1.0 and 1.0 and 1.0

I/System.out: Minhash similarity, and Range from [2, 10] color for real Jaccad similarity and weighted Jaccard similarity of a3160402803 to a3160402803 are: 1.0 and 1.0 and 1.0
	-----------------
    -----------------
I/System.out: Minhash similarity, real Jaccad similarity and weighted Jaccard similarity of a32420846530 to a3160402803 are: 0.5 and 0.44680851063829785 and 0.27897346

I/System.out: Minhash similarity, and Top 10 color for real Jaccad similarity and weighted Jaccard similarity of a32420846530 to a3160402803 are: 0.5 and 0.05263157894736842 and 0.7328688

I/System.out: Minhash similarity, and Range from [2, 10] color for real Jaccad similarity and weighted Jaccard similarity of a32420846530 to a3160402803 are: 0.5 and 0.0 and 0.0
	-----------------
    -----------------
I/System.out: Minhash similarity, real Jaccad similarity and weighted Jaccard similarity of a333333 to a3160402803 are: 0.7916666666666666 and 0.7638888888888888 and 0.5300653

I/System.out: Minhash similarity, and Top 10 color for real Jaccad similarity and weighted Jaccard similarity of a333333 to a3160402803 are: 0.7916666666666666 and 0.5384615384615384 and 0.79379493

I/System.out: Minhash similarity, and Range from [2, 10] color for real Jaccad similarity and weighted Jaccard similarity of a333333 to a3160402803 are: 0.7916666666666666 and 0.6 and 0.8309728
    -----------------
    -----------------
I/System.out: Minhash similarity, real Jaccad similarity and weighted Jaccard similarity of a444444 to a3160402803 are: 0.875 and 0.7205882352941176 and 0.69745255

I/System.out: Minhash similarity, and Top 10 color for real Jaccad similarity and weighted Jaccard similarity of a444444 to a3160402803 are: 0.875 and 0.8181818181818182 and 0.78411347

I/System.out: Minhash similarity, and Range from [2, 10] color for real Jaccad similarity and weighted Jaccard similarity of a444444 to a3160402803 are: 0.875 and 0.7777777777777778 and 0.7590321
	-----------------
    -----------------
I/System.out: Minhash similarity, real Jaccad similarity and weighted Jaccard similarity of a5042835581 to a3160402803 are: 0.75 and 0.6707317073170732 and 0.2780597

I/System.out: Minhash similarity, and Top 10 color for real Jaccad similarity and weighted Jaccard similarity of a5042835581 to a3160402803 are: 0.75 and 0.05263157894736842 and 0.7889442

I/System.out: Minhash similarity, and Range from [2, 10] color for real Jaccad similarity and weighted Jaccard similarity of a5042835581 to a3160402803 are: 0.75 and 0.0 and 0.0
    -----------------
    -----------------
I/System.out: Minhash similarity, real Jaccad similarity and weighted Jaccard similarity of a555555 to a3160402803 are: 0.875 and 0.8888888888888888 and 0.559448

I/System.out: Minhash similarity, and Top 10 color for real Jaccad similarity and weighted Jaccard similarity of a555555 to a3160402803 are: 0.875 and 0.6666666666666666 and 0.8265129

I/System.out: Minhash similarity, and Range from [2, 10] color for real Jaccad similarity and weighted Jaccard similarity of a555555 to a3160402803 are: 0.875 and 0.7777777777777778 and 0.88587797
    -----------------
    -----------------
I/System.out: Minhash similarity, real Jaccad similarity and weighted Jaccard similarity of a666666 to a3160402803 are: 0.7916666666666666 and 0.7887323943661971 and 0.44734788

I/System.out: Minhash similarity, and Top 10 color for real Jaccad similarity and weighted Jaccard similarity of a666666 to a3160402803 are: 0.7916666666666666 and 0.6666666666666666 and 0.5877676

I/System.out: Minhash similarity, and Range from [2, 10] color for real Jaccad similarity and weighted Jaccard similarity of a666666 to a3160402803 are: 0.7916666666666666 and 0.6 and 0.7327709
    -----------------
    -----------------
I/System.out: Minhash similarity, real Jaccad similarity and weighted Jaccard similarity of a777777 to a3160402803 are: 0.7083333333333334 and 0.6666666666666666 and 0.2136851

I/System.out: Minhash similarity, and Top 10 color for real Jaccad similarity and weighted Jaccard similarity of a777777 to a3160402803 are: 0.7083333333333334 and 0.1111111111111111 and 0.5529794

I/System.out: Minhash similarity, and Range from [2, 10] color for real Jaccad similarity and weighted Jaccard similarity of a777777 to a3160402803 are: 0.7083333333333334 and 0.06666666666666667 and 0.9618053
    -----------------
    -----------------
I/System.out: Minhash similarity, real Jaccad similarity and weighted Jaccard similarity of a87651420677 to a3160402803 are: 0.5833333333333334 and 0.5679012345679012 and 0.22782388

    Minhash similarity, and Top 10 color for real Jaccad similarity and weighted Jaccard similarity of a87651420677 to a3160402803 are: 0.5833333333333334 and 0.1111111111111111 and 0.35659212

I/System.out: Minhash similarity, and Range from [2, 10] color for real Jaccad similarity and weighted Jaccard similarity of a87651420677 to a3160402803 are: 0.5833333333333334 and 0.06666666666666667 and 0.48621413
    -----------------
    -----------------
I/System.out: Minhash similarity, real Jaccad similarity and weighted Jaccard similarity of a888888 to a3160402803 are: 0.8333333333333334 and 0.8405797101449275 and 0.50047594

I/System.out: Minhash similarity, and Top 10 color for real Jaccad similarity and weighted Jaccard similarity of a888888 to a3160402803 are: 0.8333333333333334 and 0.6666666666666666 and 0.6268324

I/System.out: Minhash similarity, and Range from [2, 10] color for real Jaccad similarity and weighted Jaccard similarity of a888888 to a3160402803 are: 0.8333333333333334 and 0.6 and 0.7464819
    -----------------
    -----------------
I/System.out: Minhash similarity, real Jaccad similarity and weighted Jaccard similarity of a894047001034 to a3160402803 are: 0.625 and 0.6091954022988506 and 0.24266633

I/System.out: Minhash similarity, and Top 10 color for real Jaccad similarity and weighted Jaccard similarity of a894047001034 to a3160402803 are: 0.625 and 0.17647058823529413 and 0.41432905

I/System.out: Minhash similarity, and Range from [2, 10] color for real Jaccad similarity and weighted Jaccard similarity of a894047001034 to a3160402803 are: 0.625 and 0.14285714285714285 and 0.34434104
    -----------------
    -----------------
I/System.out: Minhash similarity, real Jaccad similarity and weighted Jaccard similarity of a898440001028 to a3160402803 are: 0.7916666666666666 and 0.6521739130434783 and 0.1491377

I/System.out: Minhash similarity, and Top 10 color for real Jaccad similarity and weighted Jaccard similarity of a898440001028 to a3160402803 are: 0.7916666666666666 and 0.1111111111111111 and 0.38690364

I/System.out: Minhash similarity, and Range from [2, 10] color for real Jaccad similarity and weighted Jaccard similarity of a898440001028 to a3160402803 are: 0.7916666666666666 and 0.06666666666666667 and 0.7935058
    -----------------
    -----------------
I/System.out: Minhash similarity, real Jaccad similarity and weighted Jaccard similarity of a999999 to a3160402803 are: 0.875 and 0.8382352941176471 and 0.5243975

I/System.out: Minhash similarity, and Top 10 color for real Jaccad similarity and weighted Jaccard similarity of a999999 to a3160402803 are: 0.875 and 0.5384615384615384 and 0.875762

I/System.out: Minhash similarity, and Range from [2, 10] color for real Jaccad similarity and weighted Jaccard similarity of a999999 to a3160402803 are: 0.875 and 0.6 and 0.9062542
I/System.out: -----------------
