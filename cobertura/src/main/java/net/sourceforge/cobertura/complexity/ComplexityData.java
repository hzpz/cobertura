package net.sourceforge.cobertura.complexity;

import net.sourceforge.cobertura.coveragedata.ClassData;
import net.sourceforge.cobertura.coveragedata.PackageData;
import net.sourceforge.cobertura.coveragedata.ProjectData;
import net.sourceforge.cobertura.coveragedata.SourceFileData;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds complexity information for a project.
 */
public class ComplexityData {

    private final ProjectData projectData;
    private final double ccnForProject;

    private Map<PackageData, Double> ccnForPackages;
    private Map<SourceFileData, Double> ccnForSourceFiles;
    private Map<ClassData, Double> ccnForClasses;
    private Map<MethodData, Integer> ccnForMethods;

    public ComplexityData(ProjectData projectData, double ccnForProject) {
        this.projectData = projectData;
        this.ccnForProject = ccnForProject;
        this.ccnForPackages = new HashMap<PackageData, Double>();
        this.ccnForSourceFiles = new HashMap<SourceFileData, Double>();
        this.ccnForClasses = new HashMap<ClassData, Double>();
        this.ccnForMethods = new HashMap<MethodData, Integer>();
    }

    public ProjectData getProjectData() {
        return projectData;
    }

    public double getCCNForProject() {
        return ccnForProject;
    }

    public void addCCNForPackage(PackageData packageData, double ccn) {
        ccnForPackages.put(packageData, ccn);
    }

    public double getCCNForPackage(PackageData packageData) {
        return doubleOrNothing(ccnForPackages, packageData);
    }

    public void addCCNForSourceFile(SourceFileData sourceFile, double ccn) {
        ccnForSourceFiles.put(sourceFile, ccn);
    }

    public double getCCNForSourceFile(SourceFileData sourceFile) {
        return doubleOrNothing(ccnForSourceFiles, sourceFile);
    }

    public void addCCNForClass(ClassData classData, double ccn) {
        ccnForClasses.put(classData, ccn);
    }

    public double getCCNForClass(ClassData classData) {
        return doubleOrNothing(ccnForClasses, classData);
    }

    public void addCCNForMethod(MethodData methodData, int ccn) {
        ccnForMethods.put(methodData, ccn);
    }

    public int getCCNForMethod(MethodData methodData) {
        return intOrNothing(ccnForMethods, methodData);
    }

    private <K> double doubleOrNothing(Map<K, Double> map, K key) {
        if (map.containsKey(key)) {
            return map.get(key);
        } else {
            return 0;
        }
    }

    private <K> int intOrNothing(Map<K, Integer> map, K key) {
        if (map.containsKey(key)) {
            return map.get(key);
        } else {
            return 0;
        }
    }

}
