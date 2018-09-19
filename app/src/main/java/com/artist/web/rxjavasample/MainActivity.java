package com.artist.web.rxjavasample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.reactivex.Observable;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Observable<String> animalsObservable = Observable.just("Ant", "Bee", "Cat", "Dog", "Fox");

    }
}
