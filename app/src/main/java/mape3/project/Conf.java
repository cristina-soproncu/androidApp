package mape3.project;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.List;
import java.util.Locale;
@RequiresApi(api = Build.VERSION_CODES.M)
public class Conf extends DetaliiFragmente{

    List<TextToSpeech.EngineInfo> listInstalledEngines;
    TextToSpeech tts;
    EditText campText;
    Button btn;

    public static Conf newInstance(int index) {
        Conf f = new Conf();
        // preluam argumentele
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        View view = inflater.inflate(R.layout.fragment_conf, container, false);
        TextView text = (TextView)view.findViewById(R.id.titlu);
        text.setText(Resurse.TITLURI[daIndexSelectat()]);
        text = (TextView)view.findViewById(R.id.titlu);

        campText=(EditText)view.findViewById(R.id.campText);
        btn=(Button)view.findViewById(R.id.buton);
        initializareTTS();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textDeCitit = campText.getText().toString();
                tts.speak(textDeCitit, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
/*
        getTTS();
        for(int i = 0; i < listInstalledEngines.size(); i++){
            listInstalledEngines.get(i).label;
        }*/

        return view;
    }

    private void initializareTTS(){
        tts = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.ENGLISH);
                }
            }
        });
    }

/*
    public void getTTS () {
        tts = new TextToSpeech(y, android.speech.tts.TextToSpeech.OnInitListener listener);
        listInstalledEngines = tts.getEngines();
    }*/
}