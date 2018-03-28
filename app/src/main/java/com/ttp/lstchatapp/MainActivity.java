package com.ttp.lstchatapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.quickblox.auth.session.QBSettings;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.ttp.lstchatapp.R;

public class MainActivity extends AppCompatActivity {

    static final String APP_ID = "67623";
    static final String APP_KEY = "fxqv9jkk6y5ePPm";
    static final String AUTH_SECRET = "fNbmhgxuHdXPWe5";
    static final String ACCOUNT_KEY = "F2gUVB_dFV5kq5LJKdx6";

    static final int REQUEST_CODE = 1000;

    Button btnLogin, btnSignup;
    EditText editUser, editPw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestRunTimePermission();

        initializeFramework();

        btnLogin = findViewById(R.id.main_btnLigin);
        btnSignup = findViewById(R.id.main_btnSignup);

        editUser = findViewById(R.id.main_editLogin);
        editPw = findViewById(R.id.main_editPassword);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String user = editUser.getText().toString();
                final String pw = editPw.getText().toString();

                QBUser qbUser = new QBUser(user, pw);
                QBUsers.signIn(qbUser).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        Toast.makeText(getApplicationContext(), "Login successfully", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(MainActivity.this, ChatDialogsActivity.class);
                        intent.putExtra("user", user);
                        intent.putExtra("pw", pw);
                        startActivity(intent);
                        finish(); //close loginactivity after login

                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }



        });
    }

    private void requestRunTimePermission() {

        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, REQUEST_CODE);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       switch (requestCode){
           case REQUEST_CODE :
               if (grantResults[0] == PackageManager.PERMISSION_GRANTED) Toast.makeText(getBaseContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
               else Toast.makeText(getBaseContext(), "Permission Denied", Toast.LENGTH_SHORT).show();

       }
    }

    private void initializeFramework() {

        QBSettings.getInstance().init(getApplicationContext(), APP_ID, APP_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);
    }
}
