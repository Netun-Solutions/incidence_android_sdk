package es.incidence.core.fragment.common;

import static com.e510.commons.utils.LogUtil.makeLogTag;
import static com.mapbox.core.constants.Constants.PRECISION_5;
import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.maps.extension.style.expressions.generated.Expression.concat;
import static com.mapbox.maps.extension.style.expressions.generated.Expression.get;
import static com.mapbox.maps.extension.style.expressions.generated.Expression.gt;
import static com.mapbox.maps.extension.style.expressions.generated.Expression.literal;
import static com.mapbox.maps.extension.style.expressions.generated.Expression.rgb;
import static com.mapbox.maps.extension.style.expressions.generated.Expression.subtract;
import static com.mapbox.maps.extension.style.expressions.generated.Expression.toNumber;
import static com.mapbox.maps.extension.style.sources.generated.GeoJsonSourceKt.geoJsonSource;
import static com.mapbox.maps.viewannotation.ViewAnnotationOptionsKtxKt.geometry;
/*
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
*/
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableKt;

import com.e510.commons.utils.LogUtil;
import com.e510.commons.utils.Utils;
import com.e510.commons.view.Hud;
import com.e510.incidencelibrary.R;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.directions.v5.models.RouteOptions;
import com.mapbox.bindgen.Expected;
import com.mapbox.bindgen.Value;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.LayerPosition;
import com.mapbox.maps.MapView;
import com.mapbox.maps.MapboxMap;
import com.mapbox.maps.Style;
import com.mapbox.maps.StyleManager;
import com.mapbox.maps.StyleObjectInfo;
import com.mapbox.maps.extension.style.StyleContract;
import com.mapbox.maps.extension.style.StyleExtensionImpl;
import com.mapbox.maps.extension.style.StyleExtensionImplKt;
import com.mapbox.maps.extension.style.expressions.generated.Expression;
import com.mapbox.maps.extension.style.image.ImageExtensionImpl;
import com.mapbox.maps.extension.style.image.ImageNinePatchExtensionImpl;
import com.mapbox.maps.extension.style.image.ImageUtils;
import com.mapbox.maps.extension.style.layers.Layer;
import com.mapbox.maps.extension.style.layers.generated.CircleLayer;
import com.mapbox.maps.extension.style.layers.generated.LineLayer;
import com.mapbox.maps.extension.style.layers.generated.RasterLayer;
import com.mapbox.maps.extension.style.layers.generated.SymbolLayer;
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap;
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin;
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor;
import com.mapbox.maps.extension.style.sources.Source;
import com.mapbox.maps.extension.style.sources.SourceUtils;
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource;
import com.mapbox.maps.extension.style.sources.generated.ImageSource;
import com.mapbox.maps.plugin.MapPlugin;
import com.mapbox.maps.plugin.Plugin;
import com.mapbox.maps.plugin.gestures.GesturesUtils;
import com.mapbox.maps.plugin.scalebar.ScaleBarPlugin;
/*
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
*/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import es.incidence.core.fragment.IFragment;
import es.incidence.library.IncidenceLibraryManager;
import kotlin.Pair;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;

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

    private MapboxMap mapboxMap;
    private Point origin;
    private Point destination;
    private Point extraPoint;
    private boolean loaded;

    private DirectionsRoute currentRoute;
    private MapboxDirections client;
    private FeatureCollection dashedLineDirectionsFeatureCollection;
/*
    private MapboxMap.OnCameraMoveListener onCameraMoveListener;
    private CameraPosition cameraPosition;
    private MapboxMap.OnMapClickListener onMapClickListener;
*/
    public void overrideOnCreateView(View view, Bundle savedInstanceState)
    {

        mapView = view.findViewById(R.id.mapView);
        //mapView.onCreate(savedInstanceState);
        MapBoxFragment.this.mapboxMap = mapView.getMapboxMap();
        ScaleBarPlugin pluginScaleBar = mapView.getPlugin(Plugin.MAPBOX_SCALEBAR_PLUGIN_ID);
        if (pluginScaleBar != null) {
            pluginScaleBar.setEnabled(false);
        }

        if (isMoveDisabled()) {
            disableMove();
        }

        MapBoxFragment.this.mapboxMap.loadStyle(Style.DARK);

        if (origin != null && !loaded)
        {
            drawRoute(origin, destination, extraPoint);
        }

        /*
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
        */
    }
    /*
    public void addOnCameraMoveListener(MapboxMap.OnCameraMoveListener onCameraMoveListener)
    {
        this.onCameraMoveListener = onCameraMoveListener;
    }

    public void removeOnCameraMoveListener()
    {
        this.onCameraMoveListener = null;
    }
    */
    public boolean isMoveDisabled() {
        return false;
    }
    private void disableMove()
    {
        /*
        mapboxMap.getUiSettings().setQuickZoomGesturesEnabled(false);
        mapboxMap.getUiSettings().setZoomGesturesEnabled(false);
        mapboxMap.getUiSettings().setScrollGesturesEnabled(false);
        mapboxMap.getUiSettings().setRotateGesturesEnabled(false);
        mapboxMap.getUiSettings().setTiltGesturesEnabled(false);

        mapboxMap.getUiSettings().setAllGesturesEnabled(false);
        */
    }
    /*
    public void enableClickPoints(MapboxMap.OnMapClickListener listener)
    {
        if (mapboxMap != null) {
            mapboxMap.addOnMapClickListener(listener);
        }
        onMapClickListener = listener;
    }
    */
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

            destination = Point.fromLngLat(1.3915057, 41.1419304);

            if (mapboxMap != null)
            {
                loaded = true;
                if (destination == null)
                {
                    final Bitmap bitmap = getBitmapFromDrawable(d1);

                    StyleExtensionImpl.Builder builder = new StyleExtensionImpl.Builder(Style.DARK);
                    builder.addSource(new GeoJsonSource.Builder(PERSON_SOURCE_ID)
                            .feature(Feature.fromGeometry(origin))
                            .build());
                    builder.addImage(new ImageExtensionImpl.Builder(PERSON_ICON_ID, bitmap).build());
                    builder.addLayer(new SymbolLayer(PERSON_LAYER_ID, PERSON_SOURCE_ID)
                            .iconImage(PERSON_ICON_ID)
                            .iconSize(0.3f)
                            .iconAllowOverlap(true).
                            iconIgnorePlacement(true));

                    if (extraPoint != null) {
                        final Bitmap bitmapExtra = getBitmapFromDrawable(dExtra);
                        builder.addImage(new ImageExtensionImpl.Builder(EXTRA_ICON_ID, bitmapExtra).build());
                        builder.addSource(new GeoJsonSource.Builder(EXTRA_SOURCE_ID)
                                .feature(Feature.fromGeometry(extraPoint))
                                .build());
                        builder.addLayer(new SymbolLayer(EXTRA_LAYER_ID, EXTRA_SOURCE_ID)
                                .iconImage(EXTRA_ICON_ID)
                                .iconSize(0.3f)
                                .iconAllowOverlap(true).
                                iconIgnorePlacement(true));
                    }

                    StyleContract.StyleExtension cStyle = builder.build();
                    mapboxMap.loadStyle(cStyle, style -> {
                        if (animate)
                        {
                            mapboxMap.setCamera(new CameraOptions.Builder().center(origin).zoom(10.0).build());
                        }
                    });
                    //GesturesUtils.addOnMapClickListener(mapboxMap, this);
                }
                else
                {
                    final Bitmap bitmapOrig = getBitmapFromDrawable(d1);
                    final Bitmap bitmapDest = getBitmapFromDrawable(d2);

                    StyleExtensionImpl.Builder builder = new StyleExtensionImpl.Builder(Style.DARK);
                    builder.addSource(new GeoJsonSource.Builder(PERSON_SOURCE_ID)
                            .feature(Feature.fromGeometry(origin))
                            .build());
                    builder.addImage(new ImageExtensionImpl.Builder(PERSON_ICON_ID, bitmapOrig).build());
                    builder.addLayer(new SymbolLayer(PERSON_LAYER_ID, PERSON_SOURCE_ID)
                            .iconImage(PERSON_ICON_ID)
                            .iconSize(0.3f)
                            .iconAllowOverlap(true).
                            iconIgnorePlacement(true));

                    builder.addSource(new GeoJsonSource.Builder(MARKER_SOURCE_ID)
                            .feature(Feature.fromGeometry(destination))
                            .build());
                    builder.addImage(new ImageExtensionImpl.Builder(SYMBOL_ICON_ID, bitmapDest).build());
                    builder.addLayer(new SymbolLayer(LAYER_ID, MARKER_SOURCE_ID)
                            .iconImage(SYMBOL_ICON_ID)
                            .iconSize(0.5f)
                            .iconAllowOverlap(true)
                            .iconIgnorePlacement(true)
                            .iconOffset(Arrays.asList(new Double[]{0d, -4d})));

                    builder.addSource(new GeoJsonSource.Builder(DASHED_DIRECTIONS_LINE_LAYER_SOURCE_ID)
                            .build());

                    LayerPosition layerPosition = new LayerPosition(null, PERSON_LAYER_ID, null);

                    LineLayer lineLayer = new LineLayer(DASHED_DIRECTIONS_LINE_LAYER_ID, DASHED_DIRECTIONS_LINE_LAYER_SOURCE_ID)
                            .lineWidth(4f)
                            .lineCap(LineCap.ROUND)
                            .lineJoin(LineJoin.ROUND)
                            .lineOpacity(0.7)
                            .lineWidth(8.0)
                            .lineJoin(LineJoin.ROUND)
                            .lineColor(Utils.getColor(getContext(), android.R.color.white));

                    //builder.addLayerAtPosition(new Pair<Layer, LayerPosition>(lineLayer, layerPosition));

                    builder.addLayer(lineLayer);

                    if (extraPoint != null) {
                        final Bitmap bitmapExtra = getBitmapFromDrawable(dExtra);
                        builder.addImage(new ImageExtensionImpl.Builder(EXTRA_ICON_ID, bitmapExtra).build());
                        builder.addSource(new GeoJsonSource.Builder(EXTRA_SOURCE_ID)
                                .feature(Feature.fromGeometry(extraPoint))
                                .build());
                        builder.addLayer(new SymbolLayer(EXTRA_LAYER_ID, EXTRA_SOURCE_ID)
                                .iconImage(EXTRA_ICON_ID)
                                .iconSize(0.3f)
                                .iconAllowOverlap(true).
                                iconIgnorePlacement(true));
                    }

                    StyleContract.StyleExtension cStyle = builder.build();
                    mapboxMap.loadStyle(cStyle, style -> {
                        if (animate)
                        {
                            mapboxMap.setCamera(new CameraOptions.Builder().center(origin).zoom(10.0).build());
                        }

                        if (animate)
                        {
                            if (zoomOrigin)
                            {
                                mapboxMap.setCamera(new CameraOptions.Builder().center(origin).zoom(10.0).build());
                            }
                            else
                            {
                                double midlat = (origin.latitude() + destination.latitude())/2;
                                double midlng = (origin.longitude() + destination.longitude())/2;

                                mapboxMap.setCamera(new CameraOptions.Builder().center(Point.fromLngLat(midlng, midlat)).zoom(6.0).build());
                            }
                        }

                        getRoute(origin, destination);
                    });
                    //GesturesUtils.addOnMapClickListener(mapboxMap, this);
                }
            }
        }
        catch (Exception e)
        {
            LogUtil.logE(TAG, e.getMessage());
        }
    }
    /*
    private void addImage(Style style, Drawable drawable, String imageId) {
        //final Bitmap bitmap = DrawableKt.toBitmap(drawable, 64, 64, null);
        final Bitmap bitmap = DrawableKt.toBitmap(drawable);
        ImageUtils.addImage(style, delegate -> delegate.addImage(imageId, bitmap));
    }
    */

    public static Bitmap getBitmapFromDrawable(@Nullable Drawable sourceDrawable) {
        if (sourceDrawable == null) {
            return null;
        }

        if (sourceDrawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) sourceDrawable).getBitmap();
        } else {
            //copying drawable object to not manipulate on the same reference
            Drawable.ConstantState constantState = sourceDrawable.getConstantState();
            if (constantState == null) {
                return null;
            }
            Drawable drawable = constantState.newDrawable().mutate();

            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
    }

    /*
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
    */
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
        //mapView.onDestroy();

        super.onDestroy();
    }
    /*
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    */

    public void centerMap()
    {
        /*
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

         */
    }

    private void getRoute(Point origin, Point destination) {
        client = MapboxDirections.builder()
                .routeOptions(RouteOptions.builder()
                        .coordinatesList(Arrays.asList(origin, destination))
                        .overview(DirectionsCriteria.OVERVIEW_FULL)
                        .profile(DirectionsCriteria.PROFILE_DRIVING)
                        .build())
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

                    Feature directionsRouteFeature = Feature.fromGeometry(LineString.fromPolyline(route.geometry(), PRECISION_5));

                    List<Feature> directionsRouteFeatureList = new ArrayList<>();
                    LineString lineString = LineString.fromPolyline(route.geometry(), PRECISION_5);
                    List<Point> lineStringCoordinates = lineString.coordinates();
                    for (int i = 0; i < lineStringCoordinates.size(); i++) {
                        directionsRouteFeatureList.add(Feature.fromGeometry(
                                LineString.fromLngLats(lineStringCoordinates)));
                    }
                    dashedLineDirectionsFeatureCollection =
                            FeatureCollection.fromFeatures(directionsRouteFeatureList);
                    /*
                    Expected<String, Value> sp = style.getStyleSourceProperties(DASHED_DIRECTIONS_LINE_LAYER_SOURCE_ID);

                    Value v = sp.getValue();
                    Object c = v.getContents();

                    Expected<String, Value> aa = style.addGeoJSONSourceFeatures() getStyleLayerProperties(DASHED_DIRECTIONS_LINE_LAYER_ID);
                    Value vaa = aa.getValue();
                    HashMap contents = (HashMap) vaa.getContents();
                    source = contents.get("source");
                    int a = 5;
                    a = a +4;
                    */
                    /*GeoJsonSource source = style.getSourceAs(DASHED_DIRECTIONS_LINE_LAYER_SOURCE_ID);
                    if (source != null) {
                        source.setGeoJson(dashedLineDirectionsFeatureCollection);
                    }
                    */
                    /*
                    List<StyleObjectInfo> sources = style.getStyleSources();
                    for (StyleObjectInfo i: sources) {
                        if (i.getId().equals(DASHED_DIRECTIONS_LINE_LAYER_SOURCE_ID)) {
                            int b = 0;
                        } else {
                            int c = 9;
                        }
                    }
                    if (dashedLineDirectionsFeatureCollection.features() != null) {
                        style.addGeoJSONSourceFeatures(DASHED_DIRECTIONS_LINE_LAYER_SOURCE_ID, "", dashedLineDirectionsFeatureCollection.features());
                    }*/

                    GeoJsonSource source = (GeoJsonSource) SourceUtils.getSource(style, DASHED_DIRECTIONS_LINE_LAYER_SOURCE_ID);
                    if (source != null) {
                        //source.setGeoJson(dashedLineDirectionsFeatureCollection);
                        source.featureCollection(dashedLineDirectionsFeatureCollection);
                    }
                    //List<Feature> fea = dashedLineDirectionsFeatureCollection.features();
                    //style.updateGeoJSONSourceFeatures(DASHED_DIRECTIONS_LINE_LAYER_SOURCE_ID, "", fea);

                    // SourceUtils.addSource(style, geoJsonSource);


                    // GeoJsonSource source = SourceUtils.getSourceAs(style, DASHED_DIRECTIONS_LINE_LAYER_SOURCE_ID);
                    //if (source != null) {
                    //    source.setGeoJson(dashedLineDirectionsFeatureCollection);
                    //}
                }
            });


        }
    }
}
