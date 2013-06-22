package br.com.jcs.magicnull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;

public class NullHandler {

	private final class MethodHandlerImplementation<T> implements MethodHandler {

		private final T originalObject;

		public MethodHandlerImplementation(T object) {
			this.originalObject = object;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object invoke(Object self, Method method, Method proceed,
				Object[] args) throws Throwable {

			Class<?> returnType = method.getReturnType();

			if (originalObject != null) {

				Object originalResult = method.invoke(originalObject, args);

				if (DEFAULT_VALUE_BY_CLASS.containsKey(returnType))
					return originalResult;

				return new NullHandler().<T> safe((T) originalResult,
						(Class<T>) returnType);

			}

			if (method.getDefaultValue() != null)
				return method.getDefaultValue();

			if (DEFAULT_VALUE_BY_CLASS.containsKey(returnType))
				return DEFAULT_VALUE_BY_CLASS.get(returnType);

			return safe(null, method.getReturnType());
		}
	}

	private final static Map<Class<?>, Object> DEFAULT_VALUE_BY_CLASS = createDefaultValuesForClasses();

	public <T> T safe(T object, Class<? extends T> clazz) {

		ProxyFactory factory = new ProxyFactory();
		if (clazz.isInterface()) {
			factory.setInterfaces(new Class[] { clazz });
		} else {
			factory.setSuperclass(clazz);
		}

		MethodHandler handler = new MethodHandlerImplementation<T>(object);
		
		

		return createProxyInstance(factory, handler, clazz);
	}

	@SuppressWarnings("unchecked")
	private <T> T createProxyInstance(ProxyFactory factory, MethodHandler handler, Class<? extends T> clazz ) {
		try {
			
			Constructor<?>[] constructors = clazz.getConstructors();
			
			Class<?>[] paramTypes = constructors.length == 0 ? new Class<?>[0] :  constructors[0].getParameterTypes();
			Object[] args = constructors.length == 0 ? new Object[0] : new Object[paramTypes.length];
			
			return (T) factory.create(paramTypes, args, handler);
			
		} catch (Exception e) {
			throw new RuntimeException("Couldn't create instance!", e);
		}
	}

	private static Map<Class<?>, Object> createDefaultValuesForClasses() {

		Map<Class<?>, Object> result = new HashMap<Class<?>, Object>();

		result.put(int.class, 0);
		result.put(Integer.class, 0);
		result.put(boolean.class, false);
		result.put(Boolean.class, false);
		result.put(String.class, "");
		result.put(double.class, 0.0);
		result.put(Double.class, 0.0);
		result.put(float.class, 0.0f);
		result.put(Float.class, 0f);
		result.put(long.class, 0L);
		result.put(Long.class, 0L);
		result.put(short.class, (short) 0);
		result.put(Short.class, (short) 0);
		result.put(byte.class, (byte) 0);
		result.put(Byte.class, (byte) 0);
		result.put(char.class, '0');
		result.put(Character.class, '0');
		result.put(void.class, new Object());

		return result;
	}

}
