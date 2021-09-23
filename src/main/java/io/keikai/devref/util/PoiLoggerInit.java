package io.keikai.devref.util;

import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.util.WebAppInit;

/**
 * Since {@link org.zkoss.poi.util.POILogFactory} uses {@link org.zkoss.poi.util.NullLogger} by default. We need to specify another logger.
 */
public class PoiLoggerInit implements WebAppInit {
    @Override
    public void init(WebApp wapp) throws Exception {
        System.setProperty("org.zkoss.poi.util.POILogger", "org.zkoss.poi.util.CommonsLogger");
    }
}
