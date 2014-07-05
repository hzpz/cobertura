BASEDIR=`dirname $0`
java -cp $BASEDIR/cobertura-${project.version}.jar:$BASEDIR/lib/asm-${asmVersion}.jar:$BASEDIR/lib/asm-tree-${asmVersion}.jar:$BASEDIR/lib/asm-commons-${asmVersion}.jar:$BASEDIR/lib/slf4j-api-${slf4jVersion}.jar:$BASEDIR/lib/oro-${oroVersion}.jar net.sourceforge.cobertura.reporting.ReportMain $*
