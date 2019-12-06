package ispam.pharmacymanager;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RepoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RepoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "RecyclerViewExample";
    private ArrayList<MedicineItem> medicineList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private MyRecyclerViewAdapter adapter;
    private ProgressBar progressBar;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RepoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RepoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RepoFragment newInstance(String param1, String param2) {
        RepoFragment fragment = new RepoFragment();
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
        return inflater.inflate(R.layout.fragment_repo, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setting up the recycler view
        mRecyclerView = view.findViewById(R.id.recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        initFavoritesList();


    }

    private void initFavoritesList() {
        progressBar.setVisibility(View.VISIBLE);
        medicineList.clear();
        RequestQueue queue = Volley.newRequestQueue(getContext());
//Eszopiclone
        String url = "https://rximage.nlm.nih.gov/api/rximage/1/rxnav?name=Eszopiclone";
        // Request a string response from the provided URL.
        Toast.makeText(getContext(), "MEAW " + url, Toast.LENGTH_LONG).show();

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
                                for (MedicineItem medicine : medicineList) {
                                    System.out.println(medicine.getName() + "\n");


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
                                        ad.setMessage("Save " + item.getName() + " into the store ?");
                                        ad.setView(input);

                                        ad.setButton(Dialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                        ad.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {

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

}