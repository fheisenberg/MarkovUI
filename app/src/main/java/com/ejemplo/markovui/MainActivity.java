package com.ejemplo.markovui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private MarkovDatabaseHelper dbHelper;
    private String lastState = null;

    private Button btnHome;
    private Button btnProfile;
    private Button btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new MarkovDatabaseHelper(this);

        btnHome = findViewById(R.id.btnHome);
        btnProfile = findViewById(R.id.btnProfile);
        btnSettings = findViewById(R.id.btnSettings);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button clicked = (Button) v;
                String currentState = clicked.getText().toString();

                if (lastState != null) {
                    dbHelper.recordTransition(lastState, currentState);
                    Toast.makeText(MainActivity.this,
                            "Transición: " + lastState + " → " + currentState,
                            Toast.LENGTH_SHORT).show();
                }

                lastState = currentState;
                highlightNextButton(lastState);
            }
        };

        btnHome.setOnClickListener(listener);
        btnProfile.setOnClickListener(listener);
        btnSettings.setOnClickListener(listener);

        resetButtonStyles();
    }

    private void highlightNextButton(String currentButton) {
        String next = dbHelper.getMostProbableNextButton(currentButton);
        resetButtonStyles();

        if (next != null) {
            Button nextBtn = findButtonByName(next);
            if (nextBtn != null) {
                nextBtn.setBackgroundColor(Color.YELLOW);
            }
        }
    }

    private void resetButtonStyles() {
        btnHome.setBackgroundColor(Color.LTGRAY);
        btnProfile.setBackgroundColor(Color.LTGRAY);
        btnSettings.setBackgroundColor(Color.LTGRAY);
    }

    private Button findButtonByName(String name) {
        switch (name) {
            case "Home": return btnHome;
            case "Profile": return btnProfile;
            case "Settings": return btnSettings;
            default: return null;
        }
    }
}


//package com.ejemplo.markovui;
//
//import android.graphics.Color;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.Toast;
//import androidx.appcompat.app.AppCompatActivity;
//import java.util.*;
//
//public class MainActivity extends AppCompatActivity {
//    private MarkovDatabaseHelper dbHelper;
//    private String lastState = null;
//    private Button btnHome;
//    private Button btnProfile;
//    private Button btnSettings;
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_main);
////        dbHelper = new MarkovDatabaseHelper(this);
////
////         btnHome = findViewById(R.id.btnHome);
////         btnProfile = findViewById(R.id.btnProfile);
////         btnSettings = findViewById(R.id.btnSettings);
////
////        View.OnClickListener listener = v -> {
////            String currentState = ((Button)v).getText().toString();
////            if (lastState != null) {
////                dbHelper.insertOrUpdateTransition(lastState, currentState);
////                Toast.makeText(this, "Transición: " + lastState + " → " + currentState, Toast.LENGTH_SHORT).show();
////            }
////            lastState = currentState;
////        };
////
////        btnHome.setOnClickListener(listener);
////        btnProfile.setOnClickListener(listener);
////        btnSettings.setOnClickListener(listener);
////    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        dbHelper = new MarkovDatabaseHelper(this);
//
//        btnHome = findViewById(R.id.btnHome);
//        btnProfile = findViewById(R.id.btnProfile);
//        btnSettings = findViewById(R.id.btnSettings);
//
//        View.OnClickListener listener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Button clickedBtn = (Button) v;
//                String currentState = clickedBtn.getText().toString();
//
//                if (lastState != null) {
//                    dbHelper.recordTransition(lastState, currentState);
//                    Toast.makeText(MainActivity.this, "Transición: " + lastState + " → " + currentState, Toast.LENGTH_SHORT).show();
//                }
//
//                lastState = currentState;
//
//                highlightNextButton(lastState);
//            }
//        };
//
//        btnHome.setOnClickListener(listener);
//        btnProfile.setOnClickListener(listener);
//        btnSettings.setOnClickListener(listener);
//
//        resetButtonStyles();
//    }
//
//    private void highlightNextButton(String currentButton) {
//        String next = dbHelper.getMostProbableNextButton(currentButton);
//        resetButtonStyles(); // Limpiar estilos anteriores
//
//        if (next != null) {
//            Button nextButton = findButtonByName(next);
//            if (nextButton != null) {
//                nextButton.setBackgroundColor(Color.YELLOW); // Color de resaltado
//            }
//        }
//    }
//
//    // Opcional: para limpiar estilos previos
//    private void resetButtonStyles() {
//        btnHome.setBackgroundColor(Color.LTGRAY);
//        btnProfile.setBackgroundColor(Color.LTGRAY);
//        btnSettings.setBackgroundColor(Color.LTGRAY);
//        // Añade todos los botones aquí
//    }
//
//    // Para buscar el botón por nombre
//    private Button findButtonByName(String name) {
//        switch (name) {
//            case "A": return btnHome;
//            case "B": return btnProfile;
//            case "C": return btnSettings;
//            // Añadir más según botones
//            default: return null;
//        }
//    }
//
//}