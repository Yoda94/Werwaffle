package layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.philip.werwaffle.R;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import static java.nio.charset.StandardCharsets.UTF_8;


public class EditProfil extends AppCompatActivity {

    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;
    public EditText inputTxt;
    public ImageButton selectBut;
    private SharedPreferences prefSettings;
    public String aName;
    String fileName;
    ImageView imgView;
    Bitmap bitmap;

    public void init(){

        inputTxt = (EditText) findViewById(R.id.creatlobname);
        selectBut = (ImageButton) findViewById(R.id.imageBut);

        prefSettings = getSharedPreferences("profil", MODE_PRIVATE);
        aName = prefSettings.getString("name", "Empty");

        inputTxt.setText(aName);

        imgView = (ImageView) findViewById(R.id.imageProfile);
        prefSettings = getSharedPreferences("profil", MODE_PRIVATE);
        fileName = prefSettings.getString("uniqueKEy", "None");



        String read = readFromFile(fileName, EditProfil.this);
        String decompressed = decompressString(read);
        if (encodeStringToBitmap(decompressed)!=null) {
            bitmap = encodeStringToBitmap(decompressed);
            try {
                imgView.setImageBitmap(com.example.philip.werwaffle.activity.RoundedImageView.getCroppedBitmap(
                        bitmap,2000 ));
            }catch (Exception e){
                System.out.println("There is no ImgFile: "+e);
            }
        }


        selectBut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void  onClick(View v){
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);

            }

        });

    };


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);

                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();

                //size
                if (sizeOf(imgDecodableString) > 300){
                    Toast.makeText(this, getString(R.string.image_to_lange), Toast.LENGTH_LONG).show();
                    return;
                }
                String string = encodeToBase64(imgDecodableString, Bitmap.CompressFormat.JPEG, 20);
                String compressed = compressString(string);
                writeToFile(fileName, compressed);
                String read = readFromFile(fileName, EditProfil.this);
                String decompressed = decompressString(read);
                Bitmap bitmap = encodeStringToBitmap(decompressed);

                imgView.setImageBitmap(com.example.philip.werwaffle.activity.RoundedImageView.getCroppedBitmap(
                        bitmap,2000 ));


            } else {
                Toast.makeText(this, getString(R.string.havent_picked_img), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.went_wrong), Toast.LENGTH_LONG)
                    .show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profil);
        init();
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        String name = inputTxt.getText().toString();
        SharedPreferences.Editor prefEditor = getSharedPreferences("profil", MODE_PRIVATE).edit();
        SharedPreferences preferences = getSharedPreferences("profil", MODE_PRIVATE);
        prefEditor.putString("name", name);
        prefEditor.apply();
        String uniqkey = preferences.getString("uniqueKEy", "None");

        addPlayer.addPlayer(name,"None",2,0,uniqkey, this);

        addPlayer.me(uniqkey).setName(name);

    }


    private String compressString(String string){
        Deflater deflater = new Deflater();
        byte [] target = new byte[string.getBytes(UTF_8).length];
        //System.out.println("Size compress: "+string.getBytes(UTF_8).length);
        try{
            deflater.setInput(string.getBytes(UTF_8));
            deflater.finish();
            deflater.deflate(target);
            return Base64.encodeToString(target, Base64.DEFAULT);
        } catch (Exception e){
            System.out.println("Error in compressString");
            System.out.println(e);
        }
        return string;
    }
    private String decompressString(String data){
        String result = null;
        try {
            byte [] input = Base64.decode(data, Base64.DEFAULT);
            Inflater inflater = new Inflater();
            int inputLenght = input.length;
            inflater.setInput(input, 0, inputLenght);

            byte [] outpit = new byte[data.getBytes(UTF_8).length];
            System.out.println("Size decompress: "+data.getBytes(UTF_8).length);
            int resultLenght = inflater.inflate(outpit);
            inflater.end();

            result = new String(outpit, 0, resultLenght);
            //System.out.println("decode result: "+result);
        } catch (DataFormatException e){
            System.out.println("Error in decompressString");
            System.out.println(e);
        }
        return result;
    }
    public String encodeToBase64(String path, Bitmap.CompressFormat compressFormat, int quality)
    {

        Bitmap image = BitmapFactory.decodeFile(path);
        System.out.println("Bitmape Size: "+sizeOf(path));
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        String result = Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
        System.out.println("BitmapeString Size: "+result.getBytes(UTF_8).length);
        return result;
    }

    protected long sizeOf(String data) {
        File file = new File(data);
        // Get length of file in bytes
        long fileSizeInBytes = file.length();
        // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
        long fileSizeInKB = fileSizeInBytes / 1024;
        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        long fileSizeInMB = fileSizeInKB / 1024;
        return fileSizeInKB;
    }

    private Bitmap encodeStringToBitmap(String encodedImage){
        try {
            byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } catch (Exception e){
            System.out.println(e);
        }
        return null;
    }

    private void writeToFile(String FILENAME, String text){
        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(text.getBytes());
            fos.close();
        }catch (Exception e){
            System.out.println("Error in writeToFile");
            System.out.println(e);
        }
    }
    private String readFromFile(String FILENAME, Context context) {
        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(FILENAME);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return ret;
    }

}
