package OOP.Tests.AkivaTests.AnotherClass;

import OOP.Provided.OOP4ObjectInstantiationFailedException;
import OOP.Solution.OOPObject;
import OOP.Solution.OOPParent;

// those classes only meant to check invoking protected constructor from a derived class
public class anotherClasess {
    static class SD_L1{
        protected SD_L1(){}
    }
    @OOPParent(parent = SD_L1.class)
    public static class SD_L2 extends OOPObject {
        public SD_L2() throws OOP4ObjectInstantiationFailedException { }
    }
}
