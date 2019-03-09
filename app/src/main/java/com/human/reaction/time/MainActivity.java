package com.human.reaction.time;

//Mohamed Elayat, Ounissa Nait Amer, Ayoub El Hadri

import android.content.DialogInterface;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button button;
    Chronometer chrono;
    TextView text;
    Handler handler;
    int count;          //to keep track of which trial we're at.
    boolean running;    //indicates if stopwatch is running or not.
    long pauseTime;     //indicates the user's reaction time.
    double totalTime;   //sum of reaction times
    String state;


    //Initializes the application variables
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        text = findViewById(R.id.text_chrono);
        chrono = findViewById(R.id.chrono);
        button.setOnClickListener(this);
        chrono.stop();
        stateZero();

    }


    //3 instances of Runnable to launch
    //their respective methods.
    Runnable waiting = new Runnable() {
        @Override
        public void run() {
            waiting();
        }
    };

    Runnable yellow = new Runnable() {
        @Override
        public void run() {
            yellow();
        }
    };

    Runnable end = new Runnable() {
        @Override
        public void run() {
            end();
        }
    };


    //Listener for the application button.
    @Override
    public void onClick(View v) {

        if(  state == "stateZero"  ){
            waiting();
        }

        else if(  state == "waiting"  ){
            warning();
        }

        else if(  state == "yellow"  ){
            green();
        }

    }

    //The state before the user starts playing.("Repos")
    protected void stateZero(){

        state = "stateZero";
        handler = new Handler();
        chrono.setVisibility(chrono.INVISIBLE);
        text.setVisibility(text.INVISIBLE);
        button.setBackgroundColor(getResources().getColor(R.color.gray));
        button.setText(  "CLIQUEZ POUR COMMENCER"  );
        count = 1;
        totalTime = 0;

    }

    //Waiting state before the button turns yellow.
    //Lasts 3-10 secs.
    protected void waiting(){

        state = "waiting";
        resetChrono(  chrono  );
        pauseChrono(  chrono  );
        button.setBackgroundColor(getResources().getColor(R.color.gray));
        button.setText(  "ATTENDRE QUE LE BOUTON DEVIENNE JAUNE"  );
        chrono.setVisibility(  chrono.VISIBLE  );
        text.setText(  "Essai " + count +" de 5"  );
        text.setVisibility(  text.VISIBLE  );
        handler.postDelayed(  yellow,  randomTime()  );

    }


    //Warning state that's triggered by a
    //click during the waiting state.
    //Lasts 1.5 secs.
    protected void warning(){

        state = "warning";
        pauseChrono(  chrono  );
        handler.removeCallbacks(  yellow  );
        button.setBackgroundColor(getResources().getColor(R.color.red));
        button.setText("IL FAUT ATTENDRE QUE LE BOUTTON SOIT DEVENU JAUNE AVANT DE CLIQUER!");
        handler.postDelayed(  waiting, 1500  );

    }

    //State where the user needs to click.
    //Stopwatch is running.
    protected void yellow(){

        state = "yellow";
        startChrono(  chrono  );
        button.setBackgroundColor(getResources().getColor(R.color.yellow));
        button.setText(  "CLIQUEZ MAINTENANT!"  );

    }

    //Success state that comes after the user
    //clicks on the yellow button. Lasts 1.5 secs.
    //It either increments the count or ends the game.
    protected void green(){

        state = "green";
        count++;
        pauseChrono(  chrono  );
        button.setBackgroundColor(  getResources().getColor(  R.color.green  )  );
        button.setText(  "BON TRAVAIL"  );
        totalTime = totalTime + pauseTime;

        if(  count <= 5  ){
            handler.postDelayed(  waiting, 1500  );
        }

        else{
            handler.postDelayed(  end, 1500  );
        }

    }

    //Final state of the game.
    //Informs the user of his score and restarts
    //game upon a click.
    protected void end(){

        state = "end";

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Test complété");
        alertDialog.setMessage("Temps de réaction moyen: " + avgTime() + "s" );
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        stateZero();
                    }
                });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                stateZero();
            }
        });
        alertDialog.show();

    }

    //Method that generates a random long value
    //between 3000 and 10000.
    public long randomTime(){
        Random random = new Random();
        double randomDouble = 3 + random.nextDouble() * 7;
        long randomTime = (long)  (  randomDouble * 1000  );
        return randomTime;
    }

    //Method that returns the average reaction time
    public String avgTime(){
        String time;
        double t =   (totalTime/1000) / 5 ;
        DecimalFormat df = new DecimalFormat("#.000");

        if(  t < 1  ){
            time = "0" + df.format(  t  );
        }
        else {
            time = df.format(t);
        }

        return time;
    }

    //Method to start stopwatch
    public void startChrono(  View v  ){
        if(  !running  ){
            chrono.setBase(  SystemClock.elapsedRealtime() - pauseTime  );
            chrono.start();
            running = true;
        }
    }

    //Method to pause stopwatch
    public void pauseChrono(  View v  ){
        if(  running  ){
            pauseTime = SystemClock.elapsedRealtime() - chrono.getBase();
            chrono.stop();
            running = false;
        }
    }
    //Method to reset stopwatch
    public void resetChrono(  View v  ) {
        chrono.setBase(SystemClock.elapsedRealtime());
        pauseTime = 0;
    }

}

