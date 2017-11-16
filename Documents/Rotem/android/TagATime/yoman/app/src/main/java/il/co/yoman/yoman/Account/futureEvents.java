package il.co.yoman.yoman.Account;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import il.co.yoman.yoman.DataSource.AccountDataSource;
import il.co.yoman.yoman.DataSource.FutureDataSource;
import il.co.yoman.yoman.R;
import il.co.yoman.yoman.accountsFrag;

import static il.co.yoman.yoman.welcomeScreen.getMobileNumber;
import static il.co.yoman.yoman.welcomeScreen.getToken;


/**
 * A simple {@link Fragment} subclass.
 */
public class futureEvents extends Fragment  implements FutureDataSource.OnFutureArrivedListener {

    private TextView         topRightTitleAccount, webViewFragTitle, bottom_Line, futureEventsFragTitle, descriptionFragTitle, noneEvents;
    private ProgressBar      progressBar;
    private RecyclerView     rvEvents;
    private AccountDataSource.Account CurrentAccount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        CurrentAccount              =        this.getArguments().getParcelable("Account");

        View v           =        inflater.inflate(R.layout.fragment_future_event, container, false);
        //upper layer
        topRightTitleAccount        =        v.findViewById(R.id.topRightTitleAccount);
        webViewFragTitle            =        v.findViewById(R.id.webViewFragTitle);
        futureEventsFragTitle       =        v.findViewById(R.id.futureEventsFragTitle);
        descriptionFragTitle        =        v.findViewById(R.id.descriptionFragTitle);
        bottom_Line                 =        v.findViewById(R.id.bottom_line);

        topRightTitleAccount.setText(CurrentAccount.getTitle());
        webViewFragTitle.setTextColor(Color.WHITE);
        bottom_Line.setBackground( getResources().getDrawable(R.drawable.shadow) );
        futureEventsFragTitle.setText("פגישות עתידיות " + "(" + CurrentAccount.getFutureEvents() +")");

        progressBar      =        v.findViewById(R.id.progressBar);
        rvEvents         =        v.findViewById(R.id.rvEvents);
        noneEvents       =        v.findViewById(R.id.noneEvents);

        FutureDataSource.getEvents(this, CurrentAccount.getNick(), getMobileNumber() , getToken());
        webViewFragTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToMeetings();
            }
        });
        descriptionFragTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToDescription();
            }
        });

        return v;
    }
    public void moveToMeetings() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("Account", CurrentAccount);
        webViewFrag fragInfo = new webViewFrag();
        fragInfo.setArguments(bundle);

        getFragmentManager().
                beginTransaction().
                replace(R.id.futureContainer, fragInfo.newInstance(CurrentAccount )).
                commit();
    }
    public void moveToDescription() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("Account", CurrentAccount);
        il.co.yoman.yoman.Account.description fragInfo = new description();
        fragInfo.setArguments(bundle);
        getFragmentManager().
                beginTransaction().
                replace(R.id.futureContainer, fragInfo).
                commit();
    }
    @Override
    public void OnFutureArrivedListener(List<FutureDataSource.FutureEvent> EventsFuture) {
        progressBar.setVisibility(View.GONE);
        if (EventsFuture.isEmpty())
            noneEvents.setVisibility(View.VISIBLE);
        else if (EventsFuture != null) {
            //1)rv.setLayoutManager
            rvEvents.setLayoutManager(new LinearLayoutManager(getActivity()));
            //2)rv.setAdapter
            rvEvents.setAdapter(new futureEventsAdapter(getActivity(), EventsFuture));
        }

    }
    class futureEventsAdapter extends RecyclerView.Adapter<futureEventsAdapter.FutureViewHolder> {
        //properties:
        List<FutureDataSource.FutureEvent> data;
        LayoutInflater inflater;
        Context context;

        //Constructor:
        public futureEventsAdapter(Context context, List<FutureDataSource.FutureEvent> data)  {
            this.data = data;
            this.context = context;
            this.inflater = LayoutInflater.from(context); //Got the inflater from the Context.
        }
        @Override
        public int getItemViewType(int position) {
            String date = this.data.get(position).getDate();
            if (position >0) {
                String lastDate = this.data.get(position-1).getDate();
                if (date.equals(lastDate))
                    return 0;
            }
                return 2;
        }
        @Override
        public FutureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case 0:
                    View v = inflater.inflate(R.layout.zfuture_appoitments_item, parent, false);
                    return new FutureViewHolder(v);

                case 2:
                    View v1 = inflater.inflate(R.layout.zfuture_item_with_date, parent, false);
                    return new FutureViewHolder(v1);

            }
            return null;

        }
        @Override
        public void onBindViewHolder(FutureViewHolder holder, int position) {
            FutureDataSource.FutureEvent event = data.get(position);
            holder.tvTitle.setText(event.getServices());
            holder.tvStartTime.setText(event.getStartTime());
            holder.tvStartTime.setTextColor(Color.parseColor(event.geteColor()));
            holder.line.setBackgroundColor(Color.parseColor(event.geteColor()));
            holder.Date.setText(event.getDate() );
            holder.tvStatus.setText(event.getStatus());
            holder.create.setText(event.getResource());
            //change circle background
            GradientDrawable sd = (GradientDrawable) holder.picStatus.getBackground().mutate();
            sd.setColor(Color.parseColor(event.geteStatusColor()));
            sd.invalidateSelf();
        }
        @Override
        public int getItemCount() {
            return data.size();
        }
        class FutureViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {//} implements View.OnClickListener {
            TextView tvStartTime, tvTitle, tvStatus, picStatus, Date, line, create;
            public FutureViewHolder(View itemView) {
                super(itemView);

                tvTitle = itemView.findViewById(R.id.title);
                tvStartTime = itemView.findViewById(R.id.startTime);
                tvStatus = itemView.findViewById(R.id.status);
                picStatus = itemView.findViewById(R.id.picStatus);
                Date = itemView.findViewById(R.id.dateholder);
                line = itemView.findViewById(R.id.verticalLine);
                create = itemView.findViewById(R.id.create);
                itemView.setOnClickListener(this);
            }
            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                String link = data.get(position).getUrl();
                if (context instanceof FragmentActivity) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("Account", CurrentAccount);
                    bundle.putString("link", link);
                    fullWebView fragInfo = new fullWebView();
                    fragInfo.setArguments(bundle);


                    getFragmentManager().
                            beginTransaction().
                            replace(R.id.futureContainer, fragInfo.newInstance(link, CurrentAccount)).
                            commit();
                }
            }

        }
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
                            replace(R.id.futureContainer, new accountsFrag()).
                            commit();

                    return true;
                }
                return false;
            }
        });
    }
}

