package net.sourceforge.cobertura.reporting.html.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import net.sourceforge.cobertura.complexity.ComplexityData;
import net.sourceforge.cobertura.coveragedata.*;
import net.sourceforge.cobertura.reporting.html.JavaToHtml;
import net.sourceforge.cobertura.reporting.html.SourceFileDataBaseNameComparator;
import net.sourceforge.cobertura.reporting.html.files.CopyFiles;
import net.sourceforge.cobertura.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.*;

public class HTMLReport {

    private static final Logger LOGGER = LoggerFactory.getLogger(HTMLReport.class);

    private File destinationDir;

    private FileFinder finder;

    private ComplexityData complexity;

    private ProjectData projectData;

    private String encoding;

    private final Configuration freemarkerCfg;

    /**
     * Create a coverage report
     */
    public HTMLReport(ProjectData projectData, File outputDir,
                      FileFinder finder, ComplexityData complexity, String encoding) throws Exception {
        this.destinationDir = outputDir;
        this.finder = finder;
        this.complexity = complexity;
        this.projectData = projectData;
        this.encoding = encoding;

        CopyFiles.copy(outputDir);

        freemarkerCfg = new Configuration(Configuration.VERSION_2_3_22);
        freemarkerCfg.setClassForTemplateLoading(this.getClass(), "templates");
        freemarkerCfg.setDefaultEncoding(encoding);
        // TODO
        freemarkerCfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);

        generatePackageList();
        generateSourceFileLists();
        generateOverviews();
        generateSourceFiles();
    }

    private void generatePackageList() throws IOException, TemplateException {
        Template framePackagesTemplate = freemarkerCfg.getTemplate("frame-packages.html");
        Map<String, Object> templateModel = new HashMap<String, Object>();
        templateModel.put("project", projectData);
        templateModel.put("report", this);

        File file = new File(destinationDir, "frame-packages.html");
        PrintWriter out = null;
        try {
            out = IOUtil.getPrintWriter(file);
            framePackagesTemplate.process(templateModel, out);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public String generatePackageName(PackageData packageData) {
        if (packageData == null) {
            return "All Packages";
        } else if (packageData.getName().equals("")) {
            return "(default)";
        } else {
            return packageData.getName();
        }
    }

    private void generateSourceFileLists() throws IOException, TemplateException {
        generateSourceFileList(null);
        for (Object packageObject : projectData.getPackages()) {
            PackageData packageData = (PackageData) packageObject;
            generateSourceFileList(packageData);
        }
    }

    private void generateSourceFileList(PackageData packageData) throws IOException, TemplateException {
        String filename;
        Collection sourceFiles;
        if (packageData == null) {
            filename = "frame-sourcefiles.html";
            sourceFiles = projectData.getSourceFiles();
        } else {
            filename = "frame-sourcefiles-" + packageData.getName() + ".html";
            sourceFiles = packageData.getSourceFiles();
        }

        // sourceFiles may be sorted, but if so it's sorted by
        // the full path to the file, and we only want to sort
        // based on the file's basename.
        Vector sortedSourceFiles = new Vector();
        sortedSourceFiles.addAll(sourceFiles);
        Collections.sort(sortedSourceFiles,
                new SourceFileDataBaseNameComparator());

        Template frameSourcefilesTemplate = freemarkerCfg.getTemplate("frame-sourcefiles.html");
        Map<String, Object> templateModel = new HashMap<String, Object>();
        templateModel.put("report", this);
        templateModel.put("package", packageData);
        templateModel.put("sourceFiles", sortedSourceFiles);

        File file = new File(destinationDir, filename);
        PrintWriter out = null;
        try {
            out = IOUtil.getPrintWriter(file);
            frameSourcefilesTemplate.process(templateModel, out);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public String getLineCoverageInPercent(CoverageDataContainer coverageData) {
        if (coverageData.getNumberOfValidLines() > 0) {
            return StringUtil.getPercentValue(coverageData.getLineCoverageRate());
        } else {
            return "N/A";
        }
    }

    public String getBranchCoverageInPercent(CoverageDataContainer coverageData) {
        if (coverageData.getNumberOfValidBranches() > 0) {
            return StringUtil.getPercentValue(coverageData.getBranchCoverageRate());
        } else {
            return "N/A";
        }
    }

    public String getLineCoverageGraphWidth(CoverageDataContainer coverageData) {
        return (coverageData.getNumberOfCoveredLines() * 100 / coverageData.getNumberOfValidLines()) + "px";
    }

    public String getBranchCoverageGraphWidth(CoverageDataContainer coverageData) {
        return (coverageData.getNumberOfCoveredBranches() *  100 / coverageData.getNumberOfValidBranches()) + "px";
    }

    public String formatDoubleValue(double value) {
        return new DecimalFormat().format(value);
    }

    private void generateOverviews() throws IOException, TemplateException {
        generateOverview(null);
        for (Object packageObject : projectData.getPackages()) {
            PackageData packageData = (PackageData) packageObject;
            generateOverview(packageData);
        }
    }

    private void generateOverview(PackageData packageData) throws IOException, TemplateException {
        String filename;
        if (packageData == null) {
            filename = "frame-summary.html";
        } else {
            filename = "frame-summary-" + packageData.getName() + ".html";
        }

        SortedSet packages;
        if (packageData == null) {
            packages = projectData.getPackages();
        } else {
            packages = projectData.getSubPackages(packageData.getName());
        }

        // Get the list of source files in this package
        Collection sourceFiles;
        if (packageData == null) {
            PackageData defaultPackage = (PackageData) projectData.getChild("");
            if (defaultPackage != null) {
                sourceFiles = defaultPackage.getSourceFiles();
            } else {
                sourceFiles = new TreeSet();
            }
        } else {
            sourceFiles = packageData.getSourceFiles();
        }

        Template frameSummaryTemplate = freemarkerCfg.getTemplate("frame-summary.html");
        Map<String, Object> templateModel = new HashMap<String, Object>();
        templateModel.put("report", this);
        templateModel.put("project", projectData);
        templateModel.put("rootPackage", packageData);
        templateModel.put("packages", packages);
        templateModel.put("complexity", complexity);
        templateModel.put("sourceFiles", sourceFiles);
        templateModel.put("coberturaVersion", Header.version());
        templateModel.put("generationDate", DateFormat.getInstance().format(new Date()));

        File file = new File(destinationDir, filename);
        PrintWriter out = null;
        try {
            out = IOUtil.getPrintWriter(file);
            frameSummaryTemplate.process(templateModel, out);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private void generateSourceFiles() {
        for (Object sourceFileObject : projectData.getSourceFiles()) {
            SourceFileData sourceFileData = (SourceFileData) sourceFileObject;
            try {
                generateSourceFile(sourceFileData);
            } catch (IOException e) {
                LOGGER.info("Could not generate HTML file for source file " + sourceFileData.getName(), e);
            } catch (TemplateException e) {
                LOGGER.info("Could not generate HTML file for source file " + sourceFileData.getName(), e);
            }
        }
    }

    private void generateSourceFile(SourceFileData sourceFileData) throws IOException, TemplateException {
        if (!sourceFileData.containsInstrumentationInfo()) {
            LOGGER.info("Data file does not contain instrumentation "
                    + "information for the file " + sourceFileData.getName()
                    + ".  Ensure this class was instrumented, and this "
                    + "data file contains the instrumentation information.");
        }

        Template sourceTemplate = freemarkerCfg.getTemplate("source.html");
        Map<String, Object> templateModel = new HashMap<String, Object>();
        templateModel.put("report", this);
        templateModel.put("sourceFile", sourceFileData);
        templateModel.put("complexity", complexity);
        templateModel.put("coberturaVersion", Header.version());
        templateModel.put("generationDate", DateFormat.getInstance().format(new Date()));
        addSourceCodeToTemplateModel(sourceFileData, templateModel);

        String filename = sourceFileData.getNormalizedName() + ".html";
        File file = new File(destinationDir, filename);
        PrintWriter out = null;
        try {
            out = IOUtil.getPrintWriter(file);
            sourceTemplate.process(templateModel, out);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private void addSourceCodeToTemplateModel(SourceFileData sourceFileData, Map<String, Object> templateModel) {
        Source source = finder.getSource(sourceFileData.getName());

        if (source == null) {
            templateModel.put("fatalError", true);
            templateModel.put("errorMessage", "Unable to locate " + sourceFileData.getName() + ". Have you specified the source directory?");
            return;
        }

        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(source.getInputStream(), encoding));
        } catch (UnsupportedEncodingException e) {
            templateModel.put("fatalError", true);
            templateModel.put("errorMessage", "Unable to open " + source.getOriginDesc() + ": The encoding '" + encoding + "' is not supported by your JVM.");
            return;
        } catch (Throwable t) {
            templateModel.put("fatalError", true);
            templateModel.put("errorMessage", "Unable to open " + source.getOriginDesc() + ": " + t.getLocalizedMessage());
            return;
        }

        try {
            String lineStr;
            JavaToHtml javaToHtml = new JavaToHtml();
            int lineNumber = 1;
            List<SourceCodeLine> lines = new ArrayList<SourceCodeLine>();
            while ((lineStr = br.readLine()) != null) {
                boolean isValidSourceLineNumber = sourceFileData.isValidSourceLineNumber(lineNumber);
                LineData lineData = sourceFileData.getLineCoverage(lineNumber);
                SourceCodeLine sourceCodeLine = new SourceCodeLine(isValidSourceLineNumber, lineNumber, lineData, javaToHtml.process(lineStr));
                if (lineData != null && lineData.hasBranch()) {
                    StringBuilder conditionalCoverageDetails = new StringBuilder();
                    if (lineData.getConditionSize() > 0) {
                        conditionalCoverageDetails.append(" [each condition: ");
                        for (int i = 0; i < lineData.getConditionSize(); i++) {
                            if (i > 0) {
                                conditionalCoverageDetails.append(", ");
                            }
                            conditionalCoverageDetails.append(lineData.getConditionCoverage(i));
                        }
                        conditionalCoverageDetails.append("]");
                    }
                    sourceCodeLine.setConditionCoverageDetails(conditionalCoverageDetails.toString());
                }
                lines.add(sourceCodeLine);
                lineNumber++;
            }
            templateModel.put("lines", lines);
        } catch (IOException e) {
            templateModel.put("readError", true);
            templateModel.put("errorMessage", "Error reading " + source.getOriginDesc() + ": " + e.getLocalizedMessage());
        } finally {
            try {
                br.close();
                source.close();
            } catch (IOException e) {
            }
        }
    }

    public static class SourceCodeLine {

        private final boolean valid;
        private final int lineNumber;
        private final LineData data;
        private final String code;

        private String conditionCoverageDetails;

        public SourceCodeLine(boolean valid, int lineNumber, LineData data, String code) {
            this.valid = valid;
            this.lineNumber = lineNumber;
            this.data = data;
            this.code = code;
        }

        public boolean isValid() {
            return valid;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public LineData getData() {
            return data;
        }

        public String getCode() {
            return code;
        }

        public String getConditionCoverageDetails() {
            return conditionCoverageDetails;
        }

        public void setConditionCoverageDetails(String conditionCoverageDetails) {
            this.conditionCoverageDetails = conditionCoverageDetails;
        }

    }

}
