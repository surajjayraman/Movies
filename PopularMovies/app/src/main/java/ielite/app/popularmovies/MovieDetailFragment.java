package ielite.app.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


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
    private String overview;
    private String release_date;
    private String votes;


    public static final String TITLE_KEY = "title";
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
        this.overview = getArguments().getString(OVERVIEW_KEY);
        this.release_date = getArguments().getString(RELEASE_DATE_KEY);
        this.votes = getArguments().getString(VOTES_KEY);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_detail, container, false);

        TextView titleView = (TextView) rootView.findViewById(R.id.title);
        TextView dateView = (TextView) rootView.findViewById(R.id.releaseDate);
        TextView voteView = (TextView) rootView.findViewById(R.id.votes);
        TextView overViewTextView = (TextView) rootView.findViewById(R.id.overviewText);

        //Set Movie Attributes to the respective Views
        titleView.setText(title);
        dateView.setText(release_date);
        voteView.setText(votes);
        overViewTextView.setText(overview);

        return rootView;
    }
}
