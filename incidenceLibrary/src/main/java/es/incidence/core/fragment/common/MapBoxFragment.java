package es.incidence.core.fragment.common;

import static com.e510.commons.utils.LogUtil.makeLogTag;
import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.Property.LINE_JOIN_ROUND;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconSize;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.e510.commons.utils.LogUtil;
import com.e510.commons.utils.Utils;
import com.e510.commons.view.Hud;
import com.e510.incidencelibrary.R;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

import es.incidence.core.fragment.IFragment;
import es.incidence.library.IncidenceLibraryManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapBoxFragment extends IFragment
{
    private static final String TAG = makeLogTag(MapBoxFragment.class);

    private static final String SYMBOL_ICON_ID = "SYMBOL_ICON_ID";
    private static final String PERSON_ICON_ID = "PERSON_ICON_ID";
    private static final String EXTRA_ICON_ID = "EXTRA_ICON_ID";
    private static final String MARKER_SOURCE_ID = "MARKER_SOURCE_ID";
    private static final String PERSON_SOURCE_ID = "PERSON_SOURCE_ID";
    private static final String EXTRA_SOURCE_ID = "EXTRA_SOURCE_ID";

    private static final String DASHED_DIRECTIONS_LINE_LAYER_SOURCE_ID = "DASHED_DIRECTIONS_LINE_LAYER_SOURCE_ID";
    private static final String LAYER_ID = "LAYER_ID";
    private static final String PERSON_LAYER_ID = "PERSON_LAYER_ID";
    private static final String EXTRA_LAYER_ID = "EXTRA_LAYER_ID";
    private static final String DASHED_DIRECTIONS_LINE_LAYER_ID = "DASHED_DIRECTIONS_LINE_LAYER_ID";

    private MapView mapView;
    private DirectionsRoute currentRoute;
    private MapboxDirections client;
    private Point origin;
    private Point destination;
    private Point extraPoint;
    private MapboxMap mapboxMap;
    private FeatureCollection dashedLineDirectionsFeatureCollection;
    private boolean loaded;
    private MapboxMap.OnCameraMoveListener onCameraMoveListener;
    private CameraPosition cameraPosition;
    private MapboxMap.OnMapClickListener onMapClickListener;

    public void overrideOnCreateView(View view, Bundle savedInstanceState)
    {
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                MapBoxFragment.this.mapboxMap = mapboxMap;
                if (isMoveDisabled()) {
                    disableMove();
                }

                MapBoxFragment.this.mapboxMap.setStyle(new Style.Builder().fromUri(Style.DARK));

                if (origin != null && !loaded)
                {
                    drawRoute(origin, destination, extraPoint);
                }

                MapBoxFragment.this.mapboxMap.addOnMoveListener(new MapboxMap.OnMoveListener() {
                    @Override
                    public void onMoveBegin(@NonNull MoveGestureDetector detector) {
                    }

                    @Override
                    public void onMove(@NonNull MoveGestureDetector detector) {
                    }

                    @Override
                    public void onMoveEnd(@NonNull MoveGestureDetector detector) {

                    }
                });
                MapBoxFragment.this.mapboxMap.addOnCameraMoveListener(new MapboxMap.OnCameraMoveListener() {
                    @Override
                    public void onCameraMove() {
                        if (onCameraMoveListener != null)
                        {
                            onCameraMoveListener.onCameraMove();
                        }
                    }
                });

                if (onMapClickListener != null) {
                    MapBoxFragment.this.mapboxMap.addOnMapClickListener(onMapClickListener);
                }
            }
        });
    }

    public void addOnCameraMoveListener(MapboxMap.OnCameraMoveListener onCameraMoveListener)
    {
        this.onCameraMoveListener = onCameraMoveListener;
    }

    public void removeOnCameraMoveListener()
    {
        this.onCameraMoveListener = null;
    }

    public boolean isMoveDisabled() {
        return false;
    }
    private void disableMove()
    {
        mapboxMap.getUiSettings().setQuickZoomGesturesEnabled(false);
        mapboxMap.getUiSettings().setZoomGesturesEnabled(false);
        mapboxMap.getUiSettings().setScrollGesturesEnabled(false);
        mapboxMap.getUiSettings().setRotateGesturesEnabled(false);
        mapboxMap.getUiSettings().setTiltGesturesEnabled(false);

        mapboxMap.getUiSettings().setAllGesturesEnabled(false);
    }

    public void enableClickPoints(MapboxMap.OnMapClickListener listener)
    {
        if (mapboxMap != null) {
            mapboxMap.addOnMapClickListener(listener);
        }
        onMapClickListener = listener;
    }

    public void drawPoint(Point p1)
    {
        drawRoute(p1, null, null);
    }
    public void drawPoint(Point p1, Drawable d1)
    {
        drawRoute(p1, d1, null, null, true, true, null, null);
    }

    public void drawRoute(Point p1, Point p2, Point pExtra)
    {
        drawRoute(p1, p2, true, pExtra);
    }

    public void drawRoute(Point p1, Point p2, boolean animate, Point pExtra)
    {
        drawRoute(p1, p2, animate, false, pExtra);
    }

    public void drawRoute(Point p1, Point p2, boolean animate, boolean zoomOrigin, Point pExtra)
    {
        try
        {
            Activity activity = getActivity();
            if (activity != null)
            {
                Drawable d1 = activity.getResources().getDrawable(R.drawable.icon_user_location);
                Drawable d2 = activity.getResources().getDrawable(R.drawable.icon_grua);
                Drawable dExtra = activity.getResources().getDrawable(R.drawable.icon_devices);
                drawRoute(p1, d1, p2, d2, animate, zoomOrigin, pExtra, dExtra);
            }
            else
            {
                LogUtil.logE(TAG, "drawRoute: Activity is null");
            }
        }
        catch (Exception e)
        {
            LogUtil.logE(TAG, "drawRoute: " + e.getMessage());
        }
    }

    public void drawRoute(Point p1, Drawable d1, Point p2, Drawable d2, boolean animate, boolean zoomOrigin, Point pExtra, Drawable dExtra)
    {
        try
        {
            origin = p1;
            destination = p2;
            extraPoint = pExtra;

            if (mapboxMap != null)
            {
                loaded = true;
                if (destination == null)
                {
                    Style.Builder cStyle = new Style.Builder().fromUri(Style.DARK)

                            // Set up the image, source, and layer for the person icon,
                            // which is where all of the routes will start from
                            .withImage(PERSON_ICON_ID, BitmapUtils.getBitmapFromDrawable(d1))
                            .withSource(new GeoJsonSource(PERSON_SOURCE_ID,
                                    Feature.fromGeometry(origin)))
                            .withLayer(new SymbolLayer(PERSON_LAYER_ID, PERSON_SOURCE_ID).withProperties(
                                    iconImage(PERSON_ICON_ID),
                                    iconSize(0.3f),
                                    iconAllowOverlap(true),
                                    iconIgnorePlacement(true)
                            ));


                    if (extraPoint != null) {
                        cStyle.withImage(EXTRA_ICON_ID, BitmapUtils.getBitmapFromDrawable(dExtra))
                                .withSource(new GeoJsonSource(EXTRA_SOURCE_ID,
                                        Feature.fromGeometry(extraPoint)))
                                .withLayer(new SymbolLayer(EXTRA_LAYER_ID, EXTRA_SOURCE_ID).withProperties(
                                        iconImage(EXTRA_ICON_ID),
                                        iconSize(0.3f),
                                        iconAllowOverlap(true),
                                        iconIgnorePlacement(true)
                                ));
                    }

                    mapboxMap.setStyle(cStyle, new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {

                            if (animate)
                            {
                                cameraPosition = new CameraPosition.Builder()
                                        .target(new LatLng(origin.latitude(), origin.longitude()))
                                        .zoom(10)
                                        .build();
                                mapboxMap.setCameraPosition(cameraPosition);
                            }
                        }
                    });
                }
                else
                {
                    Style.Builder cStyle = new Style.Builder().fromUri(Style.DARK)

                            // Set up the image, source, and layer for the person icon,
                            // which is where all of the routes will start from
                            .withImage(PERSON_ICON_ID, BitmapUtils.getBitmapFromDrawable(d1))
                            .withSource(new GeoJsonSource(PERSON_SOURCE_ID,
                                    Feature.fromGeometry(origin)))
                            .withLayer(new SymbolLayer(PERSON_LAYER_ID, PERSON_SOURCE_ID).withProperties(
                                    iconImage(PERSON_ICON_ID),
                                    iconSize(0.3f),
                                    iconAllowOverlap(true),
                                    iconIgnorePlacement(true)
                            ))


                            // Set up the image, source, and layer for the potential destination markers
                            .withImage(SYMBOL_ICON_ID, BitmapUtils.getBitmapFromDrawable(d2))
                            .withSource(new GeoJsonSource(MARKER_SOURCE_ID, Feature.fromGeometry(destination)))
                            .withLayer(new SymbolLayer(LAYER_ID, MARKER_SOURCE_ID).withProperties(
                                    iconImage(SYMBOL_ICON_ID),
                                    iconSize(0.5f),
                                    iconAllowOverlap(true),
                                    iconIgnorePlacement(true),
                                    iconOffset(new Float[]{0f, -4f})
                            ))

                            // Set up the source and layer for the direction route LineLayer
                            .withSource(new GeoJsonSource(DASHED_DIRECTIONS_LINE_LAYER_SOURCE_ID))
                            .withLayerBelow(
                                    new LineLayer(DASHED_DIRECTIONS_LINE_LAYER_ID, DASHED_DIRECTIONS_LINE_LAYER_SOURCE_ID)
                                            .withProperties(
                                                    lineWidth(4f),
                                                    lineJoin(LINE_JOIN_ROUND),
                                                    lineColor(Utils.getColor(getContext(), android.R.color.white))
                                            ), PERSON_LAYER_ID);

                    if (extraPoint != null) {
                        cStyle.withImage(EXTRA_ICON_ID, BitmapUtils.getBitmapFromDrawable(dExtra))
                                .withSource(new GeoJsonSource(EXTRA_SOURCE_ID,
                                        Feature.fromGeometry(extraPoint)))
                                .withLayer(new SymbolLayer(EXTRA_LAYER_ID, EXTRA_SOURCE_ID).withProperties(
                                        iconImage(EXTRA_ICON_ID),
                                        iconSize(0.3f),
                                        iconAllowOverlap(true),
                                        iconIgnorePlacement(true)
                                ));
                    }

                    mapboxMap.setStyle(cStyle, new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {

                            if (animate)
                            {
                                if (zoomOrigin)
                                {
                                    cameraPosition = new CameraPosition.Builder()
                                            .target(new LatLng(origin.latitude(), origin.longitude()))
                                            .zoom(10)
                                            .build();
                                    mapboxMap.setCameraPosition(cameraPosition);
                                }
                                else
                                {
                                    double midlat = (origin.latitude() + destination.latitude())/2;
                                    double midlng = (origin.longitude() + destination.longitude())/2;

                                    cameraPosition = new CameraPosition.Builder()
                                            .target(new LatLng(midlat, midlng))
                                            .zoom(6)
                                            .build();
                                    mapboxMap.setCameraPosition(cameraPosition);
                                }
                            }

                            getRoute(origin, destination);
                        }
                    });
                }
            }
        }
        catch (Exception e)
        {
            LogUtil.logE(TAG, e.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        // Cancel the Directions API request
        if (client != null) {
            client.cancelCall();
            try {
                Hud hud = getView().findViewById(R.id.hud);
                if (hud != null) {
                    hideHud();
                }
            } catch (Exception e) {}
        }
        mapView.onDestroy();

        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public void centerMap()
    {
        if (cameraPosition != null && mapboxMap != null)
        {
            //mapboxMap.setCameraPosition(cameraPosition);

            if (origin != null && destination != null)
            {
                double midlat = (origin.latitude() + destination.latitude())/2;
                double midlng = (origin.longitude() + destination.longitude())/2;

                cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(midlat, midlng))
                        .zoom(10)
                        .build();
                mapboxMap.setCameraPosition(cameraPosition);
            }
            else if (origin != null)
            {
                cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(origin.latitude(), origin.longitude()))
                        .zoom(10)
                        .build();
                mapboxMap.setCameraPosition(cameraPosition);
            }
            else
            {
                CameraPosition cameraPosition1 = new CameraPosition.Builder()
                        .target(cameraPosition.target)
                        //mantenemos el zoom .zoom(cameraPosition.zoom)
                        .build();
                mapboxMap.setCameraPosition(cameraPosition1);
            }
        }
    }

    private void getRoute(Point origin, Point destination) {
        client = MapboxDirections.builder()
                .origin(origin)
                .destination(destination)
                .overview(DirectionsCriteria.OVERVIEW_SIMPLIFIED)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .accessToken(IncidenceLibraryManager.instance.getMapboxAccessToken())
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                // You can get the generic HTTP info about the response
                //Timber.d("Response code: " + response.code());
                if (response.body() == null) {
                    Log.e("xavi", "No routes found, make sure you set the right user and access token.");
                    return;
                } else if (response.body().routes().size() < 1) {
                    Log.e("xavi", "No routes found");
                    return;
                }

                // Get the directions route
                currentRoute = response.body().routes().get(0);
                drawNavigationPolylineRoute(currentRoute);
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Log.e("xavi", "Error: " + throwable.getMessage());
                //Toast.makeText(DirectionsActivity.this, "Error: " + throwable.getMessage(),
                //      Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void drawNavigationPolylineRoute(final DirectionsRoute route) {
        if (mapboxMap != null && route != null ) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {

                    List<Feature> directionsRouteFeatureList = new ArrayList<>();
                    LineString lineString = LineString.fromPolyline(route.geometry(), PRECISION_6);
                    List<Point> lineStringCoordinates = lineString.coordinates();
                    for (int i = 0; i < lineStringCoordinates.size(); i++) {
                        directionsRouteFeatureList.add(Feature.fromGeometry(
                                LineString.fromLngLats(lineStringCoordinates)));
                    }
                    dashedLineDirectionsFeatureCollection =
                            FeatureCollection.fromFeatures(directionsRouteFeatureList);
                    GeoJsonSource source = style.getSourceAs(DASHED_DIRECTIONS_LINE_LAYER_SOURCE_ID);
                    if (source != null) {
                        source.setGeoJson(dashedLineDirectionsFeatureCollection);
                    }
                }
            });
        }
    }
}
