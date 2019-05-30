package skhu.com;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class MainActivity extends AppCompatActivity {
    public static String MQTTHOST_iot = "tcp://192.168.0.2:1883";
    public static String MQTTHOST_server = "tcp://192.168.0.14:1883";
    static String USERNAME = "teamE";
    static String PASSWORD = "1q2w3e4r!";
    String topicstr_iot = "iot/";
    String topicstr_server = "app/";
    MqttAndroidClient client_iot;
    MqttAndroidClient client_server;

    Switch switch1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switch1 = findViewById(R.id.switch1);

        String clientId = MqttClient.generateClientId();
        client_iot = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST_iot, clientId);
        client_server = new MqttAndroidClient(this.getApplicationContext(), MQTTHOST_server, clientId);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(USERNAME);
        options.setPassword(PASSWORD.toCharArray());

        try {
            IMqttToken token_iot = client_iot.connect(options);
            IMqttToken token_server = client_server.connect(options);
            token_iot.setActionCallback(new IMqttActionListener() {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_camera) {
//            Toast.makeText(this, "메뉴 클릭", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://192.168.0.12:625/videostream.cgi?user=admin&pwd=123456789"));
            //웹페이지 들어가기
            startActivity(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void ledActivity(View view) {
        Intent intent2 = new Intent(MainActivity.this, ledActivity.class);
        startActivity(intent2);
    }

    public void TemphumiActivity(View view) {
        Intent intent3 = new Intent(MainActivity.this, TemphumiActivity.class);
        startActivity(intent3);
    }

    public void DoorActivity(View view) {
        Intent intent4 = new Intent(MainActivity.this, DoorActivity.class);
        startActivity(intent4);
    }

    public void CameraActivity(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://192.168.0.12:625/videostream.cgi?user=admin&pwd=123456789"));
        startActivity(intent);
    }

    public void onClick(View view) {
        View door_layout;
        door_layout = findViewById(R.id.door_layout);

        if ((switch1.isChecked()) == true) {
            final Snackbar snackbar = Snackbar.make(door_layout, "외출모드로 변환합니다.", Snackbar.LENGTH_INDEFINITE);

            snackbar.setAction("확인", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                    try {
                        client_iot.publish("iot/outside", "ON".getBytes(), 0, false);
                        client_server.publish("app/outside", "ON".getBytes(), 0, false);

                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            });
            snackbar.show();
        }

        if ((switch1.isChecked()) == false) {
                try {
                    client_iot.publish("iot/outside", "OFF".getBytes(), 0, false);
                    client_server.publish("app/outside ", "OFF".getBytes(), 0, false);

                } catch (MqttException e) {
                    e.printStackTrace();
                }

        }
    }
}
