package com.v1.avatar.v1.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.v1.avatar.v1.R;


public class LoginPageActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView (R.layout.activity_loginpage);

        Button loginBtn = (Button) findViewById(R.id.loginBtn);
        Button createNewAccount = (Button) findViewById(R.id.createNewAccount);

        loginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick (View view) {

                //username
                EditText userName = (EditText) findViewById (R.id.userName);
                String username = userName.getText().toString();


                //password
                EditText passWord = (EditText) findViewById (R.id.passWord);
                String password = passWord.getText().toString();

                //We must change the checkIfValidUser logic once we build a database of user profile in the server
                if (checkIfValidUser (username, password)) {
                    Toast.makeText(getApplicationContext(), "Welcome " + username, Toast.LENGTH_LONG).show();
                    Intent loginIntent = new Intent (getApplicationContext(), MainActivity.class);
                    loginIntent.putExtra("username", username);
                    loginIntent.putExtra("password", password);
                    startActivity (loginIntent);

                } else {
                    Toast.makeText(getApplicationContext(), "Login Information error", Toast.LENGTH_LONG).show();
                }


            }



            private boolean checkIfValidUser(String username, String password) {
                return username.equals("1") && password.equals ("1");
             }

        });

    }


}
