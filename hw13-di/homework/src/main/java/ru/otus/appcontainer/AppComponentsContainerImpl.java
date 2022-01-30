package ru.otus.appcontainer;

import org.reflections.Reflections;
import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

public class AppComponentsContainerImpl implements AppComponentsContainer {

    private static final String ERR_NO_COMPONENT_FOUND_BY_CLASS = "Не обнаружен компонент типа {}";
    private static final String ERR_TOO_MUCH_COMPONENT_FOUND_BY_CLASS = "Обнаружено несколько вариантов реализации компонента типа {}";

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

    public AppComponentsContainerImpl(String configPath) {
        Reflections reflections = new Reflections(configPath);
        Set<Class<?>> configSet = reflections.getTypesAnnotatedWith(AppComponentsContainerConfig.class);
        handleConfigArray(new ArrayList<>(configSet));
    }

    public AppComponentsContainerImpl(Class<?> ... initialConfigClasses) {
        handleConfigArray(Arrays.stream(initialConfigClasses).collect(Collectors.toList()));
    }

    public AppComponentsContainerImpl(Class<?> initialConfigClass) {
        processConfig(initialConfigClass);
    }

    private void processConfig(Class<?> configClass){
        checkConfigClass(configClass);
        // You code here...
        Object config;
        try {
            config = configClass.getConstructor().newInstance();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
            Arrays
                    .stream(configClass.getMethods())
                    .filter(m->m.isAnnotationPresent(AppComponent.class))
                    .sorted(Comparator.comparingInt(m -> m.getAnnotation(AppComponent.class).order()))
                    .forEach(m->{
                        // получаем список входных параметров для создания компонента
                        Object[] inParams = getObjects4InParams(m);
                        // вызываем сам метод и создаем компонент
                        createAndSetComponent(config, m, inParams);
                    });

    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", configClass.getName()));
        }
    }

    @Override
    public <C> C getAppComponent(Class<C> componentClass) {
        List<Object> components =
                appComponents
                .stream()
                .filter(c->c.getClass() == componentClass ||
                        Arrays.stream(c.getClass().getInterfaces()).anyMatch(i->i == componentClass))
                .collect(Collectors.toList());
        if (components.size() == 0) {
            throw new RuntimeException(String.format(ERR_NO_COMPONENT_FOUND_BY_CLASS,componentClass.getName()));
        } else if (components.size() > 1) {
            throw new RuntimeException(String.format(ERR_TOO_MUCH_COMPONENT_FOUND_BY_CLASS,componentClass.getName()));
        } else {
            return (C)components.get(0);
        }
    }

    @Override
    public <C> C getAppComponent(String componentName) {
        return (C)appComponentsByName.get(componentName);
    }

    private void handleConfigArray(List<Class<?>> configs) {
        configs
                .stream()
                .filter(cnf->cnf.isAnnotationPresent(AppComponentsContainerConfig.class))
                .sorted(Comparator.comparingInt(cnf -> cnf.getAnnotation(AppComponentsContainerConfig.class).order()))
                .forEach(this::processConfig);
    }

    private Object[] getObjects4InParams(Method method) {
        return Arrays
                .stream(method.getParameters())
                .map(p -> appComponents
                        .stream()
                        .filter(c -> c.getClass() == p.getType() ||
                                Arrays.stream(c.getClass().getInterfaces()).anyMatch(i -> i == p.getType()))
                        .findFirst().orElseThrow(() -> {
                            throw new RuntimeException(String.format(ERR_NO_COMPONENT_FOUND_BY_CLASS, p.getType().getName()));
                        })).toArray();
    }

    private void createAndSetComponent(Object config, Method m, Object[] inParams) {
        try {
            Object newBean = m.invoke(config,inParams);
            appComponents.add(newBean);
            appComponentsByName.put(m.getAnnotation(AppComponent.class).name(),newBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
