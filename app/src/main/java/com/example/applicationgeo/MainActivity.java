package com.example.applicationgeo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.applicationgeo.Models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

public class MainActivity extends AppCompatActivity {

    Button btnSignIn, btnRegister;
    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;

    RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignIn = findViewById( R.id.btnSignIn );
        btnRegister = findViewById( R.id.btnRegister );

        root = findViewById( R.id.root_element );

        auth    = FirebaseAuth.getInstance();
        db      = FirebaseDatabase.getInstance();
        users   = db.getReference("Users");

        btnSignIn.setOnClickListener( new View.OnClickListener() {
           @Override
           public void onClick(View v) {
                showSignInWindow();
           }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegisterWindow();
            }
        });
    }

    private void showSignInWindow() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Sign In");
        dialog.setMessage("Enter Email & Pass");

        // Find Layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View sign_in_window = inflater.inflate( R.layout.sign_in_window, null );
        dialog.setView( sign_in_window );

        final MaterialEditText email = findViewById( R.id.emailField );
        final MaterialEditText pass  = findViewById( R.id.passField );

        dialog.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });

        dialog.setPositiveButton("Sign In", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if(TextUtils.isEmpty(email.getText().toString())) {
                    Snackbar.make(root, "Enter your email addr", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if( pass.getText().toString().length() < 5 ) {
                    Snackbar.make(root, "Enter your email addr", Snackbar.LENGTH_LONG).show();
                    return;
                }

                auth.signInWithEmailAndPassword( email.getText().toString(), pass.getText().toString() )
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make( root, "Sign In Failed" + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                });

            }
        });

        dialog.show();
    }


    private void showRegisterWindow() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Register");
        dialog.setMessage("Enter all details neccessary.");

        // Find Layout
        LayoutInflater inflator = LayoutInflater.from(this);
        View register_window = inflator.inflate( R.layout.register_window , null );
        dialog.setView(register_window);

        // Get Fields
        final MaterialEditText email = register_window.findViewById(R.id.emailField);
        final MaterialEditText name  = register_window.findViewById(R.id.nameField);
        final MaterialEditText pass  = register_window.findViewById(R.id.passField);
        final MaterialEditText phone = register_window.findViewById(R.id.phoneField);

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();
            }
        });



        dialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if(TextUtils.isEmpty(email.getText().toString())) {
                    Snackbar.make(root, "Enter your email addr", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if(TextUtils.isEmpty(name.getText().toString())) {
                    Snackbar.make(root, "Enter your Name", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if( pass.getText().toString().length() < 5 ) {
                    Snackbar.make(root, "Enter your email addr", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if(TextUtils.isEmpty(phone.getText().toString())) {
                    Snackbar.make(root, "Enter your phone", Snackbar.LENGTH_LONG).show();
                    return;
                }

                // Register User | need to use Auth
                auth.createUserWithEmailAndPassword( email.getText().toString(), pass.getText().toString() )
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            User user = new User(
                            name.getText().toString(),
                            email.getText().toString(),
                            pass.getText().toString(),
                            phone.getText().toString() );

                            // indexing user by field: email
                            users.child(user.getEmail())
                                    .setValue(user)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Snackbar.make( root, "User has been created!", Snackbar.LENGTH_SHORT ).show();
                                }
                            });
                        }
                });
            }
        });

        dialog.show();

    }
}
