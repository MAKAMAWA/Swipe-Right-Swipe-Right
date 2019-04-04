package com.example.a32;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.sql.Blob;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase db = null;
    byte[] ba ;
    int totalResult;
    int offsetNum = 0;
    Cursor countC;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = openOrCreateDatabase("MyDatabse", Context.MODE_PRIVATE, null);

        db.execSQL("DROP TABLE IF EXISTS Photos;");



        db.execSQL("create table Photos( Photo Blob, Tag text, Size int, ID int);");




    }


    public  void forward(View view){
        offsetNum += 1;


        Context context = getApplicationContext();

        int duration = Toast.LENGTH_SHORT;

        // get value of tag and size after load

        EditText tag = (EditText) findViewById(R.id.editText);

        EditText size = (EditText) findViewById(R.id.editText3);


        String t = tag.getText().toString();



        String s = size.getText().toString();



        String offset = Integer.toString(offsetNum);

        Cursor c;


        c = db.rawQuery("SELECT  Photo ,Tag, Size  FROM Photos WHERE Tag = ? and Size = ? order by ID limit 1 OFFSET ? ", new String[] {t,s,offset});

        c.moveToFirst();

        if(c.getCount()!=0){




            byte[] ba = c.getBlob(0);

            String text = c.getString(1);

            String text2 = c.getString(2);


            c.moveToNext();

            tag.setText(text);

            size.setText(text2);

            Bitmap b = BitmapFactory.decodeByteArray(ba, 0, ba.length);

            ImageView img = (ImageView) findViewById(R.id.imageView);
            img.setImageBitmap(b);

        }else{

            offsetNum -= 1;

            CharSequence text = "No Result !";
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

        }





    }



    public  void back(View view){

        Context context = getApplicationContext();

        int duration = Toast.LENGTH_SHORT;

        if(offsetNum -1 >=0){

            offsetNum -= 1;




            // get value of tag and size after load

            EditText tag = (EditText) findViewById(R.id.editText);

            EditText size = (EditText) findViewById(R.id.editText3);


            String t = tag.getText().toString();



            String s = size.getText().toString();



            String offset = Integer.toString(offsetNum);

            Cursor c;


            c = db.rawQuery("SELECT  Photo ,Tag, Size  FROM Photos WHERE Tag = ? and Size = ? order by ID limit 1 OFFSET ? ", new String[] {t,s,offset});

            c.moveToFirst();

            if(c.getCount()!=0){




                byte[] ba = c.getBlob(0);

                String text = c.getString(1);

                String text2 = c.getString(2);


                c.moveToNext();

                tag.setText(text);

                size.setText(text2);

                Bitmap b = BitmapFactory.decodeByteArray(ba, 0, ba.length);

                ImageView img = (ImageView) findViewById(R.id.imageView);
                img.setImageBitmap(b);

            }else{

                offsetNum += 1;

                CharSequence text = "No Result !";
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

            }






        }else{



            CharSequence text = "No Result !";
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

        }





    }

    public void load(View view){

        totalResult = 0;

        // Toast

        Context context = getApplicationContext();

        int duration = Toast.LENGTH_SHORT;

        EditText tag = (EditText) findViewById(R.id.editText);

        EditText size = (EditText) findViewById(R.id.editText3);

        //test

        TextView test = (TextView) findViewById(R.id.test);


        String t = tag.getText().toString();

        String[] tagArray = t.split(";");

        String s = size.getText().toString();

        //change string s to int sizeResult

        int sizeResult;
        try {
            sizeResult = Integer.parseInt(s);
        }
        catch (NumberFormatException e)
        {
            sizeResult = 0;
        }


        int max = (int)Math.round(sizeResult*1.25);

        int min = (int)Math.round(sizeResult*0.75);

        String maxResult = String.valueOf(max);

        String minResult = String.valueOf(min);



        Cursor c;

        //Cursor countC;





        if(s.isEmpty()){

            // initialize c

            c = db.rawQuery("SELECT  Photo , Size FROM Photos WHERE Tag = ? order by ID limit 1 ", new String[] {tagArray[0]});


            for(int i = 0; i< tagArray.length;i++){

                c = db.rawQuery("SELECT  Photo , Size FROM Photos WHERE Tag = ? order by ID limit 1 ", new String[] {tagArray[i]});



                if(c.getCount()!=0){
                    break;
                }

            }


            // count total result number


            for(int i = 0; i< tagArray.length;i++){

                countC = db.rawQuery("SELECT  count(*) FROM Photos WHERE Tag = ? ", new String[] {tagArray[i]});



                countC.moveToFirst();

                for(int m = 0; m < countC.getCount(); m++){

                    for(int j = 0; j < countC.getColumnCount(); j++) {
                        String r = countC.getString(j);

                        try {
                            totalResult = totalResult + Integer.parseInt(r);
                        }
                        catch (NumberFormatException e)
                        {
                            totalResult = 0;
                        }
                    }

                    countC.moveToNext();

                }


            }



        }
        else if (t.isEmpty()){
            c = db.rawQuery("SELECT  Photo , Size FROM Photos WHERE Size BETWEEN ? AND ? order by ID limit 1 ", new String[] {minResult,maxResult});


            // count total result number


            for(int i = 0; i< tagArray.length;i++){

                countC = db.rawQuery("SELECT  count(*) FROM Photos WHERE Size BETWEEN ? AND ? ", new String[] {minResult,maxResult});



                countC.moveToFirst();

                for(int m = 0; m < countC.getCount(); m++){

                    for(int j = 0; j < countC.getColumnCount(); j++) {
                        String r = countC.getString(j);

                        try {
                            totalResult = totalResult + Integer.parseInt(r);
                        }
                        catch (NumberFormatException e)
                        {
                            totalResult = 0;
                        }
                    }

                    countC.moveToNext();

                }


            }
        }
        else{
            c = db.rawQuery("SELECT  Photo , Size FROM Photos WHERE Tag = ? and Size BETWEEN ? AND ? order by ID limit 1 ", new String[] {tagArray[0],minResult,maxResult});


            for(int i = 0; i< tagArray.length;i++){

                c = db.rawQuery("SELECT  Photo , Size FROM Photos WHERE Tag = ? and Size BETWEEN ? AND ? order by ID limit 1 ", new String[] {tagArray[i],minResult,maxResult});


                if(c.getCount()!=0){
                    break;
                }



            }

            // count total result number


            for(int i = 0; i< tagArray.length;i++){

                countC = db.rawQuery("SELECT  count(*) FROM Photos WHERE Tag = ? and Size BETWEEN ? AND ?  ", new String[] {tagArray[i],minResult,maxResult});



                countC.moveToFirst();

                for(int m = 0; m < countC.getCount(); m++){

                    for(int j = 0; j < countC.getColumnCount(); j++) {
                        String r = countC.getString(j);

                        try {
                            totalResult = totalResult + Integer.parseInt(r);
                        }
                        catch (NumberFormatException e)
                        {
                            totalResult = 0;
                        }
                    }

                    countC.moveToNext();

                }


            }



        }



        c.moveToFirst();

        if(c.getCount()!=0){
            byte[] ba = c.getBlob(0);

            String text = c.getString(1);

            c.moveToNext();

            size.setText(text);

            Bitmap b = BitmapFactory.decodeByteArray(ba, 0, ba.length);

            ImageView img = (ImageView) findViewById(R.id.imageView);
            img.setImageBitmap(b);
        }else{

            CharSequence text = "No Result !";
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

        }

        // test

        test.setText(Integer.toString(totalResult));








    }


    public void save(View view){

        // Can't use this line don't know why
        // db.execSQL("INSERT INTO Photos VALUES (ba , 'UNC' ,100);");




        // get tags and size

        EditText tag = (EditText) findViewById(R.id.editText);

        EditText size = (EditText) findViewById(R.id.editText3);


        String t = tag.getText().toString();

        String[] tagArray = t.split(";");

        String s = size.getText().toString();

        //change string s to int sizeResult

        int sizeResult;
        try {
            sizeResult = Integer.parseInt(s);
        }
        catch (NumberFormatException e)
        {
            sizeResult = 0;
        }



        // toast

        Context context = getApplicationContext();

        int duration = Toast.LENGTH_SHORT;





        // Check if tag or size is empty



        if(t.isEmpty() && s.isEmpty()){
            CharSequence text = "Take a Photo First !";
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return;
        }

        if(t.isEmpty() ){
            CharSequence text = "Please Add Tag !";
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return;
        }

        for(int i = 0; i < tagArray.length ; i++){

            Cursor c = db.rawQuery("SELECT COUNT(*) FROM Photos  ", null);


            c.moveToFirst();

            int count = c.getInt(0);


            c.moveToNext();


            ContentValues cv = new ContentValues();
            cv.put("Photo", ba);
            cv.put("Tag", tagArray[i]);
            cv.put("Size", sizeResult);
            cv.put("ID", count);


            db.insert("Photos", null, cv);

            Log.v("tag", "Adding photo with tag/tags to table");


        }


        tag.setText("");
        size.setText("");

        CharSequence text = "Photo Saved !";
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();



        /*

        Cursor c = db.rawQuery("SELECT * from Photos", null);
        c.moveToFirst();

        for(int i = 0; i < c.getCount(); i++){

            for(int j = 0; j < c.getColumnCount(); j++) {
                Log.v("Mytag", "Yes");
            }

            c.moveToNext();

        }

        */

    }


    public void foo(View view){
        Intent w = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(w, 1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent x)
    {
        //if (requestCode == 1 && resultCode == RESULT_OK)
        {
            Bundle extras = x.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            int s = imageBitmap.getByteCount();
            String s2 = String.valueOf(s);


            ImageView img = (ImageView) findViewById(R.id.imageView);
            img.setImageBitmap(imageBitmap);

            TextView size = (TextView) findViewById(R.id.editText3);
            size.setText(s2);


            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            ba = stream.toByteArray();

        }
    }
}
