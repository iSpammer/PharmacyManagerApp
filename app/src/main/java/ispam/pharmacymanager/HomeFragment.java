package ispam.pharmacymanager;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ispam.pharmacymanager.api.ApiConfig;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private static final String TAG = "RecyclerViewExample";
    private ArrayList<MedicineItem> medicineList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private MyRecyclerViewAdapter adapter;
    private EditText search;
    private Button medinceSearch;
    private ProgressBar progressBar;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setting up the recycler view
        mRecyclerView = view.findViewById(R.id.recycler_view);
        progressBar =  view.findViewById(R.id.progress_bar);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        search = view.findViewById(R.id.searchfield);
        medinceSearch = view.findViewById(R.id.searchButton);
//        city = findViewById(R.id.searchfield);

        medinceSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                medicineList.clear();
                Toast.makeText(getContext(), "MEAW"+search.getText().toString(), Toast.LENGTH_LONG).show();
                RequestQueue queue = Volley.newRequestQueue(getContext());
//Eszopiclone
                String url = "https://rximage.nlm.nih.gov/api/rximage/1/rxnav?name="+search.getText().toString();
                // Request a string response from the provided URL.
                Toast.makeText(getContext(), "MEAW "+url, Toast.LENGTH_LONG).show();

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(final String response) {
                                progressBar.setVisibility(View.GONE);
                                // Display the first 500 characters of the response string.

                                try {
                                    System.out.println(response.toString());
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONObject replyStatus = jsonObject.getJSONObject("replyStatus");
                                    boolean success = replyStatus.optBoolean("success");

                                    if (success) {
                                        JSONArray nlmRxImages = jsonObject.getJSONArray("nlmRxImages");
                                        for (int i = 0; i < nlmRxImages.length(); i++) {
                                            JSONObject med = nlmRxImages.optJSONObject(i);
                                            MedicineItem item = new MedicineItem();
                                            item.setName(med.optString("name"));
                                            item.setImg(med.optString("imageUrl"));
                                            item.setLabeler(med.optString("labeler"));
                                            medicineList.add(item);
                                        }
                                        for (MedicineItem medicine: medicineList) {
                                            System.out.println(medicine.getName()+"\n");


                                        }
                                        System.out.println("success printed el list");
                                        adapter = new MyRecyclerViewAdapter(getContext(), medicineList);
                                        mRecyclerView.setAdapter(adapter);
                                        adapter.setOnItemClickListener(new OnItemClickListener() {
                                            @Override
                                            public void onItemClick(MedicineItem item) {
                                                Toast.makeText(getContext(), item.getName(), Toast.LENGTH_LONG).show();
                                                final EditText input = new EditText(getContext());
                                                input.setInputType(InputType.TYPE_CLASS_NUMBER);

                                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                                        LinearLayout.LayoutParams.MATCH_PARENT);
                                                input.setLayoutParams(lp);
                                                AlertDialog ad = new AlertDialog.Builder(getContext())
                                                        .create();
                                                ad.setCancelable(true);
                                                ad.setTitle("Add to the store");
                                                ad.setMessage("Save "+item.getName()+" into the store ?");
                                                ad.setView(input);

                                                ad.setButton(Dialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                                ad.setButton(Dialog.BUTTON_POSITIVE,"OK",new DialogInterface.OnClickListener(){

                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        int quanitiy = Integer.parseInt(input.getText().toString());

                                                        dialog.dismiss();

                                                    }
                                                });

                                                ad.show();
                                            }
                                        });
                                    }

                                    // System.out.println(medicineList);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                    }
                });

                // Add the request to the RequestQueue.
                queue.add(stringRequest);

            }
        });

    }

    public void callvolly(final String userId, final String quanitity, final String acqDate, final String name, final String labeler, final String deaSchedule, final String attribution, final String id, final String imageUrl){
        RequestQueue MyRequestQueue = Volley.newRequestQueue(getContext());
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, ApiConfig.URL_POST_MEDICINE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds
                //The String 'response' contains the server's response.
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.optBoolean("success");

                    if (success) {
                        Toast.makeText(getContext(), "Added Successfuly", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getContext(), "Error Occured"+jsonObject.optString("Error"), Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> postData = new HashMap<String, String>();
                postData.put("userId", userId);
                postData.put("quanitity", quanitity);
                postData.put("acqDate", acqDate);
                postData.put("name", name);
                postData.put("labeler", labeler);
                postData.put("deaSchedule", deaSchedule);
                postData.put("attribution", attribution);
                postData.put("id", id);
                postData.put("imageUrl", imageUrl);

                return postData;
            }
        };


        MyRequestQueue.add(MyStringRequest);
    }

}