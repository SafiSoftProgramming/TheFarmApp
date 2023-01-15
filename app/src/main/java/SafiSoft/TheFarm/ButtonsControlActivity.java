package SafiSoft.TheFarm;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

public class ButtonsControlActivity extends AppCompatActivity {
    String bluetooth_name ;
    String bluetooth_mac ;

    String MODULE_MAC ;
    public final static int REQUEST_ENABLE_BT = 1;
    UUID MY_UUID ;

    BluetoothAdapter bta;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    ConnectedThread btt = null;
    public Handler mHandler;

    TextView txtv_command_history ;

    ImageButton btn_Food ;
    ImageButton btn_Water ;
    ImageButton btn_Hot_Fan ;
    ImageButton btn_Cold_Fan ;
    ImageButton btn_White_Lamp ;
    ImageButton btn_Blue_Lamp ;
    ImageButton btn_Street_Lamp ;
    ImageButton btn_turn_off ;

    TextView txtv_bluetooth_name ;
    TextView txtv_bluetooth_mac ;
    TextView txtv_temp ;
    TextView txtv_solar_state ;
    TextView txtv_volt_value ;

    LottieAnimationView lottie_icon_battry ;

    boolean Hot_Fan_ON = false ;
    boolean Cold_Fan_ON = false ;
    boolean White_Lamp_ON = false ;
    boolean Blue_Lamp_ON = false ;
    boolean Street_Lamp_ON = false ;
    boolean Water = false ;
    boolean stop_anim = false ;


    int temp ;
    int temp_val ;
    CountDownTimer countDownTimer_for_hot_fan ;
    CountDownTimer countDownTimer_for_cold_fan ;
    char solar = '0';




    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buttons_control);

        bluetooth_name = getIntent().getStringExtra("bluetooth_name");
        bluetooth_mac = getIntent().getStringExtra("bluetooth_mac");

        MODULE_MAC = bluetooth_mac ;
        MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        bta = BluetoothAdapter.getDefaultAdapter();

        //if bluetooth is not enabled then create Intent for user to turn it on
        if(!bta.isEnabled()){
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
        }else{
       //     initiateBluetoothProcess();
        }

        btn_Food = findViewById(R.id.btn_Food);
        btn_Water = findViewById(R.id.btn_Water);
        btn_Hot_Fan = findViewById(R.id.btn_Hot_Fan);
        btn_Cold_Fan = findViewById(R.id.btn_Cold_Fan);
        btn_White_Lamp = findViewById(R.id.btn_White_Lamp);
        btn_Blue_Lamp = findViewById(R.id.btn_Blue_Lamp);
        btn_Street_Lamp = findViewById(R.id.btn_Street_Lamp);
        btn_turn_off = findViewById(R.id.btn_turn_off);

        txtv_bluetooth_name =findViewById(R.id.txtv_bluetooth_name);
        txtv_bluetooth_mac =findViewById(R.id.txtv_bluetooth_mac);
        txtv_command_history = findViewById(R.id.txtv_command_history);
        txtv_temp = findViewById(R.id.txtv_temp);
        txtv_solar_state = findViewById(R.id.txtv_solar_state);
        txtv_volt_value =  findViewById(R.id.txtv_volt_value);

        lottie_icon_battry = findViewById(R.id.lottie_icon_battry);


        txtv_bluetooth_name.setText(bluetooth_name);
        txtv_bluetooth_mac.setText(bluetooth_mac);



        btn_Food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SEND_COMMAND("F"); // end
            }
        });

        btn_Water.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Water == false) {
                    SEND_COMMAND("P"); // end
                    Water = true ;
                }
                else {
                    SEND_COMMAND("p");
                    Water = false ;
                }
            }
        });

        btn_Hot_Fan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Hot_Fan_ON == false) {
                    SEND_COMMAND("H");
                    Hot_Fan_ON = true ;

                    //turn off cold fan
                    SEND_COMMAND("c");

                    if(Cold_Fan_ON) {
                        countDownTimer_for_cold_fan.cancel();
                        Cold_Fan_ON = false ;
                    }

                    temp_val = Integer.valueOf(txtv_temp.getText().toString());

                    countDownTimer_for_hot_fan = new CountDownTimer(temp_val*1000, 1000) {
                        public void onTick(long millisUntilFinished) {
                            if(temp_val <= 32) {
                                temp_val = temp_val + 1;
                                txtv_temp.setText(Integer.toString(temp_val));
                            }
                            else {
                                this.cancel();
                            }
                        }
                        public void onFinish() {

                        }
                    }.start();


                }
                else {
                    SEND_COMMAND("h");
                    Hot_Fan_ON = false ;
                    countDownTimer_for_hot_fan.cancel();
                }

            }
        });

        btn_Cold_Fan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Cold_Fan_ON == false) {
                    SEND_COMMAND("C");
                    Cold_Fan_ON = true ;

                    // turn off hot fan
                    SEND_COMMAND("h");

                    if(Hot_Fan_ON) {
                        countDownTimer_for_hot_fan.cancel();
                        Hot_Fan_ON = false ;
                    }


                    temp_val = Integer.valueOf(txtv_temp.getText().toString());

                    countDownTimer_for_cold_fan = new CountDownTimer(temp_val*1000, 1000) {
                        public void onTick(long millisUntilFinished) {
                            if(temp_val >= 22) {
                                temp_val = temp_val - 1;
                                txtv_temp.setText(Integer.toString(temp_val));
                            }
                            else {
                                this.cancel();
                            }
                        }
                        public void onFinish() {

                        }
                    }.start();


                }
                else {
                    SEND_COMMAND("c");
                    Cold_Fan_ON = false ;
                    countDownTimer_for_cold_fan.cancel();
                }
            }
        });

        btn_White_Lamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(White_Lamp_ON == false) {
                    SEND_COMMAND("W");
                    White_Lamp_ON = true ;
                }
                else {
                    SEND_COMMAND("w");
                    White_Lamp_ON = false ;
                }
            }
        });

        btn_Blue_Lamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Blue_Lamp_ON == false) {
                    SEND_COMMAND("B");
                    Blue_Lamp_ON = true ;
                }
                else {
                    SEND_COMMAND("b");
                    Blue_Lamp_ON = false ;
                }
            }
        });

        btn_Street_Lamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Street_Lamp_ON == false) {
                    SEND_COMMAND("L");
                    Street_Lamp_ON = true ;
                }
                else {
                    SEND_COMMAND("l");
                    Street_Lamp_ON = false ;
                }
            }
        });


        btn_turn_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                   resetConnection();
                   Intent intent = new Intent(ButtonsControlActivity.this, ChooseToDoActivity.class);
                   startActivity(intent);
                   finish();
            }
        });


        txtv_solar_state.setText("not Charging");
        txtv_volt_value.setText("1V");
        lottie_icon_battry.setFrame(0);
        lottie_icon_battry.pauseAnimation();
        stop_anim = false ;


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == REQUEST_ENABLE_BT){
      //      initiateBluetoothProcess();
        }
    }



    public void initiateBluetoothProcess(){

        if(bta.isEnabled()){
            //attempt to connect to bluetooth module
            BluetoothSocket tmp = null;
            mmDevice = bta.getRemoteDevice(MODULE_MAC);

            //create socket
            try {
                tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
                mmSocket = tmp;
                mmSocket.connect();
                Log.i("[BLUETOOTH]","Connected to: "+mmDevice.getName());
            }catch(IOException e){
                try{mmSocket.close();}catch(IOException c){return;}
            }

            Log.i("[BLUETOOTH]", "Creating handler");
            mHandler = new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(Message msg) {

                    super.handleMessage(msg);
                    if(msg.what == ConnectedThread.RESPONSE_MESSAGE){
                        String txt = (String)msg.obj;

                        StringBuilder sb = new StringBuilder();
                        sb.append(txt);                                      // append string
                        String sbprint = sb.substring(0, sb.length());            // extract string
                        sb.delete(0, sb.length());
                        final String finalSbprint = sb.append(sbprint).toString();

                        txtv_command_history.setText("");
                        txtv_command_history.setText(finalSbprint);


                        solar = finalSbprint.charAt(0) ;





                        if(solar == '0'){
                            txtv_solar_state.setText("not Charging");
                            txtv_volt_value.setText("1V");
                            lottie_icon_battry.setFrame(0);
                            lottie_icon_battry.pauseAnimation();
                            stop_anim = false ;
                        }
                        else if (solar == '1'){
                            txtv_solar_state.setText("Charging");
                            txtv_volt_value.setText("6V");

                            if(stop_anim == false ) {
                                lottie_icon_battry.playAnimation();
                            }
                            stop_anim = true ;

                        }



                    }
                }
            };

            Log.i("[BLUETOOTH]", "Creating and running Thread");
            btt = new ConnectedThread(mmSocket,mHandler);
            btt.start();


        }
    }

    private void resetConnection() {
        if (mmSocket != null) {
            try {mmSocket.close();} catch (Exception e) {}
            mmSocket = null;

        }
    }

    private void SEND_COMMAND(String Commend){

        try {
             if(!isConnected(mmDevice)){
                 Toast.makeText(getApplicationContext(), "Bluetooth Connection Lost!", Toast.LENGTH_SHORT).show();
                 txtv_command_history.setText("");
                 txtv_command_history.setText("فقد الأتصال بمتحكم المزرعه الألكترونى راجع أتصال البلوتوث مرة اخرى");
             }

        if (mmSocket.isConnected() && btt != null) {
            btt.write(Commend.getBytes());
        } else {

            txtv_command_history.setText("");
            txtv_command_history.setText("يوجد خطئ ما راجع الأتصال بالمتحكم");
        }

     }
     catch (Exception E){
         txtv_command_history.setText("");
         txtv_command_history.setText("يوجد خطئ ما راجع الأتصال بالمتحكم");
     }


    }

    public static boolean isConnected(BluetoothDevice device) {
        try {
            Method m = device.getClass().getMethod("isConnected", (Class[]) null);
            boolean connected = (boolean) m.invoke(device, (Object[]) null);
            return connected;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }


    @Override
    public void onBackPressed() {
        resetConnection();
        Intent intent = new Intent(ButtonsControlActivity.this, ChooseToDoActivity.class);
        startActivity(intent);
        finish();
    }





}
