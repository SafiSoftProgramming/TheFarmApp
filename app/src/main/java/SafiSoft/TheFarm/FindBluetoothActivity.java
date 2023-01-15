package SafiSoft.TheFarm;

import static SafiSoft.TheFarm.ButtonsControlActivity.REQUEST_ENABLE_BT;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import java.util.ArrayList;

public class FindBluetoothActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String> mDeviceList = new ArrayList<String>();
    private BluetoothAdapter mBluetoothAdapter;
    SwipeRefreshLayout pullToRefresh ;
    String PROJECT_CONTROL_TYPE ;
    String PROJECT_NAME ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_bluetooth);



        PROJECT_CONTROL_TYPE = getIntent().getStringExtra("PROJECT_CONTROL_TYPE");
        PROJECT_NAME = getIntent().getStringExtra("PROJECT_NAME");

        listView = (ListView) findViewById(R.id.listView);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        //if bluetooth is not enabled then create Intent for user to turn it on
        if(!mBluetoothAdapter.isEnabled()){
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);

        }else{
            mBluetoothAdapter.startDiscovery();
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mReceiver, filter);
        }


        pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setColorSchemeColors(Color.parseColor("#179BA0"));
        pullToRefresh.setProgressBackgroundColorSchemeColor(Color.parseColor("#40C9CE"));

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mDeviceList.clear();
                listView.setAdapter(null);
                mBluetoothAdapter.startDiscovery();
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mReceiver, filter);
                pullToRefresh.setRefreshing(false);

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selectedItem = (String) parent.getItemAtPosition(position);
                String[] split = selectedItem.split("\n");
                String bluetooth_name = split[0];
                String bluetooth_mac = split[1];



                Intent intent = new Intent(FindBluetoothActivity.this, ButtonsControlActivity.class);
                intent.putExtra("bluetooth_name",bluetooth_name);
                intent.putExtra("bluetooth_mac",bluetooth_mac);
                intent.putExtra("PROJECT_NAME",PROJECT_NAME);
                startActivity(intent);
                finish();


            }
        });

        callActivity(); //////////////////////////////////// test only

    }




    public void callActivity() { /////////////////////////// test only
        new CountDownTimer(4000, 1000) {
            public void onTick(long millisUntilFinished) { }
            public void onFinish() {
                Intent intent = new Intent(FindBluetoothActivity.this, ButtonsControlActivity.class);
                startActivity(intent);
                finish();
            }
        }.start();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK){
                mBluetoothAdapter.startDiscovery();
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mReceiver, filter);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }
    } //onActivityResult

    @Override
    protected void onDestroy() {
     //   unregisterReceiver(mReceiver);
        super.onDestroy();
    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                mDeviceList.add(device.getName() + "\n" + device.getAddress());
                listView.setAdapter(new ArrayAdapter<String>(context,
                        android.R.layout.simple_list_item_1, mDeviceList));
            }
        }
    };

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(FindBluetoothActivity.this, ChooseToDoActivity.class);
        intent.putExtra("AD_STATE","false" );
        startActivity(intent);
        finish();
    }


}