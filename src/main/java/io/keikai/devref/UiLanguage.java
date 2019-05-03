package io.keikai.devref;

import io.keikai.client.api.*;
import io.keikai.util.Maps;

import java.util.Locale;

/**
 * enforce UI language
 */
public class UiLanguage implements KeikaiCase {
    private Spreadsheet spreadsheet;

    @Override
    public void init(String keikaiEngineAddress) {
        Settings settings = Settings.DEFAULT_SETTINGS.clone();
        settings.set(Settings.Key.SPREADSHEET_CONFIG, Maps.toMap("language", Locale.TAIWAN.toString()));
        spreadsheet = Keikai.newClient(keikaiEngineAddress, settings);
    }

    @Override
    public String getJavaScriptURI(String domId) {
        return spreadsheet.getURI(domId);
    }

    /**
     * create a drop-down to select languages.
     */
    @Override
    public void run() {
        spreadsheet.getRange("A1").setValue("We enforce Traditional Chinese UI in this page.");
    }
}
