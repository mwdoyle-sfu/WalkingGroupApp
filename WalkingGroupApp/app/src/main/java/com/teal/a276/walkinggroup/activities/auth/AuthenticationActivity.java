package com.teal.a276.walkinggroup.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.activities.BaseActivity;
import com.teal.a276.walkinggroup.activities.map.MapsActivity;
import com.teal.a276.walkinggroup.model.ModelFacade;
import com.teal.a276.walkinggroup.model.dataobjects.GroupManager;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;

import retrofit2.Call;

/**
 * Abstract class that encapsulates the shared login code for create account and logging in.
 */

public abstract class AuthenticationActivity extends BaseActivity {

    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ModelFacade.getInstance().setAppResources(getResources());
    }

    void toggleSpinner(int visibility) {
        final ProgressBar spinner = findViewById(R.id.authenticationProgress);
        runOnUiThread(() -> spinner.setVisibility(visibility));
    }

    void successfulLogin(Void ans) {
        ServerProxy proxy = ServerManager.getServerProxy();
        Call<User> userByEmailCall = proxy.getUserByEmail(user.getEmail(), 1L);
        ServerManager.serverRequest(userByEmailCall, this::userResult, this::authError);
    }

    private void userResult(User user) {
        ModelFacade.getInstance().setCurrentUser(user);
        ModelFacade.getInstance().setGroupManager(new GroupManager());
        storeLogin();
        Intent intent = MapsActivity.makeIntent(this);
        startActivity(intent);

        finish();
    }

    void storeLogin() {
        EditText emailInput = findViewById(R.id.email);
        EditText passwordInput = findViewById(R.id.password);

        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        saveAccountLoginInfo(email, password);
    }

    boolean validInput(EditText emailInput, EditText passwordInput) {
        boolean validInputs = true;

        String password = passwordInput.getText().toString();
        if (password.isEmpty()) {
            passwordInput.setError(getString(R.string.empty_password));
            validInputs = false;
        }

        String email = emailInput.getText().toString();
        if (!User.validateEmail(email)) {
            emailInput.setError(getString(R.string.invalid_email));
            validInputs = false;
        }
        return validInputs;
    }

    void authError(String error) {
        toggleSpinner(View.INVISIBLE);
        super.error(error);
    }
}
