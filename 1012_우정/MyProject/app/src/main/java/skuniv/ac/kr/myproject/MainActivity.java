package skuniv.ac.kr.myproject;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.Toast;

import static skuniv.ac.kr.myproject.R.id.sensitivity;
import static skuniv.ac.kr.myproject.R.layout.activity_main;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton[] myBtn = new ImageButton[4];
    private CalendarView myCal;


    int i = 1;  // alert 용
    int b = 1;  // bluetooth 용
    int s = 3;


    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){


        final MenuItem me = (MenuItem) findViewById(R.id.shinNkim);

        switch (item.getItemId()){
            case R.id.alertSetting:
                Toast.makeText(getApplicationContext(), "Item 1 Selected", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.alertTimeLimitSetting:
                Toast.makeText(getApplicationContext(), "Item 2 Selected", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.diary:
                Toast.makeText(getApplicationContext(), "Item 3 Selected", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.babyInfo:
                Toast.makeText(getApplicationContext(), "Item 4 Selected", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.shinNkim: // 여기 기능을 넣다가 말았음.. 어플 아이콘이나 만들러 가야쥥!~~

                me.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        AlertDialog.Builder dialogItem = new AlertDialog.Builder(MainActivity.this);
                        dialogItem.setTitle("title").setMessage("message").show();
                        //.setIcon(R.drawable.icon)
                        return false;
                    }
                });

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);

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

        final ImageButton sensButton = (ImageButton) findViewById(R.id.sensitivity);

        for (ImageButton tempButton : myBtn) {

            if (tempButton == newImageButton) {
                int position = (Integer) view.getTag();

                switch (position){


                    case 0: // BLUETOOTH
                        if (b % 2 == 1) {
                        myBtn[0].setImageResource(R.drawable.bluetooth);    // bluetooth on 이미지 만들어야 됨
                        Toast.makeText(getApplicationContext(), "bluetooth on", Toast.LENGTH_SHORT).show();
                        // bluetooth 연결 기능 넣기. 아두이노에서 정보 가져오기
                        b++;
                    } else {
                        myBtn[0].setImageResource(R.drawable.bluetooth);
                        Toast.makeText(getApplicationContext(), "bluetooth off", Toast.LENGTH_SHORT).show();
                        // bluetooth 연결 끊는 기능 넣기. 아두이노에서 정보 가져오기
                        b--;
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
                            i--;
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

}
