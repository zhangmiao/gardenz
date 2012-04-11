package org.walkmanz.gardenz.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 反射工具类
 * 
 * @author zhangmiao
 *
 */
public class ReflectUtils {

	public static boolean extendsClass(final Class<?> clazz, String className) {
        Class<?> superClass = clazz.getSuperclass();

        while (superClass != null) {
            if (superClass.getName().equals(className)) {
                return true;
            }
            superClass = superClass.getSuperclass();

        }
        return false;
    }
	
	public static ClassLoader getClassLoader(){
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null) {
			loader = ReflectUtils.class.getClassLoader();
		}
		return loader;
	}
	
	public static Object createInstance(String clazz) {
		Object obj = null;
		try {
			obj = createInstance(Class.forName(clazz));
		} catch (ClassNotFoundException e) {
			throw new ReflectException("reflect map to bean fail, " + e.getMessage());
		}
		return obj;
	}
	

	public static Object createInstance(Class<?> clazz) {
		Object obj = null;
		try {
		    obj = clazz.newInstance();
		} catch (InstantiationException e) {
			throw new ReflectException("reflect map to bean fail, " + e.getMessage());
		} catch (IllegalAccessException e) {
			throw new ReflectException("reflect map to bean fail, " + e.getMessage());
		}
		return obj;
	}

	public static Map<String, Object> beanToMap(Object entity,
			String clazz) {
		Map<String, Object> map = null;
		try {
			map = beanToMap(entity, Class.forName(clazz));
		} catch (ClassNotFoundException e) {
			throw new ReflectException("reflect bean to map fail, " + e.getMessage());
		}
		return map;
	}
	
	public static Map<String, Object> beanToMap(Object entity,
			Class<?> clazz) {
		Object fieldValue = null;
		String fieldName = null;
		List<Field> fields = getAllFields(clazz);
		Map<String, Object> fieldMap = new HashMap<String, Object>();
		for (Field field : fields) {
			fieldName = field.getName();
			if (field.getModifiers() == Modifier.PUBLIC) {
				try {
					fieldValue = field.get(entity);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} else {
				try{
					fieldValue = invokeGet(entity, fieldName);
				}catch(Exception e){
					throw new RuntimeException(e);
				}
			}
			if (fieldValue != null
					&& fieldValue.getClass().getName().equals(clazz.getName())) {
				fieldValue = beanToMap(fieldValue, clazz);
			}
			fieldMap.put(fieldName, fieldValue);
		}
		return fieldMap;
	}
	

	/**
	 * 
	 * @param map
	 * @param clazz
	 * @return
	 */
	public static Object mapToBean(Map<String, Object> map, String clazz) {
		Object obj = null;
		try {
			obj = mapToBean(map, Class.forName(clazz));
		} catch (ClassNotFoundException e) {
			throw new ReflectException("reflect map to bean fail, " + e.getMessage());
		}
		return obj;
	}

	/**
	 * 
	 * @param map
	 * @param cls
	 * @return
	 */
	public static <T> T mapToBean(Map<String, Object>  map, Class<T> cls) {
		T obj = null;

		try {
			obj = cls.newInstance();
		} catch (InstantiationException e) {
			throw new ReflectException("reflect map to bean fail, " + e.getMessage());
		} catch (IllegalAccessException e) {
			throw new ReflectException("reflect map to bean fail, " + e.getMessage());
		}

		// 取出bean里的所有方法
		List<Method> methods = getAllMethods(cls);
		for (int i = 0; i < methods.size(); i++) {
			// 取方法名
			String method = methods.get(i).getName();
			// 取出方法的类型
			Class<?>[] cc = methods.get(i).getParameterTypes();
			if (cc.length != 1)
				continue;

			// 如果方法名没有以set开头的则退出本次for
			if (method.indexOf("set") < 0)
				continue;
			// 类型
			String type = cc[0].getSimpleName();

			// 转成小写
			// Object value = method.substring(3).toLowerCase();
			Object value = method.substring(3, 4).toLowerCase()
					+ method.substring(4);
			// 如果map里有该key
			if (map.containsKey(value) && map.get(value) != null) {
				// 调用其底层方法
				try {
					setValue(type, map.get(value), i, methods, obj);
				} catch (IllegalArgumentException e) {
					throw new ReflectException("reflect map to bean fail, " + e.getMessage());
				} catch (IllegalAccessException e) {
					throw new ReflectException("reflect map to bean fail, " + e.getMessage());
				} catch (InvocationTargetException e) {
					throw new ReflectException("reflect map to bean fail, " + e.getMessage());
				}
			}
		}
		return obj;
	}

	public static List<Class<?>> getAllClasses(Class<?> clazz){
		List<Class<?>> list = new ArrayList<Class<?>>();
		if(clazz.getSuperclass() != null){
			list = getAllClasses(clazz.getSuperclass());
		}
		list.add(clazz);
		return list;
	}
	
	public static List<Field> getAllFields(Class<?> clazz){
		List<Class<?>> classes  = getAllClasses(clazz);
		List<Field> fields = new ArrayList<Field>();
		for(Class<?> c : classes){
			fields.addAll(Arrays.asList(c.getDeclaredFields()));
		}
		return fields;
	}

	public static List<Method> getAllMethods(Class<?> clazz){
		List<Class<?>> classes  = getAllClasses(clazz);
		List<Method> methods = new ArrayList<Method>();
		for(Class<?> c : classes){
			methods.addAll(Arrays.asList(c.getDeclaredMethods()));
		}
		return methods;
	}
	
	private static void setValue(String type, Object value, int i,
			List<Method> methods, Object bean) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (value != null && !value.equals("")) {

			if (type.equals("String")) {
				// 第一个参数:从中调用基础方法的对象 第二个参数:用于方法调用的参数
				methods.get(i).invoke(bean, new Object[] { value });
			} else if (type.equals("int") || type.equals("Integer")) {
				methods.get(i).invoke(bean, new Object[] { new Integer(""
						+ value) });
			} else if (type.equals("long") || type.equals("Long")) {
				methods.get(i).invoke(bean,
						new Object[] { new Long("" + value) });
			} else if (type.equals("float") || type.equals("Float")) {
				methods.get(i).invoke(bean,
						new Object[] { new Float("" + value) });
			} else if (type.equals("double") || type.equals("Double")) {
				methods.get(i).invoke(bean,
						new Object[] { new Double("" + value) });
			} else if (type.equals("boolean") || type.equals("Boolean")) {
				methods.get(i).invoke(bean, new Object[] { new Boolean(""
						+ value) });
			} else if (type.equals("BigDecimal")) {
				methods.get(i).invoke(bean, new Object[] { new BigDecimal(""
						+ value) });
			} else if (type.equals("Date")) {
				Date date = null;
				if (value.getClass().getName().equals("java.util.Date")) {
					date = (Date) value;
				} else {
					String format = ((String) value).indexOf(":") > 0 ? "yyyy-MM-dd hh:mm:ss"
							: "yyyy-MM-dd";
					date = DateUtils.convertStrToDate((String) (value),
							format);
				}
				if (date != null) {
					methods.get(i).invoke(bean, new Object[] { date });
				}
			} else if (type.equals("byte[]")) {
				methods.get(i).invoke(bean,
						new Object[] { new String(value + "").getBytes() });
			}
		}
	}
	
	private static Object invokeGet(Object entity, String fieldName) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		String methodPrefix = "get";
        if(fieldName.substring(0, 2).equals("is")){
        	fieldName = fieldName.substring(2);
        	methodPrefix = "is";
        }
    	String prefix = fieldName.substring(0,1);
    	String suffix = fieldName.substring(1);
        Method method = entity.getClass().getMethod(
        		methodPrefix + prefix.toUpperCase() + suffix);
        return method.invoke(entity);
        
    }
}
