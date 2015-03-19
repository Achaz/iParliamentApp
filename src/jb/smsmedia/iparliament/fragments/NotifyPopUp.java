package jb.smsmedia.iparliament.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;


public class NotifyPopUp extends DialogFragment {
    onDlgListClick mCallback;

    private String[] lista = {"hi", "there", "how", "are", "you"};//the list you want to show with the dialog

    public static NotifyPopUp newInstance(Bundle fB){
    	NotifyPopUp lstFrag = new NotifyPopUp();
        Bundle args = new Bundle();
        args.putStringArray("lista", fB.getStringArray("lista"));//the list
        args.putString("titulo", fB.getString("titulo"));//the title of the list
        lstFrag.setArguments(args);

        return lstFrag;
    }

    public interface onDlgListClick{
        public void onLstItemSelected(String selection);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (onDlgListClick) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onLstItemSelected");
        }
        this.setCancelable(false);
    }

     @Override
     public Dialog onCreateDialog(Bundle savedInstanceState) {
         lista = getArguments().getStringArray("lista");

         return new AlertDialog.Builder(getActivity())
         .setTitle(getArguments().getString("titulo"))
         .setCancelable(false)
         .setItems(lista, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int item){

                mCallback.onLstItemSelected(lista[item]);
                getDialog().dismiss(); //maybe you dont need these two lines
                NotifyPopUp.this.dismiss();
            }
         }).create();
     }

}
