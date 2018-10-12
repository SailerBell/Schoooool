package com.example.user.diapper;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;




public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton[] myBtn = new ImageButton[4];
    private CalendarView myCal;
    private RadioGroup myRadioGroup;

    int i = 1;  // alert 용
    int b = 1;  // bluetooth 용
    int s = 3;

    //////////////for bluetooth
    private BluetoothService btService=null;
    private static final String TAG = "Main";
    // Intent request code
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    // Layout
    private Button btn_Connect;
    private TextView txt_Result;

    private final Handler mHandler=new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
        }
    };

    @Override
    ///////////////////////
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate"); //////////
        setContentView(R.layout.activity_main);
        /////
        // BluetoothService 클래스 생성
        if(btService == null)
        {
            btService = new BluetoothService(this, mHandler);
        }


        myBtn[0] = (ImageButton) findViewById(R.id.bluetooth);   // 0 : 블루투스 (연결 / 끊기)
        myBtn[1] = (ImageButton) findViewById(R.id.alert);   // 1 : 알람 (on / off)
        myBtn[2] = (ImageButton) findViewById(R.id.sensitivity);   // 2 : 기저귀 민감도 (상/중/하)
        myBtn[3] = (ImageButton) findViewById(R.id.calender);   // 3 : 달력
        myCal = (CalendarView) findViewById(R.id.calendarView); // 달력과 함께 사용할, 캘린더뷰
        myRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        for (int j = 0; j < 4; j++) {

            myBtn[j].setTag(j);
            myBtn[j].setOnClickListener(this);

        }
    }

    public void onClick(View view) {


        ImageButton newImageButton = (ImageButton) view;
        int radioID = myRadioGroup.getCheckedRadioButtonId();
        RadioButton rb = (RadioButton) findViewById(radioID);

        for (ImageButton tempButton : myBtn) {

            if (tempButton == newImageButton) {
                int position = (Integer) view.getTag();

                if (position == 0) {    // BLUETOOTH
                    if (b % 2 == 1) {
                        if(btService.getDeviceState())
                        { // 블루투스가 지원 가능한 기기일 때
                            myBtn[0].setImageResource(R.drawable.bluetooth);    // bluetooth on 이미지 만들어야 됨
                             btService.enableBluetooth();
                            b++;
                        }
                        else
                        {
                            //finish();
                        }
                        //Toast.makeText(getApplicationContext(), "bluetooth on", Toast.LENGTH_SHORT).show();
                        // bluetooth 연결 기능 넣기. 아두이노에서 정보 가져오기

                    } else {
                        myBtn[0].setImageResource(R.drawable.bluetooth);
                        Toast.makeText(getApplicationContext(), "bluetooth off", Toast.LENGTH_SHORT).show();
                        // bluetooth 연결 끊는 기능 넣기. 아두이노에서 정보 가져오기
                        b--;
                    }

                } else if (position == 1) { // ALERT
                    if (i % 2 == 1) {
                        myBtn[1].setImageResource(R.drawable.alert_on);
                        Toast.makeText(getApplicationContext(), "alert on", Toast.LENGTH_SHORT).show();
                        i++;
                    } else {
                        myBtn[1].setImageResource(R.drawable.alert_off);
                        Toast.makeText(getApplicationContext(), "alert off", Toast.LENGTH_SHORT).show();
                        i--;
                    }
                } else if (position == 2) { // SENSIBILITY
        /* toString으로는 비교가 불가능하다 */
                    if (rb.getText().toString() == "top") {
                        Toast.makeText(getApplicationContext(), "top", Toast.LENGTH_SHORT).show();

                    } else if (rb.getText().toString() == "mid") {
                        Toast.makeText(getApplicationContext(), "mid", Toast.LENGTH_SHORT).show();

                    } else if (rb.getText().toString() == "low") {
                        Toast.makeText(getApplicationContext(), "low", Toast.LENGTH_SHORT).show();

                    }

                } else if (position == 3) { // CALENDER
                    if (myCal.getVisibility() == CalendarView.VISIBLE) {
                        myCal.setVisibility(CalendarView.GONE);
                        Toast.makeText(getApplicationContext(), "달력 꺼져주세욤~", Toast.LENGTH_SHORT).show();
                    } else {
                        myCal.setVisibility(CalendarView.VISIBLE);
                        Toast.makeText(getApplicationContext(), "달력을 켜주세요!^0^", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        }

    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + resultCode);

        switch (requestCode) {

            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {

                } else {

                    Log.d(TAG, "Bluetooth is not enabled");
                }
                break;
        }
    }




}