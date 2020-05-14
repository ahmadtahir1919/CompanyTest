package com.example.companytest.Views;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.room.Room;

import com.airbnb.lottie.LottieAnimationView;
import com.example.companytest.Data.UserDao;
import com.example.companytest.Data.UserDataBase;
import com.example.companytest.Model.User;
import com.example.companytest.R;
import com.example.companytest.viewModel.UserViewModel;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.appCompatTextView)
    AppCompatTextView appCompatTextView;
    @BindView(R.id.animation_view)
    LottieAnimationView animationView;
    @BindView(R.id.edt_user_name)
    AppCompatEditText edtUserName;
    @BindView(R.id.layout_edt_user_name)
    TextInputLayout layoutEdtUserName;
    @BindView(R.id.layout_edt_register_password)
    TextInputLayout layoutEdtRegisterPassword;
    @BindView(R.id.appCompatTextView2)
    AppCompatTextView appCompatTextView2;
    @BindView(R.id.edt_date_of_birth)
    AppCompatEditText edtDateOfBirth;
    @BindView(R.id.layout_edt_register_date_of_birth)
    TextInputLayout layoutEdtRegisterDateOfBirth;
    @BindView(R.id.card_view)
    CardView cardView;
    @BindView(R.id.btn_sign_up)
    AppCompatButton btnSignUp;
    @BindView(R.id.register_view)
    ConstraintLayout registerView;
    @BindView(R.id.edt_login_user_name)
    AppCompatEditText edtLoginUserName;
    @BindView(R.id.layout_login_edt_user_name)
    TextInputLayout layoutLoginEdtUserName;
    @BindView(R.id.layout_login_edt_register_password)
    TextInputLayout layoutLoginEdtRegisterPassword;
    @BindView(R.id.btn_login)
    AppCompatButton btnLogin;
    @BindView(R.id.txt_sign_in)
    AppCompatTextView txtSignIn;
    @BindView(R.id.login_view)
    ConstraintLayout loginView;
    @BindView(R.id.edt_sign_up_password)
    AppCompatEditText edtSignUpPassword;
    @BindView(R.id.edt_login_password)
    AppCompatEditText edtLoginPassword;
    @BindView(R.id.txt_sign_up)
    AppCompatTextView txtSignUp;
    @BindView(R.id.male)
    RadioButton male;
    @BindView(R.id.female)
    RadioButton female;
    @BindView(R.id.rd_gender)
    RadioGroup rdGender;
    int gender = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ButterKnife.bind(this);
        loginView.setVisibility(View.VISIBLE);
        UserViewModel userViewModel = getUserViewModel();
        ClickListners(userViewModel);

    }


    private void ClickListners(UserViewModel userViewModel) {
        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegisterScreen(loginView, registerView);
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edtUserName.getText().toString().trim().equals("") || !edtSignUpPassword.getText().toString().trim().equals("") || !layoutEdtRegisterDateOfBirth.getEditText().getText().toString().trim().equals("")) {
                    User user = new User(edtUserName.getText().toString().trim(), edtSignUpPassword.getText().toString().trim(),
                            String.valueOf(gender), layoutEdtRegisterDateOfBirth.getEditText().getText().toString().trim());
                    userViewModel.checkAlreadyUserExist(edtUserName.getText().toString().trim()).observe(LoginActivity.this, new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean isUserExist) {
                            if (isUserExist) {
                                Toast.makeText(LoginActivity.this, "User Already Exist", Toast.LENGTH_SHORT).show();


                            } else {
                                userViewModel.setUserData(user);
                                Toast.makeText(LoginActivity.this, "Sign up success", Toast.LENGTH_SHORT).show();
                                showRegisterScreen(registerView, loginView);
                            }
                        }

                    });
                } else {
                    Toast.makeText(LoginActivity.this, "Please fill All fields", Toast.LENGTH_SHORT).show();

                }
            }
        });
        edtDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAndSetDateOfBirth();
            }
        });
        rdGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                setGender(checkedId);

            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userViewModel.getAllData(edtLoginUserName.getText().toString().trim(), layoutLoginEdtRegisterPassword.getEditText().getText().toString().trim()).observe(LoginActivity.this, new Observer<List<User>>() {
                    @Override
                    public void onChanged(List<User> users) {

                        if (users != null) {
                            if (users.size() > 0) {
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                i.putExtra("User", users.get(0).getUserName());
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "User Not Exist or password not match", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "User Not Exist", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });
        txtSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerView.setVisibility(View.GONE);
                loginView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setGender(int checkedId) {
        if (checkedId == R.id.male) {
            gender = 1;
        } else if (checkedId == R.id.female) {
            gender = 0;
        }
    }

    private void showAndSetDateOfBirth() {
        final Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog StartTime = new DatePickerDialog(LoginActivity.this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                edtDateOfBirth.setText(DateFormat.getDateInstance().format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        StartTime.show();
    }

    private void showRegisterScreen(ConstraintLayout loginView, ConstraintLayout registerView) {
        loginView.setVisibility(View.GONE);
        registerView.setVisibility(View.VISIBLE);
    }

    public UserViewModel getUserViewModel() {
        return ViewModelProviders.of(this)
                .get(UserViewModel.class);
    }
}
