package edu.temple.serviceproject;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class MyService extends Service {


    IBinder myBinder = new TestBinder();

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {


        Notification.Builder n;

        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setAction("SOME_ACTION");
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, i, 0);
        n  = new Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Your service is running")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pIntent)
                .setAutoCancel(false);

        startForeground(1, n.build());
        return myBinder;
    }

    public class TestBinder extends Binder {
        MyService getService (){
            return MyService.this;
        }
    }

    Handler handler;

    public void getQuote(final String symbol, final Handler handler){
        this.handler = handler;

        Thread t = new Thread () {
            @Override
            public void run (){
                HttpClient httpClient = AndroidHttpClient.newInstance("Android Quoter");

                String quoteUrl = "http://finance.yahoo.com/webservice/v1/symbols/" + symbol + "/quote?" +

                        "format=json&view=basic";
                HttpGet getUrl = new HttpGet(quoteUrl);
                try {
                    HttpResponse response = httpClient.execute(getUrl);
                    JSONObject stockObject = new JSONObject(EntityUtils.toString(response.getEntity()));

                    Message msg = Message.obtain();
                    msg.obj = stockObject.toString();

                    handler.sendMessage(msg);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }



}
