package com.pickaid.pmmojs.kubejs.probe;

public final class PmmoJSProbeCompat {
    private static boolean installed;

    private PmmoJSProbeCompat() {
    }

    public static synchronized void install() {
        if (installed) {
            return;
        }
        installed = true;
        new PmmoJSProbePlugin().registerEvents();
    }

    public static synchronized void reinstallAfterServerReload() {
        installed = false;
        install();
    }
}
