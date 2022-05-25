package com.example.Home_Control_System;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginPage extends AppCompatActivity {

    EditText phonenum;
    EditText password;
    Button login, signup;
    DBHelper dbHelper = new DBHelper(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        phonenum = findViewById(R.id.phone_number_for_login);
        password = findViewById(R.id.password_for_login);
        login = findViewById(R.id.login_in_button);
        signup = findViewById(R.id.sign_up_button_for_log_in);
        login.setOnClickListener(view -> checkDatabase(checkdataentered()));
        signup.setOnClickListener(view -> {
                Intent intent = new Intent (LoginPage.this, RegistrationPage.class);
                startActivity(intent);
        });
    }

    private boolean checkdataentered() {
        boolean isValid;
        String regEx = "^(03)([0-9]{9})$";
        Pattern pattern;
        Matcher matcher;
        pattern = Pattern.compile(regEx);

        isValid = true;

        if (isEmpty(phonenum)) {
            phonenum.setError("you must enter phone number for login");
            isValid = false;
        } else if (isEmpty(password)) {
            password.setError("you must enter password for login");
            isValid = false;
        } else if (!isEmpty(phonenum)) {
            matcher = pattern.matcher(phonenum.getText().toString());
            if (!matcher.find()) {
                phonenum.setError("Phone number is invalid");
                isValid = false;
            }
        }
        return isValid;
    }
    public void checkDatabase(boolean isValid){
        if(isValid){
            User loginUser = new User();
            loginUser.setMobileNumber(phonenum.getText().toString());
            Cursor cursor = dbHelper.validateuser(loginUser);
            if (!cursor.isFirst()){
                if(cursor.moveToFirst()){
                        do{
                            User savedUser;
                            if (!cursor.isAfterLast()){
                                savedUser = new User();
                                savedUser.setMobileNumber(cursor.getString(0));
                                savedUser.setPassword(cursor.getString(1));
                                loginUser.setPassword(password.getText().toString());
                                if (savedUser.getMobilenumber().equals(loginUser.getMobilenumber())
                                        &&savedUser.getPassword().equals(loginUser.getPassword())){
                                    Toast.makeText(this, "Correct", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginPage.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                    break;
                                }else{
                                    password.setError("Invalid password");
                                }

                            }else{
                                Toast.makeText(this, "Incorrect", Toast.LENGTH_SHORT).show();

                            }
                        }while(cursor.moveToNext());
                }
                else{
                    Toast.makeText(this, "Not Registered yet", Toast.LENGTH_SHORT).show();

                }

            }

        }


    }
    boolean isEmpty(EditText text){
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }




}