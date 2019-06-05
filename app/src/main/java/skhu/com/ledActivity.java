package skhu.com;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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

//import org.eclipse.paho.client.mqttv3.IMqttMessageListener;

public class ledActivity extends MainActivity {
    public static String MQTTHOST = "tcp://192.168.0.2:1883";
    MqttAndroidClient client;
    int flag1_img = 1;
    int flag2_img = 1;
    String flag1 = "off";
    String flag2 = "off";
    ImageButton img_btn1;
    ImageButton img_btn2;
    String msg1, msg2, status_msg1, status_msg2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led);

        img_btn1 = (ImageButton) findViewById(R.id.led_living_btn);
        img_btn2 = (ImageButton) findViewById(R.id.led_kit_btn);

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST, clientId);
        client_server = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST_server, clientId);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());

        try {
            IMqttToken token = client.connect(options);
            IMqttToken token_server = client_server.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(ledActivity.this, "connected!", Toast.LENGTH_SHORT).show();
                    try {
                        client.subscribe("iot/led1", 0);
                        client.subscribe("iot/led2", 0);
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


        client.setCallback(new MqttCallback() {  //클라이언트의 콜백을 처리하는부분
            @Override
            public void connectionLost(Throwable cause) {
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d(this.getClass().getName(), "버튼 Message Arrive 들어옴");
                if(topic.equals("iot/led1_status")){
                    msg1 = new String(message.getPayload());
                    if (msg1== "0") {
                        Toast.makeText(ledActivity.this, "iotiot", Toast.LENGTH_SHORT).show();
                        Log.d(this.getClass().getName(), "버튼 눌렀을때 led1 on");
                        TextView led_text1 = (TextView) findViewById(R.id.led1_view);
                        led_text1.setText("거실 상태 : " + msg1);
                        img_btn1.setSelected(true);
                    }
                }
            }
            @Override

            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });


    }


    public void pub_led1(View v) {
        String topic = "iot/led1";
        String topic_server = "app/led1";
        String message = "";

        if (flag1 == "off") {
            flag1 = "on";
            message = "ON";
            img_btn1.setSelected(true);
//            btn_change.setSelected(true);
        } else if (flag1 == "on") {
            flag1 = "off";
            message = "OFF";
            img_btn1.setSelected(false);
//            btn_change.setSelected(false);
        }
        try {
            client.publish(topic, message.getBytes(), 0, false);
            client_server.publish(topic_server, message.getBytes(), 0, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void pub_led2(View v) {
        String topic = "iot/led2";
        String topic_server = "app/led2";
        String message = "";
        if (flag2 == "off") {
            flag2 = "on";
            message = "ON";
            img_btn2.setSelected(true);
        } else if (flag2 == "on") {
            flag2 = "off";
            message = "OFF";
            img_btn2.setSelected(false);
        }
        try {
            client.publish(topic, message.getBytes(), 0, false);
            client_server.publish(topic_server, message.getBytes(), 0, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void pub_led_refresh(View v) {
        String topic = "iot/led";
        String topic_server = "app/led";
        String message = "status";
        try {
            client.publish(topic, message.getBytes(), 0, false);
            client_server.publish(topic_server, message.getBytes(), 0, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}