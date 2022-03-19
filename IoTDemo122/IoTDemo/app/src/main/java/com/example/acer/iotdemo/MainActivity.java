package com.example.acer.iotdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {

    TextView txtTemp, txtLight;
    MQTTHelper mqttHelper;
    ToggleButton btnLED;

    private void startMQTT() {
        mqttHelper = new MQTTHelper(this,"asdfgh");

        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.w("Mqtt", "message is from: " + topic);
                Log.w("Mqtt", message.toString());
                if(topic.equals("Bach14637/feeds/Temperature")) {
                    txtTemp.setText(message.toString() + "°C");
                }
                if(topic.equals("Bach14637/feeds/Light")) {
                    txtLight.setText(message.toString() + "°C");
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }
    private  void sendDataToMQTT(String topic, String mess){

        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(true);

        byte[] b = mess.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);

        try {
            mqttHelper.mqttAndroidClient.publish(topic,msg);
        }catch (Exception e){}
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLED = findViewById(R.id.btnLED);
        txtTemp = findViewById(R.id.txtTemperature);
        txtLight = findViewById(R.id.txtLight);

        txtTemp.setText("25°C");
        txtLight.setText("80%");
        startMQTT();

        btnLED.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b == true){
                    sendDataToMQTT("Bach14637/feeds/LED", "1");
                }else{
                    sendDataToMQTT("Bach14637/feeds/LED", "0");
                }
            }
        });
    }
}
