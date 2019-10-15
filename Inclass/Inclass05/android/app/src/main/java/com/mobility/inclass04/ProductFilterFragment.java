package com.mobility.inclass04;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.switchmaterial.SwitchMaterial;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProductFilterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ProductFilterFragment extends DialogFragment {

    private OnFragmentInteractionListener mListener;

    int position = 0;
    boolean smartFiler;
    ListView mList;
    SwitchMaterial mSwitch;
    AlertDialog alertDialog;

    String[] list;
    public ProductFilterFragment(int p, boolean sf) {
        position = p;
        smartFiler = sf;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        list = getActivity().getResources().getStringArray(R.array.filter_items);

        View v = getActivity().getLayoutInflater().inflate(R.layout.filter_dialog, null);

        mList = v.findViewById(R.id.dialog_listView);

        final FilterArrayAdapter adapter = new FilterArrayAdapter(getActivity(),  list);
        adapter.setEnableAll(!smartFiler);
        mList.setAdapter(adapter);
        mList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                position = i;

                CheckedTextView cv = (CheckedTextView) view;
                cv.setChecked(true);

            }
        });
        mList.performItemClick(mList.getAdapter().getView(position, null, null), position, position);

        mSwitch = v.findViewById(R.id.smart_switch);
        mSwitch.setChecked(smartFiler);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                enableListView(!isChecked);
                mListener.onToggleSmartFilter(isChecked);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setPositiveButton(R.string.apply, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mListener.onSelectItem(list, position);
            }
        }).setNeutralButton(R.string.bt_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });

        builder.setView(v);

        alertDialog = builder.create();
        alertDialog.show();

        Button btnPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button btnNeutral = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnPositive.getLayoutParams();
        layoutParams.weight = 10;
        btnPositive.setLayoutParams(layoutParams);
        btnNeutral.setLayoutParams(layoutParams);

        btnPositive.setEnabled(!smartFiler);

        return alertDialog;
    }


    private static class FilterArrayAdapter extends ArrayAdapter<String> {
        boolean enableAll;

        public FilterArrayAdapter(final Context context, final String[] objects) {
            super(context, android.R.layout.simple_list_item_single_choice, objects);
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            final CheckedTextView view = (CheckedTextView) super.getView(position, convertView, parent);
            view.setEnabled(enableAll);
            return view;
        }

        public void setEnableAll(boolean enabled) {
            enableAll = enabled;
        }
    }

    private void enableListView(boolean enabled) {
        for (int i = 0; i < list.length; i++) {
            mList.getChildAt(i).setEnabled(enabled);
        }
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(enabled);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (OnFragmentInteractionListener) getParentFragment();
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onSelectItem(String[] list, int position);
        void onToggleSmartFilter(boolean applySmartFilter);
    }
}
