package org.imgt.asteroides;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class PreferenciasFragment extends PreferenceFragment {

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);
        
        //código de verificación de valores de preferencias (3.7.4)

        final SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(this.getActivity());

        final EditTextPreference fragmentos = (EditTextPreference) findPreference("fragmentos");
        fragmentos.setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object
                            newValue) {
                        int valor;
                        try {
                            valor = Integer.parseInt((String)newValue);
                        } catch(Exception e) {
                            Toast.makeText(getActivity(), getResources().getString(R.string.restrfragmentos1),
                                    Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        if (valor>=0 && valor<=9) {
                            fragmentos.setSummary(
                                    //R.string.numfragmentosdescripcion + " ("+pref.getString("fragmentos","0")+")");
                                    getResources().getString(R.string.numfragmentosdescripcion) + " ("+valor+")");
                            return true;
                        } else {
                            Toast.makeText(getActivity(), getResources().getString(R.string.restrfragmentos2)+": 9",
                                    Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    }
                });
    }
}
