package digital.srp.sreport.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.BeforeClass;
import org.junit.Test;

import digital.srp.sreport.model.Question;

public class NullAwareBeanUtilsTest {

    private static final String NAME = "Foo";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Test
    public void testCopyNonNullProperties() {
        Question srcBean = new Question();
        srcBean.id(1l);
        srcBean.name(NAME);

        Question trgtBean = new Question();

        NullAwareBeanUtils.copyNonNullProperties(srcBean, trgtBean, "id");

        assertNull(trgtBean.id());
        assertEquals(NAME, trgtBean.name());
    }

    @Test
    public void testTrimStringProperties() {
        Question srcBean = new Question();
        srcBean.id(1l);
        srcBean.name(NAME + " ");

        NullAwareBeanUtils.trimStringProperties(srcBean);

        assertEquals(NAME, srcBean.name());
    }
}
