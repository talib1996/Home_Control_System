package com.example.Home_Control_System;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegistrationPage extends AppCompatActivity {
    EditText password_field_1, password_field_2;
    EditText phone_num;
    Button register, login;
    DBHelper dbHelper = new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);
        phone_num = findViewById(R.id.phone_number_for_reg);
        password_field_1 = findViewById(R.id.password_field_1__for_reg);
        password_field_2 = findViewById(R.id.password_field_2__for_reg);
        register = findViewById(R.id.sign_up_button);
        login = findViewById(R.id.login_in_button_in_reg);
        register.setOnClickListener(view -> enterintoDatabase(checkDataEntered()));
        login.setOnClickListener(view -> {
            Intent intent = new Intent (this, LoginPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            RegistrationPage.this.finish();
        });


    }
    public boolean checkDataEntered() {
        String regEx = "^(03)([0-9]{9})$";
        Pattern pattern = Pattern.compile(regEx);
        boolean isValid = true;
        Matcher matcher;
        if (isEmpty(phone_num)) {
            phone_num.setError("Phone Number is required");
            isValid = false;
        } else if (isEmpty(password_field_1)) {
            password_field_1.setError("Password is required");
            isValid = false;
        } else if (isEmpty(password_field_2)) {
            password_field_2.setError("Password is required again");
            isValid = false;
        } else if (!isEmpty(phone_num)) {
            matcher = pattern.matcher(phone_num.getText().toString());
            if (!matcher.find()) {
                phone_num.setError("Phone number is invalid");
                isValid = false;
            }
        }
        if (!password_field_1.getText().toString().equals(password_field_2.getText().toString())) {
            password_field_1.setError("Password does not match");
            password_field_2.setError("Password does not match");
            isValid = false;
        }
        return isValid;
    }
    public void enterintoDatabase(boolean isValid){
        if (isValid){
            User user = new User();
            user.setMobileNumber(phone_num.getText().toString());
            user.setPassword(password_field_1.getText().toString());
            Toast.makeText(this, user.getMobilenumber(), Toast.LENGTH_SHORT).show();
            long result = dbHelper.insertData(user);
            if(result == -1){
                Toast.makeText(this, "An error occured", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();

            }
        }


        }

    boolean isEmpty(EditText text){
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(this, "back is pressed", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(RegistrationPage.this, LoginPage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        RegistrationPage.this.finish();
    }

}