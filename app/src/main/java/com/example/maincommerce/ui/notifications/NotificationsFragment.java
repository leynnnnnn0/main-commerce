package com.example.maincommerce.ui.notifications;

import com.example.maincommerce.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maincommerce.adapters.OrderAdapter;
import com.example.maincommerce.databinding.FragmentNotificationsBinding;
import com.example.maincommerce.models.OrderModel;
import com.example.maincommerce.services.Dialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

    RecyclerView ordersRecyclerView;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    ArrayList<OrderModel> orderModelArrayList;
    OrderAdapter orderAdapter;
    Dialog dialog;
    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dialog = new Dialog(requireContext());
        ordersRecyclerView = root.findViewById(R.id.ordersRecyclerView);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        orderModelArrayList = new ArrayList<>();
        orderAdapter = new OrderAdapter(requireContext(), orderModelArrayList);

        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        ordersRecyclerView.setHasFixedSize(true);
        ordersRecyclerView.setAdapter(orderAdapter);

        dialog.showDialog();

        if (firebaseAuth.getCurrentUser() != null) {
            String userId = firebaseAuth.getCurrentUser().getUid();
            firebaseFirestore.collection("Orders")
                    .document(userId)
                    .collection("User Orders")
                    .orderBy("orderDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            orderModelArrayList.clear();
                            for (com.google.firebase.firestore.QueryDocumentSnapshot document : task.getResult()) {
                                OrderModel orderModel = document.toObject(OrderModel.class);
                                orderModelArrayList.add(orderModel);
                            }
                            dialog.dismiss();
                            orderAdapter.notifyDataSetChanged();

                        } else {

                            Toast.makeText(requireContext(), "Failed to load orders", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(requireContext(), "No Orders", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }






        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}