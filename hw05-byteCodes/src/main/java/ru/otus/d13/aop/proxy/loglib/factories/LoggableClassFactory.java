package ru.otus.d13.aop.proxy.loglib.factories;

import ru.otus.d13.aop.proxy.loglib.annotations.Log;
import ru.otus.d13.aop.someclasses.DoSomething;
import ru.otus.d13.aop.someclasses.SomeInterface;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Optional;

public class LoggableClassFactory {

    private static final HashMap<Class,Class> implFactory = new HashMap<>(){{put(SomeInterface.class, DoSomething.class);}};

    public static <T> T createImplWithLoggingPrms(Class<T> srcInterface) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<T> implClazz = implFactory.get(srcInterface);
        if (implClazz == null) {
            System.out.println("Implementation does not defined");
            return null;
        }
        Constructor<T> cnstr = implClazz.getConstructor();
        T inst = cnstr.newInstance();
        ImplementationLoggableCreatorInvocationHandler<T> handler =new ImplementationLoggableCreatorInvocationHandler<T>(inst);
        return (T)Proxy.newProxyInstance(LoggableClassFactory.class.getClassLoader(),new Class<?>[]{srcInterface},handler);
    }

    static class ImplementationLoggableCreatorInvocationHandler<C> implements InvocationHandler {
        private final C implClass;

        ImplementationLoggableCreatorInvocationHandler(C implClass) {
            this.implClass = implClass;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (implClass.getClass().getMethod(method.getName(),method.getParameterTypes()).getDeclaredAnnotation(Log.class) != null) {
                printMethodAndParams(method,args);
            }
            return method.invoke(implClass, args);
        }

        private void printMethodAndParams(Method method, Object[] args) {
            StringBuilder sb = new StringBuilder("Called method ").append(method.getDeclaringClass()).append(".").append(method.getName());
             for (int i = 0; i < method.getParameterCount(); i++) {
                 Parameter prm = method.getParameters()[i];
                 sb.append ((i == 0) ? "(" : ", ")
                         .append("/*")
                         .append(prm.getType().getSimpleName())
                         .append("*/")
                         .append(prm.getName())
                         .append(" = ")
                         .append( args[i]);
             }
            System.out.println(sb.append( ")"));
        }

        @Override
        public String toString() {
            return "ImplementationLoggableCreatorInvocationHandler{" + "implClass=" + implClass +'}';
        }
    }
}
