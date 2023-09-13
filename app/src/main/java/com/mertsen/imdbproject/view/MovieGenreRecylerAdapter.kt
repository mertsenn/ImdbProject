import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.mertsen.imdbproject.R
import com.mertsen.imdbproject.databinding.SingleCastDescriptionBinding
import com.mertsen.imdbproject.model.Genres
import com.mertsen.imdbproject.modelView.DetailMovieModelView

class MovieGenreRecylerAdapter(val genres : List<String>) : RecyclerView.Adapter<MovieGenreRecylerAdapter.MovieGenreHolder>() {

    inner class MovieGenreHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val movieGenreTextView: TextView = itemView.findViewById(R.id.movieGenre)

        fun bind(movieGenre: String) {
            movieGenreTextView.text = movieGenre
        }
    }





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieGenreHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemView = inflater.inflate(R.layout.movie_category_item, parent, false)
        return MovieGenreHolder(itemView)
    }

    override fun onBindViewHolder(holder: MovieGenreHolder, position: Int) {
        val genre = genres[position]
        holder.bind(genre)
    }

    override fun getItemCount(): Int {
        return genres.size
    }
}
