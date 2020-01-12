package OOP.Tests.AkivaTests;

import OOP.Solution.*;
import OOP.Provided.*;
import OOP.Tests.AkivaTests.AnotherClass.anotherClasess;
import OOP.Tests.AkivaTests.MyTestsClasses;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.fail;

import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OOPObjectVirtualSimpleTest {
    private static MyTestsClasses.SD_C simple_virtual_instance;
    private static MyTestsClasses.SD_A simple_diamond_root;


    @BeforeClass
    public static void initialize_fields() throws OOP4ObjectInstantiationFailedException {
        MyTestsClasses.setConstruction_order(new ArrayList<>());
        simple_virtual_instance = new MyTestsClasses.SD_C();
        simple_diamond_root = new MyTestsClasses.SD_A();
    }

    // addition
    @Test(expected = OOP4ObjectInstantiationFailedException.class)
    public void privateConstructor() throws OOP4ObjectInstantiationFailedException {
        // check that the right exception is thrown.
        // beside that, I found that failing in this test may cause another failing test.
        new MyTestsClasses.SD_Y2();
    }

    //addition
    @Test
    public void protectedConstructor() throws OOP4ObjectInstantiationFailedException {
        // check if derived class can invoke protected constructor of her base class
        new anotherClasess.SD_L2();
    }
    /*
    Things to check that were not covered by the other tests:
        - Initialization order (algorithm from tutorial) - done.
        - Ability to invoke methods on virtual ancestors - done.
        - Distinctness of non virtual instances from the virtual instance - done.
        - Different appearances of the same virtual parent class pointing to the same virtual ancestor (use == ) - done.
        - Check that virtual inheritance solves inherent ambiguity - done.
    */

    /* ############################### multInheritsFrom ###############################*/

    @Test
    public void testSimpleDiamondClassInheritanceBasic1() {
        // Checks virtual inheritance is registered in the system as inheritance.
        Assert.assertTrue(simple_virtual_instance.multInheritsFrom(MyTestsClasses.SD_D.class));
    }

    @Test
    public void testSimpleDiamondClassInheritanceBasic2() {
        // Checks the system recognized inheritance when it passes virtual and then regular inheritance.
        Assert.assertTrue(simple_virtual_instance.multInheritsFrom(MyTestsClasses.SD_E.class));
    }

    @Test
    public void testSimpleDiamondClassInheritanceBasic3() {
        // Checks the system recognized inheritance when it passes regular and then virtual inheritance.
        Assert.assertTrue(simple_diamond_root.multInheritsFrom(MyTestsClasses.SD_D.class));
    }
    /* ############################# initialization order ############################ */

    @Test
    public void testSimpleDiamondInitializationOrderBasic1() throws OOP4ObjectInstantiationFailedException {
        MyTestsClasses.setConstruction_order(new ArrayList<>());
        List<String> expected_order = new ArrayList<>(Arrays.asList("G", "E", "D", "G","P", "F","G", "B", "C", "A"));
        MyTestsClasses.SD_A a_obj = new MyTestsClasses.SD_A();
        Assert.assertEquals(expected_order, MyTestsClasses.getConstruction_order());
    }
    @Test
    public void complexTreeInitializationOrder() throws OOP4ObjectInstantiationFailedException {
        MyTestsClasses.setConstruction_order(new ArrayList<>());
        List<String> expected_order = new ArrayList<>(Arrays.asList("G", "E", "D", "G","P", "F","G", "B", "C", "A"));
        MyTestsClasses.SD_A a_obj = new MyTestsClasses.SD_A();
        Assert.assertEquals(expected_order, MyTestsClasses.getConstruction_order());
    }

    /* ################################ definingObject ############################### */

    @Test
    public void testSimpleDiamondDefiningObjectBasic1() throws OOP4ObjectInstantiationFailedException, OOP4NoSuchMethodException, OOP4AmbiguousMethodException {
        // Checks classes with single virtual inheritance can inherit methods correctly.
        Assert.assertEquals(MyTestsClasses.SD_D.class,
                new MyTestsClasses.SD_B().definingObject("public_to_inherit").getClass());
    }

    @Test
    public void testSimpleDiamondDefiningObjectBasic2() throws OOP4ObjectInstantiationFailedException {
        // Checks definingObject will give the same object for virtual inheritance.
        MyTestsClasses.SD_A a_obj = new MyTestsClasses.SD_A();
        Object b_instance = null;
        Object c_instance = null;
        try {
            b_instance = ((OOPObject) a_obj.definingObject("no_ambiguity_B"))
                    .definingObject("public_to_inherit");
            c_instance = ((OOPObject) a_obj.definingObject("no_ambiguity_C"))
                    .definingObject("public_to_inherit");
        } catch (OOP4AmbiguousMethodException | OOP4NoSuchMethodException e) {
            System.out.println(e);
            fail();
        }
        Assert.assertSame(b_instance, c_instance);
    }

    @Test
    public void testSimpleDiamondDefiningObjectBasic3() throws OOP4NoSuchMethodException, OOP.Provided.OOP4AmbiguousMethodException {
        // Checks virtual diamond inheritance can solve inherent method ambiguity.
        Assert.assertEquals(MyTestsClasses.SD_D.class,
                simple_diamond_root.definingObject("public_to_inherit").getClass());
    }

    @Test
    public void testSimpleDiamondDefiningObjectBasic4() throws OOP4NoSuchMethodException, OOP4AmbiguousMethodException {
        // Checks method inheritance flows smoothly through regular (multiple) and virtual inheritance together.
        Assert.assertEquals(MyTestsClasses.SD_E.class,
                simple_diamond_root.definingObject("older_public_method").getClass());
    }

    @Test(expected = OOP4AmbiguousMethodException.class)
    public void testSimpleDiamondDefiningObjectBasic5() throws OOP4NoSuchMethodException, OOP4AmbiguousMethodException {
        // Checks virtual diamond inheritance does not solve accidental ambiguity.
        simple_diamond_root.definingObject("almost_accidental_ambiguate");
    }

    @Test
    public void testNonVirtualHybridDefiningObjectBasic1() throws OOP4ObjectInstantiationFailedException {
        // Checks we can hold separate instances for virtual and non-virtual ancestors.
        MyTestsClasses.NVH_A a_obj = new MyTestsClasses.NVH_A();
        Object c_instance = null;
        Object d_instance = null;
        try {
            c_instance = ((OOPObject) a_obj.definingObject("no_ambiguity_C"))
                    .definingObject("public_to_inherit");
            d_instance = ((OOPObject) a_obj.definingObject("no_ambiguity_D"))
                    .definingObject("public_to_inherit");
        } catch (OOP4NoSuchMethodException | OOP4AmbiguousMethodException e) {
            fail();
        }
        Assert.assertNotSame(c_instance, d_instance);
    }

    // addition
    @Test(expected = OOP4AmbiguousMethodException.class)
    public void anotherAmbiguity() throws OOP4AmbiguousMethodException, OOP4ObjectInstantiationFailedException {
        // check some interesting ambiguity
        MyTestsClasses.SD_A a_obj = new MyTestsClasses.SD_A();
        try {
            OOPObject b_obj = ((OOPObject) a_obj.definingObject("jumpToB"));
            b_obj.invoke("getChangeAble");
        }catch (OOP4NoSuchMethodException  | OOP4MethodInvocationFailedException e1){ fail();}
    }

    // addition
    @Test
    public void testSeparateInstance() throws OOP4ObjectInstantiationFailedException {
        // check that the virtual instance and the non-virtual are separate
        MyTestsClasses.SD_A a_obj = new MyTestsClasses.SD_A();
        try {
            OOPObject f_obj = ((OOPObject) a_obj.definingObject("jumpToF"));
            OOPObject p_obj = ((OOPObject) a_obj.definingObject("jumpToP"));
            OOPObject d_obj = ((OOPObject) a_obj.definingObject("jumpToD"));
            f_obj.invoke("changeChangeAble", "this is virtual");
            String changeAbleD = (String)d_obj.invoke("getChangeAble");
            // check that 'changeChangeAble' on G that came from F, change the G that came from D
            Assert.assertEquals("this is virtual", changeAbleD);
            String changeAbleB = (String)p_obj.invoke("getChangeAble");
            // check that 'changeChangeAble' on G that came from F, not change the G that came from P
            Assert.assertEquals("just been initialize", changeAbleB);
        }catch (OOP4NoSuchMethodException | OOP4AmbiguousMethodException | OOP4MethodInvocationFailedException e1){ fail();}
    }
    /* ################################ invoke ############################### */

    @Test
    public void testSimpleDiamondInvokeBasic1() throws OOP4NoSuchMethodException, OOP4AmbiguousMethodException, OOP4MethodInvocationFailedException {
        Assert.assertEquals("This is SD_A's function",
                simple_diamond_root.invoke("root_function"));
    }

    @Test
    public void testSimpleDiamondInvokeBasic2() throws OOP4NoSuchMethodException, OOP4AmbiguousMethodException, OOP4MethodInvocationFailedException {
        int original_id = (Integer) simple_diamond_root.invoke("no_ambiguity_C");
        simple_diamond_root.invoke("no_ambiguity_B");
        Assert.assertEquals(original_id + 1, simple_diamond_root.invoke("no_ambiguity_C"));
    }
}
