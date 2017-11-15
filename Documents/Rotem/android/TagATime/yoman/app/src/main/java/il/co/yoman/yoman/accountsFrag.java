package il.co.yoman.yoman;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import il.co.yoman.yoman.Account.webViewFrag;
import il.co.yoman.yoman.DataSource.AccountDataSource;

import static il.co.yoman.yoman.welcomeScreen.getToken;
import static il.co.yoman.yoman.welcomeScreen.token;


/**
 * A simple {@link Fragment} subclass.
 */
public class accountsFrag extends Fragment implements AccountDataSource.OnAccountArrivedListener,
        NavigationView.OnNavigationItemSelectedListener{
    private RecyclerView             accounts;
    private ProgressBar              progressBar;
    private TextView                 logout, manager, conatct, noneEvents ;
    private SharedPreferences        prefs ;
    protected static DrawerLayout             drawer;
    private NavigationView           navigationView;
    private ActionBarDrawerToggle    toogle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        View v                             =    inflater.inflate(R.layout.fragment_accounts, container, false);
        Toolbar toolbar                    =    v.findViewById(R.id.toolbar);
        accounts                           =    v.findViewById(R.id.rvAccounts);
        progressBar                        =    v.findViewById(R.id.progressBar);
        noneEvents                         =    v.findViewById(R.id.noneEvents);

        drawer                             =    v.findViewById(R.id.drawer_layout);
        navigationView                     =    v.findViewById(R.id.nav_view);
        toogle = new ActionBarDrawerToggle(this.getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toogle);
        toogle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        AccountDataSource.getAccounts(this, getToken());
        return v;
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.logoutMenu) {
            logOut();
        }
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = getView().findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onAccountsArrived(List<AccountDataSource.Account> businessAccount) {
        progressBar.setVisibility(View.GONE);
        if (businessAccount.isEmpty())
            noneEvents.setVisibility(View.VISIBLE);
        else
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
    static class accountsAdapter extends RecyclerView.Adapter<accountsAdapter.AccountsViewHolder> {
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
            View v = inflater.inflate(R.layout.zaccountitem, parent, false);
            return new AccountsViewHolder(v);
        }

        @Override
        public void onBindViewHolder(AccountsViewHolder holder, int position) {
        AccountDataSource.Account account = data.get(position);
            holder.tvTitle.setText(account.getTitle());
            holder.tvCount.setText(account.getFutureEvents());

        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class AccountsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView tvTitle;
            TextView tvCount;

            public AccountsViewHolder(View itemView) {
                super(itemView);

                tvTitle = itemView.findViewById(R.id.tvTitle);
                tvCount = itemView.findViewById(R.id.tvFuture);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                String linkAccount = data.get(position).getSiteURL();
                String title = data.get(position).getTitle();
                String future = data.get(position).getFutureEvents();
                String nick = data.get(position).getNick();


                if (context instanceof FragmentActivity) {
                    FragmentActivity activity = (FragmentActivity) context;

                    Bundle bundle = new Bundle();
                    bundle.putString("title",title);
                    bundle.putString("future", future);
                    bundle.putString("nick",nick);
                    bundle.putString("token",token);
                    webViewFrag fragInfo = new webViewFrag();
                    fragInfo.setArguments(bundle);
//                    ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
//                    if (actionBar != null) {
//                        actionBar.hide();
                    activity.getSupportFragmentManager().
                            beginTransaction().
                            replace(R.id.accCotnainer, fragInfo.newInstance(linkAccount, title, future, nick)).
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
//TODO : GET BACK WHEN DRAWER OPEN - CLOSE IT
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK && drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);

                }
                else if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    Intent a = new Intent(Intent.ACTION_MAIN);
                    a.addCategory(Intent.CATEGORY_HOME);
                    a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(a);

                    return true;

                }
                return false;
            }
        });

    }
            private void logOut() {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setCancelable(false);
                dialog.setTitle("יציאה");
                dialog.setMessage("הנך מבצע התנתקות, האם הינך בטוח?");
                dialog.setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        prefs = getActivity().getSharedPreferences("pref", 0);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.remove("verifyToken");
                        editor.remove("mobileNumber");
                        editor.clear();
                        editor.commit();

                        Intent intent = new Intent(getActivity(), loginActivity.class);
                        startActivity(intent);
                    }
                }).setNegativeButton("בטל", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                        .create();
                final AlertDialog alert = dialog.create();
                alert.show();

            }

        }








