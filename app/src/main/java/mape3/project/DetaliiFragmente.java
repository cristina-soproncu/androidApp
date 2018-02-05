package mape3.project;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

public class DetaliiFragmente extends Fragment{
    TextView text ;
    String color;
    View view;

    /*Cream o noua instanta pentru FragmentDetalii care va afisa textul corespunzator lui index*/
    public static DetaliiFragmente newInstance(int index) {
        DetaliiFragmente f = new DetaliiFragmente();
        // preluam argumentele
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        return f;
    }

    public int daIndexSelectat() {
        return getArguments().getInt("index", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        //cream elementele pentru vizualizare
         ScrollView scroller = new ScrollView(getActivity());
        text = new TextView(getActivity());
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,4, getActivity().getResources().getDisplayMetrics());

        text.setPadding(padding, padding, padding, padding);
        scroller.addView(text);
        text.setText(Resurse.TITLURI[daIndexSelectat()]);
        return scroller;
    }

    public void setColor(String color) {
        this.color = color;
        view.setBackgroundColor(Color.parseColor(color));
    }

    public void setView(View view){
        this.view = view;
    }
}