package il.co.yoman.yoman.DataSource;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by rotems on 01/11/2017.
 */

public class FutureDataSource  {



    public interface OnFutureArrivedListener {
        void OnFutureArrivedListener(List<FutureEvent> EventsFuture);
    }

    public static void getEvents(final OnFutureArrivedListener listener, final String nick, final String mobileNumber, final String token) {
        final String AccountsUrl = "http://mdev1.yoman.co.il/api/Client/GetEvents";
        final ServerReq client = new ServerReq(AccountsUrl);
        new AsyncTask<Void, Void, List<FutureEvent>>() {
            @Override
            protected List<FutureEvent> doInBackground(Void... params) {
                try {
                    client.AddParam("siteNick", nick);
                    client.AddParam("phoneNum", mobileNumber);
                    client.AddHeader("TATtkn", token);
                    client.Execute(ServerReq.RequestMethod.GET);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int responseCode = client.getResponseCode();
                if (responseCode == 200) {
                    String response = client.getResponse();
                    try {
                        List<FutureEvent> EventsFuture = getData(response);
                        return EventsFuture;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (responseCode == 401) {
                    return null;
                } else if (responseCode == 400) {
                    return null;

                    //// TODO: 25/10/2017  popup - and null to login
                }


                return null;
            }


            public List<FutureEvent> getData(String response) throws JSONException {
                final ArrayList<FutureEvent> EventsFuture = new ArrayList<>();
                JSONObject root = new JSONObject(response);
                JSONArray data = root.getJSONArray("data");

         for (int i = 0; i < data.length(); i++) {
                    JSONObject c = data.getJSONObject(i);


                    String st  = c.getString("dt");
                    char[] myCharArray = st.toCharArray();
                    String startTime="";
                    for (int j = 11 ; j<16 ; j++){
                    startTime += myCharArray[j];
                    }
                    String date = "";
                    for (int z = 0 ; z<10 ; z ++) {
                        date += myCharArray[z];
                    }
                    String splitDate[] = date.split("-");
                    int Year = Integer.parseInt(splitDate[0]);
                    int month = Integer.parseInt(splitDate[1]);
                    int day = Integer.parseInt(splitDate[2]);

                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.YEAR, Year);
                    cal.set(Calendar.DAY_OF_MONTH, day);
                    cal.set(Calendar.MONTH, month);


                    String FullDate = new SimpleDateFormat("EEEE, d MMMM , yyyy").format(cal.getTime());

                    String endTime = "00:00";//c.getString("title");
                    String nameOfCreate = c.getString("resource");
                    String title = c.getString("services");
                    String picTitle = "pi";//c.getString(" futureEvents ");
                    String status = "status";//c.getString("siteURL");
                    String picStatus = "pi";//c.getString("siteURL");

                    FutureEvent Event = new FutureEvent(startTime,endTime,nameOfCreate,
                            title,picTitle,status,picStatus, FullDate, Year, month,day);
                    EventsFuture.add(Event);
                }
                Collections.sort(EventsFuture, new Comparator<FutureEvent>(){
                    @Override
                    public int compare(FutureEvent t1, FutureEvent t2) {
                       if (t1.getYear() > t2.getYear())
                           return 1;
                       else if (t1.getYear() == t2.getYear())
                           if (t1.getMonth() > t2.getMonth())
                               return 1;
                       else if (t1.getMonth() == t2.getMonth())
                           if (t1.getDay() > t2.getDay())
                               return 1;
                       else return -1;
                        return 0;
                    }
                });

                return EventsFuture;

            }

            @Override
            protected void onPostExecute(List<FutureEvent> futureEvents) {
                listener.OnFutureArrivedListener(futureEvents);
            }

        }.execute();
    }

    public static class FutureEvent {
        private String startTime;
        private String endTime;
        private String nameOfCreate;
        private String title;
        private String picTitle;
        private String status;
        private String picStatus;
        private String date;
        private int year, month, day;


        @Override
        public String toString() {
            return "FutureEvent{" +
                    "startTime='" + startTime + '\'' +
                    ", endTime='" + endTime + '\'' +
                    ", nameOfCreate='" + nameOfCreate + '\'' +
                    ", title='" + title + '\'' +
                    ", picTitle='" + picTitle + '\'' +
                    ", status='" + status + '\'' +
                    ", picStatus='" + picStatus + '\'' +
                    ", date='" + date + '\'' +
                    '}';
        }

        public FutureEvent(String startTime, String endTime, String nameOfCreate,
                           String title, String picTitle, String status, String picStatus, String date, int year, int month, int day)  {
            this.startTime = startTime;
            this.endTime = endTime;
            this.nameOfCreate = nameOfCreate;
            this.title = title;
            this.picTitle = picTitle;
            this.status = status;
            this.date = date;
            this.picStatus = picStatus;
            this.year = year;
            this.month = month;
            this.day = day;
        }


        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getNameOfCreate() {
            return nameOfCreate;
        }

        public void setNameOfCreate(String nameOfCreate) {
            this.nameOfCreate = nameOfCreate;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPicTitle() {
            return picTitle;
        }

        public void setPicTitle(String picTitle) {
            this.picTitle = picTitle;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getPicStatus() {
            return picStatus;
        }

        public void setPicStatus(String picStatus) {
            this.picStatus = picStatus;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }


        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }
    }


}