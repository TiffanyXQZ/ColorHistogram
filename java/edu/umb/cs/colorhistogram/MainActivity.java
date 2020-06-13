package edu.umb.cs.colorhistogram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.umb.cs.lsh.MyMinHash;
import edu.umb.cs.lsh.WeightedJaccard;

public class MainActivity extends AppCompatActivity {

    private MyAdapter adapt;
    private List<ImageData_MinHash> imData_List =new ArrayList<>();
    private List<ImageBitmaps> imBitmaps_List = new ArrayList<>();
    int index=0;// image index to be compared
    int num=5;//num: number of buckets for RGB to integers.
    int seed = 2020;
    long duration, startTime,endTime;
    //     Create minhash to calculte the minhash of images in folder "res-drawable"
//     a3160402803.jpg is the original image to be compared by other images
//     a111111, a222222, a333333, a444444, a555555, a666666, a777777, a888888, a999999 are the modified images from a316402803 (change brightness)
//     a5042835581.jpg is the complete different image from the a3160402803


    //     We set the parameters for minhash:
//     size: number of minhash signatures we want to calculate
//     dict_size: 10x10x10, the number of the hashed RGB integers, originally was 256x256x256
    MyMinHash minhash = new MyMinHash(0.2,num*num*num,seed);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Field[] drawablesFields = R.drawable.class.getFields();

        for (int i=0;i< drawablesFields.length;i++) {
            Field field=drawablesFields[i];

            field.getName().matches("a[0-9]+");
            try {
                if(field.getName().matches("a[0-9]+")){
                    Drawable img=getResources().getDrawable(field.getInt(null));
                    Bitmap bmp= BitmapFactory.decodeResource(getResources(), field.getInt(null));
//                    System.out.println(field.getName());
                    Bitmap copyBitmap=Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
                    /*crop white borders */
                    CropMiddleFirstPixelTransformation ct=new CropMiddleFirstPixelTransformation();
                    Bitmap bitmap = ct.transform(bmp);

                    imBitmaps_List.add(new ImageBitmaps(field.getName(),bitmap));
                    ImageData_MinHash imdata = new ImageData_MinHash(field.getName(),bitmap,num,minhash);
                    imData_List.add(imdata);

                    System.out.printf("\nMinhash value of image of " +
                            field.getName()+ " is: "+ Arrays.toString(imdata.getMin_hash()));

                    if (field.getName()=="a3160402803"){
                        index = i;
                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (ImageData_MinHash imageData: imData_List){
            System.out.printf("\nNumber of pixels and distinct colors in image of %-13s are: %-10d and %-10d",
                    imageData.getName(),imageData.getNum_pixel(), imageData.getNum_color());
        }

        for (ImageData_MinHash imageData: imData_List){
            System.out.printf("\nTime of rgb hashing and minhashing in image of %-15s are: %-10d and %-10d " ,
                    imageData.getName(), imageData.getTime_rgbhashing(), imageData.getTime_minhashing());
        }


// Time duration of MyMinHash Similarity calculation
        startTime = System.nanoTime();
        for (ImageData_MinHash imageData: imData_List){
            minhash.similarity(imData_List.get(index).getMin_hash(),imageData.getMin_hash());
        }
        endTime = System.nanoTime();
        duration = (endTime - startTime);
        System.out.printf("\nThe average time used for calculating minhash similarity is: %d nanoseconds \n", duration/imData_List.size());

// Time duration of real jaccard similarity calcuation
        startTime = System.nanoTime();
        for (ImageData_MinHash imageData: imData_List){
            minhash.jaccard(imData_List.get(index).getPixel_hash(),imageData.getPixel_hash());
        }
        endTime = System.nanoTime();
        duration = (endTime - startTime);
        System.out.printf("\nThe average time used for calculating Jaccard similarity is: %d nanoseconds\n", duration/imData_List.size());

        startTime = System.nanoTime();
        for (ImageData_MinHash imageData: imData_List){
            WeightedJaccard.similarity(imData_List.get(index).getColor_hist(),imageData.getColor_hist());
        }
        endTime = System.nanoTime();
        duration = (endTime - startTime);
        System.out.printf("\nThe average time used for calculating weighted Jaccard is: %d nanoseconds \n", duration/imData_List.size());


//     Consider all colors
//     Print minhash similarity between each image to a3160402803.jpg
//     Print Real Jaccard similarity between each image to a3160402803.jpg
//     Print Weighted Jaccard similarity between each image to a3160402803.jpg
        ImageData_MinHash.setN(10);// Top n
        int[] range = {2,10}; // Filter out the top 2 color from the top 10 colors, consider white background is usually the top 2 colors
        ImageData_MinHash.setRange(range);
        for (ImageData_MinHash imageData: imData_List){
            System.out.println("-----------------\n");
            System.out.printf("Minhash similarity, real Jaccad similarity and weighted Jaccard similarity of " + imageData.getName()
                    +" to a3160402803 are: "+minhash.similarity(imData_List.get(index).getMin_hash(),
                    imageData.getMin_hash()) + " and " +
                    minhash.jaccard(imData_List.get(index).getPixel_hash(),imageData.getPixel_hash()) + " and " +
                    WeightedJaccard.similarity(imData_List.get(index).getColor_hist(), imageData.getColor_hist())+ "\n");

//     Consider only TOP N colors
//     Print minhash similarity between each image to a3160402803.jpg
//     Print Real Jaccard similarity between each image to a3160402803.jpg
//     Print Weighted Jaccard similarity between each image to a3160402803.jpg
            System.out.printf("Minhash similarity, and Top " + ImageData_MinHash.getN()
                    + " color for real Jaccad similarity and weighted Jaccard similarity of " + imageData.getName()
                    + " to a3160402803 are: "+minhash.similarity(imData_List.get(index).getMin_hash(), imageData.getMin_hash())
                    + " and "
                    + minhash.jaccard(imData_List.get(index).getTopNColor().keySet(), imageData.getTopNColor().keySet())
                    + " and "
                    + WeightedJaccard.similarity(imData_List.get(index).getTopNColor(), imageData.getTopNColor())+ "\n\n");


//     Filter out top 1 and top 2 colors, Consider only TOP 3 to TOP N colors
//     Because we propose the white background of each product images usually belong to the top 2 colors.
//     Print minhash similarity between each image to a3160402803.jpg
//     Print Real Jaccard similarity between each image to a3160402803.jpg
//     Print Weighted Jaccard similarity between each image to a3160402803.jpg
            System.out.printf("Minhash similarity, and Range from " + Arrays.toString(ImageData_MinHash.getRange())
                    + " color for real Jaccad similarity and weighted Jaccard similarity of " + imageData.getName()
                    + " to a3160402803 are: "+minhash.similarity(imData_List.get(index).getMin_hash(), imageData.getMin_hash())
                    + " and "
                    + minhash.jaccard(imData_List.get(index).getTopRangeColor().keySet(), imageData.getTopRangeColor().keySet())
                    + " and "
                    + WeightedJaccard.similarity(imData_List.get(index).getTopRangeColor(), imageData.getTopRangeColor())+ "\n\n");


            System.out.println("-----------------\n");


        }


//        for (ImageData_MinHash imageData: imData_List){
//            System.out.printf("Minhash similarity, and Top " + ImageData_MinHash.getN()
//                    + " color for real Jaccad similarity and weighted Jaccard similarity of " + imageData.getName()
//                    + " to a3160402803 are: "+minhash.similarity(imData_List.get(index).getMin_hash(), imageData.getMin_hash())
//                    + " and "
//                    + minhash.jaccard(imData_List.get(index).getTopNColor().keySet(), imageData.getTopNColor().keySet())
//                    + " and "
//                    + WeightedJaccard.similarity(imData_List.get(index).getTopNColor(), imageData.getTopNColor())+ "\n");
//
//        }







// Below is trying to see how the difference between Minhash Similarity and Real Jaccard Similarity will trend
// by increasing the length of minhash signatures/values
//        List<AverageDifferenceBySize> accs = new ArrayList<>();
//
//        List<Integer> sizes = new ArrayList<>();
//        List<Double> accVals = new ArrayList<>();
//        for (int size=100;size<=1250;size+=50){
//            AverageDifferenceBySize averageAccuracyBySize = new AverageDifferenceBySize(imBitmaps_List,index,size,num,seed);
//            accs.add(averageAccuracyBySize);
//            sizes.add(size);
//            accVals.add(averageAccuracyBySize.getDiff_average());
//
//        }
//        System.out.println("The Minhash Signature Lengths are: "); // size: Minhash Signature/values length
//        for (int i=0;i<sizes.size();i++){
//            System.out.printf("%d,",sizes.get(i));
//        }


        //Average is taken by adding all difference between MyMinHash Similarity VS. Real Jaccard Similarity of
        //all pairs of pictures comparison, then divide the sum by the number of pairs of comparison
//        System.out.println("\n The corresponding average difference between Minhash Similarity VS. Real Jaccard Similarity are: ");
//        for (int i=0;i<sizes.size();i++){
//            System.out.printf("%f,",Math.abs(accVals.get(i)));
//        }
//
//        System.out.println("");
// End of the testing trend of difference between Minhash Similarity and Real Jaccard Similarity above.



//All below coding is the listview coding by Professor and edited by Xiaoqian to show attributes added in ImageData_Minhash
        adapt = new MyAdapter(this, R.layout.image_item, imData_List);
        ListView listTask = (ListView) findViewById(R.id.listview);
        listTask.setAdapter(adapt);
//
//        cal_update();
    }

//    void cal_update(){
//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                final Long st = System.currentTimeMillis();
//                calDistance();
//                final Long et= System.currentTimeMillis();
//
//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        final Toast toast=Toast.makeText(getApplicationContext(),"calDistance finished in "+
//                                Long.toString(et-st)+"ms",Toast.LENGTH_LONG);
//                        toast.show();
//                        adapt.notifyDataSetChanged();
//
//                    }
//                });
//            }
//        });
//    }
//    @Override
//    protected void onResume(){
//        super.onResume();
//
//    }
//

    String topHist(int idx, int rank){
        String ret=new String();
        ret+=Integer.toString(idx)+":"+Integer.toString(rank);

        return ret;
    }

    private class MyAdapter extends ArrayAdapter<Drawable> {
        Context context;
        private List<ImageData_MinHash> imgs=new ArrayList<>();

        public MyAdapter(Context c, int rId, List objects) {
            super(c, rId, objects);
            imgs = objects;
            context = c;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {

                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.image_item, parent, false);
            }

                ImageView img= (ImageView) convertView.findViewById(R.id.imageView);
                img.setImageBitmap(imBitmaps_List.get(position).getBitmap());
                img.setBackgroundColor(Color.parseColor("#000000"));

                TextView txv=convertView.findViewById(R.id.textView2);


                txv.setText("Image "+Integer.toString(position)
                        +"\nNumber of pixels: "+imgs.get(position).getNum_pixel()
                        +"\nNumber of colors: "+imgs.get(position).getNum_color()
                        +"\nNumber of buckets: "+imgs.get(position).getNum()

                        +"\nTime of rhb_hashing: "+imgs.get(position).getTime_rgbhashing()
                        +"\nTime of min_hashing: "+imgs.get(position).getTime_minhashing()
                        +"\nTop " + ImageData_MinHash.getN() + " colors are : "+
                        imgs.get(position).getTopNColor()
                        +"\n Range from " + Arrays.toString(ImageData_MinHash.getRange()) + " colors are : "+
                        imgs.get(position).getTopRangeColor()

                );



            return convertView;
        }


    }

//    public void doit(View view){
//        EditText edt=findViewById(R.id.editText);
//        if(edt.getText()!=null&&edt.getText().length()>0){
//            int num=Integer.parseInt(edt.getText().toString());
//            for(ImageData imgData : imData_List){
//                imgData.calHist(num);
//            }
//
//            cal_update();
//        }
//    }


//    public void calDistance(){
//        for(int i = 0; i< imData_List.size(); i++) {
//            ImageData id1 = imData_List.get(i);
//
//            for (int j = i + 1; j < imData_List.size(); j++) {
//                ImageData id2= imData_List.get(j);
//
//                //calculate Euclidean dist, can be replaced by others
//                double dist=calEuclideanDistance(id1, id2);
//
//                id1.addDistList(id2,dist);
//                id2.addDistList(id1,dist);
//            }
//        }
//    }

//    public double calEuclideanDistance(ImageData id1, ImageData id2){
//        int[] hist1=id1.getBinHist();
//        int[] hist2=id2.getBinHist();
//        int p1=id1.getNumOfPixels(), p2=id2.getNumOfPixels();
//        double sum=0;
//        for(int i=0;i<hist1.length;i++){
//            sum+=Math.pow(((double)hist1[i]/p1-(double)hist2[i]/p2),2);
//        }
//        return Math.sqrt(sum);
//    }
}
















//        MyMinHash minHash = new MyMinHash(0.1,128);
//        Set<Integer> set1 = new HashSet<>();
//        for (int t : imData_List.get(0).getOrig_pixels())
//            set1.add(t);
//        Set<Integer> set2 = new HashSet<>();
//        for (int t : imData_List.get(1).getOrig_pixels())
//            set2.add(t);
//
////        System.out.printf("the two images been compared are %s and %s", imData_List.get(0).g)
//        System.out.printf("the lenght of set1 and set2 are %d and %d\n ", set1.size(),set2.size());


//        long startTime = System.nanoTime();
//        int[] sig1 = minHash.signature(set1);
//        long time = System.nanoTime() -startTime;
//        System.out.println("time for getting signature is: " + time/1000000 + "milliseconds");
//
//        int[] sig2 = minHash.signature(set2);
//        System.out.println("Signature similarity: " + minHash.similarity(sig1, sig2));
//        System.out.println("Real similarity (Jaccard index)" +
//                MyMinHash.jaccardIndex(set1, set2));