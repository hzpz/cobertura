package net.sourceforge.cobertura.complexity;

import net.sourceforge.cobertura.coveragedata.ClassData;
import net.sourceforge.cobertura.coveragedata.PackageData;
import net.sourceforge.cobertura.coveragedata.ProjectData;
import net.sourceforge.cobertura.coveragedata.SourceFileData;
import net.sourceforge.cobertura.reporting.ComplexityCalculator;

/**
 * Calculates complexity information for a project using {@link ComplexityCalculator}.
 */
public class ComplexityTask {

    private final ComplexityCalculator complexityCalculator;

    public ComplexityTask(ComplexityCalculator complexityCalculator) {
        this.complexityCalculator = complexityCalculator;
    }

    public ComplexityData calculateComplexity(ProjectData projectData) {
        double ccnForProject = complexityCalculator.getCCNForProject(projectData);
        ComplexityData complexityData = new ComplexityData(projectData, ccnForProject);

        calculatePackages(projectData, complexityData);

        return complexityData;
    }

    private void calculatePackages(ProjectData projectData, ComplexityData complexityData) {
        for (Object packageData: projectData.getPackages()) {
            calculatePackage((PackageData) packageData, complexityData);
        }
    }

    private void calculatePackage(PackageData packageData, ComplexityData complexityData) {
        complexityData.addCCNForPackage(packageData, complexityCalculator.getCCNForPackage(packageData));

        for (Object sourceFile : packageData.getSourceFiles()) {
            calculateSourceFile((SourceFileData) sourceFile, complexityData);
        }
    }

    private void calculateSourceFile(SourceFileData sourceFile, ComplexityData complexityData) {
        complexityData.addCCNForSourceFile(sourceFile, complexityCalculator.getCCNForSourceFile(sourceFile));

        for (Object classData : sourceFile.getClasses()) {
            calculateClass((ClassData) classData, complexityData);
        }
    }

    private void calculateClass(ClassData classData, ComplexityData complexityData) {
        complexityData.addCCNForClass(classData, complexityCalculator.getCCNForClass(classData));

        for (String methodNameAndDescriptor : classData.getMethodNamesAndDescriptors()) {
            calculateMethod(classData, methodNameAndDescriptor, complexityData);
        }
    }

    private void calculateMethod(ClassData classData, String methodNameAndDescriptor, ComplexityData complexityData) {
        MethodData method = new MethodData(classData, methodNameAndDescriptor);
        int ccn = complexityCalculator.getCCNForMethod(classData, method.getName(), method.getDescriptor());
        complexityData.addCCNForMethod(method, ccn);
    }

}
