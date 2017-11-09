package il.co.yoman.yoman;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import il.co.yoman.yoman.Account.webViewFrag;
import il.co.yoman.yoman.DataSource.AccountDataSource;


/**
 * A simple {@link Fragment} subclass.
 */
public class accountsFrag extends Fragment implements AccountDataSource.OnAccountArrivedListener{
    private RecyclerView             accounts;
    private ProgressBar              progressBar;
    private String                   verifyToken, mobileNumber;
    private SharedPreferences        prefs ;
    private Button                   logout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        View v          =    inflater.inflate(R.layout.fragment_accounts, container, false);
        verifyToken     =    this.getArguments().getString("token");
        mobileNumber    =    this.getArguments().getString("mobileNumber");
        logout          =    v.findViewById(R.id.btnNewLogOut);
        accounts        =    v.findViewById(R.id.rvAccounts);
        progressBar     =    v.findViewById(R.id.progressBar);

        AccountDataSource.getAccounts(this, verifyToken);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });
        return v;
    }
    @Override
    public void onAccountsArrived(List<AccountDataSource.Account> businessAccount) {
        progressBar.setVisibility(View.GONE);
        if (businessAccount != null) {
            //1)rv.setLayoutManager
            accounts.setLayoutManager(new LinearLayoutManager(getActivity()));
            //2)rv.setAdapter
            accounts.setAdapter(new accountsAdapter(getActivity(), businessAccount));
        }
        else {
            logOut();
            Intent intent = new Intent(getActivity(),loginActivity.class);
            startActivity(intent);

        }
    }
    class accountsAdapter extends RecyclerView.Adapter<accountsAdapter.AccountsViewHolder> {
        //properties:
        List<AccountDataSource.Account> data;
        LayoutInflater inflater;
        Context context;

        //Constructor:
        public accountsAdapter(Context context, List<AccountDataSource.Account> data) {
            this.data = data;
            this.context = context;
            this.inflater = LayoutInflater.from(context); //Got the inflater from the Context.
        }

        @Override
        public AccountsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = inflater.inflate(R.layout.zaccount_item, parent, false);
            return new AccountsViewHolder(v);
        }

        @Override
        public void onBindViewHolder(AccountsViewHolder holder, int position) {
        AccountDataSource.Account account = data.get(position);
            holder.tvTitle.setText(account.getTitle());
            holder.tvDescription.setText(account.getDescription());
            holder.tvCount.setText(account.getFutureEvents() + " תורים עתידיים");

        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class AccountsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {//} implements View.OnClickListener {
            TextView tvTitle;
            TextView tvDescription;
            TextView tvCount;
            Button ivThumbnail;

            public AccountsViewHolder(View itemView) {
                super(itemView);

                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvDescription = itemView.findViewById(R.id.tvDescription);
                tvCount = itemView.findViewById(R.id.tvFuture);
                ivThumbnail = itemView.findViewById(R.id.ivThumbnail);

                ivThumbnail.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                String link = data.get(position).getSiteURL();
                String title = data.get(position).getTitle();
                String description = data.get(position).getDescription();
                String future = data.get(position).getFutureEvents();
                String nick = data.get(position).getNick();


                if (context instanceof FragmentActivity) {
                    FragmentActivity activity = (FragmentActivity) context;

                    Bundle bundle = new Bundle();
                    bundle.putString("title",title);
                    bundle.putString("description", description);
                    bundle.putString("future", future);
                    bundle.putString("mobileNumber",mobileNumber);
                    bundle.putString("nick",nick);
                    bundle.putString("token",verifyToken);
                    webViewFrag fragInfo = new webViewFrag();
                    fragInfo.setArguments(bundle);


                    activity.getSupportFragmentManager().
                            beginTransaction().
                            replace(R.id.accCotnainer, fragInfo.newInstance(link, title, description, future, nick)).
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
//                    logOut();
                    Intent a = new Intent(Intent.ACTION_MAIN);
                    a.addCategory(Intent.CATEGORY_HOME);
                    a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(a);
                    // handle back button's click listener move to verify TODO: delete ? logout?
//                    Intent intent = new Intent(getActivity(), verifyActivity.class);
//                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

    }
    private void logOut() {
        prefs = this.getActivity().getSharedPreferences("pref", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("verifyToken");
        editor.remove("mobileNumber");
        editor.clear();
        editor.commit();

        Intent intent = new Intent(this.getActivity(), loginActivity.class);
        startActivity(intent);
    }
}








