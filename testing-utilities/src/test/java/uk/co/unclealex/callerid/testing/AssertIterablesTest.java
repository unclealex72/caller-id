package uk.co.unclealex.callerid.testing;

import static ch.lambdaj.Lambda.on;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Test;

import com.google.common.collect.Lists;

public class AssertIterablesTest {

  static TestingObject one = testingObject(1, "one");
  static TestingObject two = testingObject(2, "two");
  static TestingObject three = testingObject(3, "three");
  
  static List<TestingObject> listA = Lists.newArrayList(one, two, three);
  static List<TestingObject> listB = Lists.newArrayList(three, two, one);
  static List<TestingObject> listC = Lists.newArrayList(three, three, two);
  
  @Test
  public void testNullsEqual() {
    test(null, null);
  }

  @Test(expected=AssertionError.class)
  public void testNullNotNullEqual() {
    test(null, listA);
  }

  @Test(expected=AssertionError.class)
  public void testNotNullNullEqual() {
    test(listA, null);
  }

  @Test
  public void testIdentity() {
    test(listA, listA);    
  }
  
  @Test
  public void testDifferentOrdersEqual() {
    test(listA, listB);    
  }

  @Test(expected=AssertionError.class)
  public void testDifferentElementsNotEqual() {
    test(listA, listC);    
  }

  protected void test(Iterable<TestingObject> expecteds, Iterable<TestingObject> actuals) {
    AssertIterables.iterablesEqual("My message", on(TestingObject.class).getName(), expecteds, actuals);
  }
  
  static TestingObject testingObject(int id, String name) {
    return new TestingObject(id, name);
  }
  
  static class TestingObject {
    
    private final int id;
    private final String name;

    public TestingObject(int id, String name) {
      super();
      this.id = id;
      this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
      return EqualsBuilder.reflectionEquals(this, obj);
    }
    
    @Override
    public int hashCode() {
      return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
      return ToStringBuilder.reflectionToString(this);
    }
    
    public int getId() {
      return id;
    }

    public String getName() {
      return name;
    }
    
    
  }
}
