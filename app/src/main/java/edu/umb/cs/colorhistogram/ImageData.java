package edu.umb.cs.colorhistogram;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.ArrayMap;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


public class ImageData {
    private Bitmap bitmap=null;
    protected int[] getOrig_pixels() {
        return orig_pixels;
    }
    private int[] orig_pixels;
    private int[] bin_idx;
    private int[] bin_hist;


    private int num_pixels;
    private int num_color;
    private long time_rgbhash;
    private long time_minhash;
    private Set<Integer> colorSet;

    public Set<Integer> getColorSet() {
        return colorSet;
    }

    public void setColorSet(Set<Integer> colorSet) {
        this.colorSet = colorSet;
    }

    public int getNum_pixels() {
        return num_pixels;
    }

    public void setNum_pixels(int num_pixels) {
        this.num_pixels = num_pixels;
    }

    public int getNum_color() {
        return num_color;
    }

    public void setNum_color(int num_color) {
        this.num_color = num_color;
    }

    public long getTime_rgbhash() {
        return time_rgbhash;
    }

    public void setTime_rgbhash(long time_rgbhash) {
        this.time_rgbhash = time_rgbhash;
    }

    public long getTime_minhash() {
        return time_minhash;
    }

    public void setTime_minhash(long time_minhash) {
        this.time_minhash = time_minhash;
    }

    public String getImage_name() {
        return image_name;
    }

    private String image_name;
    private int[] minhash;


    //Xiaoqian added
    // minhash values
    public int[] getMinhash() { return minhash; }
    public void setMinhash(int[] minhash) { this.minhash = minhash; }


    //Xiaoqian added
    //pixels_hash: RGB to integers
    private int[] pixels_Hash;
    public void setPixels_Hash(int[] pixels_Hash) {
        this.pixels_Hash = pixels_Hash;
    }
    public int[] getPixels_Hash() {
        return pixels_Hash;
    }



    //Xiaoqian added
    // RGB three dimensions to int one dimension
    //num: 0-255 to num buckets
    public int[] getPixels_Hash(int num){
        int[] pixels_Hash = new int[this.orig_pixels.length];

        int w=(int)Math.ceil((float)256/num);
        for (int j=0;j<this.orig_pixels.length;j++) {
            int red = Color.red(orig_pixels[j]);
            int green = Color.green(orig_pixels[j]);
            int blue = Color.blue(orig_pixels[j]);

            pixels_Hash[j] = (int) Math.floor(red / w) * num * num
                    + (int) Math.floor(green / w) * num
                    + (int) Math.floor(blue / w);
        }

        this.setPixels_Hash(pixels_Hash);
        return pixels_Hash;
    }




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

    ImageData(Bitmap bmp, String name){
        bitmap=bmp;
        image_name = name;
        orig_pixels=readPixels(bitmap);
        bin_idx=new int[orig_pixels.length];
        calHist(num_default);
    }

    protected int getId(){return id;}
    protected int getNumOfPixels(){return orig_pixels.length;}

    private int[] readPixels (Bitmap bitmap){

        this.setNum_pixels(bitmap.getWidth() * bitmap.getHeight());
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

    public String getMinHashString(){
        return Arrays.toString(this.getMinhash());
    }

    public static void main(String[] args) {
        Field[] drawablesFields = R.drawable.class.getFields();
        Field field=drawablesFields[1];
        System.out.printf("images number,"+field.getName());


        Bitmap bmp = BitmapFactory.decodeFile("res/drawable/a01.jpg");


        System.out.printf("\n,"+bmp.getHeight());

//        try {
//            System.out.printf("images number,"+field.getInt(null));
////            Bitmap bmp= BitmapFactory.decodeResource(getResources(), field.getInt(null));
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }
    }

}
