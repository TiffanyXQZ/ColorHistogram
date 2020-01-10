package edu.umb.cs.colorhistogram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import info.debatty.java.lsh.MinHash;

public class MainActivity extends AppCompatActivity {

    private MyAdapter adapt;
    private List<ImageData> img_list=new ArrayList<ImageData>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int index=0;
        int num=10;//num: number of buckets for RGB to integers.


//     Create minhash to calculte the minhash of images in folder "res-drawable"
//     a3160402803.jpg is the original image to be compared by other images
//     a111111, a222222, a333333, a444444, a555555, a666666, a777777, a888888, a999999 are the modified images from a316402803 (change brightness)
//     a5042835581.jpg is the complete different image from the a3160402803


//     We set the parameters for minhash:
//     size: number of minhash signatures we want to calculate
//     dict_size: 10x10x10, the number of the hashed RGB integers, originally was 256x256x256
        MinHash minhash = new MinHash(1000,num*num*num);

        Field[] drawablesFields = R.drawable.class.getFields();
        long duration, startTime,endTime;
        List<ImageData> imageDatas = new ArrayList<>();
        List<ImageBitmaps> imageBitmaps = new ArrayList<>();


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
                    imageBitmaps.add(new ImageBitmaps(field.getName(),bitmap));
                    ImageData imageData =new ImageData(bitmap,field.getName());


//     change RGB to an integer, and merge the 0-255 into num = 10 buckets, 256x256x256 to 10x10x10
                    startTime = System.nanoTime();
                    int[] imageRGB_Hash = imageData.getPixels_Hash(num);
                    endTime = System.nanoTime();
                    duration = endTime - startTime;
                    imageData.setTime_rgbhash(duration);

//     Below is calculating minhash signatures (minhash values) of this image

                    startTime = System.nanoTime();
                    Set<Integer> set = new HashSet<>();
                    for (int t : imageRGB_Hash)
                        set.add(t);
                    int[] minHash = minhash.signature(set);//this minHash is the minhash value of this image
                    endTime = System.nanoTime();
                    duration = endTime - startTime;

                    imageData.setColorSet(set);
                    imageData.setNum_color(set.size());
                    imageData.setTime_minhash(duration);
                    imageData.setMinhash(minHash);
                    imageDatas.add(imageData);



                    System.out.printf("\nMinhash value of image of " +
                            field.getName()+ " is: "+imageData.getMinHashString());




                    img_list.add(new ImageData(bitmap,field.getName()));

                    if (field.getName()=="a3160402803"){
                        index = i;
                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (ImageData imageData: imageDatas){
            System.out.printf("\nNumber of pixels and distinct colors in image of %-13s are: %-10d and %-10d",
                    imageData.getImage_name(),imageData.getNum_pixels(), imageData.getNum_color());
        }

        for (ImageData imageData: imageDatas){
            System.out.printf("\nTime of rgb hashing and minhashing in image of %-15s are: %-10d and %-10d " ,
                    imageData.getImage_name(), imageData.getTime_rgbhash(), imageData.getTime_minhash());
        }


// Time duration of MinHash Similarity calculation
        startTime = System.nanoTime();
        for (int i=0;i< imageDatas.size();i++) {
            minhash.similarity(imageDatas.get(index).getMinhash(),imageDatas.get(i).getMinhash());
        }
        endTime = System.nanoTime();
        duration = (endTime - startTime);
        System.out.printf("\nThe average time used for calculating minhash similarity is: %d nanoseconds \n", duration/imageDatas.size());

// Time duration of real jaccard similarity calcuation
        startTime = System.nanoTime();
        for (int i=0;i< imageDatas.size();i++) {
            minhash.jaccardIndex(imageDatas.get(index).getColorSet(),imageDatas.get(i).getColorSet());
        }
        endTime = System.nanoTime();
        duration = (endTime - startTime);
        System.out.printf("\nThe average time used for calculating Jaccard similarity is: %d nanoseconds\n", duration/imageDatas.size());


//     Print minhash and real similarity between each image to a3160402803.jpg
        for (int i=0;i<imageDatas.size();i++) {
            System.out.printf("Minhash similarity and real Jaccad similarity of " + img_list.get(i).getImage_name()
                    +" to a3160402803 are: "+minhash.similarity(imageDatas.get(index).getMinhash(),
                    imageDatas.get(i).getMinhash()) + " and " +
                    minhash.jaccardIndex(imageDatas.get(index).getColorSet(),imageDatas.get(i).getColorSet()) + "\n");

        }


// Below is trying to see how the difference between Minhash Similarity and Real Jaccard Similarity will trend
// by increasing the length of minhash signatures/values
        List<AverageDifferenceBySize> accs = new ArrayList<>();

        List<Integer> sizes = new ArrayList<>();
        List<Double> accVals = new ArrayList<>();
        for (int size=100;size<=1250;size+=50){
            AverageDifferenceBySize averageAccuracyBySize = new AverageDifferenceBySize(imageBitmaps,index,size,num);
            accs.add(averageAccuracyBySize);
            sizes.add(size);
            accVals.add(averageAccuracyBySize.getDiff_average());

        }
        System.out.println("The Minhash Signature Lengths are: "); // size: Minhash Signature/values length
        for (int i=0;i<sizes.size();i++){
            System.out.printf("%d,",sizes.get(i));
        }


        //Average is taken by adding all difference between MinHash Similarity VS. Real Jaccard Similarity of
        //all pairs of pictures comparison, then divide the sum by the number of pairs of comparison
        System.out.println("\n The corresponding average difference between Minhash Similarity VS. Real Jaccard Similarity are: ");
        for (int i=0;i<sizes.size();i++){
            System.out.printf("%f,",Math.abs(accVals.get(i)));
        }

        System.out.println("");
// End of the testing trend of difference between Minhash Similarity and Real Jaccard Similarity above.



//All below coding is the listview coding by Professor
        adapt = new MyAdapter(this, R.layout.image_item, img_list);
        ListView listTask = (ListView) findViewById(R.id.listview);
        listTask.setAdapter(adapt);

        cal_update();
    }

    void cal_update(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                final Long st = System.currentTimeMillis();
                calDistance();
                final Long et= System.currentTimeMillis();

                runOnUiThread(new Runnable() {
                    public void run() {
                        final Toast toast=Toast.makeText(getApplicationContext(),"calDistance finished in "+
                                Long.toString(et-st)+"ms",Toast.LENGTH_LONG);
                        toast.show();
                        adapt.notifyDataSetChanged();

                    }
                });
            }
        });
    }
    @Override
    protected void onResume(){
        super.onResume();

    }


    String topHist(int idx, int rank){
        String ret=new String();
        ret+=Integer.toString(idx)+":"+Integer.toString(rank);

        return ret;
    }

    private class MyAdapter extends ArrayAdapter<Drawable> {
        Context context;
        private List<ImageData> imgs=new ArrayList<ImageData>();

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
                img.setImageBitmap(imgs.get(position).getBitmap());
                img.setBackgroundColor(Color.parseColor("#000000"));

                TextView txv=convertView.findViewById(R.id.textView2);


                txv.setText("Image "+Integer.toString(position)
                        +"\n\n"+imgs.get(position).getTopNString(5)
//                        +"\nMinHash:"+imgs.get(position).getMinHashString()
                );



            return convertView;
        }


    }

    public void doit(View view){
        EditText edt=findViewById(R.id.editText);
        if(edt.getText()!=null&&edt.getText().length()>0){
            int num=Integer.parseInt(edt.getText().toString());
            for(ImageData imgData : img_list){
                imgData.calHist(num);
            }

            cal_update();
        }
    }


    public void calDistance(){
        for(int i=0;i<img_list.size();i++) {
            ImageData id1 = img_list.get(i);

            for (int j = i + 1; j < img_list.size(); j++) {
                ImageData id2=img_list.get(j);

                //calculate Euclidean dist, can be replaced by others
                double dist=calEuclideanDistance(id1, id2);

                id1.addDistList(id2,dist);
                id2.addDistList(id1,dist);
            }
        }
    }

    public double calEuclideanDistance(ImageData id1, ImageData id2){
        int[] hist1=id1.getBinHist();
        int[] hist2=id2.getBinHist();
        int p1=id1.getNumOfPixels(), p2=id2.getNumOfPixels();
        double sum=0;
        for(int i=0;i<hist1.length;i++){
            sum+=Math.pow(((double)hist1[i]/p1-(double)hist2[i]/p2),2);
        }
        return Math.sqrt(sum);
    }
}
















//        MinHash minHash = new MinHash(0.1,128);
//        Set<Integer> set1 = new HashSet<>();
//        for (int t : img_list.get(0).getOrig_pixels())
//            set1.add(t);
//        Set<Integer> set2 = new HashSet<>();
//        for (int t : img_list.get(1).getOrig_pixels())
//            set2.add(t);
//
////        System.out.printf("the two images been compared are %s and %s", img_list.get(0).g)
//        System.out.printf("the lenght of set1 and set2 are %d and %d\n ", set1.size(),set2.size());


//        long startTime = System.nanoTime();
//        int[] sig1 = minHash.signature(set1);
//        long time = System.nanoTime() -startTime;
//        System.out.println("time for getting signature is: " + time/1000000 + "milliseconds");
//
//        int[] sig2 = minHash.signature(set2);
//        System.out.println("Signature similarity: " + minHash.similarity(sig1, sig2));
//        System.out.println("Real similarity (Jaccard index)" +
//                MinHash.jaccardIndex(set1, set2));