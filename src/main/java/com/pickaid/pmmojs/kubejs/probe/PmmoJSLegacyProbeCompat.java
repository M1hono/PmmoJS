package com.pickaid.pmmojs.kubejs.probe;

import zzzank.probejs.plugin.ProbeJSPlugins;

public final class PmmoJSLegacyProbeCompat {
    private static boolean installed;

    private PmmoJSLegacyProbeCompat() {
    }

    public static synchronized void install() {
        if (installed) {
            return;
        }
        installed = true;
        ProbeJSPlugins.remove(PmmoJSLegacyProbePlugin.class);
        ProbeJSPlugins.register(new PmmoJSLegacyProbePlugin());
    }
}
