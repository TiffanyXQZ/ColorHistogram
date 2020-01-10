# ColorHistogram January 10, 2020

Original Image to be Compared with: a3160402803.jpg
Differentiated image from original image are: a111111 ~ a999999.jpg
Totally different product images: a1650052227.jpg, a32420846530.jpg, a5042835581.jpg, a87651420677.jpg, a894047001034.jpg, a898440001028.jpg


In this update:
1) Added counter of number of distinct colors in each image

2) Added timer of duration of RGB hash to integers

3) Added timer of duration of MinHash Signature Calculation for each image

4) Added counter of number of pixels in each image

5) Added "AverageDifferenceBySize" class to test how the trend of the difference between Minhash Similarity VS. Real Jaccard Similarity will look like by changing "Size"(size of Minhash Signatures)

	a) In MainActivity.java, from Line 153 to Line 180

	b) It will print the size starting from being 100, adding 50 each time, and ending at 1250

	c) It will print the corresponding absolute values of Minhash Similarity - Jaccard Similarity, which shows how much difference/error is the MinHash Similarity to the Jaccard Similarity

	d) Please see attached result trending image "accuracyDifferenceTrend.png" in the following message.

	c) It generally shows a linear decrease of the difference between the MinHash Similrity to the Jaccard Similarity, in short term (size increases from 100 to 1250).
	   But, more like a quadratic shape in longer term (size increases from 100 to 5000, please see image "longterm_accuracyDifferenceTrend.png"

![](accuracyDifferenceTrend.png)
![](Longterm_accuracyDifferenceTrend.png)

For printing result, it is attached in a text file named result.txt

