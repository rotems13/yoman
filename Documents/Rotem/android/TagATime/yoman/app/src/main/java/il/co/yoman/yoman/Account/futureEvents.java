package il.co.yoman.yoman.Account;


import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import il.co.yoman.yoman.DataSource.FutureDataSource;
import il.co.yoman.yoman.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class futureEvents extends Fragment  implements FutureDataSource.OnFutureArrivedListener {

    private TextView         title, count, meetingsTag, futureEvents, bottom_line, descriptionFrag;
    private String           link, strTitle, strFuture, description, token,mobileNumber, nick;
    private ProgressBar      progressBar;
    private RecyclerView     rvEvents;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }

        link             =        getArguments().getString("link");
        strTitle         =        this.getArguments().getString("title");
        strFuture        =        this.getArguments().getString("future");
        mobileNumber     =        getArguments().getString("mobileNumber");
        token            =        this.getArguments().getString("token");
        nick             =        this.getArguments().getString("nick");

        View v           =        inflater.inflate(R.layout.fragment_future_event, container, false);
        progressBar      =        v.findViewById(R.id.progressBar);
        title            =        v.findViewById(R.id.titleAccount);
        count            =        v.findViewById(R.id.countAccount);
        futureEvents     =        v.findViewById(R.id.futureEvents);
        bottom_line      =        v.findViewById(R.id.bottom_line);
        descriptionFrag  =        v.findViewById(R.id.businessDescription);
        meetingsTag      =        v.findViewById(R.id.webViewTag);
        rvEvents         =        v.findViewById(R.id.rvEvents);

        bottom_line.setBackground(getResources().getDrawable(R.drawable.shadow));
        futureEvents.setTextColor(Color.WHITE);
        title.setText(strTitle);
        count.setText("מס׳ אירועים: " + strFuture);

        FutureDataSource.getEvents(this, nick, mobileNumber , token);

        meetingsTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToMeetings();
            }
        });
        descriptionFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveToDescription();
            }
        });

        return v;
    }

    public void moveToMeetings() {
        Bundle bundle = new Bundle();
        bundle.putString("title", strTitle);
        bundle.putString("link", link);
        bundle.putString("token", token);
        bundle.putString("future", strFuture);
        bundle.putString("nick", nick);
        bundle.putString("mobileNumber", mobileNumber);
        webViewFrag fragInfo = new webViewFrag();
        fragInfo.setArguments(bundle);

        getFragmentManager().
                beginTransaction().
                replace(R.id.futureContainer, fragInfo.newInstance(link, strTitle, description, strFuture, nick)).
                commit();


    }
    public void moveToDescription() {
        Bundle bundle = new Bundle();
        bundle.putString("title", strTitle);
        bundle.putString("link", link);
        bundle.putString("description", description);
        bundle.putString("future", strFuture);
        bundle.putString("token", token);
        bundle.putString("nick", nick);
        bundle.putString("mobileNumber", mobileNumber);

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
        if (EventsFuture != null) {
            //1)rv.setLayoutManager
            rvEvents.setLayoutManager(new LinearLayoutManager(getActivity()));
            //2)rv.setAdapter
            rvEvents.setAdapter(new futureEventsAdapter(getActivity(), EventsFuture));
        }
//        else if (EventsFuture == 0) {
//         makeToast("לא קיימים אירועים קרובים", 10 , getActivity());
//
//        }
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
            holder.tvTitle.setText(event.getTitle());
            holder.tvStartTime.setText(event.getStartTime());
//            holder.tvEndTime.setText(event.getEndTime());
            holder.tvCreator.setText(event.getNameOfCreate() );
            holder.Date.setText(event.getDate() );
         //   holder.picStatus.setText(event.getStartTime() + " תורים עתידיים");
           // holder.picTitle.setImageIcon(event.getStartTime() + " תורים עתידיים");

        }


        @Override
        public int getItemCount() {
            return data.size();
        }

        class FutureViewHolder extends RecyclerView.ViewHolder {//implements View.OnClickListener {//} implements View.OnClickListener {

         //   Button ivThumbnail;

            TextView tvStartTime, tvEndTime, tvCreator, tvTitle, tvStatus, picStatus, Date;
            ImageView picTitle;

            public FutureViewHolder(View itemView) {
                super(itemView);

                tvTitle = itemView.findViewById(R.id.title);
                tvStartTime = itemView.findViewById(R.id.startTime);
//                tvEndTime = itemView.findViewById(R.id.FinishTime);
                tvCreator = itemView.findViewById(R.id.create);
                tvStatus = itemView.findViewById(R.id.status);
                picStatus = itemView.findViewById(R.id.picStatus);
                picTitle = itemView.findViewById(R.id.picTitle);
                Date = itemView.findViewById(R.id.dateholder);



                //  ivThumbnail = itemView.findViewById(R.id.ivThumbnail);

//                ivThumbnail.setOnClickListener(this);
            }
//
//            @Override
//            public void onClick(View view) {
//                int position = getAdapterPosition();
//                String link = data.get(position).getSiteURL();
//                String title = data.get(position).getTitle();
//                String description = data.get(position).getDescription();
//                String future = data.get(position).getFutureEvents();
//
//
//                if (context instanceof FragmentActivity) {
//                    FragmentActivity activity = (FragmentActivity) context;
//
//                    Bundle bundle = new Bundle();
//                    bundle.putString("title", title);
//                    bundle.putString("description", description);
//                    bundle.putString("future", future);
//                    webViewFrag fragInfo = new webViewFrag();
//                    fragInfo.setArguments(bundle);
//
//
//                    activity.getSupportFragmentManager().
//                            beginTransaction().
//                            replace(R.id.accCotnainer, fragInfo.newInstance(link, title, description, future)).
//                            commit();
//                }
//            }
        }

    }
}

