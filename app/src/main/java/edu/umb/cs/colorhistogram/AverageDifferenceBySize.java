package edu.umb.cs.colorhistogram;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import info.debatty.java.lsh.MinHash;

public class AverageDifferenceBySize {
    private int index;
    private int dict_size;
    private int size;
    private double diff_average;


    public double getDiff_average() {
        return diff_average;
    }

    public AverageDifferenceBySize(List<ImageBitmaps> imageBitmaps, int index, int size, int num) {

        this.index = index;
        this.dict_size = num;
        this.size = size;
        this.diff_average = this.calcu_diff( imageBitmaps);
    }

    private double calcu_diff(List<ImageBitmaps> imageBitmaps) {

        MinHash minHash = new MinHash(size,dict_size*dict_size*dict_size);
        List<ImageData> imageDatas = new ArrayList<>();
        long duration, startTime,endTime;
        for (ImageBitmaps imageBitmap: imageBitmaps){
            ImageData imageData =new ImageData(imageBitmap.getBitmap(),imageBitmap.getName());
            startTime = System.nanoTime();
            int[] imageRGB_Hash = imageData.getPixels_Hash(this.dict_size);
            endTime = System.nanoTime();
            duration = endTime - startTime;
            imageData.setTime_rgbhash(duration);


            startTime = System.nanoTime();
            Set<Integer> set = new HashSet<>();
            for (int t : imageRGB_Hash)
                set.add(t);
            int[] minhash = minHash.signature(set);//this minHash is the minhash value of this image
            endTime = System.nanoTime();
            duration = endTime - startTime;

            imageData.setColorSet(set);
            imageData.setNum_color(set.size());
            imageData.setTime_minhash(duration);
            imageData.setMinhash(minhash);
            imageDatas.add(imageData);

        }


        double jac,minSim, diff=0;
        ImageData imageData = imageDatas.get(index);
        for (ImageData im: imageDatas){
            jac = minHash.jaccardIndex(imageData.getColorSet(),im.getColorSet());
            minSim = minHash.similarity(imageData.getMinhash(),im.getMinhash());
            diff = diff + Math.abs(jac - minSim) ;
        }

        return diff/imageDatas.size();
    }
}


