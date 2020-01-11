package OOP.Solution;

import OOP.Provided.OOP4AmbiguousMethodException;
import OOP.Provided.OOP4MethodInvocationFailedException;
import OOP.Provided.OOP4NoSuchMethodException;
import OOP.Provided.OOP4ObjectInstantiationFailedException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Method;

public class OOPObject {

    ArrayList<Object> directParents;
    Map<String, Object> virtualAncestors;
    private static Map<String, Object> staticVirtualAncestors;
    private static boolean isStaticVirtualAncestorsInitiated = false; // True if staticVirtualAncestors was initiated.
    private boolean isMostDerived = false; // True if the constructor was called for the most derived object.

    static private void initStaticVirtualAncestors(OOPParent[] parents) throws OOP4ObjectInstantiationFailedException{
        if (parents.length == 0) return;    //since now getAnnotationsByType returns array of size 0 if none was found
        for (OOPParent i : parents) {
            if (i.isVirtual()) {
                // If it inherits virtually, first check if the object was already initiated.
                /*if (Modifier.isPrivate(constructor.getModifiers())) {
                    throw new OOP4ObjectInstantiationFailedException();
                }*/ //TODO: check if really need to check private or it will auto throw.
                try {
                    if (!staticVirtualAncestors.containsKey(i.parent().getSimpleName())) { //TODO getSimpleName or getName
                        Constructor constructor = i.parent().getDeclaredConstructor();
                        staticVirtualAncestors.put(i.parent().getSimpleName(), constructor.newInstance());
                    }
                }catch(Exception e){
                    throw new OOP4ObjectInstantiationFailedException();
                }
            }
            else{       //TODO: if here is ment for not sending the virtual classes because they are built.
                initStaticVirtualAncestors(i.parent().getAnnotationsByType(OOPParent.class));
            }
        }
    }


    public OOPObject() throws OOP4ObjectInstantiationFailedException {
        directParents = new ArrayList<>();
        if (!isStaticVirtualAncestorsInitiated) {
            staticVirtualAncestors = new HashMap<>();
            isMostDerived = true;
            isStaticVirtualAncestorsInitiated = true;
        }
        Class<?> c = this.getClass();
        //OOPParents annotation = c.getAnnotation(OOPParents.class); // For some reason this sometimes return the container as null
        OOPParent[] parents = c.getAnnotationsByType(OOPParent.class); // This fixed it
        if (parents.length != 0) {  //since now getAnnotationsByType returns array of size 0 if none was found
            try {
                // Create the virtual ancestors structure.
                initStaticVirtualAncestors(parents);

                for (OOPParent i : parents) {
                    if (!i.isVirtual()) {
                        // If the inheritance is non-virtual, create an instance.
                        Constructor constructor = i.parent().getDeclaredConstructor();
                /*if (Modifier.isPrivate(constructor.getModifiers())) {
                    throw new OOP4ObjectInstantiationFailedException();
                }*/ //TODO: check if really need to check private or it will auto throw.
                        directParents.add(constructor.newInstance());
                    } else {
                        // In this case the inheritance is virtual, so the object was already initiated previously.
                        directParents.add(staticVirtualAncestors.get(i.parent().getSimpleName()));
                    }
                }
            } catch (Exception e) {
                throw new OOP4ObjectInstantiationFailedException();
            }
        }
        if (isMostDerived) {
            virtualAncestors = staticVirtualAncestors;
            isStaticVirtualAncestorsInitiated = false;
            isMostDerived = false;
        }
    }

    public boolean multInheritsFrom(Class<?> cls) {
       for (Object i : directParents) { //TODO: check if all are OOPboject childs.
           if ( (i.getClass() == cls) ||
               (!(i instanceof  OOPObject) && cls.isAssignableFrom(i.getClass())) ||
               (i instanceof OOPObject && ((OOPObject) i).multInheritsFrom(cls)) ) {
               return true;
           }
       }
       return false;
    }

    /**
     * Receives a class, a method name and arguments and checks if the method is defined within the class. The purpose
     * is to avoid the exception thrown from getMethod.
     */
    static boolean isMethodSelfDefined(Class<?> c, String methodName, Class<?> ...argTypes) {
        try {
            c.getMethod(methodName, argTypes); //TODO if there are no protected methods this should be fine
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public Object definingObject(String methodName, Class<?> ...argTypes)
            throws OOP4AmbiguousMethodException, OOP4NoSuchMethodException {
        ArrayList<Object> definers = new ArrayList<>(); // Hold all of the objects defining the method.
        if (isMethodSelfDefined(this.getClass(), methodName, argTypes)) return this; // Check if the current class defines the method.

        // Check if any of the objects in directParents defines the methods
        for (Object i : directParents) {
            if (i instanceof OOPObject) {
                // If the object is of type OOPObject, call recursively.
               try {
                   Object o = ((OOPObject) i).definingObject(methodName, argTypes);
                   if (!definers.contains(o)) {
                       definers.add(o);
                   }
               } catch (OOP4NoSuchMethodException ignored) {} // Ignore this exception, continue iterating.
            }
            else {
                // If the object is of type Object, use getMethod.
                if (isMethodSelfDefined(i.getClass(), methodName, argTypes)) definers.add(i);
            }
        }
        if (definers.size() > 1) {
            throw new OOP4AmbiguousMethodException();
        }
        else if (definers.size() == 0) {
            throw new OOP4NoSuchMethodException();
        }

        return definers.get(0);
    }

    public Object invoke(String methodName, Object... callArgs) throws
            OOP4AmbiguousMethodException, OOP4NoSuchMethodException, OOP4MethodInvocationFailedException {
        Class<?>[] args = new Class<?>[callArgs.length];
        for (int i = 0 ; i < callArgs.length ; i++) {
            args[i] = callArgs[i].getClass();
        }
        Object o = definingObject(methodName, args);
        try {
            Method m = o.getClass().getMethod(methodName, args);
            return m.invoke(o, callArgs);
        } catch (Exception e) {
            throw new OOP4MethodInvocationFailedException();
        }
    }
}
