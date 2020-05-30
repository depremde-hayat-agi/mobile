package com.deha.app.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.deha.app.R;
import com.deha.app.databinding.FragmentCreateUserBinding;
import com.deha.app.di.DI;
import com.deha.app.model.UserModel;

import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateUserFragment extends Fragment {

  private FragmentCreateUserBinding binding;

  private CreateUserInterface createUserInterface;

  private ProgressDialog progressDialog;

  public static CreateUserFragment newInstance() {
    CreateUserFragment fragment = new CreateUserFragment();
    return fragment;
  }

  public CreateUserFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_user, container, false);
    binding.setLifecycleOwner(this);
    binding.button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        boolean valid = true;
        if (binding.nameField.getText() == null || binding.nameField.getText().toString().isEmpty()) {
          binding.nameLayout.setError("Lütfen bu alanı boş bırakmayın");
          valid = false;
        } else {
          binding.nameLayout.setError(null);
        }

        if (binding.phoneField.getText() == null || binding.phoneField.getText().toString().isEmpty()) {
          binding.phoneLayout.setError("Lütfen bu alanı boş bırakmayın");
          valid = false;
        } else {
          binding.phoneLayout.setError(null);
        }

        if (valid) {
          progressDialog = ProgressDialog.show(getContext(), "Yükleniyor", "");
          DI.getFusedLocationClient().getLastLocation().addOnSuccessListener(location -> {
            DI.getLocalStorageService().setUser(new UserModel(UUID.randomUUID().toString(), binding.nameField.getText().toString(), binding.phoneField.getText().toString(), location.getLatitude(), location.getLongitude(), 0, ""));
            progressDialog.dismiss();
            createUserInterface.userCreated();
          }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            new AlertDialog.Builder(getContext()).setTitle("Konum bulunamadı").setMessage("Lütfen konum ayarlarının açık olduğundan emin olun").setPositiveButton("Tamam", (dialog, which) -> {

            }).show();
          });
        }
      }
    });
    return binding.getRoot();
  }

  public void setCreateUserInterface(CreateUserInterface createUserInterface) {
    this.createUserInterface = createUserInterface;
  }

  public interface CreateUserInterface {
    void userCreated();
  }
}
