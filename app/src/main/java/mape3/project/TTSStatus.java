package mape3.project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by crist on 11.06.2017.
 */

public class TTSStatus extends DetaliiFragmente {
    public static TTSStatus newInstance(int index) {
        TTSStatus f = new TTSStatus();
        // preluam argumentele
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        View view = inflater.inflate(R.layout.fragment_valori, container, false);
        TextView text = (TextView)view.findViewById(R.id.titlu);
        text.setText(Resurse.TITLURI[daIndexSelectat()]);
        return view;

    }
}
