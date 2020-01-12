package OOP.Tests.AkivaTests;

import OOP.Provided.OOP4AmbiguousMethodException;
import OOP.Provided.OOP4MethodInvocationFailedException;
import OOP.Provided.OOP4NoSuchMethodException;
import OOP.Provided.OOP4ObjectInstantiationFailedException;
import OOP.Solution.OOPObject;
import OOP.Solution.OOPParent;

import java.util.List;

public class MyTestsClasses {
    private static List<String> construction_order;

    public static void setConstruction_order(List<String> construction_ord) {
        construction_order = construction_ord;
    }

    public static List<String> getConstruction_order() {
        return construction_order;
    }
    /*
    First hierarchy: Simple Diamond.
    Checks:
        - Initializing and invoking methods works with virtual inheritance.
        - Virtual inheritance solves inherent ambiguities.
        - Initialization order - simple check (more advanced ones in future hierarchies).
        - That the inheritance is actually virtual - checking that both ancestors are the same instance (use ==).

                                            SD_E
                                              |
                                           SD_D
                                         //     \\
                                 SD_F  SD_B      SD_C
                                    \     \      /
                                      ----  SD_A
     Second hierarchy: None/Virtual hybrid.
     Checks:
        - Ability to support both virtual and non virtual ancestors of the same type.

                                 NVH_E            NVH_E
                                   |           //       \\
                                 NVH_D       NVH_C      NVH_B
                                    \             \    /
                                     \_____________NVH_A
     */

    // addition
    public static class SD_G extends OOPObject {
        private String changeAble;
        public SD_G() throws OOP4ObjectInstantiationFailedException {
            construction_order.add("G");
            changeAble = "just been initialize";
        }
        public void changeChangeAble(String change){
            changeAble = change;
        }

        public String getChangeAble() {
            return changeAble;
        }
    }

    // addition
    @OOPParent(parent = SD_G.class)
    public static class SD_P extends OOPObject {
        public SD_P() throws OOP4ObjectInstantiationFailedException {
            construction_order.add("P");
        }
        public void jumpToP(){}
    }

    public static class SD_E extends OOPObject {
        public SD_E() throws OOP4ObjectInstantiationFailedException {
            construction_order.add("E");
        }

        public String older_public_method() {
            return "opm_E";
        }
    }

    // addition (only one annotation added)
    @OOPParent(parent = SD_G.class, isVirtual = true)
    @OOPParent(parent = SD_E.class)
    public static class SD_D extends OOPObject {
        public int identifier;
        public SD_D() throws OOP4ObjectInstantiationFailedException {
            construction_order.add("D");
            identifier = 1;
        }

        public void jumpToD(){}

        public int getIdentifier() {
            return this.identifier;
        }

        public void incrementIdentifier() {
            identifier += 1;
        }

        public String public_to_inherit() {
            return "pti_D";
        }
    }

    @OOPParent(parent = SD_D.class, isVirtual = true)
    public static class SD_C extends OOPObject {
        public SD_C() throws OOP4ObjectInstantiationFailedException {
            construction_order.add("C");
        }

        public int no_ambiguity_C() throws OOP4NoSuchMethodException, OOP4MethodInvocationFailedException, OOP4AmbiguousMethodException {
            return (Integer)this.invoke("getIdentifier");
        }

        public String almost_accidental_ambiguate() {
            return "aaa_C";
        }
    }

    // addition (only onr annotation added)
    @OOPParent(parent = SD_G.class)
    @OOPParent(parent = SD_D.class, isVirtual = true)
    public static class SD_B extends OOPObject {
        public SD_B() throws OOP4ObjectInstantiationFailedException {
            construction_order.add("B");
        }

        public void jumpToB(){}
        public void no_ambiguity_B() throws OOP4NoSuchMethodException {
            try{
                this.invoke("incrementIdentifier");
            } catch (OOP4NoSuchMethodException | OOP4AmbiguousMethodException | OOP4MethodInvocationFailedException e) {
                throw new OOP4NoSuchMethodException();
            }
        }

        public String almost_accidental_ambiguate() {
            return "aaa_B";
        }
    }

    @OOPParent(parent = SD_G.class, isVirtual = true)
    public static class SD_F extends OOPObject {
        public SD_F() throws OOP4ObjectInstantiationFailedException {
            construction_order.add("F");
        }
        public void jumpToF(){}
    }

    @OOPParent(parent = SD_F.class)
    @OOPParent(parent = SD_B.class)
    @OOPParent(parent = SD_C.class)
    @OOPParent(parent = SD_P.class, isVirtual = true)
    public static class SD_A extends OOPObject {
        public SD_A() throws OOP4ObjectInstantiationFailedException {
            construction_order.add("A");
        }

        public String root_function() {
            return "This is SD_A's function";
        }
    }

    static class SD_Y1{
        private SD_Y1(){construction_order.add("Y1");}
    }

    @OOPParent(parent = SD_Y1.class)
    public static class SD_Y2 extends OOPObject {
        public SD_Y2() throws OOP4ObjectInstantiationFailedException {
            construction_order.add("Y2");
        }
    }






        /* ########################### Starting Non/Virtual Hybrid ##################### */

        public static class NVH_E extends OOPObject {
            public NVH_E() throws OOP4ObjectInstantiationFailedException {
                construction_order.add("E");
            }

            public String public_to_inherit() {
                return "pti_E";
            }

        }

        @OOPParent(parent = NVH_E.class)
        public static class NVH_D extends OOPObject {
            public NVH_D() throws OOP4ObjectInstantiationFailedException {
                construction_order.add("D");
            }

            public void no_ambiguity_D() {
            }

        }

        @OOPParent(parent = NVH_E.class, isVirtual = true)
        public static class NVH_C extends OOPObject {
            public NVH_C() throws OOP4ObjectInstantiationFailedException {
                construction_order.add("C");
            }

            public void no_ambiguity_C() {
            }

        }

        @OOPParent(parent = NVH_E.class, isVirtual = true)
        public static class NVH_B extends OOPObject {
            public NVH_B() throws OOP4ObjectInstantiationFailedException {
                construction_order.add("B");
            }
        }

        @OOPParent(parent = NVH_D.class)
        @OOPParent(parent = NVH_C.class)
        @OOPParent(parent = NVH_B.class)
        public static class NVH_A extends OOPObject {
            public NVH_A() throws OOP4ObjectInstantiationFailedException {
                construction_order.add("A");
            }
        }
    }

