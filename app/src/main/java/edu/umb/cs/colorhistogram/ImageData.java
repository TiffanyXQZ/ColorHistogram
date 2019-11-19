package edu.umb.cs.colorhistogram;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.List;

public class ImageData {
    private Bitmap bitmap=null;
    private int[] orig_pixels;
    private int[] bin_idx;
    private int[] bin_hist;

    private ArrayList<ImageDist> distList=new ArrayList<ImageDist>();
    private int num_default=5;
    private int id=-1; //index number as the ID

    class ImageDist{
        private ImageData imgData;
        private double dist;
        ImageDist(ImageData imgData, double dist){
            this.imgData=imgData;
            this.dist=dist;
        }
        protected ImageData getImageData(){return imgData;}
        protected double getDist(){return dist;}
    }

    ImageData(Bitmap bmp, int idx){
        id=idx;
        bitmap=bmp;
        orig_pixels=readPixels(bitmap);
        bin_idx=new int[orig_pixels.length];
        calHist(num_default);
    }

    protected int getId(){return id;}
    protected int getNumOfPixels(){return orig_pixels.length;}

    private int[] readPixels (Bitmap bitmap){
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0,
                bitmap.getWidth(), bitmap.getHeight());
        return pixels;
    }

    protected Bitmap getBitmap(){return bitmap;}

    void calHist(int num){
        int w=(int)Math.ceil((float)256/num);
        bin_hist=new int[num*num*num];
        for (int j=0;j<orig_pixels.length;j++) {
            int red =  Color.red(orig_pixels[j]);
            int green =  Color.green(orig_pixels[j]);
            int blue =  Color.blue(orig_pixels[j]);

            bin_idx[j]= (int)Math.floor(red/w)*num*num
                        +(int)Math.floor(green/w)*num
                        +(int)Math.floor(blue/w);

            if(bin_idx[j]>bin_hist.length){
                System.out.printf("num:%d, w:%d,r:%d,g:%d,b:%d\n",num,w,red,green,blue);
            }
            bin_hist[bin_idx[j]]++;

                //int alpha=Color.alpha(pixels[j]);
                //int luma = (int)(0.299f * red + 0.587f * green + 0.114f * blue);
        }
    }

    protected int[] getBinHist(){return bin_hist;}

    protected void addDistList(ImageData id, double dist){
        if(distList.size()==0){
            distList.add(new ImageDist(id, dist));
            return;
        }
        for(int i=0;i<distList.size();i++){
            double curDist=distList.get(i).getDist();
            if(dist<curDist){
                distList.add(i, new ImageDist(id, dist));
                break;
            }
        }
    }

    protected List<ImageDist> getTopN(int n){
        if(n>distList.size()){
            System.out.println("out of distList's index range");
            return distList;
        }
        return distList.subList(0,n);
    }

    protected String getTopNString(int n){
        List alist=getTopN(n);
        String ret=new String();
        for(int i=0;i<alist.size();i++){
            ImageDist imd= (ImageDist)alist.get(i);
            ret+="ID:"+Integer.toString(imd.getImageData().getId())+",dist:"
                    +String.format("%.03f",imd.getDist())+"\n";
        }
        return ret;
    }
}
