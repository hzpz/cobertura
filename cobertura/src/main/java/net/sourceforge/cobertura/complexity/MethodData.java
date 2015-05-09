package net.sourceforge.cobertura.complexity;

import net.sourceforge.cobertura.coveragedata.ClassData;

public class MethodData {

    private final ClassData classData;
    private final String name;
    private final String descriptor;

    public MethodData(ClassData classData, String name, String descriptor) {
        this.classData = classData;
        this.name = name;
        this.descriptor = descriptor;
    }

    public MethodData(ClassData classData, String nameAndDescriptor) {
        this(classData, getNameFrom(nameAndDescriptor), getDescriptorFrom(nameAndDescriptor));
    }

    private static String getNameFrom(String nameAndDescriptor) {
        return nameAndDescriptor.substring(0, nameAndDescriptor.indexOf('('));
    }

    private static String getDescriptorFrom(String nameAndDescriptor) {
        return nameAndDescriptor.substring(nameAndDescriptor.indexOf('('));
    }

    public ClassData getClassData() {
        return classData;
    }

    public String getName() {
        return name;
    }

    public String getDescriptor() {
        return descriptor;
    }
}
