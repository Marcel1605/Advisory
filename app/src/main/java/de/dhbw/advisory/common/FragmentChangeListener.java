package de.dhbw.advisory.common;

import android.support.v4.app.Fragment;

/**
 * Wenn ein Fragment A ein anderes Fragment B innerhalb einer Activity anzeigen lassen will, so muss diese Activity das folgende Interface implementieren.
 * Zusätzlich muss Fragment A die Methode onAttach(Activity) überschreiben und die übergebene Activity als FragmentChangeListener casten
 */

public interface FragmentChangeListener {
    /**
     * Das ist ein Callback, welches aufgerufen wird, wenn das {@code fragment} im Sichtfeld des Nutzers angezeigt werden soll.
     * @param fragment Das Fragment das angezeigt werden soll
     * @param pushOnBackStack Wahr, wenn das Fragment mit dem Zurück-Button erreicht werden soll
     */
    public void onFragmentChangeRequest(Fragment fragment, boolean pushOnBackStack);
}
