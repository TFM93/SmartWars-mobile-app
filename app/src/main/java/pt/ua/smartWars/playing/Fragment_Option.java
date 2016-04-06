package pt.ua.smartWars.playing;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import pt.ua.smartWars.OnGameData.FirePlayers;
import pt.ua.smartWars.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {link Fragment_Option.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment_Option#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Option extends Fragment{

    public static Fragment_Option newInstance() {
        Fragment_Option fragment = new Fragment_Option();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_option, container, false);

        EditText code_m = (EditText) v.findViewById(R.id.code_match);
        code_m.setText(FirePlayers.getInstance().getMatch_id());

        return v;
    }

    public Fragment_Option()
    {}

}
