package com.assistance.hashtagapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.tapadoo.alerter.Alerter;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import maes.tech.intentanim.CustomIntent;

public class ForgotPasswordActivity extends AppCompatActivity {

    ImageView backArrow, forgotPassword;
    EditText emailField;
    ConstraintLayout reset;
    CardView emailCard, resetCard;

    FirebaseAuth firebaseAuth;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccent));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

        backArrow = findViewById(R.id.arrow_back);
        forgotPassword = findViewById(R.id.forgot_password);
        emailField = findViewById(R.id.email);
        reset = findViewById(R.id.reset);
        resetCard = findViewById(R.id.reset_card);
        emailCard = findViewById(R.id.email_card);

        firebaseAuth = FirebaseAuth.getInstance();

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        progressDialog = new ProgressDialog(ForgotPasswordActivity.this);
        progressDialog.setMessage("Hold on...sending password reset link!");

        KeyboardVisibilityEvent.setEventListener(ForgotPasswordActivity.this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if(!isOpen)
                {
                    emailField.clearFocus();
                }
            }
        });

        emailField.addTextChangedListener(resetPasswordTextWatcher);
    }

    private TextWatcher resetPasswordTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            final String email = emailField.getText().toString().trim();

            if(!email.isEmpty())
            {
                resetCard.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                reset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UIUtil.hideKeyboard(ForgotPasswordActivity.this);
                        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                        {
                            YoYo.with(Techniques.Shake)
                                    .duration(700)
                                    .repeat(1)
                                    .playOn(emailCard);
                            Alerter.create(ForgotPasswordActivity.this)
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
                        else
                        {
                            progressDialog.show();
                            firebaseAuth.sendPasswordResetEmail(email)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                progressDialog.dismiss();
                                                Toast.makeText(ForgotPasswordActivity.this, "A password reset link has been sent to your Email!", Toast.LENGTH_LONG).show();
                                                onBackPressed();
                                            }
                                            else
                                            {
                                                progressDialog.dismiss();
                                                Alerter.create(ForgotPasswordActivity.this)
                                                        .setText("Whoa! We didn't find any account using that Email!")
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
                resetCard.setCardBackgroundColor(getResources().getColor(R.color.grey));
                reset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(email.isEmpty())
                        {
                            YoYo.with(Techniques.Shake)
                                    .duration(700)
                                    .repeat(1)
                                    .playOn(emailCard);
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
    protected void onStart() {
        super.onStart();
        YoYo.with(Techniques.Shake)
                .duration(700)
                .repeat(3)
                .playOn(forgotPassword);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(ForgotPasswordActivity.this, "right-to-left");
    }
}