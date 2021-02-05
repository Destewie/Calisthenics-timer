package paccoTimer;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Random;

import static android.app.Notification.GROUP_ALERT_SUMMARY;


public class ServizioTimer extends Service
{
    public static final int NOTIF_ID_TEMPORESTANTE = 666;
    public static final String NOTIF_CHANNEL_ID_TEMPORESTANTE = "777";
    public static final int NOTIF_ID_MUOVERSI = 222;
    public static final String NOTIF_CHANNEL_ID_MUOVERSI = "333";
    NotificationManager notificationManager;

    public CountDownTimer timer;
    long millisRestanti, oraDiFine;

    Random rand;
    MediaPlayer mp;

    //--------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onCreate()
    {
        super.onCreate();

        creazioneCanaleDiNotifica(NOTIF_CHANNEL_ID_TEMPORESTANTE, true);
        creazioneCanaleDiNotifica(NOTIF_CHANNEL_ID_MUOVERSI, false);
    }

    //--------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        oraDiFine = intent.getLongExtra(MainActivity.TEMPO_FINE, 123456);
        millisRestanti = oraDiFine - System.currentTimeMillis();

        if(millisRestanti > 0)
            avviaTimer();


        return START_STICKY;
    }

    //--------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        try {
            timer.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //--------------------------------------------------------------------------------------------------------------------------------------

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // NOTIFICHE
    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void creazioneCanaleDiNotifica(String numCanale, Boolean canaleSilenzioso)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = "Canale";
            String description = "Canale per i perditempo";


            NotificationChannel channel = new NotificationChannel(numCanale, name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);

            if(canaleSilenzioso)
            {
                channel.setSound(null, null);
                channel.setShowBadge(false);
            }

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @SuppressLint("WrongConstant")
    public void creazioneNotifica(String channelID, int notificationID, String titolo, String testo, boolean siCancellaSulClick, boolean nonEliminabile, int priorita, boolean foreground, boolean silent) {

        NotificationCompat.Builder mBuilder;

        if (foreground)
        {
            mBuilder = new NotificationCompat.Builder(this, channelID)
                    .setSmallIcon(R.mipmap.icona)
                    .setContentTitle(titolo)
                    .setContentText(testo)
                    .setPriority(priorita) //MAX = 2 //MIN = -2
                    .setOngoing(nonEliminabile);
        }
        else
        {
            Intent intent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            mBuilder = new NotificationCompat.Builder(this, channelID)
                    .setSmallIcon(R.mipmap.icona)
                    .setContentTitle(titolo)
                    .setContentText(testo)
                    .setPriority(priorita) //MAX = 2 //MIN = -2
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(siCancellaSulClick)
                    .setOngoing(nonEliminabile);
        }

        if(silent)
        {
            mBuilder.setVibrate(new long[]{0L}) //no vibrazione
                    .setGroupAlertBehavior(GROUP_ALERT_SUMMARY)
                    .setGroup("My group")
                    .setGroupSummary(false);
                    //.setDefaults(NotificationCompat.DEFAULT_ALL);
        }
        else
        {
            // specifica il suono
            mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setVibrate(new long[] { 0, 500, 200, 500, 200});
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationID, mBuilder.build());
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public String calcolaTestoNotifica()
    {
        int minuti, secondi;

        minuti = (int) millisRestanti / 1000 / 60;
        secondi = (int) (millisRestanti / 1000) % 60 ;

        return String.format("%02d:%02d", minuti, secondi);
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void distruggiNotifica()
    {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        StatusBarNotification[] notifications = notificationManager.getActiveNotifications();

        //notificationManager.cancelAll();

        for (StatusBarNotification notification : notifications)
        {
            try
            {
                if (notification.getId() == NOTIF_ID_TEMPORESTANTE)    //se la notifica è attiva in questo momento, allora cancellala
                {
                    notificationManager.cancel(NOTIF_ID_TEMPORESTANTE);
                } else if (notification.getId() == NOTIF_ID_MUOVERSI)    //se la notifica è attiva in questo momento, allora cancellala
                {
                    notificationManager.cancel(NOTIF_ID_MUOVERSI);
                    //notificationManager.cancelAll();
                }

            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    //----------------------------------------------------------------------------------------------------------------------------------------

    private void startForeground()
    {
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        startForeground(NOTIF_ID_TEMPORESTANTE, new NotificationCompat.Builder(this, NOTIF_CHANNEL_ID_TEMPORESTANTE)
                .setOngoing(true)
                .setSmallIcon(R.mipmap.bicep_icon)
                .setContentTitle("Servizio pausa")
                .setContentText("Service is running foreground")
                .setContentIntent(pendingIntent)
                .build());
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // TIMER
    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void avviaTimer()
    {
        startForeground();
        distruggiNotifica();

        MainActivity.vocePartita = false;

        timer = new CountDownTimer(millisRestanti,1000)
        {
            @Override
            public void onTick(long millisAllaFine)
            {
                millisRestanti = millisAllaFine;
                creazioneNotifica(NOTIF_CHANNEL_ID_TEMPORESTANTE, NOTIF_ID_TEMPORESTANTE,"Riposati pure :)", calcolaTestoNotifica(), false, true, 2, true, true);
            }

            @Override
            public void onFinish()
            {
                rand = new Random();

                if(MainActivity.voce && !MainActivity.notifica)
                {
                    int r = rand.nextInt(15);
                    switch (r)
                    {
                        case 0:
                            mp = MediaPlayer.create(getApplicationContext(), R.raw.durouomo);
                            break;
                        case 1:
                            mp = MediaPlayer.create(getApplicationContext(), R.raw.bastacincischiare);
                            break;
                        case 2:
                            mp = MediaPlayer.create(getApplicationContext(), R.raw.oradispingere);
                            break;
                        case 3:
                            mp = MediaPlayer.create(getApplicationContext(), R.raw.spingi);
                            break;
                        case 4:
                            mp = MediaPlayer.create(getApplicationContext(), R.raw.spingiunpochino);
                            break;
                        case 5:
                            mp = MediaPlayer.create(getApplicationContext(), R.raw.cazzeggiare);
                            break;
                        case 6:
                            mp = MediaPlayer.create(getApplicationContext(), R.raw.pump);
                            break;
                        case 7:
                            mp = MediaPlayer.create(getApplicationContext(), R.raw.secco);
                            break;
                        case 8:
                            mp = MediaPlayer.create(getApplicationContext(), R.raw.vaiuomo);
                            break;
                        case 9:
                            mp = MediaPlayer.create(getApplicationContext(), R.raw.microbo);
                            break;
                        case 10:
                            mp = MediaPlayer.create(getApplicationContext(), R.raw.soffrire);
                            break;
                        case 11:
                            mp = MediaPlayer.create(getApplicationContext(), R.raw.spingivise);
                            break;
                        case 12:
                            mp = MediaPlayer.create(getApplicationContext(), R.raw.spugna);
                            break;
                        default:
                            mp = MediaPlayer.create(getApplicationContext(), R.raw.vaiuomo);
                            break;
                    };
                    mp.start();
                    MainActivity.vocePartita = true;

                    creazioneNotifica(NOTIF_CHANNEL_ID_MUOVERSI, NOTIF_ID_MUOVERSI,"Fine della pacchia...", "Torna a spingere!", true, false, 2, false, true);
                }

                else if(!MainActivity.voce && MainActivity.notifica)
                {
                    creazioneNotifica(NOTIF_CHANNEL_ID_MUOVERSI, NOTIF_ID_MUOVERSI,"Fine della pacchia...", "Torna a spingere!", true, false, 2, false, false);
                }

                stopSelf();
            }

        }.start();
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    
}
