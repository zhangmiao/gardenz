package org.walkmanz.gardenz.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 反射工具类
 * 
 * @author zhangmiao
 * 
 * @param <T>
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
	

	public static Object createInstance(Class clazz) {
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
			Class clazz) {
		Class c = entity.getClass();
		Object fieldValue = null;
		String fieldName = null;
		Field[] fields = c.getDeclaredFields();
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
	public static Object mapToBean(Map map, String clazz) {
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
	public static Object mapToBean(Map map, Class cls) {
		Object obj = null;

		try {
			obj = cls.newInstance();
		} catch (InstantiationException e) {
			throw new ReflectException("reflect map to bean fail, " + e.getMessage());
		} catch (IllegalAccessException e) {
			throw new ReflectException("reflect map to bean fail, " + e.getMessage());
		}

		// 取出bean里的所有方法
		Method[] methods = cls.getMethods();
		for (int i = 0; i < methods.length; i++) {
			// 取方法名
			String method = methods[i].getName();
			// 取出方法的类型
			Class[] cc = methods[i].getParameterTypes();
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


	private static void setValue(String type, Object value, int i,
			Method[] method, Object bean) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (value != null && !value.equals("")) {

			if (type.equals("String")) {
				// 第一个参数:从中调用基础方法的对象 第二个参数:用于方法调用的参数
				method[i].invoke(bean, new Object[] { value });
			} else if (type.equals("int") || type.equals("Integer")) {
				method[i].invoke(bean, new Object[] { new Integer(""
						+ value) });
			} else if (type.equals("long") || type.equals("Long")) {
				method[i].invoke(bean,
						new Object[] { new Long("" + value) });
			} else if (type.equals("float") || type.equals("Float")) {
				method[i].invoke(bean,
						new Object[] { new Float("" + value) });
			} else if (type.equals("double") || type.equals("Double")) {
				method[i].invoke(bean,
						new Object[] { new Double("" + value) });
			} else if (type.equals("boolean") || type.equals("Boolean")) {
				method[i].invoke(bean, new Object[] { new Boolean(""
						+ value) });
			} else if (type.equals("BigDecimal")) {
				method[i].invoke(bean, new Object[] { new BigDecimal(""
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
					method[i].invoke(bean, new Object[] { date });
				}
			} else if (type.equals("byte[]")) {
				method[i].invoke(bean,
						new Object[] { new String(value + "").getBytes() });
			}
		}
	}
	
	private static Object invokeGet(Object entity, String fieldName) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
       
    	String prefix = fieldName.substring(1);
    	String suffix = fieldName.substring(0,1);
        Method method = entity.getClass().getMethod(
                "get" + prefix.toUpperCase() + suffix);
        return method.invoke(entity);
        
    }
}
