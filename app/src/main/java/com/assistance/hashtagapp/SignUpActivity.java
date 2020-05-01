package com.assistance.hashtagapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.assistance.hashtagapp.BottomSheets.PasswordRequirementsBottomSheet;
import com.assistance.hashtagapp.Common.Common;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.tapadoo.alerter.Alerter;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;
import maes.tech.intentanim.CustomIntent;

public class SignUpActivity extends AppCompatActivity {

    ImageView backArrow;
    EditText nameField, emailField, mobileField, createPasswordField, confirmPasswordField;
    MaterialCheckBox showPassword;
    TextView termOfServices, privacyPolicy, signIn;
    ConstraintLayout signUp;
    CardView nameCard, emailCard, mobileCard, createPasswordCard, confirmPasswordCard, signUpCard;
    FirebaseAuth firebaseAuth;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +                 //at least 1 digit
                    "(?=.*[a-z])" +                 //at least 1 lowercase letter
                    "(?=.*[A-Z])" +                 //at least 1 uppercase letter
                    "(?=.*[!@#$%^&*+=_])" +         //at least 1 special character
                    "(?=\\S+$)" +                   //no white spaces
                    ".{6,}" +                       //at least 6-character long
                    "$");

    AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.background));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

        firebaseAuth = FirebaseAuth.getInstance();

        backArrow = findViewById(R.id.arrow_back);
        nameField = findViewById(R.id.name);
        emailField = findViewById(R.id.email);
        mobileField = findViewById(R.id.mobile);
        createPasswordField = findViewById(R.id.create_password);
        confirmPasswordField = findViewById(R.id.confirm_password);
        nameCard = findViewById(R.id.name_card);
        emailCard = findViewById(R.id.email_card);
        mobileCard = findViewById(R.id.mobile_card);
        createPasswordCard = findViewById(R.id.create_password_card);
        confirmPasswordCard = findViewById(R.id.confirm_password_card);
        showPassword = findViewById(R.id.show_password);
        termOfServices = findViewById(R.id.terms_of_service);
        privacyPolicy = findViewById(R.id.privacy_policy);
        signUp = findViewById(R.id.sign_up);
        signUpCard = findViewById(R.id.sign_up_card);
        signIn = findViewById(R.id.sign_in);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        progressDialog = new SpotsDialog.Builder().setContext(SignUpActivity.this)
                .setMessage("Sending Verification Code..")
                .setCancelable(false)
                .setTheme(R.style.SpotsDialog)
                .build();


        KeyboardVisibilityEvent.setEventListener(SignUpActivity.this, new KeyboardVisibilityEventListener(){
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if(!isOpen)
                {
                    nameField.clearFocus();
                    emailField.clearFocus();
                    mobileField.clearFocus();
                    createPasswordField.clearFocus();
                    confirmPasswordField.clearFocus();
                }
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                CustomIntent.customType(SignUpActivity.this, "left-to-right");
            }
        });

        nameField.addTextChangedListener(signupTextWatcher);
        emailField.addTextChangedListener(signupTextWatcher);
        mobileField.addTextChangedListener(signupTextWatcher);
        createPasswordField.addTextChangedListener(signupTextWatcher);
        confirmPasswordField.addTextChangedListener(signupTextWatcher);

        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    createPasswordField.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    confirmPasswordField.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else
                {
                    createPasswordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    confirmPasswordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }

    private TextWatcher signupTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            final String name = nameField.getText().toString().trim();
            final String email = emailField.getText().toString().trim();
            final String mobile = mobileField.getText().toString().trim();
            final String createPassword = createPasswordField.getText().toString().trim();
            final String confirmPassword = confirmPasswordField.getText().toString().trim();

            if(!name.isEmpty() && !email.isEmpty() && !mobile.isEmpty() && !createPassword.isEmpty() && !confirmPassword.isEmpty())
            {
                signUpCard.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                signUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UIUtil.hideKeyboard(SignUpActivity.this);
                        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                        {
                            YoYo.with(Techniques.Shake)
                                    .duration(700)
                                    .repeat(1)
                                    .playOn(emailCard);
                            Alerter.create(SignUpActivity.this)
                                    .setText("Please enter a valid Email!")
                                    .setTextAppearance(R.style.ErrorAlert)
                                    .setBackgroundColorRes(R.color.errorColor)
                                    .setIcon(R.drawable.error)
                                    .setDuration(3000)
                                    .enableSwipeToDismiss()
                                    .enableIconPulse(true)
                                    .enableVibration(true)
                                    .disableOutsideTouch()
                                    .enableProgress(true)
                                    .setProgressColorInt(getResources().getColor(R.color.white))
                                    .show();
                            return;
                        }
                        else if(mobile.length() != 10)
                        {
                            YoYo.with(Techniques.Shake)
                                    .duration(700)
                                    .repeat(1)
                                    .playOn(mobileCard);
                            Alerter.create(SignUpActivity.this)
                                    .setText("Please enter a valid Mobile Number!")
                                    .setTextAppearance(R.style.ErrorAlert)
                                    .setBackgroundColorRes(R.color.errorColor)
                                    .setIcon(R.drawable.error)
                                    .setDuration(3000)
                                    .enableSwipeToDismiss()
                                    .enableIconPulse(true)
                                    .enableVibration(true)
                                    .disableOutsideTouch()
                                    .enableProgress(true)
                                    .setProgressColorInt(getResources().getColor(R.color.white))
                                    .show();
                            return;
                        }
                        else if(!createPassword.equals(confirmPassword))
                        {
                            YoYo.with(Techniques.Shake)
                                    .duration(700)
                                    .repeat(1)
                                    .playOn(createPasswordCard);
                            YoYo.with(Techniques.Shake)
                                    .duration(700)
                                    .repeat(1)
                                    .playOn(confirmPasswordCard);
                            Alerter.create(SignUpActivity.this)
                                    .setText("Those passwords didn't match. Try again!")
                                    .setTextAppearance(R.style.ErrorAlert)
                                    .setBackgroundColorRes(R.color.errorColor)
                                    .setIcon(R.drawable.error)
                                    .setDuration(3000)
                                    .enableSwipeToDismiss()
                                    .enableIconPulse(true)
                                    .enableVibration(true)
                                    .disableOutsideTouch()
                                    .enableProgress(true)
                                    .setProgressColorInt(getResources().getColor(R.color.white))
                                    .show();
                            return;
                        }
                        else if(!PASSWORD_PATTERN.matcher(createPassword).matches())
                        {
                            PasswordRequirementsBottomSheet passwordRequirementsBottomSheet = new PasswordRequirementsBottomSheet();
                            passwordRequirementsBottomSheet.show(getSupportFragmentManager(), "passwordRequirementsBottomSheet");
                            passwordRequirementsBottomSheet.setEnterTransition(R.anim.item_animation_slide_from_bottom);
                            return;
                        }
                        else {
                            progressDialog.show();
                            firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                    if(task.isSuccessful())
                                    {
                                        if(task.getResult().getSignInMethods().isEmpty())
                                        {
                                            progressDialog.dismiss();
                                            Common.signUpName = name;
                                            Common.signUpEmail = email;
                                            Common.signUpMobile = mobile;
                                            Common.signUpPassword = createPassword;
                                            startActivity(new Intent(SignUpActivity.this, VerifyOTPActivity.class));
                                            CustomIntent.customType(SignUpActivity.this, "left-to-right");
                                        }
                                        else
                                        {
                                            progressDialog.dismiss();
                                            YoYo.with(Techniques.Shake)
                                                    .duration(700)
                                                    .repeat(1)
                                                    .playOn(emailCard);
                                            Alerter.create(SignUpActivity.this)
                                                    .setText("There's already an account with that Email. Try another!")
                                                    .setTextAppearance(R.style.InfoAlert)
                                                    .setBackgroundColorRes(R.color.infoColor)
                                                    .setIcon(R.drawable.info)
                                                    .setDuration(3000)
                                                    .enableSwipeToDismiss()
                                                    .enableIconPulse(true)
                                                    .enableVibration(true)
                                                    .disableOutsideTouch()
                                                    .enableProgress(true)
                                                    .setProgressColorInt(getResources().getColor(R.color.white))
                                                    .show();
                                            return;
                                        }
                                    }
                                    else
                                    {
                                        progressDialog.dismiss();
                                        Alerter.create(SignUpActivity.this)
                                                .setText("Whoops! There was some error in that process!")
                                                .setTextAppearance(R.style.ErrorAlert)
                                                .setBackgroundColorRes(R.color.errorColor)
                                                .setIcon(R.drawable.error)
                                                .setDuration(3000)
                                                .enableSwipeToDismiss()
                                                .enableIconPulse(true)
                                                .enableVibration(true)
                                                .disableOutsideTouch()
                                                .enableProgress(true)
                                                .setProgressColorInt(getResources().getColor(R.color.white))
                                                .show();
                                        return;
                                    }
                                }
                            });
                        }
                    }
                });
            }
            else
            {
                signUpCard.setCardBackgroundColor(getResources().getColor(R.color.grey));
                signUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UIUtil.hideKeyboard(SignUpActivity.this);
                        if(name.isEmpty())
                        {
                            YoYo.with(Techniques.Shake)
                                    .duration(700)
                                    .repeat(1)
                                    .playOn(nameCard);
                        }
                        else if(email.isEmpty())
                        {
                            YoYo.with(Techniques.Shake)
                                    .duration(700)
                                    .repeat(1)
                                    .playOn(emailCard);
                        }
                        else if(mobile.isEmpty())
                        {
                            YoYo.with(Techniques.Shake)
                                    .duration(700)
                                    .repeat(1)
                                    .playOn(mobileCard);
                        }
                        else if(createPassword.isEmpty())
                        {
                            YoYo.with(Techniques.Shake)
                                    .duration(700)
                                    .repeat(1)
                                    .playOn(createPasswordCard);
                        }
                        else if(confirmPassword.isEmpty())
                        {
                            YoYo.with(Techniques.Shake)
                                    .duration(700)
                                    .repeat(1)
                                    .playOn(confirmPasswordCard);
                        }
                    }
                });
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(SignUpActivity.this, "right-to-left");
    }
}