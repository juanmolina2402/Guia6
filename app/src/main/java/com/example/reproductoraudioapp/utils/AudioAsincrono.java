package com.example.reproductoraudioapp.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.widget.TextView;

import com.example.reproductoraudioapp.R;

import java.util.concurrent.TimeUnit;

public class AudioAsincrono extends AsyncTask<Void, String,String> {

    Context context;
    TextView txvActual, txvFinal;

    MediaPlayer reproductorMusica;

    boolean pause = false;
    private String VIGILANTE = "vigilante";

    public AudioAsincrono(Context context, TextView txvActual, TextView txvFinal) {
        this.context = context;
        this.txvActual = txvActual;
        this.txvFinal = txvFinal;
    }

    @Override
    protected String doInBackground(Void... voids) {

        reproductorMusica.start();
        while( reproductorMusica.isPlaying() ) {
            esperaUnSegundo();
            publishProgress(tiempo(reproductorMusica.getCurrentPosition()));
            if ( pause == true ){
                synchronized (VIGILANTE){
                    try{
                        /** Realiza una pausa en el hilo */
                        reproductorMusica.pause();
                        VIGILANTE.wait();
                    }catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                    pause = false;
                    reproductorMusica.start();
                }
            }
        }
        return null;
    }


    private void esperaUnSegundo() {
        try{
            TimeUnit.SECONDS.sleep(0);
        } catch (InterruptedException ignore){}
    }

    /** Notifica al vigilante en todas sus llamadas con syncronized **/
    public void reanudarAudio() {
        synchronized (VIGILANTE){
            VIGILANTE.notify();
        }
    }

    public void pausarAudio() {
        pause = true;
    }

    public boolean esPause() {
        return pause;
    }

    private String tiempo(long tiempo) {
        long fin_min = TimeUnit.MILLISECONDS.toMinutes(tiempo);
        long fin_sec = TimeUnit.MILLISECONDS.toSeconds(tiempo) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(tiempo));
        return fin_min + ":" + fin_sec;
    }

    @Override
    protected void onPreExecute() {
        reproductorMusica = MediaPlayer.create(context, R.raw.nokia_tune);
        long fin = reproductorMusica.getDuration();
        txvFinal.setText(tiempo(fin));
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        txvActual.setText(values[0]);
        super.onProgressUpdate(values);
    }

    public void reiniciarAudio() {
        synchronized (VIGILANTE){
            VIGILANTE.notify();
            reproductorMusica.reset();
        }
    }
}
