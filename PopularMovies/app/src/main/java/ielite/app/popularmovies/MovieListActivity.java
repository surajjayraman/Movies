package ielite.app.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of Movies, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * Movie details. On tablets, the activity presents the list of Movies and
 * Movie details side-by-side using two vertical panes.
 */
public class MovieListActivity extends AppCompatActivity {

    //Movie DB Params
    private static final String LOG_TAG = MovieListActivity.class.getSimpleName();
    private static final String HIGHEST_RATED = "highest_rated";
    private static final String MOST_POPULAR = "popular";
    private static final String MOVIE_DB_KEY = "moviedb";

    //To set Layout type
    RecyclerView.LayoutManager mLayoutManager;

    //container to hold Movie objects while passing between activities
    MovieParcel[] movies;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private MovieAdapter adapter;
    private RecyclerView mRecyclerView;
    private ArrayList<MovieParcel> movieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Always call the superclass first
        super.onCreate(savedInstanceState);

        Log.d(LOG_TAG, "onCreate");

        setContentView(R.layout.activity_movie_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        //plug in RecyclerView widget
        mRecyclerView = (RecyclerView) findViewById(R.id.movie_list);
        assert mRecyclerView != null;
        setupRecyclerView(mRecyclerView);

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        // Check whether we're recreating a previously destroyed instance

        if (savedInstanceState == null || !savedInstanceState.containsKey(MOVIE_DB_KEY)) {

            Log.d(LOG_TAG, "Initialize movieList with a new instance");

            if (isNetworkAvailable()) {

                // Download data from Movie DB Website
                new FetchMoviesTask().execute(MOST_POPULAR);

            } else {

                Toast.makeText(getApplicationContext(), "Please enable Internet Connection!", Toast.LENGTH_SHORT).show();
            }
        } else {
            //Restore value of members from saved state
            movieList = savedInstanceState.getParcelableArrayList(MOVIE_DB_KEY);
            Log.d(LOG_TAG, "movieList state restored with size : " + movieList.size());

            //Create new MovieAdapter and plug into RecyclerView
            adapter = new MovieAdapter(getApplicationContext(), movieList);
            if(mRecyclerView !=null) {
                mRecyclerView.setAdapter(adapter);
            }
        }


    }

    //Initialize Layout type to GridLayout
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        //set Layout type to GridLayout with 2 columns
        mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_popular_movies) {
            if (isNetworkAvailable()) {
                new FetchMoviesTask().execute(MOST_POPULAR);
            } else {
                Toast.makeText(getApplicationContext(), "Please enable Internet Connection!", Toast.LENGTH_SHORT).show();

            }
            return true;
        } else {
            if (isNetworkAvailable()) {
                new FetchMoviesTask().execute(HIGHEST_RATED);
            } else {

                Toast.makeText(getApplicationContext(), "Please enable Internet Connection!", Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    //verify network availability
    private boolean isNetworkAvailable() {

        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    //Saving state information for movieList
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIE_DB_KEY, movieList);
        Log.d(LOG_TAG, "Saved state information for movieList");
    }

    /**
     * The MovieAdapter class extends from RecyclerView.Adapter
     * The adapter will populate Movie Data onto the RecyclerView
     * The custom ViewHolder gives access to all the Views within
     * each Movie Row Item contained in the layout movie_list
     */

    public class MovieAdapter
            extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

        //Member variable declaration for Collection of Movies
        private ArrayList<MovieParcel> movieListItem;
        private Context mContext;

        //Pass the Collection of Movies into the constructor
        public MovieAdapter(Context context, ArrayList<MovieParcel> movieListItem) {

            this.movieListItem = movieListItem;
            this.mContext = context;
        }


        //inflate the movie_list_row layout and create the holder
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.movie_list_row, parent, false);

            return new ViewHolder(view);
        }

        //set the view attributes based on movieListItem data
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            final String posterImageBaseUrl = getApplicationContext().getResources().
                    getString(R.string.image_base_url);

            final MovieParcel movie = movieListItem.get(position);

            //Using third party library Picasso to download and cache
            //movie poster images
            if(movie.poster !=null) {
                Picasso.with(mContext)
                        .load(posterImageBaseUrl + movie.poster)
                        .into(holder.poster);
            }

            //On click of the poster image, set up a listener to delegate
            //content to the fragment if layout is in two pane mode(tablets)
            //pass the Movie object to the Detail Activity in the case of Handsets

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {

                        Bundle arguments = new Bundle();
                        arguments.putString(MovieDetailFragment.TITLE_KEY, movie.title);
                        arguments.putString(MovieDetailFragment.OVERVIEW_KEY, movie.overview);
                        arguments.putString(MovieDetailFragment.RELEASE_DATE_KEY, movie.release_date);
                        arguments.putString(MovieDetailFragment.VOTES_KEY, movie.vote);

                        MovieDetailFragment fragment = new MovieDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.movie_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();

                        //Passing Movie Parcel to Detail Activity
                        Intent intent = new Intent(context, MovieDetailActivity.class);
                        intent.putExtra("movie", movie);
                        context.startActivity(intent);
                    }
                }
            });
        }

        //determine number of movieListItem objects
        @Override
        public int getItemCount() {

            return movieListItem.size();
        }

        /**
         * Provide a direct reference to each of the views within movieListItem
         * Cache the View items within movie_list_row layout for fast access
         */
        public class ViewHolder extends RecyclerView.ViewHolder {

            //member variables for view rendering
            protected final View mView;
            protected ImageView poster;


            public ViewHolder(View view) {
                super(view);
                mView = view;
                this.poster = (ImageView) view.findViewById(R.id.poster);

            }


        }
    }

    /**
     * This class handles connection to themoviedb.org and
     * parsing of JSON object response
     */

    public class FetchMoviesTask extends AsyncTask<String, Void, String[][]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private String[][] getMovieDataFromJson(String movieJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String MOVIE_LIST = "results";
            final String MOVIE_TITLE = "original_title";
            final String MOVIE_POSTER = "poster_path";
            final String MOVIE_OVERVIEW = "overview";
            final String MOVIE_RELEASE_DATE = "release_date";
            final String MOVIE_VOTES = "vote_average";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(MOVIE_LIST);

            String[][] resultStrs = new String[movieArray.length()][5];
            movies = new MovieParcel[movieArray.length()];
            for (int i = 0; i < movieArray.length(); i++) {
                int k = 0;

                // Get the JSON object representing the movie
                JSONObject movieData = movieArray.getJSONObject(i);
                movies[i] = new MovieParcel(
                        movieData.getString(MOVIE_TITLE),
                        movieData.getString(MOVIE_POSTER),
                        movieData.getString(MOVIE_OVERVIEW),
                        movieData.getString(MOVIE_RELEASE_DATE),
                        movieData.getString(MOVIE_VOTES));
                resultStrs[i][k] = movieData.getString(MOVIE_TITLE);
                resultStrs[i][k + 1] = movieData.getString(MOVIE_POSTER);
                resultStrs[i][k + 2] = movieData.getString(MOVIE_OVERVIEW);
                resultStrs[i][k + 3] = movieData.getString(MOVIE_RELEASE_DATE);
                resultStrs[i][k + 4] = movieData.getString(MOVIE_VOTES);

                Log.d(LOG_TAG, "Movie data :" + i + "\n" + resultStrs[i]);
            }
            movieList = new ArrayList<MovieParcel>(Arrays.asList(movies));
            return resultStrs;
        }


        @Override
        protected String[][] doInBackground(String... params) {

            if (params.length == 0)
                return null;

            HttpURLConnection httpURLConnection = null;
            BufferedReader bufferedReader = null;

            String movieJsonStr = null;

            String sort_order = "popularity.desc";

            try {
                final String MOVIES_BASE_URL = getApplicationContext().getResources().
                        getString(R.string.movies_base_url);
                final String SORT_PARAM = "sort_by";
                final String API_PARAM = "api_key";
                final String api_key = getApplicationContext().getResources().
                        getString(R.string.api_key);

                if (params[0].equals("highest_rated")) {
                    sort_order = "vote_average.desc";
                }

                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, sort_order)
                        .appendQueryParameter(API_PARAM, api_key)
                        .build();
                URL url = new URL(builtUri.toString());

                Log.d(LOG_TAG, "movies Uri  : " + builtUri.toString());

                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    return null;
                }

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while ((line = bufferedReader.readLine()) != null) {

                    buffer.append(line + "\n");

                }

                if (buffer.length() == 0) {
                    return null;
                }

                movieJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "IO Error " + e);
                return null;
            } finally {
                if (httpURLConnection != null)
                    httpURLConnection.disconnect();
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }

            }
            try {
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(final String[][] result) {

            //Plug in Movie RecyclerView Adapter

            if (result != null) {
                adapter = new MovieAdapter(getApplicationContext(), movieList);
                mRecyclerView.setAdapter(adapter);
            } else {
                Toast.makeText(getApplicationContext(), "OnPostExecute:Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }


        }
    }

}
