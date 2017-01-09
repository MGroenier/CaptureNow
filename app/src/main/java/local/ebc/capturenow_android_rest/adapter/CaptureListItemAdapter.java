package local.ebc.capturenow_android_rest.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.List;
import local.ebc.capturenow_android_rest.R;
import local.ebc.capturenow_android_rest.helper.RotateTransformation;
import local.ebc.capturenow_android_rest.model.Capture;
import local.ebc.capturenow_android_rest.service.ServiceGenerator;

/**
 * @author Emil Claussen on 15.12.2016.
 */

public class CaptureListItemAdapter extends RecyclerView.Adapter<CaptureListItemAdapter.ViewHolder> {
    private final Context context;
    private final List<Capture> captureArrayList;

    // Set the URL to interact with the Google Static Maps Web API
    private final String mapApiBaseString = "https://maps.googleapis.com/maps/api/staticmap?center=";
    // Set the map configuration
    private final String mapApiConfigString = "&zoom=11&size=300x300&maptype=roadmap&markers=color:red%7C";

    public CaptureListItemAdapter(List<Capture> list, Context context) {
        captureArrayList = list;
        this.context = context;
    }
    @Override
    public int getItemCount() {
        return captureArrayList.size();
    }
    private Capture getItem(int position) {
        return captureArrayList.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.capture_item, parent, false);
        return new ViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Populate the row
        holder.populateRow(getItem(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView title;
        private final ImageView imageView;
        private final ImageView mapImageView;

        // Initialize the variables
        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.capture_item_title);
            imageView = (ImageView) view.findViewById(R.id.capture_item_image);
            mapImageView = (ImageView) view.findViewById(R.id.capture_map_image);
            view.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
        }

        // Populate each item of the RecyclerView with the capture data.
        public void populateRow(Capture capture) {
            title.setText(capture.getTitle());
            String latitude = String.valueOf(capture.getLatitude());
            String longitude = String.valueOf(capture.getLongitude());

            RotateTransformation t = new RotateTransformation(context, 90);

            // Get local image data if it exists, if not use server resource.
            if (capture.getImgcapture() != null) {
                Glide.with(context)
                        .load(capture.getImgcapture())
                        .transform(t)
                        .into(imageView);
            } else {
                Glide.with(context)
                        .load(ServiceGenerator.API_BASE_URL + "captures/jpg/" + capture.getId())
                        .transform(t)
                        .into(imageView);
            }

            // Generate a map and a marker based off of the capture coordinates.
            String googleMap = mapApiBaseString + latitude + "," + longitude + mapApiConfigString + latitude + "," + longitude;
            Glide.with(context)
                    .load(googleMap)
                    .into(mapImageView);

        }
    }
}