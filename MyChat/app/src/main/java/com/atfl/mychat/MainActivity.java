package com.atfl.mychat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private EditText edtName;
    private EditText edtServer;
    private EditText edtNPort;
    private Button btnConnect;
    private String alias;
    private String server;
    private String port;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtName = findViewById(R.id.edtAlias);
        edtServer = findViewById(R.id.edtServer);
        edtNPort = findViewById(R.id.edtPort);
        btnConnect = findViewById(R.id.btnConectar);
        btnConnect.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnConectar:{
                    server = edtServer.getText().toString();
                    port = edtNPort.getText().toString();
                    alias = edtName.getText().toString();

                    if(!server.trim().isEmpty() && !port.trim().isEmpty() && !alias.trim().isEmpty()){
                        Intent it = new Intent(this,ChatActivity.class);
                        it.putExtra(Util.ALIAS,alias);
                        it.putExtra(Util.PORT,port);
                        it.putExtra(Util.SERVER,server);

                        startActivity(it);
                    }else{
                        Toast.makeText(this,"Campos inválidos!", Toast.LENGTH_SHORT).show();
                    }
            }break;
        }
    }
}
