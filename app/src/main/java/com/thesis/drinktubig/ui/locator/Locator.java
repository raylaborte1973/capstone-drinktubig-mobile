package com.thesis.drinktubig.ui.locator;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.squareup.picasso.Picasso;
import com.thesis.drinktubig.R;
import com.thesis.drinktubig.ui.storeproducts.ProductOrders;
import com.thesis.drinktubig.ui.storeproducts.StoreProducts;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

public class Locator extends Fragment implements OnMapReadyCallback {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference storeRef = db.collection("StoreList");
    private MapView mapView;
    private MaterialCardView mapDetailsLayout;
    private ImageView mapStoreImage;
    private TextView mapStoreName, mapStoreLocation, mapStoreContact;

    private final HashMap<String, MarkerOptions> storeMarkerList = new HashMap<>();
    private String storeID;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Mapbox.getInstance(requireContext(), getString(R.string.mapbox_access_token));
        View view = inflater.inflate(R.layout.fragment_locator, container, false);

        mapStoreImage = view.findViewById(R.id.mapStoreImage);
        mapStoreName = view.findViewById(R.id.mapStoreName);
        mapStoreLocation = view.findViewById(R.id.mapStoreLocation);
        mapStoreContact = view.findViewById(R.id.mapStoreContact);
        mapDetailsLayout = view.findViewById(R.id.mapDetailsLayout);
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        mapDetailsLayout.setOnClickListener(view1 -> storeRef.document(storeID).get()
                .addOnSuccessListener(doc ->{
                    String StoreName = doc.getString("StoreName");
                    String StoreOwner_ID = doc.getString("StoreOwner_ID");
                    Intent intent = new Intent(getActivity(), StoreProducts.class);
                    intent.putExtra("StoreID", storeID);
                    intent.putExtra("StoreName", StoreName);
                    intent.putExtra("StoreOwner_ID", StoreOwner_ID);
                    startActivity(intent);
                }));

        return view;
    }

    @SuppressLint("Range")
    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        IconFactory iconFactory = IconFactory.getInstance(requireActivity());
        Icon icon = iconFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.mapbox_drink_tubig));
        List<Marker> markers = new ArrayList<>();
        HashMap<Marker, String> revMap = new HashMap<>();
        HashMap<String, String> storeNames = new HashMap<>();

        storeRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot doc : queryDocumentSnapshots){
                String storeStatus = doc.getString("StoreStatus");
                String storeName = doc.getString("StoreName");
                String storeLat = doc.getString("StoreLat");
                String storeLong = doc.getString("StoreLong");
                if(Objects.requireNonNull(storeStatus).equals("Verified")){
                    LatLng latLng = new LatLng();
                    latLng.setLatitude(Double.parseDouble(Objects.requireNonNull(storeLat)));
                    latLng.setLongitude(Double.parseDouble(Objects.requireNonNull(storeLong)));
                    latLng.setAltitude(0.0d);
                    MarkerOptions marker = new MarkerOptions().position(latLng);
                    storeMarkerList.put(doc.getId(), marker);
                    storeNames.put(doc.getId(), storeName);
                }
            }

            mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41")
                    , style -> {
                        for(String key: storeMarkerList.keySet()){
                            if(storeNames.containsKey(key)){
                                Marker marker = mapboxMap.addMarker(Objects.requireNonNull(storeMarkerList.get(key)));
                                marker.setIcon(icon);
                                marker.setTitle(storeNames.get(key));
                                markers.add(marker);
                                revMap.put(marker, key);
                            }
                        }
                    });

            mapboxMap.setOnMarkerClickListener(marker -> {
                for (Marker storeMark : markers){
                    if(marker.getPosition().equals(storeMark.getPosition())){
                        storeID = revMap.get(storeMark);
                        showDetails(storeID);
                    }
                }
                return false;
            });

            mapboxMap.addOnMapClickListener(point -> {
                if (mapDetailsLayout.getVisibility() == View.VISIBLE){
                    mapDetailsLayout.setVisibility(View.INVISIBLE);
                }
                return false;
            });

        });

    }

    private void showDetails(String storeID){
        storeRef.document(storeID).get()
                .addOnSuccessListener(doc ->{
                    String storeImage = doc.getString("StoreImage");
                    String storeName = doc.getString("StoreName");
                    String storeLocation = doc.getString("StoreLocation");
                    String storeSitio = doc.getString("StoreSitio");
                    String storeContact = doc.getString("StoreContactNumber");

                    Uri uri = Uri.parse(storeImage);
                    Picasso.get().load(uri).into(mapStoreImage);
                    mapStoreName.setText(storeName);
                    mapStoreLocation.setText(storeLocation + " Sitio " + storeSitio);
                    mapStoreContact.setText(storeContact);
                    mapDetailsLayout.setVisibility(View.VISIBLE);
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
        //Generate all Long,Lat
    }
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }
    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }
}