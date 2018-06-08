package pl.piotrskiba.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import pl.piotrskiba.android.popularmovies.Utils.NetworkUtils;
import pl.piotrskiba.android.popularmovies.models.Video;
import pl.piotrskiba.android.popularmovies.models.VideoList;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoViewHolder> {
    VideoList mVideoList;

    private final VideoListAdapterOnClickHandler clickHandler;

    public interface VideoListAdapterOnClickHandler{
        void onClick(Video clickedVideo);
    }

    public VideoListAdapter(VideoListAdapterOnClickHandler clickHandler){
        this.clickHandler = clickHandler;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.video_list_item, parent, false);

        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Video video = mVideoList.getMovies()[position];

        String imageUrl = NetworkUtils.buildYoutubeThumbnailUrl(video.getKey()).toString();

        holder.mVideoTitle.setText(video.getName());
        Picasso.get()
                .load(imageUrl)
                .error(R.drawable.ic_broken_image_white_80dp)
                .into(holder.mVideoThumbnail);
    }

    @Override
    public int getItemCount() {
        if(mVideoList == null)
            return 0;
        else
            return mVideoList.getMovies().length;
    }

    class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mVideoTitle;
        ImageView mVideoThumbnail;

        public VideoViewHolder(View itemView) {
            super(itemView);

            mVideoTitle = itemView.findViewById(R.id.tv_video_title);
            mVideoThumbnail = itemView.findViewById(R.id.iv_thumbnail);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            Video video = mVideoList.getMovies()[pos];
            clickHandler.onClick(video);
        }
    }

    public void setData(VideoList videoList){
        mVideoList = videoList;
        notifyDataSetChanged();
    }
}
