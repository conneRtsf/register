package myapp1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.myapp1.R;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button log = findViewById(R.id.btn1);
        log.setOnClickListener(this::postSync);
        Button Register = findViewById(R.id.btn2);
        Register.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, register.class);
            startActivity(intent);
        });
    }

    private OkHttpClient buildHttpClient() {
        return new OkHttpClient.Builder().retryOnConnectionFailure(true).connectTimeout(30, TimeUnit.SECONDS).build();
    }

    public void postSync(View view) {
        EditText editText1 = findViewById(R.id.enter1);
        EditText editText2 = findViewById(R.id.enter2);
        Log.e("postSync: ", String.valueOf(editText1.getText()));
        Log.e("postSync: ", String.valueOf(editText2.getText()));
        OkHttpClient httpClient = buildHttpClient();
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("password", String.valueOf(editText2.getText()));
        paramsMap.put("username", String.valueOf(editText1.getText()));
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : paramsMap.keySet()) {
            builder.add(key, Objects.requireNonNull(paramsMap.get(key)));
        }

        RequestBody formBody = builder.build();
        Request request = new Request.Builder().url("http://124.222.59.40:7000/user/login")
                .post(formBody)
                .addHeader("Connection", "close")
                .addHeader("content-type", "application/json;charset:utf-8")
                .build();
        Call call = httpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String ResponseData = response.body().string();
                Log.e("onResponse: ", ResponseData);
                new Thread(() -> {
                    try {
                        Looper.prepare();
                        JSONObject jsonObject1 = new JSONObject(ResponseData);
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                        builder1.setMessage(jsonObject1.getString("msg"));
                        builder1. setPositiveButton("确定", (dialog, which) -> {
                        });
                        AlertDialog alert = builder1.create();
                        alert.show();
                        Looper.loop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        });
    }
}
//get同步请求
    /*public void getSync(View view){
        new Thread(){
            @Override
            public void run(){
                Request request=new Request.Builder().url("http://httpbin.org/get").build();
                Call call=httpClient.newCall(request);
                try {
                    Response response=call.execute();
                    Log.i(TAG, "getSync: "+response.body().string());
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }.start();
    }*/

    //get异步请求
    /*public void getAsync(View view){
        Request request=new Request.Builder().url("http://httpbin.org/post").build();
        Call call=httpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    Log.i(TAG, "getAsync: "+response.body().string());
                }
            }
        });
    }*/