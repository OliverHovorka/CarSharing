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
import at.ac.htlstp.carsharing.app.carsharingapp.model.AndroidLogin;
import at.ac.htlstp.carsharing.app.carsharingapp.model.Job;
import at.ac.htlstp.carsharing.app.carsharingapp.model.User;
import at.ac.htlstp.carsharing.app.carsharingapp.service.GenericService;
import at.ac.htlstp.carsharing.app.carsharingapp.service.JobClient;
import at.ac.htlstp.carsharing.app.carsharingapp.service.UserClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String TAG = Login.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.login_activity);
    }

    public void forgotPassword(View v) {
        Toast.makeText(this, "Redirecting to Passwordreset!", Toast.LENGTH_LONG).show();
        String url = "http://carsharing.privatevoid.net/webseite/faces/resetpwd.xhtml";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public void redirectLogin(View v) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            UserClient loginClient = GenericService.getClient(UserClient.class, "ITz3WIaL3m8dWbXyMhdZkvATdhTbFo91cWab2JGgo23dWW4zWq5BUonb5nVpwU6X");
            EditText emailEdit = (EditText) findViewById(R.id.email_input);
            EditText passwordEdit = (EditText) findViewById(R.id.password_input);
            final String email = emailEdit.getText().toString();
            String password = passwordEdit.getText().toString();
            if (email.matches("") || password.matches("")) {
                Toast.makeText(Login.this, "Username or password is empty!", Toast.LENGTH_LONG).show();
            } else {
                Call<AndroidLogin> loginCall = loginClient.checkAndroidLogin(email, password);
                loginCall.enqueue(new Callback<AndroidLogin>() {
                    @Override
                    public void onResponse(Call<AndroidLogin> call, Response<AndroidLogin> response) {
                        if (response.body() != null) {
                            AndroidLogin log = response.body();
                            if(log.isSuccessful()) {
                                Job curJ = log.getCurrentJob();
                                User u = log.getUser();
                                if (curJ == null) {
                                    Log.i(TAG, "User: " + u.getEmail() + " has no curjob");
                                    Intent imain = new Intent(Login.this, Main_drawer.class);
                                    imain.putExtra("userID", u.getId());
                                    Login.this.startActivity(imain);
                                } else {
                                    Intent icur = new Intent(Login.this, CurTask.class);
                                    Log.i(TAG, "User: " + u.getEmail() + " Job: " + curJ.getId());
                                    icur.putExtra("userID", u.getId());
                                    Login.this.startActivity(icur);
                                }
                            }else{
                                Toast.makeText(Login.this, "Email or password incorrect!", Toast.LENGTH_LONG).show();
                                return;
                            }

                        } else {
                            Log.e(TAG, "LoginCall exception: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<AndroidLogin> call, Throwable t) {
                        Log.e(TAG, "LoginCall failed: " + t.getMessage());
                    }
                });
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        boolean permGranted = false;
        if (requestCode == 1) {
            for (int i = 0; i < 2; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    permGranted = true;
                }

            }
        }
        if (permGranted) {
            Toast.makeText(this, "The Location and Internet permission must be granted!", Toast.LENGTH_LONG).show();
        }
    }

}
