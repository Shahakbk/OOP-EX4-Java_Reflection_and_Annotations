package OOP.Solution;

import OOP.Provided.OOP4AmbiguousMethodException;
import OOP.Provided.OOP4MethodInvocationFailedException;
import OOP.Provided.OOP4NoSuchMethodException;
import OOP.Provided.OOP4ObjectInstantiationFailedException;

import javax.lang.model.type.ArrayType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.lang.reflect.Method;
import java.util.stream.Stream;

public class OOPObject {

    ArrayList<Object> directParents;

    public OOPObject() throws OOP4ObjectInstantiationFailedException {
        Class<?> c = this.getClass(); //TODO make sure this is necessary
        OOPParents annotation = c.getAnnotation(OOPParents.class);
        OOPParent[] parents = annotation.value();
        try {
            for(OOPParent i : parents) {
                Constructor constructor = i.parent().getDeclaredConstructor();
                /*if (Modifier.isPrivate(constructor.getModifiers())) {
                    throw new OOP4ObjectInstantiationFailedException();
                }*/ //TODO: check if really need to check private or it will auto throw.
                directParents.add(constructor.newInstance());
            }
        } catch (Exception e) {
            throw new OOP4ObjectInstantiationFailedException();
        }
    }

    public boolean multInheritsFrom(Class<?> cls) {
       for (Object i : directParents) { //TODO: check if all are OOPboject childs.
           if ( (i.getClass() == cls) ||
               (i.getClass().isAssignableFrom(cls)) ||
               (i instanceof OOPObject && ((OOPObject) i).multInheritsFrom(cls)) ) {
               return true;
           }
       }
       return false;
    }

   /* public Object definingObject(String methodName, Class<?> ...argTypes)
            throws OOP4AmbiguousMethodException, OOP4NoSuchMethodException {
        ArrayList<Object> definers = new ArrayList<>();
        try {
            this.getClass().getMethod(methodName, argTypes); //TODO if there are no protected methods this should be fine
            return this;
        } catch (Exception e) {
            for(Object i : directParents){
                try {
                    i.getClass().getMethod(methodName, argTypes);
                    definers.add(i);
                }catch (Exception ex){
                    if(i instanceof OOPObject){
                        Object o = ((OOPObject)i).definingObject(methodName,argTypes);
                        if(o != null){
                        definers.add(o);
                        }
                    }
                }

            }
        }
        if(definers.size()>1){
            throw new OOP4AmbiguousMethodException();
        }
        else if(definers.size()==0) {
            throw new OOP4NoSuchMethodException();
        }
        return definers.get(0);
    }*/

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
        ArrayList<Object> definers = new ArrayList<>();
        if (isMethodSelfDefined(this.getClass(), methodName, argTypes)) return this; // Check if the current class defines the method.

        // Check if any of the objects in directParents defines the methods
        for (Object i : directParents) {
            if (i instanceof OOPObject) {
                // If the object is of type OOPObject, call recursively.
               try {
                   Object o = ((OOPObject) i).definingObject(methodName, argTypes);
                   definers.add(o);
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
            Method m = o.getClass().getDeclaredMethod(methodName, args);
            return m.invoke(o, callArgs);
        } catch (Exception e) {
            throw new OOP4MethodInvocationFailedException();
        }
    }
}
