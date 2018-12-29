package com.teal.a276.walkinggroup.activities.profile;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.teal.a276.walkinggroup.R;
import com.teal.a276.walkinggroup.activities.auth.AuthenticationActivity;
import com.teal.a276.walkinggroup.model.ModelFacade;
import com.teal.a276.walkinggroup.model.dataobjects.User;
import com.teal.a276.walkinggroup.model.serverproxy.ServerManager;
import com.teal.a276.walkinggroup.model.serverproxy.ServerProxy;

import java.text.DateFormat;
import java.util.Calendar;

import retrofit2.Call;

public class UserProfile extends AuthenticationActivity {
    private final DateFormat formatDate = DateFormat.getDateInstance();
    private final Calendar dateTime = Calendar.getInstance();
    private Button dateBtn;
    User user;
    private DatePickerDialog.OnDateSetListener datePicker;
    private static final String USER = "user";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getDataFromIntent();

        fillKnownInfo();
        datePicker = (view, year, monthOfYear, dayOfMonth) -> {
            dateTime.set(Calendar.YEAR, year);
            dateTime.set(Calendar.MONTH, monthOfYear);
            dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            dateBtn.setText(formatDate.format(dateTime.getTime()));
        };

        setupBirthdayBtn();
        setUpSaveButton();
    }

    private void fillKnownInfo() {
        EditText addressInput = findViewById(R.id.editAddress);
        EditText gradeInput = findViewById(R.id.editGrade);
        EditText teachersNameInput = findViewById(R.id.editTeacherName);
        EditText contactInfoInput = findViewById(R.id.editContactInfo);

        addUserInfoToTextInput(user.getAddress(), addressInput);
        addUserInfoToTextInput(user.getGrade(), gradeInput);
        addUserInfoToTextInput(user.getTeacherName(), teachersNameInput);
        addUserInfoToTextInput(user.getEmergencyContactInfo(), contactInfoInput);

        if (user.getBirthYear() != 0) {
            dateTime.set(user.getBirthYear(), user.getBirthMonth(), Calendar.DATE);
        } else {
            dateTime.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DATE));
        }

        fillBasicInfo(true);
    }

    void fillBasicInfo(boolean editable) {
        EditText nameInput = findViewById(R.id.editName);
        EditText homePhoneInput = findViewById(R.id.editHome);
        EditText cellPhoneInput = findViewById(R.id.editCell);
        EditText emailInput = findViewById(R.id.editEmail);

        nameInput.setEnabled(editable);
        homePhoneInput.setEnabled(editable);
        cellPhoneInput.setEnabled(editable);
        emailInput.setEnabled(editable);

        addUserInfoToTextInput(user.getName(), nameInput);
        addUserInfoToTextInput(user.getHomePhone(), homePhoneInput);
        addUserInfoToTextInput(user.getCellPhone(), cellPhoneInput);
        addUserInfoToTextInput(user.getEmail(), emailInput);

        cellPhoneInput.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        homePhoneInput.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
    }

    private void addUserInfoToTextInput(String info, EditText input) {
        if(info != null) {
            input.setText(info, TextView.BufferType.EDITABLE);
        }
    }

    private void setUpSaveButton() {
        Button btn = findViewById(R.id.saveBtn);
        btn.setOnClickListener((View v) -> {
            EditText nameInput = findViewById(R.id.editName);
            EditText addressInput = findViewById(R.id.editAddress);
            EditText homePhoneInput = findViewById(R.id.editHome);
            EditText cellPhoneInput = findViewById(R.id.editCell);
            EditText emailInput = findViewById(R.id.editEmail);
            EditText gradeInput = findViewById(R.id.editGrade);
            EditText teachersNameInput = findViewById(R.id.editTeacherName);
            EditText contactInfoInput = findViewById(R.id.editContactInfo);

            if (!hasValidProfileInfo(nameInput, addressInput, homePhoneInput, cellPhoneInput, emailInput)) {
                return;
            }

            toggleSpinner(View.VISIBLE);

            String name = nameInput.getText().toString();
            String address = addressInput.getText().toString();
            String homePhone = homePhoneInput.getText().toString();
            String cellPhone = cellPhoneInput.getText().toString();
            String email = emailInput.getText().toString();
            String grade = gradeInput.getText().toString();
            String teachersName = teachersNameInput.getText().toString();
            String contactInfo = contactInfoInput.getText().toString();

            if (!User.validateEmail(email)) {
                emailInput.setError(getString(R.string.email_invalid));
                toggleSpinner(View.INVISIBLE);
                return;
            }
            if (!User.validatePhoneNumber(homePhone)) {
                homePhoneInput.setError(getString(R.string.phone_invalid));
                toggleSpinner(View.INVISIBLE);
                return;
            }
            if (!User.validatePhoneNumber(cellPhone)) {
                cellPhoneInput.setError(getString(R.string.phone_invalid));
                toggleSpinner(View.INVISIBLE);
                return;
            }

            user.setBirthMonth(dateTime.get(Calendar.MONTH));
            user.setBirthYear(dateTime.get(Calendar.YEAR));
            user.setName(name);
            user.setAddress(address);
            user.setHomePhone(homePhone);
            user.setCellPhone(cellPhone);
            user.setEmail(email);
            user.setGrade(grade);
            user.setTeacherName(teachersName);
            user.setEmergencyContactInfo(contactInfo);

            ServerProxy proxy = ServerManager.getServerProxy();
            Call<User> caller = proxy.updateUser(user.getId(), user, 1L);
            ServerManager.serverRequest(caller, UserProfile.this::successfulSave,
                    UserProfile.this::error);
        });
    }

    private void setupBirthdayBtn() {
        dateBtn = findViewById(R.id.dateButton);
        dateBtn.setOnClickListener(v -> updateDate());
        dateBtn.setText(formatDate.format(dateTime.getTime()));
    }

    private void updateDate() {
        new DatePickerDialog(this, datePicker, dateTime.get(Calendar.YEAR), dateTime.get(Calendar.MONTH), dateTime.get(Calendar.DAY_OF_MONTH)).show();
    }

    private boolean hasValidProfileInfo(EditText name, EditText address, EditText homePhone, EditText cellPhone,
                                        EditText email) {
        boolean validInputs = true;

        if (isEmpty(name)) {
            name.setError(getString(R.string.empty_name));
            validInputs = false;
        }
        if (isEmpty(address)) {
            address.setError(getString(R.string.empty_address));
            validInputs = false;
        }
        if (isEmpty(homePhone)) {
            homePhone.setError(getString(R.string.empty_home));
            validInputs = false;
        }
        if (isEmpty(cellPhone)) {
            cellPhone.setError(getString(R.string.empty_cell));
            validInputs = false;
        }
        if (isEmpty(email)) {
            email.setError(getString(R.string.empty_email));
            validInputs = false;
        }
        return validInputs;
    }

    private boolean isEmpty(EditText name) {
        return name.getText().toString().isEmpty();
    }

    private void successfulSave(User user) {
        ModelFacade.getInstance().setCurrentUser(user);
        //update shared Prefs, password is always null from the server
        saveAccountLoginInfo(user.getEmail(), null);

        Toast.makeText(this, R.string.updated_profile, Toast.LENGTH_SHORT).show();
        finish();
    }

    protected void error(String error) {
        toggleSpinner(View.INVISIBLE);
        super.error(error);
    }

    private void toggleSpinner(int visibility) {
        final ProgressBar spinner = findViewById(R.id.authenticationProgress);
        runOnUiThread(() -> spinner.setVisibility(visibility));
    }

    public static Intent makeIntent(Context context, User user) {
        Gson gson = new Gson();
        Intent intent = new Intent(context, UserProfile.class);
        intent.putExtra(USER, gson.toJson(user));
        return intent;
    }

    void getDataFromIntent() {
        Gson gson = new Gson();
        String strObj = getIntent().getStringExtra(USER);
        user = gson.fromJson(strObj, User.class);
    }
}