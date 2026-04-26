package com.pickaid.pmmojs.kubejs.probe;

import zzzank.probejs.api.dump.CustomDump;
import zzzank.probejs.docs.assignments.SpecialTypes;
import zzzank.probejs.lang.snippet.SnippetDump;
import zzzank.probejs.lang.transpiler.Transpiler;
import zzzank.probejs.lang.typescript.RequestAwareFiles;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.ts.Wrapped;
import zzzank.probejs.plugin.ProbeJSPlugin;

import java.util.Set;

public final class PmmoJSLegacyProbePlugin implements ProbeJSPlugin {
    static String specialTypesGlobalName() {
        return "special_types";
    }

    @Override
    public void assignType(ScriptDump scriptDump) {
        // PmmoJS has no ID wrapper classes that need type assignment
    }

    @Override
    public void denyTypes(Transpiler transpiler) {
        // No types to deny for PmmoJS
    }

    @Override
    public void modifyFiles(RequestAwareFiles files) {
        // No wrapper files to rewrite for PmmoJS
    }

    @Override
    public void addGlobals(ScriptDump scriptDump) {
        scriptDump.addChild(new CustomDump(
                scriptDump.writeTo().resolve(".pmmojs_legacy_cleanup"),
                path -> PmmoJSLegacyProbeTypes.cleanupLegacyGlobalFiles(scriptDump.writeTo())
        ));
        Wrapped.Namespace special = new Wrapped.Namespace(SpecialTypes.NAMESPACE);
        PmmoJSLegacyProbeTypes.specialTypeDeclarations().forEach(special::addCode);
        scriptDump.addGlobal(specialTypesGlobalName(), special);
    }

    @Override
    public Set<Class<?>> provideJavaClass(ScriptDump scriptDump) {
        return PmmoJSLegacyProbeJava.providedClasses();
    }

    @Override
    public void addVSCodeSnippets(SnippetDump dump) {
        PmmoJSLegacyProbeSnippets.addSnippets(dump);
    }
}
