package de.fhkiel.temi.robogguide

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.squareup.picasso.Picasso
import de.fhkiel.temi.robogguide.database.Media
import de.fhkiel.temi.robogguide.real.Text

class MediaAdapter(private val context: Context, private val mediaItems: List<Text>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // ViewHolder for image items
    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    // ViewHolder for video items
    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val youTubePlayerView: YouTubePlayerView = itemView.findViewById(R.id.youtubePlayerView)

        init {
            (context as? LifecycleOwner)?.lifecycle?.addObserver(youTubePlayerView)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val firstUrl = mediaItems[position].mediaUrls.firstOrNull() ?: ""
        return if (isYoutubeUrl(firstUrl)) TYPE_VIDEO else TYPE_IMAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_IMAGE) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
            ImageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
            VideoViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val mediaItem = mediaItems[position]
        val firstUrl = mediaItem.mediaUrls.firstOrNull()

        if (firstUrl != null) {
            if (holder is ImageViewHolder) {
                // Load image using Picasso
                Picasso.get().load(firstUrl).into(holder.imageView)
            } else if (holder is VideoViewHolder) {
                // Load YouTube video using YouTubePlayerView
                val videoId = extractYoutubeId(firstUrl)
                if (videoId != null) {
                    holder.youTubePlayerView.initialize(object : YouTubePlayerListener {
                        override fun onReady(youTubePlayer: YouTubePlayer) {
                            youTubePlayer.cueVideo(videoId, 0f)
                        }

                        override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {}
                        override fun onPlaybackQualityChange(youTubePlayer: YouTubePlayer, playbackQuality: PlayerConstants.PlaybackQuality) {}
                        override fun onPlaybackRateChange(youTubePlayer: YouTubePlayer, playbackRate: PlayerConstants.PlaybackRate) {}
                        override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {}
                        override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {}
                        override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {}
                        override fun onVideoLoadedFraction(youTubePlayer: YouTubePlayer, loadedFraction: Float) {}
                        override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) {}
                        override fun onApiChange(youTubePlayer: YouTubePlayer) {}
                    }, IFramePlayerOptions.Builder().build())
                }
            }
        }
    }

    override fun getItemCount(): Int = mediaItems.size

    // Helper function to check if a URL is a YouTube link
    private fun isYoutubeUrl(url: String): Boolean {
        return url.contains("youtube.com") || url.contains("youtu.be")
    }

    // Helper function to extract YouTube video ID
    private fun extractYoutubeId(url: String): String? {
        val pattern = "^(?:https?://)?(?:www\\.)?(?:youtube\\.com/watch\\?v=|youtu\\.be/)([\\w\\-]+)(?:&.*)?$"
        val regex = Regex(pattern)
        val matchResult = regex.find(url)
        return matchResult?.groups?.get(1)?.value
    }

    companion object {
        private const val TYPE_IMAGE = 0
        private const val TYPE_VIDEO = 1
    }
}
