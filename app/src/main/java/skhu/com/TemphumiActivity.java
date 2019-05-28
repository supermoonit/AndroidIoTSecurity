package skhu.com;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
import com.akaita.android.circularseekbar.CircularSeekBar;

import java.text.DecimalFormat;

public class TemphumiActivity extends MainActivity {

    //================================================================
    TextView Temp_view;
    TextView Humi_view;
    public float Temp_num;
    public float Humi_num;
    private Object v;

    //================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temphumi);

        //============Temp , Humi settng==============================================

        Temp_view = findViewById(R.id.present_temp_state);
        Temp_view.setText("현재온도 : ");

        Humi_view = findViewById(R.id.present_humi_state);
        Humi_view.setText("현재습도 : ");

        //=====================================================================
        String clientId = MqttClient.generateClientId();
        client_iot = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST_iot, clientId);
        client_server = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST_server, clientId);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());

        //========================Circle Bar=======================================
        CircularSeekBar seekBar1 = (CircularSeekBar) findViewById(R.id.Seek_bar1);
        CircularSeekBar seekBar2 = (CircularSeekBar) findViewById(R.id.Seek_bar2);

        seekBar1.setProgressTextFormat(new DecimalFormat("조정온도" + "###,###,##0"));
        seekBar2.setProgressTextFormat(new DecimalFormat("조정습도" + "###,###,##0"));
        seekBar1.setRingColor(Color.RED);
        seekBar2.setRingColor(Color.BLUE);


        try {
            IMqttToken token_iot = client_iot.connect(options);
            IMqttToken token_server = client_server.connect(options);
            token_iot.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    try {
                        client_iot.subscribe("iot/temp", 0);
                        client_iot.subscribe("iot/humi", 0);

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

        seekBar1.setOnCircularSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar seekBar, final float progress, boolean fromUser) {
                Temp_num = progress;
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
                String message = "ON";

//                Long num1 = (long) Temp_num;
//                message = num1.toString();
//                Log.d("iot/temp", message);

                try {
                    client_iot.publish("iot/led3", message.getBytes(), 0, false);
                    client_server.publish("server/led3", message.getBytes(), 0, false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        seekBar2.setOnCircularSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar seekBar, final float progress, boolean fromUser) {
                Humi_num = progress;
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
                  String message = "ON";
//                Long num2 = (long) Humi_num;
//                String message = num2.toString();
//                Log.d("iot/humi", message);
                try {
                    client_iot.publish("iot/led4", message.getBytes(), 0, false);
                    client_server.publish("server/led4", message.getBytes(), 0, false);
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        client_iot.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String msg1="", msg2="";
                Temp_view = findViewById(R.id.present_temp_state);
                Humi_view = findViewById(R.id.present_humi_state);

                    if(topic.equals("iot/temp")){
                        msg1 = new String(message.getPayload());
                        Temp_view.setText("현재온도 : " + msg1);
                    }
                    else if(topic.equals("iot/humi")){
                        msg2 = new String(message.getPayload());
                        Humi_view.setText("현재습도 : " + msg2);

                }
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }
}


