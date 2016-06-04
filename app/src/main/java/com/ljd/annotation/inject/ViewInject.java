package com.ljd.annotation.inject;

import android.app.Activity;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by ljd on 5/30/16.
 */
public class ViewInject {

    public static void inject(Activity activity){

        injectView(activity);
        injectEvent(activity);
    }

    private static void injectView(Activity activity){
        Class<?> clazz = activity.getClass();

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields){
            Inject inject = field.getAnnotation(Inject.class);
            if (inject != null){
                int id = inject.value();
                try {
                    Object view = activity.findViewById(id);
                    field.set(activity,view);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private static void injectEvent(Activity activity){

        Class<? extends Activity> clazz = activity.getClass();
        Method[] methods = clazz.getMethods();

        for (Method method : methods) {
            OnClick onClick = method.getAnnotation(OnClick.class);
            if (onClick != null){
                int[] viewIds = onClick.value();
                MyInvocationHandler handler = new MyInvocationHandler(activity,method);

                Object listenerProxy = Proxy.newProxyInstance(
                        View.OnClickListener.class.getClassLoader(),
                        new Class<?>[] { View.OnClickListener.class }, handler);
                for (int viewId : viewIds) {

                    try {
                        View view = activity.findViewById(viewId);
                        Method listenerMethod = view.getClass().getMethod("setOnClickListener", View.OnClickListener.class);
                        listenerMethod.invoke(view, listenerProxy);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }


    static class MyInvocationHandler implements InvocationHandler {

        private Object target = null;
        private Method method = null;

        public MyInvocationHandler(Object target,Method method) {
            super();
            this.target = target;
            this.method = method;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            return this.method.invoke(target,args);
        }
    }
}