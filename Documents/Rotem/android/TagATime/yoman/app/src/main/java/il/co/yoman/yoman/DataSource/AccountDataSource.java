package il.co.yoman.yoman.DataSource;

import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by rotems on 18/10/2017.
 */

public class AccountDataSource {

    public interface OnAccountArrivedListener {
        void onAccountsArrived(List<Account> businessAccount);
    }

    public static void getAccounts(final OnAccountArrivedListener listener, final String tkn) {
        final String AccountsUrl = "http://mdev1.yoman.co.il/api/Client/GetMySites";
        final ServerReq client = new ServerReq(AccountsUrl);
        new AsyncTask<Void, Void, List<Account>>() {

            @Override
            protected List<Account> doInBackground(Void... params) {
                try {
                    client.AddHeader("TATtkn", tkn);
                    client.Execute(ServerReq.RequestMethod.GET);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int responseCode = client.getResponseCode();
                if (responseCode == 200) {
                    String response = client.getResponse();
                    try {
                        List<Account> businessAccount = getData(response);
                        return businessAccount;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (responseCode == 401) {
                    return null;
                } else if (responseCode == 400) {
                    return null;

                    //// TODO: 25/10/2017  popup - and null to login
                }

//
                return null;
            }


            public List<Account> getData(String response) throws JSONException {
                ArrayList<Account> businessAccount = new ArrayList<>();
                JSONObject root = new JSONObject(response);
                JSONArray data = root.getJSONArray("data");

                for (int i = 0; i < data.length(); i++) {
                    JSONObject c = data.getJSONObject(i);

                    String nick = c.getString("nick");
                    String title = c.getString("title");
                    String description = c.getString("description");
                    String contactAddress = c.getString("contactAddress");
                    String contactNumber = c.getString("contactNumber");
                    String futureEvents = c.getString("futureEvents");
                    String siteURL = c.getString("siteURL");

                    Account account = new Account(nick,title,description,contactNumber,contactAddress,futureEvents,siteURL);
                    businessAccount.add(account);
                }
                return businessAccount;

            }

            @Override
            protected void onPostExecute(List<Account> businessAccount) {
                listener.onAccountsArrived(businessAccount);
            }
        }.execute();
    }

    public static class Account  implements Parcelable {
        private String nick;
        private String title;
        private String description;
        private String contactNumber;
        private String contactAddress;
        private String futureEvents;
        private String siteURL;


        public Account(String nick, String title, String description, String contactNumber, String contactAddress, String futureEvents, String siteURL) {
            this.nick = nick;
            this.title = title;
            this.description = description;
            this.contactNumber = contactNumber;
            this.contactAddress = contactAddress;
            this.futureEvents = futureEvents;
            this.siteURL = siteURL;
        }

        protected Account(Parcel in) {
            nick = in.readString();
            title = in.readString();
            description = in.readString();
            contactNumber = in.readString();
            contactAddress = in.readString();
            futureEvents = in.readString();
            siteURL = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(nick);
            dest.writeString(title);
            dest.writeString(description);
            dest.writeString(contactNumber);
            dest.writeString(contactAddress);
            dest.writeString(futureEvents);
            dest.writeString(siteURL);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Account> CREATOR = new Creator<Account>() {
            @Override
            public Account createFromParcel(Parcel in) {
                return new Account(in);
            }

            @Override
            public Account[] newArray(int size) {
                return new Account[size];
            }
        };

        public String getNick() {
            return nick;
        }

        public void setNick(String nick) {
            this.nick = nick;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getContactNumber() {
            return contactNumber;
        }

        public void setContactNumber(String contactNumber) {
            this.contactNumber = contactNumber;
        }

        public String getContactAddress() {
            return contactAddress;
        }

        public void setContactAddress(String contactAddress) {
            this.contactAddress = contactAddress;
        }

        public String getFutureEvents() {
            return futureEvents;
        }

        public void setFutureEvents(String futureEvents) {
            this.futureEvents = futureEvents;
        }

        public String getSiteURL() {
            return siteURL;
        }

        public void setSiteURL(String siteURL) {
            this.siteURL = siteURL;
        }

        @Override
        public String toString() {
            return "Account{" +
                    "nick='" + nick + '\'' +
                    ", title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    ", contactNumber='" + contactNumber + '\'' +
                    ", contactAddress='" + contactAddress + '\'' +
                    ", futureEvents='" + futureEvents + '\'' +
                    ", siteURL='" + siteURL + '\'' +
                    '}';
        }
    }
}