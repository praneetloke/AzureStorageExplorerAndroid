package com.pl.azurestorageexplorer.fragments;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.pl.azurestorageexplorer.AzureStorageExplorerApplication;
import com.pl.azurestorageexplorer.R;
import com.pl.azurestorageexplorer.storage.AzureStorageAccountSQLiteHelper;
import com.pl.azurestorageexplorer.storage.models.AzureStorageAccount;

import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddAccountDialogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class AddAccountDialogFragment extends DialogFragment {

    private OnFragmentInteractionListener mListener;
    private EditText storageAccountName;
    private EditText storageKey;

    public AddAccountDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_account, null);
        storageAccountName = (EditText) view.findViewById(R.id.storageAccountName);
        storageKey = (EditText) view.findViewById(R.id.storageKey);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(getResources().getString(R.string.add), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //validate the storage account name
                        String name = storageAccountName.getText().toString().toLowerCase();
//                        if (!isValidStorageAccountName(name)) {
//                            storageAccountName.setError(getResources().getString(R.string.invalid_name));
//                            return;
//                        }
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(AzureStorageAccountSQLiteHelper.NAME, name);
                        contentValues.put(AzureStorageAccountSQLiteHelper.KEY, storageKey.getText().toString());
                        long insertedId = AzureStorageExplorerApplication
                                .getCustomSQLiteHelper()
                                .getWritableDatabase()
                                .insert(AzureStorageAccountSQLiteHelper.TABLE_NAME, null, contentValues);

                        onAccountSaved(new AzureStorageAccount(insertedId, name, storageKey.getText().toString(), "", ""));
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }

    /**
     * https://azure.microsoft.com/en-us/documentation/articles/storage-java-how-to-use-blob-storage/
     *
     * @param storageAccountName
     * @return
     */
    private boolean isValidStorageAccountName(String storageAccountName) {
        //Container names must start with a letter or number, and can contain only letters, numbers, and the dash (-) character.
        if (!Pattern.compile("^[a-z0-9]").matcher(storageAccountName).matches()) {
            return false;
        }

        //consecutive dashes are not permitted in container names.
        if (Pattern.compile("-{2,}").matcher(storageAccountName).matches()) {
            return false;
        }

        //Container names must be from 3 through 63 characters long.
        return !(3 > storageAccountName.length() || storageAccountName.length() > 63);

    }

    public void onAccountSaved(AzureStorageAccount account) {
        if (mListener != null) {
            mListener.onStorageAccountAdded(account);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onStorageAccountAdded(AzureStorageAccount account);
    }
}
