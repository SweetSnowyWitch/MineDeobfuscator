package dev.booky.stackdeobf.mappings;
// Created by booky10 in StackDeobfuscator (15:08 23.03.23)

import net.fabricmc.mappingio.MappedElementKind;
import net.fabricmc.mappingio.MappingVisitor;

import java.util.List;
import java.util.Map;

public class MappingCacheVisitor implements MappingVisitor {

    private final Map<Integer, String> classes, methods, fields;
    private int dstNamespaceId = 0;

    private String srcClassName;
    private String srcMethodName;
    private String srcFieldName;

    public MappingCacheVisitor(Map<Integer, String> classes, Map<Integer, String> methods, Map<Integer, String> fields) {
        this.classes = classes;
        this.methods = methods;
        this.fields = fields;
    }

    @Override
    public void visitNamespaces(String srcNamespace, List<String> dstNamespaces) {
        int namedIndex = dstNamespaces.indexOf("named");
        if (namedIndex != -1) {
            this.dstNamespaceId = namedIndex;
        }
    }

    @Override
    public boolean visitClass(String srcName) {
        this.srcClassName = srcName;
        return true;
    }

    @Override
    public boolean visitField(String srcName, String srcDesc) {
        this.srcFieldName = srcName;
        return true;
    }

    @Override
    public boolean visitMethod(String srcName, String srcDesc) {
        this.srcMethodName = srcName;
        return true;
    }

    @Override
    public boolean visitMethodArg(int argPosition, int lvIndex, String srcName) {
        return false;
    }

    @Override
    public boolean visitMethodVar(int lvtRowIndex, int lvIndex, int startOpIdx, String srcName) {
        return false;
    }

    @Override
    public void visitDstName(MappedElementKind targetKind, int namespace, String name) {
        if (this.dstNamespaceId != namespace) {
            return;
        }

        switch (targetKind) {
            case CLASS -> {
                String srcName = this.srcClassName;
                if (srcName.equals(name)) {
                    return;
                }
                if (!srcName.startsWith("net/minecraft/class_")) {
                    // I don't know why, but in some versions (tested in 1.14.2) the
                    // names are just switched, without the namespaces being switched
                    if (name.startsWith("net/minecraft/class_")) {
                        String ogName = name;
                        name = srcName;
                        srcName = ogName;
                    } else {
                        return;
                    }
                }

                String classIdStr;
                int innerClassSeparatorIndex = srcName.lastIndexOf('$');
                if (innerClassSeparatorIndex != -1) {
                    classIdStr = srcName.substring(innerClassSeparatorIndex + 1);
                    if (!classIdStr.startsWith("class_")) {
                        return; // don't save it if it is a lambda
                    }
                    classIdStr = classIdStr.substring("class_".length());
                } else {
                    classIdStr = srcName.substring("net/minecraft/class_".length());
                }

                innerClassSeparatorIndex = name.indexOf('$');
                if (innerClassSeparatorIndex != -1) {
                    name = name.substring(innerClassSeparatorIndex + 1);
                } else {
                    name = name.replace('/', '.');
                }
                this.classes.put(Integer.parseInt(classIdStr), name);
            }
            case METHOD -> {
                String srcName = this.srcMethodName;
                if (srcName.equals(name)) {
                    return;
                }
                if (!srcName.startsWith("method_")) {
                    if (name.startsWith("method_")) {
                        String ogName = name;
                        name = srcName;
                        srcName = ogName;
                    } else {
                        return;
                    }
                }

                int methodId = Integer.parseInt(srcName.substring("method_".length()));
                this.methods.put(methodId, name);
            }
            case FIELD -> {
                String srcName = this.srcFieldName;
                if (srcName.equals(name)) {
                    return;
                }
                if (!srcName.startsWith("field_")) {
                    if (name.startsWith("field_")) {
                        String ogName = name;
                        name = srcName;
                        srcName = ogName;
                    } else {
                        return;
                    }
                }

                int fieldId = Integer.parseInt(srcName.substring("field_".length()));
                this.fields.put(fieldId, name);
            }
        }
    }

    @Override
    public void visitComment(MappedElementKind targetKind, String comment) {
    }
}
