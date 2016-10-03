package com.pl.azurestorageexplorer.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pl.azurestorageexplorer.R;
import com.pl.azurestorageexplorer.asynctask.CreateBlobContainerAsyncTask;
import com.pl.azurestorageexplorer.asynctask.interfaces.IAsyncTaskCallback;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddBlobContainerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class AddBlobContainerFragment extends DialogFragment implements IAsyncTaskCallback<String> {

    private OnFragmentInteractionListener mListener;
    private EditText blobContainerName;
    private ProgressBar progressBar;

    public AddBlobContainerFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_blob_container_layout, null);
        progressBar = (ProgressBar) view.findViewById(R.id.create_blob_container_progress_bar);
        blobContainerName = (EditText) view.findViewById(R.id.blob_container_name_edit_text);
        blobContainerName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == EditorInfo.IME_ACTION_GO) {
                    createBlobContainer();
                }

                return true;
            }
        });
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setTitle(getResources().getString(R.string.create_container))
                .setView(view)
                // Add action buttons
                .setPositiveButton(getResources().getString(R.string.add), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        createBlobContainer();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }

    private void createBlobContainer() {
        final String containerName = blobContainerName.getText().toString().trim().replace(" ", "").toLowerCase();
        if (containerName.equals("")) {
            blobContainerName.setError(getString(R.string.container_name_validation));
            return;
        }

        Bundle args = getArguments();
        if (args == null || !args.containsKey("storageAccount") || !args.containsKey("storageKey")) {
            Toast.makeText(getContext(), "Storage credentials missing. :(", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressBar();
        blobContainerName.setEnabled(false);
        final CreateBlobContainerAsyncTask createBlobContainerAsyncTask = new CreateBlobContainerAsyncTask(this);
        createBlobContainerAsyncTask.execute(getArguments().getString("storageAccount"), getArguments().getString("storageKey"), containerName);
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
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

    @Override
    public void finished(String result) {
        hideProgressBar();
        if (mListener != null) {
            mListener.onBlobContainerCreated(result);
        }

        dismiss();
    }

    @Override
    public void failed(String exceptionMessage) {
        hideProgressBar();
        Toast.makeText(getContext(), exceptionMessage, Toast.LENGTH_SHORT).show();
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
        void onBlobContainerCreated(String containerName);
    }
}
