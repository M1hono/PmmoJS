package com.pickaid.pmmojs.kubejs.probe;

import com.pickaid.pmmojs.utils.PmmoHelper;
import zzzank.probejs.docs.assignments.SpecialTypes;
import zzzank.probejs.lang.typescript.code.member.TypeDecl;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.Types;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

final class PmmoJSLegacyProbeTypes {
    private static final List<String> LEGACY_GLOBAL_FILES = List.of(
            "pmmojs_special_types.d.ts"
    );

    private PmmoJSLegacyProbeTypes() {
    }

    static Collection<TypeDecl> specialTypeDeclarations() {
        List<TypeDecl> declarations = new ArrayList<>();

        declarations.add(new TypeDecl("PmmoEventType",
                toUnionType(PmmoHelper.getEventTypeIds())));
        declarations.add(new TypeDecl("PmmoTriggerType",
                toUnionType(PmmoHelper.getTriggerTypeIds())));
        declarations.add(new TypeDecl("PmmoInternalType",
                toUnionType(PmmoHelper.getInternalTypeIds())));
        declarations.add(new TypeDecl("PmmoReqType",
                toUnionType(PmmoHelper.getReqTypeIds())));
        declarations.add(new TypeDecl("PmmoItemReqType",
                toUnionType(PmmoHelper.getItemReqTypeIds())));
        declarations.add(new TypeDecl("PmmoBlockReqType",
                toUnionType(PmmoHelper.getBlockReqTypeIds())));
        declarations.add(new TypeDecl("PmmoEntityReqType",
                toUnionType(PmmoHelper.getEntityReqTypeIds())));
        declarations.add(new TypeDecl("PmmoObjectType",
                toUnionType(PmmoHelper.getObjectTypeIds())));
        declarations.add(new TypeDecl("PmmoPerkSide",
                toUnionType(List.of("SERVER", "CLIENT", "BOTH"))));

        return declarations;
    }

    static void cleanupLegacyGlobalFiles(Path scriptRoot) {
        Path globalDir = scriptRoot.resolve("global");
        for (String fileName : LEGACY_GLOBAL_FILES) {
            Path path = globalDir.resolve(fileName);
            try {
                Files.deleteIfExists(path);
            } catch (IOException exception) {
                throw new IllegalStateException("Failed to delete legacy PmmoJS ProbeJS file " + path, exception);
            }
        }
    }

    private static BaseType toUnionType(Collection<String> ids) {
        List<BaseType> literals = ids.stream()
                .sorted()
                .distinct()
                .map(Types::literal)
                .map(BaseType.class::cast)
                .toList();
        if (literals.isEmpty()) {
            return Types.STRING;
        }
        if (literals.size() == 1) {
            return literals.get(0);
        }
        return Types.or(literals);
    }
}
