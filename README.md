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



