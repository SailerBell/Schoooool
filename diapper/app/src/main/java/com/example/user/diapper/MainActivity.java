package com.example.user.diapper;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;

import static com.example.user.diapper.R.id.sensitivity;
import static com.example.user.diapper.R.layout.activity_main;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton[] myBtn = new ImageButton[4];
    private CalendarView myCal;

    ////////////
////////////
////////////
    private BluetoothSPP bt;

    ////////////
/// ////////////
////////////
////////////
////////////
    int i = 1;  // alert 용
    int b = 1;  // bluetooth 용

    //////////////for bluetooth/////////////////////////////////////////////////////////
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


//////////////for bluetooth fin/////////////////////////////////////////////////////////

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    public Intent playVideo() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("https://youtu.be/Ns_MFqoIdsU");
        i.setDataAndType(uri, "video/*");
        return i;
    }


    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.alertSetting:
                final AlertDialog.Builder mute = new AlertDialog.Builder(MainActivity.this);
                mute.setTitle("Ignore All Data from Baby").setPositiveButton("알람 거부", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 데이터 수신하는 경우
                        Toast.makeText(getApplicationContext(), "모든 알람 수신을 거부합니다.", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("알람 수신", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 데이터를 지속적으로 받지만 알람은 울리지 않게 만든다.
                        Toast.makeText(getApplicationContext(), "모든 알람을 수신합니다.", Toast.LENGTH_SHORT).show();
                    }
                }).show();

                return true;

            case R.id.alertTimeLimitSetting:
                Toast.makeText(getApplicationContext(), "Item 2 Selected", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.diary:
                Toast.makeText(getApplicationContext(), "Item 3 Selected", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.babyInfo:
                playVideo();
                return true;

            case R.id.shinNkim:

                final AlertDialog.Builder dialogItem = new AlertDialog.Builder(MainActivity.this);
                dialogItem.setTitle("Info.").setMessage("Copyright 2018.\nSKUNIV. 2014305041 신우정\nSKUNIV. 2014305028 김혜진\n              all right reserved.")
                        .setNegativeButton("확인", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();

                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);

        /////////////

        Log.e(TAG, "onCreate"); //////////
        setContentView(R.layout.activity_main);
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // BluetoothService 클래스 생성
        if(btService == null)
        {
            btService = new BluetoothService(this, mHandler);
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////

        myBtn[0] = (ImageButton) findViewById(R.id.bluetooth);   // 0 : 블루투스 (연결 / 끊기)
        myBtn[1] = (ImageButton) findViewById(R.id.alert);   // 1 : 알람 (on / off)
        myBtn[2] = (ImageButton) findViewById(sensitivity);   // 2 : 기저귀 민감도 (상/중/하)
        myBtn[3] = (ImageButton) findViewById(R.id.calender);   // 3 : 달력
        myCal = (CalendarView) findViewById(R.id.calendarView); // 달력과 함께 사용할, 캘린더뷰


        for (int j = 0; j < 4; j++) {

            myBtn[j].setTag(j);
            myBtn[j].setOnClickListener(this);

        }

    }

    public void onClick(View view) {

        ImageButton newImageButton = (ImageButton) view;

        final ImageButton sensButton = (ImageButton) findViewById(sensitivity);

        for (ImageButton tempButton : myBtn) {

            if (tempButton == newImageButton) {
                int position = (Integer) view.getTag();

                switch (position) {


                    case 0: // BLUETOOTH
                        b=btService.checkBluetooth();   ///블루투스가 활성화 상태인지 비활성 상태인지 받아온다
                        if (b % 2 == 1) {                  //만약 블루투스가 비활성화 상태라면 on 시키기
                            myBtn[0].setImageResource(R.drawable.bluetooth);    // bluetooth on 이미지 만들어야 됨

                            ////////////////////////////////////////////////////
                            // bluetooth 연결 기능 넣기. 아두이노에서 정보 가져오기
                            //btService.enableBluetooth();
                            if(btService.getDeviceState())
                            { // 블루투스가 지원 가능한 기기일 때
                                // myBtn[0].setImageResource(R.drawable.bluetooth);    // bluetooth on 이미지 만들어야 됨

                                btService.enableBluetooth();
                                Toast.makeText(getApplicationContext(), "bluetooth on", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "noody there?", Toast.LENGTH_SHORT).show();
                                //finish();
                            }
                            //Toast.makeText(getApplicationContext(), "bluetooth on", Toast.LENGTH_SHORT).show();
                            // bluetooth 연결 기능 넣기. 아두이노에서 정보 가져오기


                            ////////////////////////////////////////////////////

                        } else {                     //만약 블루투스가 활성화 상태라면 블루투스 off 시키기
                            myBtn[0].setImageResource(R.drawable.bluetooth);
                            btService.enableBluetooth();
                            Toast.makeText(getApplicationContext(), "bluetooth off", Toast.LENGTH_SHORT).show();
                            // bluetooth 연결 끊는 기능 넣기. 아두이노에서 정보 가져오기

                        }
                        break;


                    case 1:// ALERT
                        if (i % 2 == 1) {
                            myBtn[1].setImageResource(R.drawable.alert_on);
                            Toast.makeText(getApplicationContext(), "alert on", Toast.LENGTH_SHORT).show();
                            i++;
                        } else {
                            myBtn[1].setImageResource(R.drawable.alert_off);
                            Toast.makeText(getApplicationContext(), "alert off", Toast.LENGTH_SHORT).show();
                            i --;
                        }
                        break;


                    case 2:// SENSITIVITY
                        sensButton.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                final String[] sensitivityArray = new String[]{"high", "mid", "low"};

                                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                                dlg.setTitle("Sensitivity").setSingleChoiceItems(sensitivityArray, 0, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int which) {
                                        if (sensitivityArray[which].equals("high"))
                                            myBtn[2].setImageResource(R.drawable.sensitivity_high);
                                        else if (sensitivityArray[which].equals("mid"))
                                            myBtn[2].setImageResource(R.drawable.sensitivity_middle);
                                        else if (sensitivityArray[which].equals("low"))
                                            myBtn[2].setImageResource(R.drawable.sensitivity_low);
                                    }
                                }).setPositiveButton("close", null).show();

                            }
                        });
                        break;


                    case 3: { // CALENDER
                        if (myCal.getVisibility() == CalendarView.VISIBLE) {
                            myCal.setVisibility(CalendarView.GONE);
                            Toast.makeText(getApplicationContext(), "달력 꺼져주세욤~", Toast.LENGTH_SHORT).show();
                        } else {
                            myCal.setVisibility(CalendarView.VISIBLE);
                            Toast.makeText(getApplicationContext(), "달력을 켜주세요!^0^", Toast.LENGTH_SHORT).show();
                        }
                        break;


                    }
                }
            }
        }

    }
    //////////////////////////////////////////////////////////////////////////////////////////
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + resultCode);

        switch (requestCode) {

            /** 추가된 부분 시작 **/
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    btService.getDeviceInfo(data);
                }
                break;
            /** 추가된 부분 끝 **/

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