package com.example.mrquentin.drawertest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mrquentin.drawertest.Dialogs.DialogHandler;
import com.example.mrquentin.drawertest.Navigation.BluetoothFragment;
import com.example.mrquentin.drawertest.Navigation.DashboardFragment;
import com.example.mrquentin.drawertest.Navigation.HomeFragment;
import com.example.mrquentin.drawertest.Dialogs.Fragments.BasicTextDialogFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    //User data
    private static final int RC_SIGN_IN = 658;
    private static ImageView imageUser;
    private static TextView textEmail;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static TextView textUserName;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");//remove app title
        setSupportActionBar(toolbar);



        /*==========================================================================================
                                          Floating action button
         =========================================================================================*/


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        /*==========================================================================================
                                                 Drawer
         =========================================================================================*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        /*==========================================================================================
                                                Navigation
         =========================================================================================*/
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigationView);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, new HomeFragment()).commit();

        /*========================================================================================
        *                                   Connexion User onCreate
        * ========================================================================================*/

        GoogleSignInOptions gso= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        signIn();
        if (mAuth.getCurrentUser() != null  ) {
        /*=========================================================================================
        * Gestion affichage image + email utilisateur*/
            //gestion data user

            View headerView = navigationView.getHeaderView(0);
            imageUser = (ImageView) headerView.findViewById(R.id.imageViewUser);
            textUserName = (TextView)headerView.findViewById(R.id.textViewUserName);
            textEmail = (TextView) headerView.findViewById(R.id.textViewEmailUser);



            FirebaseUser user = mAuth.getCurrentUser();
            // get le userName et l'email

            textEmail.setText(user.getEmail());
            textUserName.setText(user.getDisplayName());
            // target l'image dans le header du menu
            RequestOptions requestOptions = new RequestOptions();
            // target l'image par défaut
            requestOptions.placeholder(R.mipmap.ic_launcher_round);
            // arrondi l'image
            requestOptions.circleCrop();
            // si erreur, remet l'image par défaut
            requestOptions.error(R.mipmap.ic_launcher_round);

            //essai d'écrasement de l'image par défaut par l'image utilisateur

            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .apply(requestOptions)
                    .thumbnail(0.5f)
                    .into(imageUser);
        }else {
            Toast.makeText(this, "Vous devez être connecté pour utiliser l'application", Toast.LENGTH_SHORT).show();
        }
         /*=========================================================================================*/
    }

    /*===============================================================================================
    *                                          Authentification du compte google avec firebase
    * ==============================================================================================*/


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            String userUID = mAuth.getUid();
                            Toast.makeText(MainActivity.this, "User signed in", Toast.LENGTH_SHORT).show();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentification failed", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
    /*===============================================================================================
    *                                          onStart vérification Compte Google
    * ==============================================================================================*/

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null){
            Toast.makeText(this, "User already connected", Toast.LENGTH_SHORT).show();

           // startActivity(new Intent(this, ProfileActivity.class));
        }
    }
    /*============================================================================================
    *                                                  onActivityResult gestion connexion compte google
    * =============================================================================================*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                // ...
            }
        }
    }
    /*==============================================================================================
                                                Drawer Button
     =============================================================================================*/
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*==============================================================================================
                                          Suppression menu 3 point
     =============================================================================================*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    /*==============================================================================================
                                            ActionBar selector
     =============================================================================================*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    /*==============================================================================================
                                         Drawer Selector
     =============================================================================================*/
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_recent) {
            new DialogHandler().showBasicTextDialog("recent", "recent content", getSupportFragmentManager());
        } else if (id == R.id.nav_settings) {
            new DialogHandler().showBasicTextDialog("Settings", "settings content", getSupportFragmentManager());
        } else if (id == R.id.nav_help) {
            new DialogHandler().showBasicTextDialog("Help", "Help content here", getSupportFragmentManager());
        } else if (id == R.id.nav_about) {
            new DialogHandler().showBasicTextDialog("About", "About content here", getSupportFragmentManager());
        } else if (id == R.id.nav_house) {
            new DialogHandler().showBasicTextDialog("My home", "My home Content here", getSupportFragmentManager());
        } else if (id == R.id.nav_work) {
            new DialogHandler().showBasicTextDialog("My work", "My work content here", getSupportFragmentManager());
        }

        //close drawer onclick
        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*==============================================================================================
                                         Navigation
    ==============================================================================================*/
    private TextView mTextMessage;



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment fragment = null;
            Class fragmentClass;

            switch (item.getItemId()) {

                case R.id.navigation_home:
                    System.out.println("NavBar home");
                    fragmentClass = HomeFragment.class;
                    break;
                case R.id.navigation_dashboard:
                    System.out.println("Nav DashBoard");
                    fragmentClass = DashboardFragment.class;
                    break;
                case R.id.navigation_bluetooth:
                    System.out.println("Nav Bluetooth");
                    fragmentClass = BluetoothFragment.class;
                    break;
                default:
                    System.out.println("Default");
                    fragmentClass = HomeFragment.class;
                    return false;
            }
            
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            return true;
        }
    };

}
