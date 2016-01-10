package ielite.app.popularmovies;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * A fragment represents a single Movie detail screen.
 * This fragment is contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets).
 */
public class MovieDetailFragment extends Fragment {
    /**
     * The member variables representing the Movie
     * and it's attributes
     */
    private String title;
    private String poster;
    private String overview;
    private String release_date;
    private String votes;


    public static final String TITLE_KEY = "title";
    public static final String POSTER_KEY = "poster";
    public static final String OVERVIEW_KEY = "overview";
    public static final String RELEASE_DATE_KEY = "release_date";
    public static final String VOTES_KEY = "votes";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //Initialize Movie Attributes of the Fragment
        //passed in from MovieListActivity
        this.title = getArguments().getString(TITLE_KEY);
        this.poster = getArguments().getString(POSTER_KEY);
        this.overview = getArguments().getString(OVERVIEW_KEY);
        this.release_date = getArguments().getString(RELEASE_DATE_KEY);
        this.votes = getArguments().getString(VOTES_KEY);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_detail, container, false);

        //Get a reference to the View objects
        TextView titleView = (TextView) rootView.findViewById(R.id.title);
        ImageView posterView = (ImageView) rootView.findViewById(R.id.posterDetail);
        TextView dateView = (TextView) rootView.findViewById(R.id.releaseDate);
        TextView voteView = (TextView) rootView.findViewById(R.id.votes);
        TextView overViewTextView = (TextView) rootView.findViewById(R.id.overviewText);

        //Attach Movie Attributes to the respective Views
        //Load Image View with Poster Image
        final String posterImageBaseUrl = getActivity().getApplicationContext().getResources().
                getString(R.string.image_base_url);

        if (poster.equals("null") || poster.equals(null) || poster.equals("")) {
            posterView.setImageResource(R.drawable.empty_photo);
        } else {
            Picasso.with(getActivity())
                    .load(posterImageBaseUrl + poster)
                    .into(posterView);
        }

        //Populate TextViews with Movie Details
        titleView.setText(title);
        dateView.setText(release_date);
        voteView.setText(votes);
        overViewTextView.setText(overview);

        return rootView;
    }
}
