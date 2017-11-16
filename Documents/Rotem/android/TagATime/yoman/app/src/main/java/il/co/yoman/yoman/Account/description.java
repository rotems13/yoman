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

import il.co.yoman.yoman.DataSource.AccountDataSource;
import il.co.yoman.yoman.R;
import il.co.yoman.yoman.accountsFrag;


/**
 * A simple {@link Fragment} subclass.
 */
public class description extends Fragment implements OnMapReadyCallback  {
    private TextView     topRightTitleAccount, webViewFragTitle, futureEventsFragTitle, descriptionFragTitle, bottom_line,
            phoneDescription, descriptionOfBusiness, nameOfBusiness,emailOfBusiness, adressOfBusiness;
    private String       strAdress;
    private AccountDataSource.Account CurrentAccount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        CurrentAccount                      =        this.getArguments().getParcelable("Account");
        View v                              =        inflater.inflate(R.layout.fragment_description, container, false);
        //upper layer
        topRightTitleAccount                =        v.findViewById(R.id.topRightTitleAccount);
        webViewFragTitle                    =        v.findViewById(R.id.webViewFragTitle);
        futureEventsFragTitle               =        v.findViewById(R.id.futureEventsFragTitle);
        descriptionFragTitle                =        v.findViewById(R.id.descriptionFragTitle);
        bottom_line                         =        v.findViewById(R.id.bottom_line);

        topRightTitleAccount.setText(CurrentAccount.getTitle());
        bottom_line.setBackground(getResources().getDrawable(R.drawable.shadow));
        descriptionFragTitle.setTextColor(Color.WHITE);
        futureEventsFragTitle.setText("פגישות עתידיות " + "(" + CurrentAccount.getFutureEvents() +")");
        //whitebox
        nameOfBusiness                      =        v.findViewById(R.id.nameOfBusiness);
        descriptionOfBusiness               =        v.findViewById(R.id.descriptionOfBusiness);
        phoneDescription                    =        v.findViewById(R.id.phoneDescription);
        emailOfBusiness                     =        v.findViewById(R.id.emailOfBusiness);
        adressOfBusiness                    =        v.findViewById(R.id.adressOfBusiness);

        nameOfBusiness.setText(CurrentAccount.getTitle());
        descriptionOfBusiness.setText(CurrentAccount.getDescription());
        phoneDescription.setText(CurrentAccount.getContactNumber());
        emailOfBusiness.setText(CurrentAccount.getSiteURL());
        //adressOfBusiness.setText(AcountTrans.getAdress());


        futureEventsFragTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToFutureEvents();
            }
        });
        webViewFragTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToMeetings();
            }
        });

        if (strAdress != null) {
            SupportMapFragment mapFragment = new SupportMapFragment();
            getFragmentManager().
                    beginTransaction().
                    replace(R.id.mapForFrag, mapFragment).
                    commit();
            mapFragment.getMapAsync(this);
        }
        return v;
    }

    public void moveToFutureEvents(){
        Bundle bundle = new Bundle();
//        bundle.putString("title", strTitle);
//        bundle.putString("link", link);
//        bundle.putString("future", strFuture);
//        bundle.putString("nick", nick);
        bundle.putParcelable("Account", CurrentAccount);
//        bundle.putString("token", token);
//        bundle.putString("mobileNumber", mobileNumber);
        futureEvents fragInfo = new futureEvents();
        fragInfo.setArguments(bundle);

        getFragmentManager().
                beginTransaction().
                replace(R.id.descriptionContainer, fragInfo).
                commit();
    }
    public void moveToMeetings(){
        Bundle bundle = new Bundle();
//        bundle.putString("title",strTitle);
//        bundle.putString("link",link);
//        bundle.putString("nick", nick);
//        bundle.putString("token", token);
        bundle.putParcelable("Account", CurrentAccount);
//        bundle.putString("future", strFuture);
//        bundle.putString("mobileNumber", mobileNumber);

        webViewFrag fragInfo = new webViewFrag();
        fragInfo.setArguments(bundle);

        getFragmentManager().
                beginTransaction().
                replace(R.id.descriptionContainer, fragInfo.newInstance(CurrentAccount)).
                commit();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
            LatLng address = getLocationFromAddress(getContext(), "hamelaha 15 natanya");
            googleMap.addMarker(new MarkerOptions().position(address).title("Tag a Time"));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(address, 15));

}
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
