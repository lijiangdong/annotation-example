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

    private static Class<?> clazz;

    public static void inject(Activity activity){

        //获取activity的Class类
        clazz = activity.getClass();
        injectContent(activity);
        injectView(activity);
        injectEvent(activity);
    }

    public static void unInject(){
        clazz = null;
    }

    /**
     * 对ContentView注解惊醒解析
     * @param activity
     */
    private static void injectContent(Activity activity){

        //取的Activity中的ContentView注解
        ContentView contentView = clazz.getAnnotation(ContentView.class);
        if (contentView != null){

            //取出ContentView注解中的值
            int id = contentView.value();
            try {

                //获取Activity中setContentView方法,执行setContentView方法为Activity设置ContentView
                //在这一步中我们也可以直接使用 activity.setContentView(id) 来设置ContentView
                clazz.getMethod("setContentView",Integer.TYPE).invoke(activity,id);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 对InjectView注解进行解析
     * @param activity
     */
    private static void injectView(Activity activity){

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields){
            Inject inject = field.getAnnotation(Inject.class);
            if (inject != null){
                int id = inject.value();
                try {
                    //这一步中同样也能够使用 Object view = activity.findViewById(id) 来获取View
                    Object view = clazz.getMethod("findViewById",Integer.TYPE).invoke(activity,id);
                    field.set(activity,view);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * 对OnClick注解进行解析
     * @param activity
     */
    private static void injectEvent(Activity activity){

        Method[] methods = clazz.getMethods();

        for (Method method : methods) {
            OnClick onClick = method.getAnnotation(OnClick.class);
            if (onClick != null){
                int[] ids = onClick.value();
                MyInvocationHandler handler = new MyInvocationHandler(activity,method);

                //通过Java中的动态代理来执行View.OnClickListener
                Object listenerProxy = Proxy.newProxyInstance(
                        View.OnClickListener.class.getClassLoader(),
                        new Class<?>[] { View.OnClickListener.class }, handler);
                for (int id : ids) {

                    try {
                        Object view = clazz.getMethod("findViewById",Integer.TYPE).invoke(activity,id);
                        Method listenerMethod = view.getClass()
                                .getMethod("setOnClickListener", View.OnClickListener.class);
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