package net.sourceforge.cobertura.complexity;

import net.sourceforge.cobertura.coveragedata.ClassData;
import net.sourceforge.cobertura.coveragedata.PackageData;
import net.sourceforge.cobertura.coveragedata.ProjectData;
import net.sourceforge.cobertura.reporting.ComplexityCalculator;
import net.sourceforge.cobertura.util.FileFinder;
import net.sourceforge.cobertura.util.FileFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class ComplexityTaskTest {

    private FileFixture fileFixture;
    private FileFinder fileFinder;

    @Before
    public void setUp() throws Exception {
        fileFixture = new FileFixture();
        fileFixture.setUp();

        fileFinder = new FileFinder();
        fileFinder.addSourceDirectory(fileFixture.sourceDirectory(
                FileFixture.SOURCE_DIRECTORY_IDENTIFIER[0]).toString());
        fileFinder.addSourceDirectory(fileFixture.sourceDirectory(
                FileFixture.SOURCE_DIRECTORY_IDENTIFIER[1]).toString());
        fileFinder.addSourceFile(fileFixture.sourceDirectory(
                        FileFixture.SOURCE_DIRECTORY_IDENTIFIER[2]).toString(),
                "com/example\\Sample5.java");
        fileFinder.addSourceFile(fileFixture.sourceDirectory(
                        FileFixture.SOURCE_DIRECTORY_IDENTIFIER[2]).toString(),
                "com/example/Sample6.java");
        fileFinder.addSourceFile(fileFixture.sourceDirectory(
                        FileFixture.SOURCE_DIRECTORY_IDENTIFIER[3]).toString(),
                "com/example/Sample7.java");

    }

    @After
    public void tearDown() throws Exception {
        fileFixture.tearDown();
    }

    private ComplexityData calculateComplexity(ProjectData projectData) {
        return calculateComplexity(projectData, false);
    }

    private ComplexityData calculateComplexity(ProjectData projectData, boolean calculateMethodComplexity) {
        ComplexityCalculator complexityCalculator = new ComplexityCalculator(fileFinder);
        complexityCalculator.setCalculateMethodComplexity(calculateMethodComplexity);
        ComplexityTask complexityTask = new ComplexityTask(complexityCalculator);
        return complexityTask.calculateComplexity(projectData);
    }

    @Test
    public void testGetCCNForProject() {
        ProjectData project = new ProjectData();
        project.addClassData(new ClassData("com.example.Sample5"));
        ComplexityData complexityData = calculateComplexity(project);
        double ccn1 = complexityData.getCCNForProject();
        assertThat(ccn1, not(equalTo(0.0)));

        project.addClassData(new ClassData("com.example.Sample4"));
        ComplexityData complexityData2 = calculateComplexity(project);
        double ccn2 = complexityData2.getCCNForProject();
        assertThat(ccn2, not(equalTo(0.0)));
        assertThat(ccn1, not(equalTo(ccn2)));

        project.addClassData(new ClassData("com.example.Sample8"));
        ComplexityData complexityData3 = calculateComplexity(project);
        double ccn3 = complexityData3.getCCNForProject();
        assertThat(ccn2, equalTo(ccn3));

        ComplexityData complexityData4 = calculateComplexity(new ProjectData());
        double ccn0 = complexityData4.getCCNForProject();
        assertThat(ccn0, equalTo(0.0));
    }

    @Test
    public void testGetCCNForPackage() {
        ProjectData project = new ProjectData();
        ClassData classData = new ClassData("com.example.Sample3");
        project.addClassData(classData);
        PackageData pd = new PackageData("com.example");
        pd.addClassData(classData);

        ComplexityData complexityData = calculateComplexity(project);
        double ccn1 = complexityData.getCCNForPackage(pd);
        assertThat(ccn1, not(equalTo(0.0)));

        classData = new ClassData("com.example.Sample4");
        project.addClassData(classData);
        pd.addClassData(classData);
        ComplexityData complexityData2 = calculateComplexity(project);
        double ccn2 = complexityData2.getCCNForPackage(pd);
        double ccn3 = complexityData2.getCCNForPackage(pd);
        assertThat(ccn2, not(equalTo(0.0)));
        assertThat(ccn1, not(equalTo(ccn2)));
        assertThat(ccn2, equalTo(ccn3));

        PackageData empty = new PackageData("com.example2");
        ComplexityData complexityData3 = calculateComplexity(project);
        assertThat(complexityData3.getCCNForPackage(empty), equalTo(0.0));
    }

    @Test
    public void testGetCCNForMethod() {
        ProjectData project = new ProjectData();
        ClassData classData = new ClassData("com.example.Sample7");
        classData.addLine(1, "someMethod", "(int;)V");
        project.addClassData(classData);

        ComplexityData complexityData = calculateComplexity(project, true);
        int ccnForMethod = complexityData.getCCNForMethod(new MethodData(classData, "someMethod(int;)V"));

        assertThat(ccnForMethod, not(equalTo(0)));
    }

}
