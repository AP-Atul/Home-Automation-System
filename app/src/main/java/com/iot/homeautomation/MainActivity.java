package com.iot.homeautomation;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private ImageView fan;
    private ImageView fan_power;
    private ImageView bulb;
    private ImageView bulb_power;
    private TextView status, statusBulbText, statusFanText;
    private RelativeLayout connectBluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fan = findViewById(R.id.fan);
        fan_power = findViewById(R.id.fan_power);
        bulb = findViewById(R.id.bulb);
        bulb_power = findViewById(R.id.bulb_power);
        status = findViewById(R.id.status);
        statusBulbText = findViewById(R.id.statusBulbText);
        statusFanText = findViewById(R.id.statusFanText);

        ImageView mic = findViewById(R.id.mic);
        connectBluetooth = findViewById(R.id.connectBluetooth);
        RelativeLayout fanRL = findViewById(R.id.fanLayout);
        RelativeLayout bulbRL = findViewById(R.id.bulbLayout);

        checkBluetoothStatus();

        fanRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performTheCommand("fans");
            }
        });

        bulbRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performTheCommand("lights");
            }
        });


        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceInput();
            }
        });

        findViewById(R.id.commands).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Commands.class));
            }
        });

        connectBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        checkBluetoothStatus();
    }

    private void checkBluetoothStatus() {
        if (getBluetoothStatus()) {
            status.setTextColor(getResources().getColor(R.color.white));
            status.setText("Bluetooth Connected");
            connectBluetooth.setBackground(getResources().getDrawable(R.drawable.rounded_layout_color));
        } else {
            status.setTextColor(getResources().getColor(R.color.black));
            status.setText("Bluetooth Disconnected");
            connectBluetooth.setBackground(getResources().getDrawable(R.drawable.rounded_layout));
        }
    }

    private boolean getBluetoothStatus() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter != null & bluetoothAdapter.isEnabled()
                && bluetoothAdapter.getProfileConnectionState(BluetoothProfile.STATE_CONNECTED) == BluetoothHeadset.STATE_CONNECTED;
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?");

        startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                assert result != null;
                Toast.makeText(getApplicationContext(), result.get(0), Toast.LENGTH_SHORT).show();
                performTheCommand(result.get(0));
            }
        }
    }

    private void performTheCommand(String command) {
        switch (command) {
            case "turn on lights":
            case "light":
            case "lights":
            case "lights on":
                lightsOn();
                break;

            case "turn off lights":
            case "lights off":
                lightsOff();
                break;

            case "turn on fan":
            case "fan on":
            case "fans":
                fanOn();
                break;

            case "turn off fan":
            case "fan off":
                fanOff();
                break;
        }
    }

    private void lightsOn() {
        bulb.setImageResource(R.drawable.bulb_on);
        bulb_power.setImageResource(R.drawable.power_on);
        statusBulbText.setText("ON");
        statusBulbText.setTextColor(getResources().getColor(R.color.green));
    }

    private void lightsOff() {
        bulb.setImageResource(R.drawable.bulb_off);
        bulb_power.setImageResource(R.drawable.power_off);
        statusBulbText.setText("OFF");
        statusBulbText.setTextColor(getResources().getColor(R.color.black));
    }

    private void fanOn() {
        fan.setImageResource(R.drawable.fan_on);
        fan_power.setImageResource(R.drawable.power_on);
        statusFanText.setText("ON");
        statusFanText.setTextColor(getResources().getColor(R.color.green));
    }

    private void fanOff() {
        fan.setImageResource(R.drawable.fan_off);
        fan_power.setImageResource(R.drawable.power_off);
        statusFanText.setText("OFF");
        statusFanText.setTextColor(getResources().getColor(R.color.black));
    }
}
