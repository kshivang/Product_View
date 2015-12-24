package com.adurcup.swipable_experiment;


import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Object extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private PageIndicator mIndicator;
    private ProgressBar progressBar;
    private String loginUrl="http://api.adurcup.com/v2/products/3";
    private  static ResponseItems responseItems;
    private List <String> image_srcl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressBar =(ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        new AsyncHttpTask().execute(loginUrl);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(30,100,30)));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_object, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class AsyncHttpTask extends AsyncTask<String, Void, Integer>{
        @Override
        protected void onPreExecute(){
            setProgressBarIndeterminate(true);
        }
        private void parseResult(String result){
            try{
                JSONObject response = new JSONObject(result);
                JSONArray posts = response.optJSONArray("product");
                JSONObject post = posts.optJSONObject(0);
                JSONArray img_src=post.optJSONArray("image_src");

                image_srcl=new ArrayList<>();

                for(int i=0;i<img_src.length();i++){
                    String item;
                    item=img_src.optString(i);
                    image_srcl.add(item);
                }
                responseItems=new ResponseItems();

                responseItems.setImage_src(image_srcl);

                responseItems.setId(post.optString("id"));
                responseItems.setName(post.optString("name"));
                responseItems.setCategory(post.optString("category"));
                responseItems.setDescription(post.optString("description"));
                responseItems.setMin_quantity(post.optString("min_quantity"));
                responseItems.setInitial_cost(post.optString("initial_cost"));
                responseItems.setPrice_per_unit(post.optString("price_per_unit"));
                responseItems.setCustomizable(post.optString("customizable"));
                responseItems.setColor(post.optString("color"));
                responseItems.setTypes(post.optString("types"));
                responseItems.setSizes(post.optString("sizes"));
                responseItems.setDelivery(post.optString("delivery"));
                responseItems.setUnit_description(post.optString("unit_description"));
                responseItems.setAdvertisement(post.optString("advertisement"));
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        protected Integer doInBackground(String... params) {
            Integer result=0;
            HttpURLConnection urlConnection;

            try {
                URL url=new URL(params[0]);

                urlConnection =(HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");

                int statusCode=urlConnection.getResponseCode();
                if (statusCode==200){
                    BufferedReader r=new BufferedReader((new InputStreamReader(urlConnection.getInputStream())));
                    StringBuilder response =new StringBuilder();
                    String line;
                    while((line=r.readLine()) !=null) {
                        response.append(line);
                    }
                    parseResult(response.toString());
                    result =1;
                }
                else {
                    result =0;
                }
            }
            catch (Exception e){
                Log.d("Content_fetch", e.getLocalizedMessage());
            }

            return result;
        }
        @Override
        protected void onPostExecute(Integer result) {
            progressBar.setVisibility(View.GONE);

            if (result==1){
                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
                //we can add section as many we wish here
                mSectionsPagerAdapter.setCount(mSectionsPagerAdapter.getCount()+image_srcl.size()-1);

                mViewPager = (ViewPager) findViewById(R.id.container);
                mViewPager.setAdapter(mSectionsPagerAdapter);

                mIndicator= (CirclePageIndicator)findViewById(R.id.indicator);
                mIndicator.setViewPager(mViewPager);

                TextView ProdutId=(TextView)findViewById(R.id.productId);
                ProdutId.setText(responseItems.getId());

                TextView Name=(TextView)findViewById(R.id.name);
                Name.setText(responseItems.getName());

                TextView Category=(TextView) findViewById(R.id.category);
                Category.setText(responseItems.getCategory());

                TextView Description=(TextView) findViewById(R.id.description);
                Description.setText(responseItems.getDescription());

                TextView MinQuantity=(TextView)findViewById(R.id.min_quantity);
                MinQuantity.setText(responseItems.getMin_quantity());

                TextView InitialCost=(TextView) findViewById(R.id.initial_cost);
                InitialCost.setText(responseItems.getInitial_cost());

                TextView PricePerUnit=(TextView) findViewById(R.id.price_per_unit);
                PricePerUnit.setText(responseItems.getPrice_per_unit());

                TextView Custom=(TextView) findViewById(R.id.customizable);
                Custom.setText(responseItems.getCustomizable());

                TextView Color= (TextView) findViewById(R.id.color);
                Color.setText(responseItems.getColor());

                TextView Types=(TextView) findViewById(R.id.types);
                Types.setText(responseItems.getTypes());

                TextView Size= (TextView) findViewById(R.id.sizes);
                Size.setText(responseItems.getSizes());

                TextView Delivery=(TextView) findViewById(R.id.delivery);
                Delivery.setText(responseItems.getDelivery());

                TextView Unit_des=(TextView) findViewById(R.id.unit_description);
                Unit_des.setText(responseItems.getUnit_description());

                TextView Advertisement=(TextView) findViewById(R.id.advertisement);
                Advertisement.setText(responseItems.getAdvertisement());

            }
            else{
                Toast.makeText(Object.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment  {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_object, container, false);
            ImageView imageView= (ImageView) rootView.findViewById(R.id.section_label1);
            imageView.setImageResource(R.drawable.placeholder);
            Picasso.with(getContext()).load("http://www.adurcup.com/images/product/"+responseItems.getImage_src(getArguments().getInt(ARG_SECTION_NUMBER)))
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public static class SectionsPagerAdapter extends FragmentPagerAdapter {
        private static final String[] CONTENT =new String[] {"Section 1",};
        private int mCount=CONTENT.length;
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }
       @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Show total pages.
            return mCount;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return SectionsPagerAdapter.CONTENT[position % CONTENT.length];
        }

        public void setCount(int count) {
            if (count > 0)
                mCount=count;
        }
    }
}
