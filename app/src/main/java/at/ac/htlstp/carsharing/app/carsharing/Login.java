package at.ac.htlstp.carsharing.app.carsharing;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class Login extends AppCompatActivity {

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
        Intent i = new Intent(this,Main_drawer.class);
        this.startActivity(i);
    }
}
