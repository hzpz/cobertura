package net.sourceforge.cobertura.reporting;

import net.sourceforge.cobertura.coveragedata.ProjectData;
import net.sourceforge.cobertura.dsl.Arguments;

public class ReportingTask {

    public NativeReport report(ProjectData projectData, Arguments args) {
        ComplexityCalculator complexityCalculator = new ComplexityCalculator(args.getSources());
        complexityCalculator.setEncoding(args.getEncoding());
        complexityCalculator.setCalculateMethodComplexity(args.isCalculateMethodComplexity());

        return new NativeReport(projectData, args.getDestinationDirectory(), args.getSources(),
                complexityCalculator, args.getEncoding());
    }

}
