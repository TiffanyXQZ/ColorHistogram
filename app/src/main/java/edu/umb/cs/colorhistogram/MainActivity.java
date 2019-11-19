package edu.umb.cs.colorhistogram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MyAdapter adapt;
    private List<ImageData> img_list=new ArrayList<ImageData>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Field[] drawablesFields = R.drawable.class.getFields();

        for (int i=0;i< drawablesFields.length;i++) {
            Field field=drawablesFields[i];
            try {
                if(field.getName().startsWith("a")){
                    Drawable img=getResources().getDrawable(field.getInt(null));

                    Bitmap bmp= BitmapFactory.decodeResource(getResources(), field.getInt(null));

                    /*crop white borders */
                    CropMiddleFirstPixelTransformation ct=new CropMiddleFirstPixelTransformation();

                    img_list.add(new ImageData(ct.transform(bmp),i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



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
                        +"\n\n"+imgs.get(position).getTopNString(5));



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
