package mape3.project;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class HMI extends DetaliiFragmente {

    public static HMI newInstance(int index) {
        HMI f = new HMI();
        // preluam argumentele
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        return f;
    }
    //definim sursa pentru MediaStore.Images.Media (stocare interna sau externa)
    Uri sourceUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;//sau INTERNAL_CONTENT_URI;

    final Uri thumbUri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
    final String thumb_DATA = MediaStore.Images.Thumbnails.DATA;
    final String thumb_IMAGE_ID = MediaStore.Images.Thumbnails.IMAGE_ID;
    final String source_DATA = MediaStore.Images.Media.DATA;


    //definim un CursorAdapter
    MyAdapter mySimpleCursorAdapter;
    GridView myGridView;
    private static final int EXTERNAL_STORAGE_COD=1;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        View view = inflater.inflate(R.layout.hmi_layout, container, false);
        myGridView = (GridView) view.findViewById(R.id.gridview);

        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},EXTERNAL_STORAGE_COD);
            }
            else{
                return null;
            }
        }
        myGridView = (GridView)view.findViewById(R.id.gridview);
        myGridView.setOnTouchListener(new Swipe(getContext()));

        String[] from = {MediaStore.MediaColumns.TITLE};

        int[] to = {android.R.id.text1};
        CursorLoader cursorLoader= new CursorLoader(
                getContext(),
                sourceUri,
                null,
                null,
                null,
                MediaStore.Audio.Media.TITLE);
        Cursor cursor = cursorLoader.loadInBackground();

        mySimpleCursorAdapter = new MyAdapter(
                getContext(),
                android.R.layout.simple_list_item_1,
                cursor,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        myGridView.setAdapter(mySimpleCursorAdapter);
        myGridView.setOnItemClickListener(myOnItemClickListener);
        return view;
    }

        AdapterView.OnItemClickListener myOnItemClickListener = new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Cursor cursor = mySimpleCursorAdapter.getCursor();
            cursor.moveToPosition(position);

            int int_ID = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
            //getThumbnail(int_ID);
            openInGallery(int_ID+"");
        }};


    @RequiresApi(api = Build.VERSION_CODES.M)
    private Bitmap getThumbnail(int id){

        String[] thumbColumns = {thumb_DATA, thumb_IMAGE_ID};

        CursorLoader thumbCursorLoader = new CursorLoader(
                getContext(),
                thumbUri,
                thumbColumns,
                thumb_IMAGE_ID + "=" + id,
                null,
                null);

        Cursor thumbCursor = thumbCursorLoader.loadInBackground();

        Bitmap thumbBitmap = null;
        if(thumbCursor.moveToFirst()){
            Uri uriSourcePath = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().appendPath(id+"").build();

            Toast.makeText(getContext(),
                    uriSourcePath.getPath(),
                    Toast.LENGTH_LONG).show();

            //thumbBitmap = BitmapFactory.decodeFile(thumbPath);

            try{
                File file = new File(uriSourcePath.toString());
                FileInputStream image_stream = new FileInputStream(file);

                thumbBitmap= BitmapFactory.decodeStream(image_stream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


            //Create a Dialog to display the thumbnail
            AlertDialog.Builder thumbDialog = new AlertDialog.Builder(getContext());
            ImageView thumbView = new ImageView(getContext());
            thumbView.setImageBitmap(thumbBitmap);
            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.addView(thumbView);
            thumbDialog.setView(layout);
            thumbDialog.show();

        }else{
            Toast.makeText(getContext(),
                    "NO Thumbnail!",
                    Toast.LENGTH_LONG).show();
        }

        return thumbBitmap;
    }
    public void openInGallery(String imageId) {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().appendPath(imageId).build();
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case EXTERNAL_STORAGE_COD:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "EXTERNAL STORAGE - Ok", Toast.LENGTH_SHORT) .show();
                }
                else {
                    Toast.makeText(getContext(), "EXTERNAL STORAGE - Denied", Toast.LENGTH_SHORT) .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults); }
    }
    public class MyAdapter extends SimpleCursorAdapter {

        Cursor myCursor;
        Context myContext;

        public MyAdapter(Context context, int layout, Cursor c, String[] from,
                         int[] to, int flags) {
            super(context, layout, c, from, to, flags);

            myCursor = c;
            myContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if(row==null){
                LayoutInflater inflater= LayoutInflater.from(getContext());
                row=inflater.inflate(R.layout.row, parent, false);
            }

            ImageView thumbV = (ImageView)row.findViewById(R.id.thumb);

            myCursor.moveToPosition(position);

            int myID = myCursor.getInt(myCursor.getColumnIndex(MediaStore.Images.Media._ID));

            String[] thumbColumns = {thumb_DATA, thumb_IMAGE_ID};
            CursorLoader thumbCursorLoader = new CursorLoader(
                    myContext,
                    thumbUri,
                    thumbColumns,
                    thumb_IMAGE_ID + "=" + myID,
                    null,
                    null);
            Cursor thumbCursor = thumbCursorLoader.loadInBackground();

            Bitmap myBitmap = null;
            if(thumbCursor.moveToFirst()){
                int thCulumnIndex = thumbCursor.getColumnIndex(thumb_DATA);
                String thumbPath = thumbCursor.getString(thCulumnIndex);
                myBitmap = BitmapFactory.decodeFile(thumbPath);
                thumbV.setImageBitmap(myBitmap);
            }

            return row;
        }

    }

}
