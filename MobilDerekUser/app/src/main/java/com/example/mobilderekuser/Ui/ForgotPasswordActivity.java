package com.example.mobilderekuser.Ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.mobilderekuser.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.tapadoo.alerter.Alerter;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Button btnRecover;
    private FloatingActionButton btnBack;
    private TextInputEditText et_EmailRecover;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initViews();

    }

    private void initViews() {
        et_EmailRecover = findViewById(R.id.edtRecover_email);
        btnRecover = findViewById(R.id.btn_Recover);
        btnBack = findViewById(R.id.btnBack);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Tunggu sebentar ya...");
        progressDialog.setCanceledOnTouchOutside(false);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        btnRecover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoverPassword();
            }
        });
    }


    private String email;
    private void recoverPassword() {
        email = et_EmailRecover.getText().toString().trim();
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Salah email...", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("Mengirimkan instruksi untuk reset password...");
        progressDialog.show();

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Alerter.create(ForgotPasswordActivity.this)
                                .setTitle("Tarik.in")
                                .setText("Instruksi reset password dikirim ke email Anda...")
                                .enableProgress(false)
                                .setBackgroundColorInt(getResources().getColor(R.color.orange_200))
                                .enableSwipeToDismiss()
                                .setIcon(R.drawable.logo_tarikin)
                                .setIconColorFilter(0)
                                .show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ForgotPasswordActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}