package il.co.yoman.yoman.DataSource;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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


             String st = c.getString("dt");
             char[] myCharArray = st.toCharArray();
             String startTime = "";
             for (int j = 11; j < 16; j++) {
                 startTime += myCharArray[j];
             }
             String time[] = startTime.split(":");
             int hour = Integer.parseInt(time[0]);
             int minute = Integer.parseInt(time[1]);

             String date = "";
             for (int z = 0; z < 10; z++) {
                 date += myCharArray[z];
             }
             String splitDate[] = date.split("-");
             int Year = Integer.parseInt(splitDate[0]);
             int month = Integer.parseInt(splitDate[1]) - 1;
             int day = Integer.parseInt(splitDate[2]);

             Calendar cal = Calendar.getInstance();
             cal.set(Calendar.YEAR, Year);
             cal.set(Calendar.DAY_OF_MONTH, day);
             cal.set(Calendar.MONTH, month);



             cal.set(Year,month,day,hour,minute);


             String FullDate = new SimpleDateFormat("EEEE, d MMMM , yyyy").format(cal.getTime());

             String resource = c.getString("resource");
             String services = c.getString("services");
             String status = c.getString("eStatus");
             String eColor = c.getString("eColor");
             String eStatusColor = c.getString("eStatusColor");
             String Url = c.getString("eURL");

             Log.d(cal.toString(), "calander");
             FutureEvent Event = new FutureEvent(startTime, resource, services, status, eColor, eStatusColor, Url, FullDate, Year, month, day, hour, minute, cal);
             EventsFuture.add(Event);
         }
                Collections.sort(EventsFuture);

//                Collections.sort(EventsFuture, new Comparator<FutureEvent>(){
//                    @Override
//                    public int compare(FutureEvent t1, FutureEvent t2) {
//                       if (t1.getYear()> t2.getYear())
//                           return 1;
//                       else if (t1.getYear() == t2.getYear()) {
//                           if (t1.getMonth() > t2.getMonth())
//                               return 1;
//                           else if (t1.getMonth() < t2.getMonth())
//                               return -1;
//                       }
//                       if (t1.getMonth() == t2.getMonth()&& (t1.getYear() == t2.getYear())) {
//                           if (t1.getDay() > t2.getDay())
//                               return 1;
//                           else if (t1.getDay() < t2.getDay())
//                               return -1;
//                       }
//                       if (t1.getDay() == t2.getDay() && t1.getMonth() == t2.getMonth()&& (t1.getYear() == t2.getYear())) {
//                           if (t1.getHour() * 60 + t1.getMinute() > t2.getHour() * 60 + t2.getMinute())
//                               return 1;
//                           else if (t1.getHour() * 60 + t1.getMinute() < t2.getHour() * 60 + t2.getMinute())
//                               return -1;
//                       }
//
//                        return 0;
//                    }
//                });

                return EventsFuture;

            }

            @Override
            protected void onPostExecute(List<FutureEvent> futureEvents) {
                listener.OnFutureArrivedListener(futureEvents);
            }

        }.execute();
    }

    public static class FutureEvent implements Comparable<FutureEvent>{// implements Comparable {
        private String startTime;
        private String resource;
        private String services;
        private String status, eColor;
        private String eStatusColor;
        private String date;
        private String Url;



        private Calendar cal;
        private int year, month, day, hour, minute;

        public FutureEvent(String startTime, String resource, String services, String status, String eColor, String eStatusColor, String url, String date, int year, int month, int day, int hour, int minute, Calendar cal) {
            this.startTime = startTime;
            this.resource = resource;
            this.services = services;
            this.status = status;
            this.eColor = eColor;
            this.eStatusColor = eStatusColor;
            this.date = date;
            Url = url;
            this.year = year;
            this.month = month;
            this.day = day;
            this.hour = hour;
            this.cal = cal;
            this.minute = minute;
        }
        public Calendar getCal() {
            return cal;
        }
        public void setCal(Calendar cal) {
            this.cal = cal;
        }
        public String getStartTime() {
            return startTime;
        }
        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }
        public String getResource() {
            return resource;
        }
        public void setResource(String resource) {
            this.resource = resource;
        }
        public String getServices() {
            return services;
        }
        public void setServices(String services) {
            this.services = services;
        }
        public String getStatus() {
            return status;
        }
        public void setStatus(String status) {
            this.status = status;
        }
        public String geteColor() {
            return eColor;
        }
        public void seteColor(String eColor) {
            this.eColor = eColor;
        }
        public String geteStatusColor() {
            return eStatusColor;
        }
        public void seteStatusColor(String eStatusColor) {
            this.eStatusColor = eStatusColor;
        }
        public String getDate() {
            return date;
        }
        public void setDate(String date) {
            this.date = date;
        }
        public String getUrl() {
            return Url;
        }
        public void setUrl(String url) {
            Url = url;
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
        @Override
        public String toString() {
            return "FutureEvent{" +
                    "startTime='" + startTime + '\'' +
                    ", resource='" + resource + '\'' +
                    ", services='" + services + '\'' +
                    ", status='" + status + '\'' +
                    ", eColor='" + eColor + '\'' +
                    ", eStatusColor='" + eStatusColor + '\'' +
                    ", date='" + date + '\'' +
                    ", Url='" + Url + '\'' +
                    ", year=" + year +
                    ", month=" + month +
                    ", day=" + day +
                    '}';
        }
        public int getHour() {
            return hour;
        }
        public void setHour(int hour) {
            this.hour = hour;
        }
        public int getMinute() {
            return minute;
        }
        public void setMinute(int minute) {
            this.minute = minute;
        }

        @Override
        public int compareTo(@NonNull FutureEvent futureEvent) {
            return getCal().compareTo(futureEvent.getCal());
        }


//        @Override
//        public int compareTo(@NonNull Object o) {
//            if (o instanceof FutureEvent) {
//                FutureEvent o1 = (FutureEvent) o;
//
//
//                if (this.getYear() > o1.getYear())
//                    return 1;
//                else if (this.getYear() == o1.getYear())
//                    if (this.getMonth() > o1.getMonth())
//                        return 1;
//                    else if (this.getMonth() == o1.getMonth())
//                        if (this.getDay() > o1.getDay())
//                            return 1;
//                        else if (this.getDay() == o1.getDay())
//                            if (this.getHour() * 60 + this.getMinute() > o1.getHour() * 60 + o1.getMinute())
//                                return 1;
//
//                            else return -1;
//                return 0;
//            }
//            return 0;
//        }
    }


}