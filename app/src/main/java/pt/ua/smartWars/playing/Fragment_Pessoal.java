package pt.ua.smartWars.playing;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import pt.ua.smartWars.R;

public class Fragment_Pessoal extends Fragment {


    public static Fragment_Pessoal newInstance() {
        Fragment_Pessoal fragment = new Fragment_Pessoal();
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
        return inflater.inflate(R.layout.fragment_pessoal, container, false);
    }

    public Fragment_Pessoal()
    {}

    public interface DataListener {
        public void updateBatteryText(int value);
        public void updatePulseText(int value);
        //public byte[] getAllBytes();
    }


    @Override
    public void onStart()
    {
        super.onStart();


    }

    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            ((Gaming) context).setDataListener(new DataListener() {

                @Override
                public void updateBatteryText(int value) {
                    TextView battery = (TextView) getView().findViewById(R.id.battery);
                    battery.setText(value + " %");
                }

                @Override
                public void updatePulseText(int value) {
                    TextView pulse = (TextView) getView().findViewById(R.id.pulse);
                    pulse.setText(value + " BPM");
                    if (value>190)
                        pulse.setTextColor(getResources().getColor(R.color.dark_red));
                    else
                        pulse.setTextColor(getResources().getColor(R.color.dark_green));

                }



            });
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        public void onFragmentInteraction(Uri uri);
//    }

}
