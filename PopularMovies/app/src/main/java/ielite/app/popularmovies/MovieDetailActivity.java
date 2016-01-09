package ielite.app.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * An activity representing a single Movie detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * Movie details are presented side-by-side with a list of Movies
 * in a {@link MovieListActivity}.
 */
public class MovieDetailActivity extends AppCompatActivity {

    private final String LOG_TAG = MovieDetailActivity.class.getSimpleName();
    private MovieParcel movie;
    private String title;
    private String poster;
    private String overview;
    private String release_date;
    private String votes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(LOG_TAG, "In method: OnCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        TextView titleView = (TextView) findViewById(R.id.title);
        TextView dateView = (TextView) findViewById(R.id.releaseDate);
        TextView voteView = (TextView) findViewById(R.id.votes);
        ImageView posterView = (ImageView) findViewById(R.id.posterDetail);
        TextView overViewTextView = (TextView) findViewById(R.id.overviewText);

        movie = getIntent().getParcelableExtra("movie");

        this.title = movie.title;
        this.poster = movie.poster;
        this.overview = movie.overview;
        this.release_date = movie.release_date;
        this.votes = movie.vote;


        final String posterImageBaseUrl = getApplicationContext().getResources().
                getString(R.string.image_base_url);

        if (poster.equals("null") || poster.equals(null) || poster.equals("")) {
            posterView.setImageResource(R.drawable.empty_photo);
        } else {
            Picasso.with(this)
                    .load(posterImageBaseUrl + poster)
                    .into(posterView);
        }

        titleView.setText(title);
        dateView.setText(release_date);
        voteView.setText(votes);
        overViewTextView.setText(overview);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            //represents the Home or Up button.
            NavUtils.navigateUpTo(this, new Intent(this, MovieListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
