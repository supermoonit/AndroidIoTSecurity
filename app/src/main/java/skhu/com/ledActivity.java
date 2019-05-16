package skhu.com;

import android.os.Bundle;
import android.view.View;
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

public class ledActivity extends MainActivity{
    public static String MQTTHOST = "tcp://192.168.0.2:1883";
    static String USERNAME = "root";
    static String PASSWORD = "1234";
    String topicstr = "iot/led1";
    MqttAndroidClient client;
    //
    public String flag1 = "off";
    public String flag2 = "off";
    public String flag3 = "off";

    //    ImageButton btn_change;
//

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
                    try {
                        client.subscribe("iot/led1", 0);
                        client.subscribe("iot/led2", 0);
                        client.subscribe("iot/led3", 0);
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
                //모든 메시지가 올때 Callback method
                if (topic.equals("iot/led1")){     //topic 별로 분기처리하여 작업을 수행할수도있음
                    String msg1 = new String(message.getPayload());
                    TextView led1_view =(TextView)findViewById(R.id.led1_view);
                    led1_view.setText("led1 현재상태 : " + msg1);
                }
                if(topic.equals("iot/led2")) {
                    String msg2 = new String(message.getPayload());
                    TextView led2_view = (TextView) findViewById(R.id.led2_view);
                    led2_view.setText("led2 현재상태 : " + msg2);
                }
                if(topic.equals("iot/led3")){
                    String msg3 = new String(message.getPayload());
                    TextView led3_view =(TextView)findViewById(R.id.led3_view);
                    led3_view.setText("led3 현재상태 : " + msg3);
                    }
                }


            @Override

            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });


    }


    public void pub_led1(View v) {
        String topic = topicstr;
        String message = "";

        if (flag1 == "off") {
            flag1 = "on";
            message = "ON";
//            btn_change.setSelected(true);
        } else if (flag1 == "on") {
            flag1 = "off";
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
        if (flag2 == "off") {
            flag2 = "on";

            message = "ON";
        } else if (flag2 == "on") {
            flag2 = "off";
            message = "OFF";
        }
        try {
            client.publish(topic, message.getBytes(), 0, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void pub_led3(View v) {
        String topic = "iot/led3";
        String message = "";
        if (flag3 == "off") {
            flag3 = "on";

            message = "ON";
        } else if (flag3 == "on") {
            flag3 = "off";
            message = "OFF";
        }
        try {
            client.publish(topic, message.getBytes(), 0, false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

}
