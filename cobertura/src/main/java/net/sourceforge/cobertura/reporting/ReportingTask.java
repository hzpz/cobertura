package net.sourceforge.cobertura.reporting;

import net.sourceforge.cobertura.complexity.ComplexityData;
import net.sourceforge.cobertura.complexity.ComplexityTask;
import net.sourceforge.cobertura.coveragedata.ProjectData;
import net.sourceforge.cobertura.dsl.Arguments;

public class ReportingTask {

    public NativeReport report(ProjectData projectData, Arguments args) {
        ComplexityCalculator complexityCalculator = new ComplexityCalculator(args.getSources());
        complexityCalculator.setEncoding(args.getEncoding());
        complexityCalculator.setCalculateMethodComplexity(args.isCalculateMethodComplexity());

        ComplexityTask complexityTask = new ComplexityTask(complexityCalculator);
        ComplexityData complexityData = complexityTask.calculateComplexity(projectData);

        return new NativeReport(projectData, args.getDestinationDirectory(), args.getSources(),
                complexityData, args.getEncoding());
    }

}
