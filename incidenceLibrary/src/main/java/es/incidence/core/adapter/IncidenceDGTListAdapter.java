package es.incidence.core.adapter;

import static com.e510.commons.utils.LogUtil.logE;
import static com.e510.commons.utils.LogUtil.makeLogTag;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.e510.commons.fragment.BaseFragment;
import com.e510.commons.utils.FontUtils;
import com.e510.commons.utils.LogUtil;
import com.e510.incidencelibrary.R;
import com.mapbox.api.staticmap.v1.MapboxStaticMap;
import com.mapbox.api.staticmap.v1.StaticMapCriteria;
import com.mapbox.geojson.Point;

import java.util.ArrayList;

import es.incidence.core.Constants;
import es.incidence.core.domain.IncidenceDGT;
import es.incidence.core.manager.ImageManager;

public class IncidenceDGTListAdapter extends BaseAdapter
{
    public static final int ROW = 0;

    private static String TAG = makeLogTag(IncidenceDGTListAdapter.class);
    private BaseFragment fragment;
    private ArrayList<IncidenceDGT> items;

    public IncidenceDGTListAdapter(BaseFragment fragment, ArrayList<IncidenceDGT> items)
    {
        this.fragment = fragment;
        this.items = items;
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    @Override
    public Object getItem(int position) {
        return this.items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        return ROW;
    }

    public View getView (final int position, View convertView, ViewGroup parent)
    {
        View rowView = convertView;

        try
        {
            if (convertView == null)
            {
                LayoutInflater inflater = (LayoutInflater) this.fragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.row_incidence_dgt, parent, false);

                ViewHolder viewHolder = new ViewHolder();

                viewHolder.layoutRow = rowView.findViewById(R.id.layoutRow);
                viewHolder.txtTitle = rowView.findViewById(R.id.txtTitle);
                viewHolder.imageMap = rowView.findViewById(R.id.image_map);
                FontUtils.setTypeValueText(viewHolder.txtTitle, Constants.FONT_REGULAR, fragment.getContext());

                rowView.setTag(viewHolder);
            }

            final IncidenceDGT incidenceDGT = items.get(position);
            final ViewHolder holder = (ViewHolder) rowView.getTag();

            holder.txtTitle.setText(incidenceDGT.date + ", " + incidenceDGT.hour + "h");

            MapboxStaticMap staticImage = MapboxStaticMap.builder()
                    .accessToken(fragment.getString(R.string.mapbox_access_token))
                    .styleId(StaticMapCriteria.STREET_STYLE)
                    .cameraPoint(Point.fromLngLat(incidenceDGT.lon, incidenceDGT.lat)) // Image's centerpoint on map
                    .cameraZoom(13)
                    .width(320) // Image width
                    .height(320) // Image height
                    .retina(true) // Retina 2x image will be returned
                    .build();

            String url = staticImage.url().toString();
            logE(TAG, "URL DEL MAPA: " + url);
            ImageManager.loadUrlFileImage(fragment.getContext(), url, holder.imageMap);
            /*
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fragment.onClickRow(incidence);
                }
            });
            */
        }
        catch (Exception e)
        {
            LogUtil.logE(TAG, "Error: " + e.getMessage(), e);
        }

        return rowView;
    }

    private class ViewHolder
    {
        private RelativeLayout layoutRow;
        private TextView txtTitle;
        private ImageView imageMap;
        //private MapView mapView;
        //private MapboxStaticMap mapboxStaticMap;
    }
}