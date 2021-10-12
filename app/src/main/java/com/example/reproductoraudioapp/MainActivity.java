package com.example.reproductoraudioapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reproductoraudioapp.utils.AudioAsincrono;

public class MainActivity extends AppCompatActivity {
    private ImageView btnIniciar, btnReiniciar;
    private TextView txvActual, txvFinal;
    private AudioAsincrono audioAsincrono;
    private SeekBar seekbar;
    MediaPlayer reproductorMusica;
    Runnable runnable;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txvActual = findViewById(R.id.txvActual);
        txvFinal  = findViewById(R.id.txvFinal);
        btnIniciar = findViewById(R.id.btnIniciar);
        btnReiniciar = findViewById(R.id.btnReiniciar);
        handler = new Handler();
        seekbar = (SeekBar) findViewById(R.id.seekBar);

        btnIniciar.setOnClickListener(v -> {
            iniciar();
        });

        btnReiniciar.setOnClickListener(  v -> {
            audioAsincrono.reiniciarAudio();
            btnIniciar.setImageResource(R.drawable.play);

            audioAsincrono = new AudioAsincrono(
                    MainActivity.this,
                    txvActual, txvFinal
            );
            reproductorMusica.reset();
            reproductorMusica.start();
            audioAsincrono.execute();
            playCycle();
        });

        reproductorMusica = MediaPlayer.create(this, R.raw.nokia_tune);
        reproductorMusica.setAudioStreamType(AudioManager.STREAM_MUSIC);
//
        reproductorMusica.setOnPreparedListener(mp -> {
            seekbar.setMax(reproductorMusica.getDuration());
            playCycle();
        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar,
                                          int i/*progress*/,
                                          boolean b/*fromUser*/) {
                if(b){
                    progressChangedValue = i;
                    reproductorMusica.seekTo(i);
                    seekbar.setProgress(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(MainActivity.this,
                        "Seek bar progress is :" + progressChangedValue,
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnIniciar.setOnClickListener(v -> {
            iniciar();
        });

    }

    private void iniciar() {
        if ( audioAsincrono == null ) {
            audioAsincrono = new AudioAsincrono(MainActivity.this, txvActual, txvFinal);
            btnIniciar.setImageResource(R.drawable.pause);
            reproductorMusica.start();
            playCycle();
            audioAsincrono.execute();
        } else if ( audioAsincrono.getStatus() == AsyncTask.Status.FINISHED ) {
            audioAsincrono = new AudioAsincrono(MainActivity.this, txvActual, txvFinal);
            reproductorMusica.start();
            playCycle();
            audioAsincrono.execute();
            btnIniciar.setImageResource(R.drawable.play);
        } else if (audioAsincrono.getStatus() == AsyncTask.Status.RUNNING && !audioAsincrono.esPause() ) {
            btnIniciar.setImageResource(R.drawable.play);
            reproductorMusica.pause();
            audioAsincrono.pausarAudio();
        } else {
            reproductorMusica.start();
            playCycle();
            audioAsincrono.reanudarAudio();
            btnIniciar.setImageResource(R.drawable.pause);
        }
    }

    public void playCycle(){
        seekbar.setProgress(reproductorMusica.getCurrentPosition() );

        if(reproductorMusica.isPlaying()){
            runnable = () -> playCycle();
            handler.postDelayed(runnable,1000);
        }
    }
}