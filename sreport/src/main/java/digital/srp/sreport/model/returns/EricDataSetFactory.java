package digital.srp.sreport.model.returns;

public class EricDataSetFactory {

    public static final EricDataSet getInstance(String surveyName) {
        switch (surveyName) {
        case Eric1617.NAME:
            return new Eric1617();
        case Eric1516.NAME:
            return new Eric1516();
        case Eric1415.NAME:
            return new Eric1415();
        case Eric1314.NAME:
            return new Eric1314();
        default:
            throw new IllegalStateException(
                    String.format("No data registered for %1$s", surveyName));
        }
    }
}
