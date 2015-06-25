package com.aware.plugin.getData;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import com.aware.utils.IContextCard;

import static com.aware.plugin.getData.Plugin.MODE_PRIVATE;
import static com.aware.plugin.getData.Plugin.end;
import static com.aware.plugin.getData.Plugin.endClick;
import static com.aware.plugin.getData.Plugin.endSave;
import static com.aware.plugin.getData.Plugin.interval;
import static com.aware.plugin.getData.Plugin.start;
import static com.aware.plugin.getData.Plugin.startSave;

public class ContextCard implements IContextCard {

    // Preferences used to store values of start and end
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    // Strings for HashMap
    static final String PREFS = "getDataPrefs";
    static final String START = "start";
    static final String END = "end";
    static final String ENDCLICK = "endclick";

    //Empty constructor used to instantiate this card
    public ContextCard() {
    }

    //You may use sContext on uiChanger to do queries to databases, etc.
    private Context sContext;

    //Declare here all the UI elements you'll be accessing
    private View card;

    private TextView text;
    private TextView textPredict;

    private Button bStart;
    private Button bEnd;
    private Button bWrite;
    private Button bPredict;
    private Button bYes;
    private Button bNo;
    private Button bStill;
    private Button bWalking;
    private Button bRunning;


    private Chronometer chrono;

    /* A commenter pour enlever graphiques */
    //private static LinearLayout plot;

    //Used to load your context card
    private LayoutInflater sInflater;

    @Override
    public View getContextCard(Context context) {
        sContext = context;

        //Load card information to memory
        sInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        card = sInflater.inflate(R.layout.card, null);

        //Initialize UI elements from the card
        text = (TextView) card.findViewById(R.id.text);
        textPredict = (TextView) card.findViewById(R.id.prediction);

        bStart = (Button) card.findViewById(R.id.start);
        bEnd = (Button) card.findViewById(R.id.end);
        bWrite = (Button) card.findViewById(R.id.write);
        bPredict = (Button) card.findViewById(R.id.predictButton);
        bYes = (Button) card.findViewById(R.id.yesButton);
        bNo = (Button) card.findViewById(R.id.noButton);
        bStill = (Button) card.findViewById(R.id.stillButton);
        bWalking = (Button) card.findViewById(R.id.walkingButton);
        bRunning = (Button) card.findViewById(R.id.runningButton);

        chrono = (Chronometer) card.findViewById(R.id.chrono);

        // Load saved boolean
        sharedPreferences = sContext.getSharedPreferences(PREFS, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        endClick = sharedPreferences.getBoolean(ENDCLICK, true);

        bStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (endClick) {
                    start = System.currentTimeMillis();
                    chrono.setBase(SystemClock.elapsedRealtime());
                    chrono.start();
                    endClick = false;
                    editor.putBoolean(ENDCLICK, endClick);
                    editor.putLong(START, start);
                    editor.commit();
                }
            }
        });

        bEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!endClick) {
                    end = System.currentTimeMillis();
                    endClick = true;
                    chrono.stop();
                    editor.putBoolean(ENDCLICK, endClick);
                    editor.putLong(END, end);
                    editor.commit();
                }
            }
        });

        bWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Load start and end timestamp
                start = sharedPreferences.getLong(START, 0);
                end = sharedPreferences.getLong(END, 0);

                /* Code sans graphes */
                if (!(startSave == start || endSave == end)) {
                    start = start + interval;
                    end = end - 2 * interval;
                    long nbInterval = (long) Math.floor(((end - start) / interval));
                    long startBis = start;
                    long endBis = start + interval;
                    String s = "";
                    for (int i = 0; i < nbInterval; i++) {
                        for (int k = 0; k < 10; k++) {
                            s = DataHandler.getProcessedDataInLog(sContext.getContentResolver(),
                                    startBis + k * 1000, endBis + k * 1000);
                        }
                        startBis += interval;
                        endBis += interval;
                    }
                    text.setText(s + "/" + " Start : " + start + " End : " + end);
                } else {
                    text.setText("ALREADY SAVED" + "/" + " Start : " + start + " End : " + end);
                }
            }
        });

        bPredict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long end = System.currentTimeMillis();
                long start = end - 30000;
                long middle = (end + start) / 2;
                start = middle - 5000;
                end = middle + 5000;
                String s = DataHandler.getProcessedDataAndPredictSummary(sContext.getContentResolver(), start, end);
                textPredict.setText(s);
                bYes.setVisibility(View.VISIBLE);
                bNo.setVisibility(View.VISIBLE);
            }
        });

        bYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bStill.setVisibility(View.INVISIBLE);
                bWalking.setVisibility(View.INVISIBLE);
                bRunning.setVisibility(View.INVISIBLE);
                bYes.setVisibility(View.INVISIBLE);
                bNo.setVisibility(View.INVISIBLE);
            }
        });

        bNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bStill.setVisibility(View.VISIBLE);
                bWalking.setVisibility(View.VISIBLE);
                bRunning.setVisibility(View.VISIBLE);
            }
        });

        bStill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataHandler.PREDICTED_CLASS = 2;
                bStill.setVisibility(View.INVISIBLE);
                bWalking.setVisibility(View.INVISIBLE);
                bRunning.setVisibility(View.INVISIBLE);
                bYes.setVisibility(View.INVISIBLE);
                bNo.setVisibility(View.INVISIBLE);
            }
        });

        bWalking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataHandler.PREDICTED_CLASS = 1;
                bStill.setVisibility(View.INVISIBLE);
                bWalking.setVisibility(View.INVISIBLE);
                bRunning.setVisibility(View.INVISIBLE);
                bYes.setVisibility(View.INVISIBLE);
                bNo.setVisibility(View.INVISIBLE);
            }
        });

        bRunning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataHandler.PREDICTED_CLASS = 3;
                bStill.setVisibility(View.INVISIBLE);
                bWalking.setVisibility(View.INVISIBLE);
                bRunning.setVisibility(View.INVISIBLE);
                bYes.setVisibility(View.INVISIBLE);
                bNo.setVisibility(View.INVISIBLE);
            }
        });

        //Return the card to AWARE/apps
        return card;
    }
}