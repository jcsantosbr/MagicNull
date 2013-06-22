package br.com.jcs.magicnull;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class NullHandlerTest {

	private NullHandler handler;
	private TestTarget target;
	private TestTarget safeTarget;

	@Before
	public void setUp() throws Exception {
		handler = new NullHandler();

		target = null;

		safeTarget = handler.safe(target, TestTarget.class);
	}

	@Test
	public void testThatANullObjectReturnsDefaultValuesForPrimitives() {

		assertEquals(0, safeTarget.getInt());
		assertTrue(0.0 == safeTarget.getDouble());
		assertEquals(0L, safeTarget.getLong());
		assertEquals("", safeTarget.getString());
		assertEquals(false, safeTarget.getBoolean());

	}

	@Test
	public void testThatNullObjectsWithWrapperMethodsReturnDefaultValueAsPrimitives() {

		assertEquals(Integer.valueOf(0), safeTarget.getIntegr());
		assertEquals(Double.valueOf(0.0), safeTarget.getDoubleWrapper());
		assertEquals(Boolean.FALSE, safeTarget.getBooleanWrapper());
		assertEquals(Long.valueOf(0L), safeTarget.getLongWrapper());
		assertEquals(Float.valueOf(0.0f), safeTarget.getFloatWrapper());
		assertEquals(Short.valueOf((short) 0), safeTarget.getShortWrapper());
		assertEquals(Character.valueOf('0'), safeTarget.getCharacterWrapper());

	}

	@Test
	public void testThatVoidMethodsDoNothing() {
		StringBuilder builder = new StringBuilder();
		safeTarget.doSomething(builder);
		assertEquals("", builder.toString());
	}

	@Test
	public void testThatMethodsInChildPropertiesAreHandled() {

		assertEquals("", safeTarget.getChildObject().getString());

	}

	@Test
	public void testThatANullChildPropertieInANonNullObjectIsHandled() {

		TestTarget nonNullTarget = new TestTarget();
		safeTarget = handler.safe(nonNullTarget, TestTarget.class);

		assertEquals("bla", safeTarget.getString());

		assertEquals("", safeTarget.getChildObject().getString());

	}

	@Test
	public void testThatChildPropertiesWorkInManyLevels() {

		TestTarget deepLevelTest = new TestTarget();

		deepLevelTest.withChild(new TestTarget()).withChild(new TestTarget())
				.withChild(new TestTarget());

		safeTarget = handler.safe(deepLevelTest, TestTarget.class);

		assertEquals("bla", safeTarget.getString());
		assertEquals("bla", safeTarget.getChildObject().getString());
		assertEquals("bla", safeTarget.getChildObject().getChildObject()
				.getString());
		assertEquals("bla", safeTarget.getChildObject().getChildObject()
				.getChildObject().getString());
		assertEquals("", safeTarget.getChildObject().getChildObject()
				.getChildObject().getChildObject().getString());

	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testThatListAreHandled() {
		List<TestTarget> targets = null;
		targets = handler.safe(targets, List.class);
		
		for (@SuppressWarnings("unused") TestTarget testTarget : targets) {
			
			Assert.fail("shoudn't enter here");
		}
		
		assertEquals(0, targets.size());
		
	}
	
	@Test
	public void testThatClassesWithConstructorAreHandled() {
		TestTargetWithConstructor targetWithConstructor = null;
		TestTargetWithConstructor safeTargetWithConstructor = handler.safe(targetWithConstructor, TestTargetWithConstructor.class);
		
		assertEquals("", safeTargetWithConstructor.getSomeValue());
		
	}

}

class TestTarget {

	private TestTarget childObject;

	public double getDouble() {
		return 1.0;
	};

	public TestTarget withChild(TestTarget testTarget) {
		this.childObject = testTarget;
		return childObject;
	}

	public TestTarget getChildObject() {
		return childObject;
	}

	public void doSomething(StringBuilder builder) {
		builder.append("fill the builder with some content");
	}

	public Character getCharacterWrapper() {
		return 'A';
	}

	public Short getShortWrapper() {
		return (short) 1;
	}

	public Float getFloatWrapper() {
		return 2f;
	}

	public Long getLongWrapper() {
		return 8L;
	}

	public Boolean getBooleanWrapper() {
		return true;
	}

	public Double getDoubleWrapper() {
		return 6.7;
	}

	public Integer getIntegr() {
		return 1;
	}

	public boolean getBoolean() {
		return true;
	}

	public int getInt() {
		return 2;
	}

	public String getString() {
		return "bla";
	}

	public long getLong() {
		return 1L;
	}
}

class TestTargetWithConstructor {
	
	private final String someValue;

	public TestTargetWithConstructor(String someValue) {
		this.someValue = someValue;
	}
	
	public String getSomeValue() {
		return someValue;
	}
	
}
