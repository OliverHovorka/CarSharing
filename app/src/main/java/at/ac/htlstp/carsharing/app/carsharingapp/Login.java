package at.ac.htlstp.carsharing.app.carsharingapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class Login extends AppCompatActivity {

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

    /*public void redirectLogin(View v){
        UserClient loginClient = GenericService.getClient(UserClient.class,"ITz3WIaL3m8dWbXyMhdZkvATdhTbFo91cWab2JGgo23dWW4zWq5BUonb5nVpwU6X");
        EditText emailEdit = (EditText) findViewById(R.id.email_input);
        EditText passwordEdit = (EditText) findViewById(R.id.password_input);
        String email = emailEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        if(email.matches("") || password.matches("")){
            Toast.makeText(Login.this, "Username or password is empty!",Toast.LENGTH_LONG).show();
        }else {
            Call<Boolean> loginCall = loginClient.checkLogin(email, password);
            loginCall.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.body()) {
                        Intent i = new Intent(Login.this, Main_drawer.class);
                        Login.this.startActivity(i);
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
    }*/

    public void redirectLogin(View v){
        Intent i = new Intent(this, Main_drawer.class);
        Login.this.startActivity(i);
    }
}
