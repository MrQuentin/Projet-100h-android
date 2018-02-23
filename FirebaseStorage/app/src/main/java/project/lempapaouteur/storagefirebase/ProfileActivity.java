package project.lempapaouteur.storagefirebase;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PICK_IMAGE_REQUEST=234;
    private static final int PICK_TEXT_FILE_REQUEST = 3564;
    private Button buttonChoose,buttonUplaod,buttonSave,buttonSend;
    private ImageView imageView;
    private EditText editText;
    private Uri filepath;
    private StorageReference storageReference;
    //User data
    ImageView imageUser;
    TextView textEmail;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //gestion data user
        imageUser=(ImageView)findViewById(R.id.imgUser);
        textEmail=(TextView)findViewById(R.id.eMailUser);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user=mAuth.getCurrentUser();
        //glide is a library for manage image inside the app
        Glide.with(this)
                .load(user.getPhotoUrl())
                .into(imageUser);
        textEmail.setText(user.getEmail());

//fin gestion data user
        storageReference = FirebaseStorage.getInstance().getReference();

imageView = (ImageView) findViewById(R.id.imgUser);
        buttonChoose=(Button) findViewById(R.id.buttonChoose);
        buttonUplaod=(Button) findViewById(R.id.buttonUpload);
        buttonSave=(Button) findViewById(R.id.enregistrer);
        buttonSend=(Button) findViewById(R.id.envoyer);
        editText =(EditText) findViewById(R.id.editLocation) ;
        buttonSend.setOnClickListener(this);
        buttonSave.setOnClickListener(this);
        buttonChoose.setOnClickListener(this);
        buttonUplaod.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null){
            Toast.makeText(this, "Vous devez être connecté pour accéder à l'application", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(this,MainActivity.class));
        }
    }

    @Override
    public void onClick(View v) {
        if (v == buttonChoose){
            //open file chooser
              showFileChooser();
        }else if (v==buttonUplaod){
            // upload file to firebase storage

            uploadFile();

        }else if (v == buttonSave){
            //  writeFile();


        }else if (v == buttonSend){

        }
    }
    /**
     * this method check if this is the first time the user launch the app.
     * this method also keeps the name of the random generated file used to store the data.
     * it is stored in a preferencies file.
     * @return true if it is the first time, false if it is not the first time.
     */

    @SuppressLint("WrongConstant")
    public void checkFirstTimeAndCreateFile (){
        final String PREFS_NAME = "MyPrefsFile";
        final String PREFS_FILENAME = "MyPrefsFileForFileStorage";
        Boolean rep = null;

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        if (settings.getBoolean("my_first_time", true)) {
            //the app is being launched for first time, do something
            Log.d("Comments", "First time");

            // first time task

            // record the fact that the app has been started at least once
            settings.edit().putBoolean("my_first_time", false).commit();
            //create the file with his own random string name.

            String filePath = getUsername();
            SharedPreferences settingsFile;
            settingsFile = getSharedPreferences(PREFS_FILENAME,MODE_APPEND);
            settingsFile.edit().putString("filename",filePath).commit();
            filepath = Uri.parse(filePath);


        }else {
            SharedPreferences settingsFile;
            settingsFile = getSharedPreferences(PREFS_FILENAME,MODE_APPEND);
            filepath = Uri.parse(settingsFile.getString("filename",null));
            if (filepath == null){
                Toast.makeText(this, "failed to load preferencies", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "preferencies loaded successfully", Toast.LENGTH_SHORT).show();
            }

        }


    }
    public void writeFile(){
        String txtToStore = editText.toString();
        FileOutputStream outputStream;
        try{
            outputStream = openFileOutput(filepath.toString()+".pref",Context.MODE_APPEND);
            outputStream.write(txtToStore.getBytes());
            Toast.makeText(this, "Writting correctly handeled", Toast.LENGTH_SHORT).show();
            outputStream.close();

        }catch (Exception e){e.printStackTrace();}

    }

    private  void showFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select an Image"),PICK_IMAGE_REQUEST);


    }
    private void chooseFile(){
        Intent intent = new Intent();
        intent.setType("text/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        String scheme = filepath.getScheme();
        intent.getData();
        //   startActivityForResult(Intent.getIntentOld(intent),PICK_TEXT_FILE_REQUEST);

    }
    private void uploadFile() {

        if (filepath != null) {
            //  ProgressBar progressBar = new ProgressBar(ressBar(this));
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uplaoding ...");
            progressDialog.show();
            // le fichier sera stocké sur firebase en tps que emailUser.jpg dans le fichier image
            FirebaseUser user=mAuth.getCurrentUser();
            StorageReference riversRef = storageReference.child("images/"+user.getEmail()+".jpg");

            riversRef.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"File Uploaded", Toast.LENGTH_LONG).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),exception.getMessage(),Toast.LENGTH_LONG).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage(((int) progress) + "% Uplaod ...");
                }
            });
        } else {
            // display toast
            Toast.makeText(getApplicationContext(),"veuillez choisir une image",Toast.LENGTH_LONG).show();
        }
    }
    private void uploadTextFile(){
        if (filepath !=null){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uplaoding ...");
            progressDialog.show();
            StorageReference Ref= storageReference.child("text/"+filepath+".json");
            Ref.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Text File Uploaded", Toast.LENGTH_LONG).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),exception.getMessage(),Toast.LENGTH_LONG).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage(((int) progress) + "% Uplaod ...");
                }
            });
        } else {
            // display toast
            Toast.makeText(getApplicationContext(),"problème au niveau du text file",Toast.LENGTH_LONG).show();
        }
    }
    /**
     * get the user google account name wich is unique.
     * @return
     */

    public String getUsername() {
        AccountManager manager = AccountManager.get(this);
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<String>();

        for (Account account : accounts) {
            // TODO: Check possibleEmail against an email regex or treat
            // account.name as an email address only for certain account.type
            // values.
            possibleEmails.add(account.name);
        }

        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            String email = possibleEmails.get(0);
            String[] parts = email.split("@");
            if (parts.length > 0 && parts[0] != null)
                return parts[0];
            else
                return null;
        } else
            return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //si on va chercher une image et que l'on en choisi une et les données ciblées ne sont pas nulles
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filepath = data.getData();
//pour afficher l'image
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == PICK_TEXT_FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filepath = data.getData();
        }
    }
}
