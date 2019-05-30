package skhu.com;

import android.annotation.SuppressLint;
import android.media.Image;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class DoorActivity extends MainActivity {

    //==============================================
    String topicstr_iot = "iot/motor1";
    String topicstr_server = "app/motor1";
    //==============================================

    //==============================================
    ImageButton img_btn;
    int flag = 1;
    //==============================================

    //==============================================
    TextView text;
    WebView web;
    String url = "http://192.168.0.2:8080/stream";
    //==============================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door);

        //================================================================
        img_btn = (ImageButton) findViewById(R.id.imageButton);
        text = (TextView) findViewById(R.id.textView);
        //================================================================

        //================================================================
        web = (WebView) findViewById(R.id.webView);
        web.setWebViewClient(new WebViewClient());

        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);
        web.loadUrl(url);
        //================================================================

        //=====================================================================
        String clientId = MqttClient.generateClientId();
        client_iot = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST_iot, clientId);
        client_server = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST_server, clientId);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());


        //=====================================================================

        try {
            IMqttToken token_iot = client_iot.connect(options);
            IMqttToken token_server = client_server.connect(options);
            token_iot.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    try {
                        client_iot.subscribe(topicstr_iot, 0);
                        client_server.subscribe(topicstr_server, 0);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                }
            });

        } catch (MqttException e) {
            e.printStackTrace();
        }

        client_iot.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String msg1 = "", msg2 = "";

                if (topic.equals("iot/motor1")) {
                    msg1 = new String(message.getPayload());
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    public void onClick(View view) {
        if (flag == 1) {
            img_btn.setSelected(true);
            flag = 0;

            String topic_iot = topicstr_iot;
            String topic_server = topicstr_server;
            String message = "OPEN";

            text.setText("문이 열립니다.");

            try {
                client_iot.publish(topic_iot, message.getBytes(), 0, false);
                client_server.publish(topic_server, message.getBytes(), 0, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        } else {
            img_btn.setSelected(false);
            flag = 1;

            String topic_iot = topicstr_iot;
            String topic_server = topicstr_server;
            String message = "CLOSE";

            text.setText("문이 닫힙니다.");

            try {
                client_iot.publish(topic_iot, message.getBytes(), 0, false);
                client_server.publish(topic_server, message.getBytes(), 0, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }
}
