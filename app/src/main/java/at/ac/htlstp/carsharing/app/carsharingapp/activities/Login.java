package at.ac.htlstp.carsharing.app.carsharingapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import at.ac.htlstp.carsharing.app.carsharingapp.R;
import at.ac.htlstp.carsharing.app.carsharingapp.model.User;
import at.ac.htlstp.carsharing.app.carsharingapp.service.GenericService;
import at.ac.htlstp.carsharing.app.carsharingapp.service.UserClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    public static final String TAG = Login.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.login_activity);

    }

    public void forgotPassword(View v){
        Toast.makeText(this, "Redirecting to Passwordreset!",Toast.LENGTH_LONG).show();
        String url = "http://get.privatevoid.net";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public void redirectLogin(View v){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            UserClient loginClient = GenericService.getClient(UserClient.class, "ITz3WIaL3m8dWbXyMhdZkvATdhTbFo91cWab2JGgo23dWW4zWq5BUonb5nVpwU6X");
            EditText emailEdit = (EditText) findViewById(R.id.email_input);
            EditText passwordEdit = (EditText) findViewById(R.id.password_input);
            final String email = emailEdit.getText().toString();
            String password = passwordEdit.getText().toString();
            if (email.matches("") || password.matches("")) {
                Toast.makeText(Login.this, "Username or password is empty!", Toast.LENGTH_LONG).show();
            } else {
                Call<Boolean> loginCall = loginClient.checkLogin(email, password);
                loginCall.enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        if (response.body()) {
                            final Intent i = new Intent(Login.this, Main_drawer.class);
                            UserClient uclient = GenericService.getClient(UserClient.class, "ITz3WIaL3m8dWbXyMhdZkvATdhTbFo91cWab2JGgo23dWW4zWq5BUonb5nVpwU6X");
                            Call<User> callUser = uclient.getUser(email);
                            callUser.enqueue(new Callback<User>() {
                                @Override
                                public void onResponse(Call<User> call, Response<User> response) {
                                    User u = response.body();
                                    if(u == null){
                                        Toast.makeText(Login.this,"User is null",Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                    if(u.getId() == null){
                                        Toast.makeText(Login.this,"UserID is null",Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                    Log.e(TAG,"EMAIL: " + email + " USERID: " + u.getId());
                                    i.putExtra("userID",u.getId());
                                    Login.this.startActivity(i);
                                }

                                @Override
                                public void onFailure(Call<User> call, Throwable t) {

                                }
                            });
                        } else {
                            Toast.makeText(Login.this, "Username or password is incorrect!", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
                        Log.e(TAG, "Login call failed");
                        Toast.makeText(Login.this, "Connection not possible!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }else{
            String[] perm = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET};
            requestPermissions(perm,1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean permGranted = false;
        if(requestCode == 1){
            for(int i = 0;i < 2;i++){
                if(grantResults[i] == PackageManager.PERMISSION_DENIED){
                    permGranted = true;
                }

            }
        }
        if(permGranted){
           Toast.makeText(this,"The Location and Internet permission must be granted!",Toast.LENGTH_LONG).show();
        }
    }

    /*public void redirectLogin(View v){
        Intent i = new Intent(this, Main_drawer.class);
        Login.this.startActivity(i);
    }*/
}
