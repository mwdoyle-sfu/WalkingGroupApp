package com.teal.a276.walkinggroup.activities.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewSwitcher;


import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;

import retrofit2.Call;


/**
 * Checks Server and validates login information
 */

public class Login extends AuthenticationActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Boolean autoLogin = false;
    private ViewSwitcher switcher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        switcher = findViewById(R.id.viewSwitcher);
        checkForLogin();

        setUpPermissions();
        setUpLoginButton();
        setupCreateAccountButton();
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, Login.class);
    }


    private void setUpPermissions() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void checkForLogin() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREF_LOGGER, Context.MODE_PRIVATE);
        String userName = prefs.getString(SHARED_PREF_USERNAME, null);
        String password = prefs.getString(SHARED_PREF_PASSWORD, null);

        if (userName != null && !userName.isEmpty()) {
            switcher.showNext();
            autoLogin = true;

            user.setEmail(userName);
            user.setPassword(password);
            login();
        }
    }

    private void setUpLoginButton() {
        Button btn = findViewById(R.id.signInBtn);
        btn.setOnClickListener(v -> {
            EditText emailInput = findViewById(R.id.email);
            EditText passwordInput = findViewById(R.id.password);

            TextView signInError = findViewById(R.id.autoSignInError);
            if (signInError.getVisibility() == View.VISIBLE) {
                signInError.setVisibility(View.INVISIBLE);
            }

            if (!validInput(emailInput, passwordInput)) {
                return;
            }

            toggleSpinner(View.VISIBLE);
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();

            user.setEmail(email);
            user.setPassword(password);
            login();
        });
    }

    private void login() {
        ServerProxy proxy = ServerManager.getServerProxy();
        Call<Void> caller = proxy.login(user);
        ServerManager.serverRequest(caller, this::successfulLogin, this::errorLogin);
    }

    private void setupCreateAccountButton() {
        Button btn = findViewById(R.id.createAccntBtn);
        btn.setOnClickListener(v -> {
            Intent intent = CreateAccount.makeIntent(Login.this);
            startActivity(intent);
            finish();
        });
    }

    private void errorLogin(String error) {
        if (autoLogin) {
            switcher.showNext();

            TextView signInError = findViewById(R.id.autoSignInError);
            signInError.setVisibility(View.VISIBLE);
            autoLogin = false;
        } else {
            super.authError(error);
        }
    }
}

