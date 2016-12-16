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

/**
 * @author Emil Claussen on 15.12.2016.
 */

public class CaptureListItemAdapter extends RecyclerView.Adapter<CaptureListItemAdapter.ViewHolder> {
    final Context context;
    private final List<Capture> captureArrayList;
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
    public long getItemId(int position) {
        return captureArrayList.get(position).getId();
    }
    public void updateList(List<Capture> newlist) {
        // Set new updated list
        captureArrayList.clear();
        captureArrayList.addAll(newlist);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.capture_item, parent, false);
        return new ViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Populate the row
        holder.populateRow(getItem(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView title;
        private final TextView latitude;
        private final TextView longitude;
        private final TextView description;
        private final ImageView imageView;
        //initialize the variables
        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.capture_item_title);
            latitude = (TextView) view.findViewById(R.id.capture_item_latitude);
            longitude = (TextView) view.findViewById(R.id.capture_item_longitude);
            description = (TextView) view.findViewById(R.id.capture_item_description);
            imageView = (ImageView) view.findViewById(R.id.capture_item_image);
            view.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
        }
        public void populateRow(Capture capture) {
            title.setText(capture.getTitle());
            latitude.setText(String.valueOf(capture.getLatitude()));
            longitude.setText(String.valueOf(capture.getLongitude()));
            description.setText(capture.getDescription());
            RotateTransformation t = new RotateTransformation(context, 90);
            Glide.with(context)
                    .load(capture.getImgcapture())
                    .transform(t)
                    .into(imageView);
        }
    }
}