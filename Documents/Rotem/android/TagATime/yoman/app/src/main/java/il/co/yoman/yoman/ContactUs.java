package il.co.yoman.yoman;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import il.co.yoman.yoman.DataSource.ServerReq;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static il.co.yoman.yoman.services.NetworkStateReceiver.isOnlineReciver;
import static il.co.yoman.yoman.services.NetworkStateReceiver.makeToast;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactUs extends Fragment {
    private final String contactUrl = "https://mdev1.yoman.co.il/api/Client/ContactUs";
    private final String requestTypeUrl = "https://mdev1.yoman.co.il/api/Client/GetContactUsRequestTypes";
    private ServerReq clientRequestType = new ServerReq(requestTypeUrl);
    private ServerReq clientSend = new ServerReq(contactUrl);

    private String content, name, phone, msg, requestType;
    private EditText ContactName, ContactPhone, ContactContent;
    private Spinner ContentRequestSpinner;
    private ProgressBar progressbar;
    private Button btnSEND;
    public static boolean isCountactusShown = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        isCountactusShown = true;
        if (container != null) {
            container.removeAllViews();
        }

//        InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
//        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);


        if (isOnlineReciver) {
            getRequestType p = new getRequestType();
            p.start();
        }
        View v = inflater.inflate(R.layout.form_header, container, false);

        ContactName = v.findViewById(R.id.ContactName);
        ContactPhone = v.findViewById(R.id.ContactPhone);
        ContactContent = v.findViewById(R.id.ContactContent);
        ContentRequestSpinner = v.findViewById(R.id.ContentRequestSpinner);
        btnSEND = v.findViewById(R.id.btnSEND);
        progressbar = v.findViewById(R.id.progressBar);

        progressbar.setVisibility(View.INVISIBLE);
        ContactContent.setScroller(new Scroller(getContext()));
        ContactContent.setMaxLines(3);
        ContactContent.setVerticalScrollBarEnabled(true);
        ContactContent.setMovementMethod(new ScrollingMovementMethod());


        btnSEND.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendContactUs y = new sendContactUs();
                if (isOnlineReciver && validate()) {
                    progressbar.setVisibility(View.VISIBLE);
                    y.start();
                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                }

            }
        });
        return v;
    }

    public void addItemsOnSchoolSpiner(String[] list) {

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_textview, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_textview);

        ContentRequestSpinner.setAdapter(dataAdapter);
    }
    class getRequestType extends Thread {
        @Override
        public void run() {
            try {
                clientRequestType.Execute(ServerReq.RequestMethod.GET);
            } catch (Exception e) {
                makeToast("התרחשה שגיאה, נסה שנית", 5, getContext());
                e.printStackTrace();
                return;
            }
            int responseCode = clientRequestType.getResponseCode();
            try {
                msg = parseServerMsg(clientRequestType.getResponse());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (responseCode == 200) {
                String response = clientRequestType.getResponse();
                try {
                    final String[] list = parseResponse(response);
                    getActivity().runOnUiThread(new Thread(new Runnable() {
                        @Override
                        public void run() {
                            addItemsOnSchoolSpiner(list);
                        }
                    }));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return;
            } else {
                getActivity().runOnUiThread(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        progressbar.setVisibility(View.INVISIBLE);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                                dialog.setCancelable(false);
                                dialog.setTitle("שגיאה");
                                dialog.setMessage(msg);
                                dialog.setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                })
                                        .create();
                                final AlertDialog alert = dialog.create();
                                alert.show();


                            }
                        });

                    }
                }));
            }
        }
    }
        class sendContactUs extends Thread {
            @Override
            public void run() {
                try {
                    clientSend.AddParam("content", content);
                    clientSend.AddParam("requestType", requestType);
                    clientSend.AddParam("name", name);
                    clientSend.AddParam("phone", phone);
                    clientSend.Execute(ServerReq.RequestMethod.POST);
                } catch (Exception e) {
                    makeToast("התרחשה שגיאה, נסה שנית", 5, getContext());
                    e.printStackTrace();
                    return;
                }
                int responseCode = clientSend.getResponseCode();
                try {
                    msg = parseServerMsg(clientSend.getResponse());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (responseCode == 200) {
                    progressbar.setVisibility(View.INVISIBLE);
                    isCountactusShown = false;

                    return;
                } else {
                    getActivity().runOnUiThread(new Thread(new Runnable() {
                        @Override
                        public void run() {
                            progressbar.setVisibility(View.INVISIBLE);

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                                    dialog.setCancelable(false);
                                    dialog.setTitle("שגיאה");
                                    dialog.setMessage(msg);
                                    dialog.setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            btnSEND.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    sendContactUs y = new sendContactUs();
                                                }
                                            });
                                        }
                                    })
                                            .create();
                                    final AlertDialog alert = dialog.create();
                                    alert.show();

                                    ContactName.setText("");
                                    ContactPhone.setText("");
                                    ContactContent.setText("");
                                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                                }
                            });

                        }
                    }));
                }
            }
        }
        private String parseServerMsg(String json) throws JSONException {
            JSONObject root = new JSONObject(json);
            String msg = root.getString("Message");
            return msg;
        }
        private String[] parseResponse(String json) throws JSONException {
            JSONObject root = new JSONObject(json);
            JSONArray data = root.getJSONArray("data");
            String[] stringArray = new String[data.length()];

            for (int i = 0; i < data.length(); i++) {
                stringArray[i] = (String) data.get(i);
            }
            return stringArray;
        }
        public boolean validate() {
            boolean valid = true;

            name = ContactName.getText().toString();
            phone = ContactPhone.getText().toString();
            content = ContactContent.getText().toString();
            requestType = String.valueOf(ContentRequestSpinner.getSelectedItem());

            if (name.isEmpty() || name.length() < 3) {
                ContactName.setError("יש לרשום שם מלא");
                valid = false;
            } else {
                ContactName.setError(null);
            }
            if (phone.isEmpty() || phone.length() != 10) {
                ContactPhone.setError("מספר הטלפון אינו תקין");
                valid = false;
            } else {
                ContactPhone.setError(null);
            }
            if (content.isEmpty()) {
                ContactContent.setError("יש לרשום פרטים בפנייה");
                valid = false;
            } else {
                ContactContent.setError(null);

            }

            return valid;
        }
    }




