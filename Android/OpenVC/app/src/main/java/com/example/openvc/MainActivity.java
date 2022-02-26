package com.example.openvc;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.openvc.open_cv.OpenCVCamera;

public class MainActivity extends AppCompatActivity {

    Button btnGravarRosto;
    Button btnReconhecerRosto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        btnGravarRosto = findViewById(R.id.btnGravarRosto);
        btnGravarRosto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //////////////////////////////////////////////////////////////////////////////////////

                builder.setTitle("Nome");

                final EditText input = new EditText(getApplicationContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //m_Text = input.getText().toString();
                        //Toast toast = Toast.makeText(getApplicationContext(), input.getText().toString(),Toast.LENGTH_SHORT);
                        //toast.show();

                        if(input.getText().toString().isEmpty()){
                            Toast toast = Toast.makeText(getApplicationContext(), "Informe seu nome!",Toast.LENGTH_SHORT);
                            toast.show();
                            return;
                        }

                        Intent intent = new Intent(getApplicationContext(), OpenCVCamera.class);
                        intent.putExtra("acao", "gravar");
                        intent.putExtra("nome", input.getText().toString().toUpperCase());
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                ///////////////////////////////

            }
        });

        btnReconhecerRosto = findViewById(R.id.btnReconhecerRosto);
        btnReconhecerRosto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), OpenCVCamera.class);
                intent.putExtra("acao", "reconhecer");
                startActivity(intent);
            }
        });
    }
}