package SafiSoft.TheFarm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChooseToDoActivity extends AppCompatActivity {

    ImageButton btn_connect_control  ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_to_do);

        btn_connect_control = findViewById(R.id.btn_connect_control);


        btn_connect_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseToDoActivity.this, FindBluetoothActivity.class);
                intent.putExtra("AD_STATE","false" );
                startActivity(intent);
                finish();
            }
        });



    }



    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        Toast.makeText(getApplicationContext(), "Press Twice to Exit", Toast.LENGTH_SHORT).show();

    }








}