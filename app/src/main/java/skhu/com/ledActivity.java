package skhu.com;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import android.widget.ImageButton;

public class ledActivity extends MainActivity {

    public String flag = "off";
//    ImageButton btn_change;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led);
//        btn_change = (ImageButton) findViewById(R.id.led_living_btn);
//        btn_change.setOnClickListener(this);

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST, clientId);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(ledActivity.this, "connected!", Toast.LENGTH_SHORT).show();
                    int mqttQos = 1;
                    String topic = "iot/led1";

                    try {
                        client.subscribe(topic, mqttQos);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(ledActivity.this, "fail!", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    public void pub_led1(View v) {
        String topic = topicstr;
        String message = "";
        if (flag == "off") {
            flag = "on";
            message = "ON";
//            btn_change.setSelected(true);
        } else if (flag == "on") {
            flag = "off";
            message = "OFF";
//            btn_change.setSelected(false);
        }
        try {
            client.publish(topic, message.getBytes(), 0, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void pub_led2(View v) {
        String topic = "iot/led2";
        String message = "";
        if (flag == "off") {
            flag = "on";

            message = "ON";
        } else if (flag == "on") {
            flag = "off";
            message = "OFF";
        }
        try {
            client.publish(topic, message.getBytes(), 0, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


}