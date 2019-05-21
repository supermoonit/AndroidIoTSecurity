package skhu.com;

import android.annotation.SuppressLint;
import android.media.Image;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;


public class DoorActivity extends MainActivity{
    String topicstr = "iot/Door";
    ImageButton img_btn;
    int flag = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door);
        img_btn = (ImageButton)findViewById(R.id.imageButton);
        //=====================================================================
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST, clientId);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());
        //=====================================================================

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                }
            });

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void onClick(View view) {

        if(flag == 1) {
            img_btn.setSelected(true);
            flag = 0;

            String topic = topicstr;
            String message = "OPEN";

            Log.d(topic, message);

            try {
                client.publish(topic, message.getBytes(), 0, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        else {
            img_btn.setSelected(false);
            flag = 1;

            String topic = topicstr;
            String message = "CLOSED";

            Log.d(topic, message);

            try {
                client.publish(topic, message.getBytes(), 0, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }
}
