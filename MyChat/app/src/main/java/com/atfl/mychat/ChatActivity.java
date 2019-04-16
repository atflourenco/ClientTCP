package com.atfl.mychat;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private Socket cliente;
    private boolean conectado;
    private InputStream inputStream;
    private OutputStream outputStream;
    private List<Mensagem> msgsEnviadas;
    private String alias;
    private String server;
    private String port;
    private Button btnEnviar;
    private Button btnDesconectar;
    private EditText edtMensagem;
    private boolean mensagemConnect;
    private TextView history;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent it = getIntent();
        if(it!=null){

            alias = it.getStringExtra(Util.ALIAS);
            server = it.getStringExtra(Util.SERVER);
            port = it.getStringExtra(Util.PORT);

            edtMensagem = findViewById(R.id.edt_mensagem);

            btnEnviar= findViewById(R.id.btn_enviar_mensagem);
            btnEnviar.setOnClickListener(this);

            btnDesconectar = findViewById(R.id.btn_desconectar);
            btnDesconectar.setOnClickListener(this);

            history = findViewById(R.id.txt_mensagens_enviadas);
            this.conectar();
        }
    }

    private void conectar(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InetAddress addr = InetAddress.getByName(server);
                    cliente = new Socket(addr, Integer.parseInt(port));
                    cliente.setKeepAlive(true);
                    cliente.setSoTimeout(5000);
                    if(cliente!=null){
                        conectado = true;
                        inputStream = cliente.getInputStream();
                        outputStream = cliente.getOutputStream();
                        msgsEnviadas = new ArrayList<>();
                        ChatActivity.this.startThreadReceivedDados();
                        if(conectado){
                            mensagemConnect = true;
                            sendMessage(Util.CONNECT);
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    ChatActivity.this.finish();
                }
            }
        });
        thread.start();
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_desconectar:{
                if(conectado){
                    sendMessage(Util.DISCONNECT);
                    this.desconectar();
                }
            }break;
            case R.id.btn_enviar_mensagem:{
                if(conectado){
                    String m = edtMensagem.getText().toString();
                    if(!m.trim().isEmpty()){
                        edtMensagem.setText(null);
                        this.sendMessage(m);
                        history.setText(history.getText().toString()+"\n"+"Enviado:" + m);
                    }else{
                        Toast.makeText(this,"Mensagem em branco!", Toast.LENGTH_SHORT).show();
                    }
                }
            }break;
        }
    }

    private void desconectar(){
        if(cliente!=null){
            if(cliente.isConnected()){
                try {

                    conectado = false;
                    inputStream.close();
                    outputStream.close();
                    cliente.close();
                    Toast.makeText(this, "Conexão finalizada com sucesso!",Toast.LENGTH_SHORT).show();
                    this.finish();
                } catch (IOException ex) {
                    Toast.makeText(this, "Falha ao desconectar!",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void startThreadReceivedDados(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String dadosRecebidos;
                while(ChatActivity.this.conectado){
                    try {
                        if(inputStream.available()>0){
                            byte[] data = new byte[inputStream.available()];
                            inputStream.read(data);
                            dadosRecebidos = new String(data);
                            System.out.println(dadosRecebidos);
                            if(dadosRecebidos.equals(Util.ACK)){
                                confirmSend();
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Falha ao receber dados!");
                    }
                }
            }
        }).start();
    }

    private void confirmSend(){

        ChatActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                if(!mensagemConnect){
                    Toast.makeText(ChatActivity.this, "Mensagem entregue com sucesso!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(ChatActivity.this, "Conexão realizada com sucesso!", Toast.LENGTH_SHORT).show();
                    mensagemConnect = false;
                }
            }
        });

    }
    private void sendMessage(final String msg) {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");


        Mensagem  mensagem = new Mensagem(ip,alias,msg,format.format(new Date()));
        msgsEnviadas.add(mensagem);
        Gson gson = new Gson();
        final Type type = new TypeToken<Mensagem>() {}.getType();
        final String json = gson.toJson(mensagem, type);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                PrintWriter output = new PrintWriter(outputStream);
                output.println(json);
                output.flush();

            }
        });

        thread.start();
    }

}
