package com.teal.a276.walkinggroup.activities.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;

import retrofit2.Call;

/**
 * Creates User Account and sends information to the server
 */

public class CreateAccount extends AuthenticationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        setupCreateButton();
    }

    private void setupCreateButton() {
        Button btn =  findViewById(R.id.makeAccountBtn);

        btn.setOnClickListener(v -> {
            EditText userNameInput = findViewById(R.id.userName);
            EditText emailInput = findViewById(R.id.email);
            EditText passwordInput = findViewById(R.id.password);

            if(!validInput(userNameInput, emailInput, passwordInput)) {
                return;
            }

            toggleSpinner(View.VISIBLE);

            String name = userNameInput.getText().toString();
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();
            User user = new User(name, email, password);

            ServerProxy proxy = ServerManager.getServerProxy();
            Call<User> caller = proxy.createNewUser(user);
            ServerManager.serverRequest(caller, result -> successfulResult(result, password),
                    CreateAccount.this::authError);
        });
    }

    private boolean validInput(EditText userNameInput,
                                  EditText emailInput, EditText passwordInput) {
        boolean validInputs = true;
        String firstName = userNameInput.getText().toString();
        if (firstName.isEmpty()) {
            userNameInput.setError(getString(R.string.empty_username));
            validInputs = false;
        }

        validInputs &= super.validInput(emailInput, passwordInput);
        return validInputs;
    }

    private void successfulResult(User user, String password) {
        user.setPassword(password);
        this.user = user;

        storeLogin();
        ServerProxy proxy = ServerManager.getServerProxy();
        Call<Void> caller = proxy.login(this.user);
        ServerManager.serverRequest(caller, this::successfulLogin,
                this::authError);
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, CreateAccount.class);
    }
}
