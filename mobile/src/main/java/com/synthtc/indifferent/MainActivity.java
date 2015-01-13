package com.synthtc.indifferent;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.synthtc.indifferent.api.Deal;
import com.synthtc.indifferent.api.Meh;
import com.synthtc.indifferent.ui.ImageFragment;
import com.synthtc.indifferent.ui.NavigationDrawerFragment;
import com.synthtc.indifferent.ui.SettingsFragment;
import com.synthtc.indifferent.util.Alarm;
import com.synthtc.indifferent.util.GsonRequest;
import com.synthtc.indifferent.util.Helper;
import com.synthtc.indifferent.util.MehCache;
import com.synthtc.indifferent.util.VolleySingleton;
import com.viewpagerindicator.CirclePageIndicator;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;

import in.uncod.android.bypass.Bypass;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    public static final String LOGTAG = "Indifferent";


    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private int mColor = R.color.primary_material_dark;
    private int mColorStatus = R.color.primary_dark_material_dark;

    private boolean mIsPaused = false;
    private Fragment mFragmentToLoad = null;
    private boolean mInitialized = false;


    @Override
    protected void onPause() {
        super.onPause();
        mIsPaused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsPaused = false;
        loadFragment(mFragmentToLoad);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        if (!mInitialized) {
            initialize();
        }

        if (position == -1) {
            loadFragment(new SettingsFragment());
            mTitle = getString(R.string.action_settings);
        } else {
            Instant instant = DateTime.now(DateTimeZone.UTC).minusDays(position).withTimeAtStartOfDay().toInstant();
            Meh meh = MehCache.getInstance(this).get(instant);
            if (meh != null) {
                loadFragment(DealFragment.newInstance(instant, meh));
            } else {
                loadFragment(PlaceholderFragment.newInstance(PlaceholderFragment.ARG_SECTION_ERROR));
            }
        }
        Log.d(LOGTAG, "onNavigationDrawerItemSelected");
    }

    private void initialize() {
        if (mInitialized) {
            return;
        }

        mInitialized = true;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean(SettingsFragment.KEY_ALARM_ENABLE, SettingsFragment.DEFAULT_ALARM_ENABLE)) {
            Alarm.set(this);
        }

        final MehCache mehCache = MehCache.getInstance(this);

        // Write some existing data to the storage
        String json = "{\"deal\":{\"features\":\"- Bluetooth 3.0 with 30-foot range\\r\\n- Built-in mic and call-answering functionality\\r\\n- Rechargeable lithium battery gets 8 hours to a charge\\r\\n- Smallish ear pads might be uncomfortable on your ears if you wear them all day, but fashion and health tip: you shouldn't wear headphones all day\\r\\n- Look like you spent stupid money on overpriced headphones without actually spending stupid money\",\"id\":\"a6ki000000000zoAAA\",\"items\":[{\"attributes\":[{\"key\":\"Color\",\"value\":\"White\"}],\"condition\":\"New\",\"id\":\"102086\",\"price\":24,\"photo\":\"https://res.cloudinary.com/mediocre/image/upload/v1420573190/ggrctd9olous1avgqjbr.png\"},{\"attributes\":[{\"key\":\"Color\",\"value\":\"Red\"}],\"condition\":\"New\",\"id\":\"102087\",\"price\":14,\"photo\":\"https://res.cloudinary.com/mediocre/image/upload/v1420573142/avfz0mds1lvwlpk2zesg.png\"},{\"attributes\":[{\"key\":\"Color\",\"value\":\"Black\"}],\"condition\":\"New\",\"id\":\"102088\",\"price\":14,\"photo\":\"https://res.cloudinary.com/mediocre/image/upload/v1420573220/rb4ymj9egoyrgqmlsgwm.png\"}],\"photos\":[\"https://res.cloudinary.com/mediocre/image/upload/v1420573142/avfz0mds1lvwlpk2zesg.png\",\"https://res.cloudinary.com/mediocre/image/upload/v1420573163/ls03ro79tzkyyoamuqgk.png\",\"https://res.cloudinary.com/mediocre/image/upload/v1420573190/ggrctd9olous1avgqjbr.png\",\"https://res.cloudinary.com/mediocre/image/upload/v1420573220/rb4ymj9egoyrgqmlsgwm.png\",\"https://res.cloudinary.com/mediocre/image/upload/v1420655408/im31xz3cmqhxiewa83mj.png\",\"https://res.cloudinary.com/mediocre/image/upload/v1420655424/eejvqthksdmkifolzdfb.png\",\"https://res.cloudinary.com/mediocre/image/upload/v1420573302/hzpbsoozk8ztihos6ysn.png\",\"https://res.cloudinary.com/mediocre/image/upload/v1420674852/rb4rbl3q3hnyokq3dulm.png\"],\"title\":\"TOCCs Manhattan Bluetooth Headphones\",\"specifications\":\"Specs \\r\\n====\\r\\n- Model: TOCCS Manhattan\\r\\n- 30 ft Bluetooth 3.0 + [EDR transmission](http://en.wikipedia.org/wiki/Bluetooth#Bluetooth_v2.1_.2B_EDR)\\r\\n- 40mm stereo speaker\\r\\n- [CVC Noise cancellation](http://www.csr.com/products/22/cvc-5.0)\\r\\n- [A2DP audio distribution](http://en.wikipedia.org/wiki/List_of_Bluetooth_profiles#Advanced_Audio_Distribution_Profile_.28A2DP.29)\\r\\n- Rechargeable lithium battery with 8 hours life\\r\\n- Soft polyurethane leather with adjustable rubber headband\\r\\n- Includes 3.5mm audio cable for corded use\\r\\n\\r\\n**Condition** - New\\r\\n**Warranty** - 90 Day Replacement TOCCs\\r\\n**Ships Via** - FedEx SmartPost \\r\\n- $5 Shipping, Free with **[VMP](https://mediocre.com/vmp)** \\r\\n\\r\\nWhat's in the Box? \\r\\n====\\r\\n1x Bluetooth headphones\\r\\n1x 3.5mm audio cable\\r\\n1x Micro USB charging cable\\r\\n1x Quick start guide\\r\\n\\r\\nPictures\\r\\n====\\r\\n[What's in the box](https://res.cloudinary.com/mediocre/image/upload/v1420573163/ls03ro79tzkyyoamuqgk.png)\\r\\n[Red](https://res.cloudinary.com/mediocre/image/upload/v1420573142/avfz0mds1lvwlpk2zesg.png)\\r\\n[White](https://res.cloudinary.com/mediocre/image/upload/v1420573190/ggrctd9olous1avgqjbr.png)\\r\\n[Black](https://res.cloudinary.com/mediocre/image/upload/v1420573220/rb4ymj9egoyrgqmlsgwm.png)\\r\\n[Retail box](https://res.cloudinary.com/mediocre/image/upload/v1420573302/hzpbsoozk8ztihos6ysn.png)\\r\\n[Folds up](https://res.cloudinary.com/mediocre/image/upload/v1420655408/im31xz3cmqhxiewa83mj.png)\\r\\n[Detail of ear pad](https://res.cloudinary.com/mediocre/image/upload/v1420655424/eejvqthksdmkifolzdfb.png)\\r\\n\\r\\nPrice Check\\r\\n====\\r\\n[$119 List, $109 at TOCCs](http://www.toccs.com/headphones)\",\"story\":{\"title\":\"Beats spending 200 bucks.\",\"body\":\"Do you need \\\"studio headphones\\\"? Find out by taking this simple quiz:\\r\\n\\r\\n1. Do you think \\\"studio headphones\\\" are something you can find at Best Buy?\\r\\n\\r\\n2. Will you be using these headphones with Bluetooth audio?\\r\\n\\r\\n3. Have you ever considered buying Beats headphones, even for a second?\\r\\n\\r\\nIf you answered \\\"yes\\\" to any of the above questions, congratulations! You are not a professional music producer, recording engineer, or DJ. You don't need \\\"studio headphones\\\". And you especially don't need to pay extra for a pair of regular old headphones with a phony \\\"studio headphones\\\" label.\\r\\n\\r\\nReal talk: \\\"studio headphones\\\" now just means \\\"overpriced headphones with a plastic band that's all black and red and curvy and shit\\\". They have no more place in a professional recording studio than they do in a blacksmith's forge. \\r\\n\\r\\nIf you insist on \\\"studio headphones\\\", you might as well buy them cheap. This pair checks off all the boxes, from the aforementioned band to the ear pads made of \\\"PU leather\\\" (PU stands for \\\"polyurethane\\\" (so, not real leather) (also, note to polyurethane industry: find a better acronym)). The ear pads are a little on the little side, maybe. But those are all just matters of style. Down in them guts, these are just regular old half-decent Bluetooth headphones. \\r\\n\\r\\nSo today you can buy them for regular old headphone prices. Granted, that won't give you quite the same rush as paying way, way too much for them. If you want to play pretend record mogul, there are plenty of other places where you can go throw your money away.\\r\\n\\r\\nOn the other hand, if you answered \\\"no\\\" to all of the above questions and you *are* a professional music producer, let us know where we should send you a copy of our demo. It's like MIA meets *Chocolate Starfish*-era Limp Bizkit on a Jane's Addiction tip, with a little bit of James Brown thrown in there, only funkier. Please, serious industry requests only. You really need studio headphones to appreciate our sound.\"},\"theme\":{\"accentColor\":\"#be2b33\",\"backgroundColor\":\"#a0d0d7\",\"backgroundImage\":\"https://res.cloudinary.com/mediocre/image/upload/v1420573451/usnubcnzirjlyfofwunl.jpg\",\"foreground\":\"dark\"},\"url\":\"https://meh.com/deals/toccs-manhattan-bluetooth-headphones\",\"soldOutAt\":\"2015-01-08T07:02:57.055Z\",\"topic\":{\"commentCount\":78,\"createdAt\":\"2015-01-08T05:02:36.639Z\",\"id\":\"54ae0f6c6b977f4801a51348\",\"replyCount\":204,\"url\":\"https://meh.com/forum/topics/toccs-manhattan-bluetooth-headphones\",\"voteCount\":2}},\"poll\":{\"answers\":[{\"id\":\"a6li0000000PBj0AAG-1\",\"text\":\"under $20\",\"voteCount\":292},{\"id\":\"a6li0000000PBj0AAG-2\",\"text\":\"$20-$30\",\"voteCount\":210},{\"id\":\"a6li0000000PBj0AAG-3\",\"text\":\"$30-$50\",\"voteCount\":304},{\"id\":\"a6li0000000PBj0AAG-4\",\"text\":\"$50-$80\",\"voteCount\":278},{\"id\":\"a6li0000000PBj0AAG-5\",\"text\":\"$80-$120\",\"voteCount\":266},{\"id\":\"a6li0000000PBj0AAG-6\",\"text\":\"$120-$200\",\"voteCount\":151},{\"id\":\"a6li0000000PBj0AAG-7\",\"text\":\"more than $200\",\"voteCount\":106}],\"id\":\"a6li0000000PBj0AAG\",\"startDate\":\"2015-01-08T05:00:00.000Z\",\"title\":\"What is your gut feeling of the \\\"right\\\" price for a pair of good headphones?\",\"topic\":{\"commentCount\":17,\"createdAt\":\"2015-01-08T05:00:00.049Z\",\"id\":\"54ae0ed06b977f4801a51318\",\"replyCount\":15,\"url\":\"https://meh.com/forum/topics/what-is-your-gut-feeling-of-the-right-price-for-a-pair-of-good-headphones\",\"voteCount\":0}}}";
        Gson gson = new Gson();
        Meh sample = gson.fromJson(json, Meh.class);
        DateTime dateTime = DateTime.parse(sample.getDeal().getTopic().getCreatedAt());
        Instant instant = dateTime.withTimeAtStartOfDay().toInstant();
        mehCache.put(instant, sample, true);

        Meh meh = mehCache.get(DateTime.now(DateTimeZone.UTC).withTimeAtStartOfDay().toInstant());
        if (meh != null) {
            loadFragment(DealFragment.newInstance(instant, meh));
            restoreActionBar();
        } else {
            String url = getString(R.string.api_url, getString(R.string.api_key));
            Response.Listener<Meh> responseListener = new Response.Listener<Meh>() {
                @Override
                public void onResponse(Meh meh) {
                    DateTime dateTime = DateTime.parse(meh.getDeal().getTopic().getCreatedAt());
                    Instant instant = dateTime.withTimeAtStartOfDay().toInstant();
                    mehCache.put(instant, meh, true);
                    loadFragment(DealFragment.newInstance(instant, meh));
                }
            };
            Response.ErrorListener responseErrorListener = new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    loadFragment(PlaceholderFragment.newInstance(PlaceholderFragment.ARG_SECTION_ERROR));
                    Log.e(LOGTAG, "VolleyError", volleyError);
                }
            };
            GsonRequest request = new GsonRequest(url, Meh.class, null, responseListener, responseErrorListener);
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(mColor));
        actionBar.setTitle(mTitle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(mColorStatus);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            //getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            //return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    private void loadFragment(Fragment fragment) {
        if (fragment != null) {
            if (mIsPaused) {
                mFragmentToLoad = fragment;
            } else {
                mFragmentToLoad = null;
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, fragment)
                        .commit();
            }
        }
    }

    public static class DealFragment extends Fragment {
        public static final String ARG_INSTANT = "deal_instant";
        private static Meh mMeh;
        private ViewPager mPager;
        private ImagePagerAdapter mAdapter;

        public DealFragment() {
        }

        public static DealFragment newInstance(Instant instant, Meh mehObject) {
            mMeh = mehObject;
            DealFragment fragment = new DealFragment();
            Bundle args = new Bundle();
            args.putLong(ARG_INSTANT, instant.getMillis());
            fragment.setArguments(args);

            return fragment;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Log.d(LOGTAG, "DealFragment.onCreateView");
            Helper.cacheImages(getActivity(), mMeh);

            View rootView = inflater.inflate(R.layout.fragment_deal, container, false);

            Deal deal = mMeh.getDeal();
            Deal.Theme theme = deal.getTheme();
            Deal.Story story = deal.getStory();

            int accentColor = Color.parseColor(theme.getAccentColor());
            int foregroundColor = Helper.getForegroundColor(getActivity(), accentColor);
            int backgroundColor = Color.parseColor(theme.getBackgroundColor());
            int textColor = Helper.getForegroundColor(getActivity(), backgroundColor);

            // Set up ViewPager and backing adapter
            mAdapter = new ImagePagerAdapter(getActivity().getSupportFragmentManager(), mMeh.getDeal().getPhotos().length);
            mPager = (ViewPager) rootView.findViewById(R.id.pager);
            mPager.setAdapter(mAdapter);
            mPager.setPageMargin((int) getResources().getDimension(R.dimen.horizontal_page_margin));
            mPager.setOffscreenPageLimit(2);

            CirclePageIndicator circleIndicator = (CirclePageIndicator) rootView.findViewById(R.id.pager_indicator);
            circleIndicator.setViewPager(mPager);
            circleIndicator.setFillColor(accentColor);
            circleIndicator.setStrokeColor(textColor);

            TextView price = (TextView) rootView.findViewById(R.id.price);
            TextView title = (TextView) rootView.findViewById(R.id.title);
            TextView features = (TextView) rootView.findViewById(R.id.features);
            TextView storyTitle = (TextView) rootView.findViewById(R.id.story_title);
            TextView storyBody = (TextView) rootView.findViewById(R.id.story);
            TextView specifications = (TextView) rootView.findViewById(R.id.specifications);


            price.setText(deal.getPrices(getActivity()));
            price.setTextColor(foregroundColor);
            GradientDrawable pill = (GradientDrawable) price.getBackground();
            pill.setColor(accentColor);
            pill.setStroke(getResources().getDimensionPixelSize(R.dimen.stroke_width), Helper.getHighlightColor(accentColor, theme.getForeground()));
            title.setText(deal.getTitle());
            title.setTextColor(textColor);

            Bypass bypass = new Bypass(getActivity());
            CharSequence string = bypass.markdownToSpannable(deal.getFeatures());
            features.setText(string);
            features.setTextColor(textColor);

            if (story != null) {
                storyTitle.setText(deal.getStory().getTitle());
                storyTitle.setTextColor(textColor);
                string = bypass.markdownToSpannable(deal.getStory().getBody());
                storyBody.setText(string);
                storyBody.setTextColor(textColor);
                storyBody.setMovementMethod(LinkMovementMethod.getInstance());
            } else {
                storyTitle.setVisibility(View.GONE);
                storyBody.setVisibility(View.GONE);
            }

            string = bypass.markdownToSpannable(deal.getSpecifications());
            specifications.setText(string);
            specifications.setTextColor(textColor);
            specifications.setMovementMethod(LinkMovementMethod.getInstance());

            rootView.setBackgroundColor(backgroundColor);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            MainActivity mainActivity = (MainActivity) activity;
            mainActivity.mTitle = mMeh.getDeal().getTitle();
            Deal.Theme theme = mMeh.getDeal().getTheme();
            mainActivity.mColor = Color.parseColor(theme.getAccentColor());
            mainActivity.mColorStatus = Helper.getHighlightColor(mainActivity.mColor, theme.getForeground());
        }

        private class ImagePagerAdapter extends FragmentStatePagerAdapter {
            private final int mSize;

            public ImagePagerAdapter(FragmentManager fm, int size) {
                super(fm);
                mSize = size;
            }

            @Override
            public int getCount() {
                return mSize;
            }

            @Override
            public Fragment getItem(int position) {
                return ImageFragment.newInstance(mMeh.getDeal().getPhotos()[position]);
            }
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        public static final int ARG_SECTION_ERROR = -1;
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static int mSectionNumber = ARG_SECTION_ERROR;

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
            int layoutId = R.layout.fragment_main;
            if (mSectionNumber < 0) {
                layoutId = R.layout.fragment_error;
            }
            View rootView = inflater.inflate(layoutId, container, false);
            Log.d(LOGTAG, "PlaceholderFragmentonCreateView " + mSectionNumber);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            mSectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            MainActivity mainActivity = (MainActivity) activity;
            mainActivity.mTitle = getString(R.string.error_oops);
            mainActivity.mColor = getResources().getColor(R.color.primary_material_dark);
            mainActivity.mColorStatus = getResources().getColor(R.color.primary_dark_material_dark);
        }
    }
}