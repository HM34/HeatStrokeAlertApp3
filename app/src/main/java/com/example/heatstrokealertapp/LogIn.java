package com.example.heatstrokealertapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LogIn extends AppCompatActivity {


    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button signUpButton;
  //  private TextView forgotPasswordTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in); //


        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signUpButton);
      //  forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LogIn.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(LogIn.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                }
            }
        });


      //  signUpButton.setOnClickListener(new View.OnClickListener() {
          //  @Override
          //  public void onClick(View v) {

            //    Intent intent = new Intent(LogIn.this, SignUpActivity.class); // تأكد من وجود SignUpActivity
            //    startActivity(intent);
       //     }
      //  });

       // forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
          //  @Override
           // public void onClick(View v) {
                //
              //  Intent intent = new Intent(LogIn.this, ForgotPasswordActivity.class); //  ForgotPasswordActivity
              //  startActivity(intent);
          //  }
      //  });


    }
}
