package mape3.project;

import android.app.AlarmManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.res.Configuration;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.concurrent.TimeUnit;

public class FragmentActivity extends Activity {
    PendingIntent pi;
    BroadcastReceiver br;
    AlarmManager am;
    long exitTime;
    static long startTime;
    static String user_name;
    private static Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        TextView campMesaj = (TextView) findViewById(R.id.text_input);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String nume = extras.getString("Nume");
            if (nume != null && nume.length() > 0) {
                campMesaj.setText("Welcome, " + nume + "!");
            } else {
                campMesaj.setText("Welcome!");
            }
        } else {
            campMesaj.setText("Welcome!");
        }
        ctx = this;

        if (savedInstanceState == null) {
            /* Set Activity Start Time */
            startTime = System.currentTimeMillis();
            /* Set Exit Time */
            int seconds = getIntent().getIntExtra("alarm_second", 0);
            long s = TimeUnit.SECONDS.toMillis(seconds);
            exitTime = System.currentTimeMillis() + s;
            /* Set Name */
            user_name = getIntent().getStringExtra("Nume");
        } else {
            /* Set Activity Start Time */
            startTime = savedInstanceState.getLong("startTime");
            /* Set Exit Time */
            exitTime = savedInstanceState.getLong("exitTime");
            /* Set Name */
            user_name = savedInstanceState.getString("user_name");
        }

        /* Set Alarm */
        setAlarm();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("exitTime", exitTime);
        outState.putLong("startTime", startTime);
        outState.putString("user_name", user_name);
    }

    public static class FragmentTitluri extends ListFragment {
        int elementSelectat = 0;
        boolean tipLandscape;

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            // Populam lista dintr-un tablou static.
            setListAdapter(new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_activated_1,
                    mape3.project.Resurse.TITLURI));
            View cadruDetalii = getActivity().findViewById(R.id.detalii);
            tipLandscape = cadruDetalii != null && cadruDetalii.getVisibility() == View.VISIBLE;
            if (savedInstanceState != null) {
                // determinam elenmentul selectat.
                elementSelectat = savedInstanceState.getInt("curChoice", 0);
            }
            if (tipLandscape) {
                // afisam detalii despre elementul selectat
                getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                afiseazaDetalii(elementSelectat);
            }
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt("curChoice", elementSelectat);
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            afiseazaDetalii(position);
        }

        //functia care afiseaza detalii despre elementul selectat

        @RequiresApi(api = Build.VERSION_CODES.M)
        void afiseazaDetalii(int index) {
            elementSelectat = index;
            if (tipLandscape) {
                // evidentiem elementul selectat
                getListView().setItemChecked(index, true);
                // Verificam ce element este deja afisat si il inlocuim daca este
                //nevoie
                DetaliiFragmente detalii = (DetaliiFragmente)
                        getFragmentManager().findFragmentById(R.id.detalii);
                if (detalii == null || detalii.daIndexSelectat() != index) {
                    // Construim un fragment nou
                    if(index==0)
                        detalii=TTSStatus.newInstance(index);
                    else if(index==1)
                        detalii=Conf.newInstance(index);
                    else if(index==2)
                        detalii=HMI.newInstance(index);
                    else if(index==3)
                        detalii=Navigation.newInstance(index);
                    else if(index==4)
                        detalii=SMS.newInstance(index);
                    else if (index == 5) {
                        detalii=new DetaliiFragmente();
                        /* Send Activity status */
                        setActivityDetails();
                    } else
                        detalii = DetaliiFragmente.newInstance(index);

                    // inlocuim fragmentul existent cu cel nou.
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.detalii, detalii);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    ft.commit();
                }
            } else {
                // Lansam o noua activitate pentru a afisa afragentul cu detalii
                // despre elementul selectat.
                Intent intent = new Intent();
                intent.setClass(getActivity(), DetaliiTitluActivity.class);
                intent.putExtra("index", index);
                startActivity(intent);
            }
        }
    }

    /**
     * Este al doilea "Activity" care se va afisa cand ecranul nu este suficient de lat (modul Portret).
     */

    public static class DetaliiTitluActivity extends Activity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE) {
                // nu este necesar in modul Landscape.
                finish();
                return;
            }

            if (savedInstanceState == null) {
                // facem legatura cu fragmentul FragmentDetalii.
                int index=getIntent().getExtras().getInt("index");
                DetaliiFragmente detalii;
                if(index==0)
                    detalii = new TTSStatus();
                else if(index==1)
                    detalii=new Conf();
                else if(index==2)
                    detalii=new HMI();
                else if(index==3)
                    detalii=new Navigation();
                else if(index==4)
                    detalii=new SMS();
                else if (index == 5) {
                    /* Send Activity status */
                    setActivityDetails();
                    detalii=new DetaliiFragmente();
                }
                else
                    detalii=new DetaliiFragmente();

                /* Set Background Color */
                detalii.setView(this.getWindow().getDecorView().getRootView());
                if(Resurse.CULORI[index] != ""){
                    detalii.setColor(Resurse.CULORI[index]);
                } else {
                    detalii.setColor("#efebe9");
                }
                detalii.setArguments(getIntent().getExtras());
                getFragmentManager().beginTransaction().add(android.R.id.content, detalii).commit();
            }
        }
    }

    private void setAlarm() {
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent i) {
                /* Send Activity status */
                setActivityDetails();
            }
        };
        registerReceiver(br, new IntentFilter("com.authorwjf.wakeywakey"));
        pi = PendingIntent.getBroadcast(this, 0, new Intent("com.authorwjf.wakeywakey"), 0);
        am = (AlarmManager) (this.getSystemService(Context.ALARM_SERVICE));
        am.set(AlarmManager.RTC_WAKEUP, exitTime, pi);
    }

    public static void setActivityDetails() {
        Intent activity = new Intent(ctx, MainActivity.class);
        activity.putExtra("start_time", startTime);
        activity.putExtra("user_name", user_name);
        ctx.startActivity(activity);
    }

}
