package il.co.yoman.yoman.Account;


import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import il.co.yoman.yoman.R;
import il.co.yoman.yoman.accountsFrag;


/**
 * A simple {@link Fragment} subclass.
 */
public class description extends Fragment implements OnMapReadyCallback  {
    private TextView     title, count, meetingsTag, futureEvents, descriptionFrag, bottom_line;
    private String       link, strTitle, strFuture, description, mobileNumber, nick, token;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }

        link = getArguments().getString("link");
        strTitle = this.getArguments().getString("title");
        strFuture = this.getArguments().getString("future");
        mobileNumber = this.getArguments().getString("mobileNumber");
        token = this.getArguments().getString("token");
        nick = this.getArguments().getString("nick");


        View v           =        inflater.inflate(R.layout.fragment_description, container, false);
        title            =        v.findViewById(R.id.titleAccount);
        count            =        v.findViewById(R.id.countAccount);
        futureEvents     =        v.findViewById(R.id.futureEvents);
        descriptionFrag  =        v.findViewById(R.id.businessDescription);
        bottom_line      =        v.findViewById(R.id.bottom_line);
        meetingsTag      =        v.findViewById(R.id.webViewTag);

        title.setText(strTitle);
        bottom_line.setBackground(getResources().getDrawable(R.drawable.shadow));
        descriptionFrag.setTextColor(Color.WHITE);
        futureEvents.setText("הפגישות שלי " + "(" + strFuture +")");

        count.setText("מס׳ אירועים: " + strFuture);
        futureEvents.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                moveToFutureEvents();
            }
        });
        meetingsTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToMeetings();
            }
        });


        SupportMapFragment mapFragment = new SupportMapFragment();


            getFragmentManager().
                    beginTransaction().
                    replace(R.id.mapForFrag, mapFragment).
                    commit();

            mapFragment.getMapAsync(this);

        return v;
    }


    public void moveToFutureEvents(){
        Bundle bundle = new Bundle();
        bundle.putString("title", strTitle);
        bundle.putString("link", link);
        bundle.putString("description", description);
        bundle.putString("future", strFuture);
        bundle.putString("nick", nick);
        bundle.putString("token", token);
        bundle.putString("mobileNumber", mobileNumber);
        futureEvents fragInfo = new futureEvents();
        fragInfo.setArguments(bundle);

        getFragmentManager().
                beginTransaction().
                replace(R.id.descriptionContainer, fragInfo).
                commit();
    }
    public void moveToMeetings(){
        Bundle bundle = new Bundle();
        bundle.putString("title",strTitle);
        bundle.putString("link",link);
        bundle.putString("description", description);
        bundle.putString("nick", nick);
        bundle.putString("token", token);
        bundle.putString("future", strFuture);
        bundle.putString("mobileNumber", mobileNumber);

        webViewFrag fragInfo = new webViewFrag();
        fragInfo.setArguments(bundle);

        getFragmentManager().
                beginTransaction().
                replace(R.id.descriptionContainer, fragInfo.newInstance(link, strTitle, description, strFuture, nick)).
                commit();


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
            LatLng address = getLocationFromAddress(getContext(), "hamelaha 15 natanya");
            googleMap.addMarker(new MarkerOptions().position(address).title("Tag a Time"));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(address, 15));

}

        //TODO : get the lat lonng in the datasource of description and push it to the map with description.get()
    public LatLng getLocationFromAddress(Context context, String strAddress)
    {
        Geocoder coder= new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try
        {
            address = coder.getFromLocationName(strAddress, 5);
            if(address==null)
            {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return p1;

    }
    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    // handle back button's click listener
                    getFragmentManager().
                            beginTransaction().
                            replace(R.id.descriptionContainer, new accountsFrag()).
                            commit();

                    return true;
                }
                return false;
            }
        });
    }




}
