package demo;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.function.Function;

public abstract class TestUtils {

 public static <T> LambdaMatcher<T> lambaMatcher(String d,
  Function<T, Boolean> f) {
  return new LambdaMatcher<>(f, d);
 }

 // taken from
 // https://gist.github.com/GuiSim/e1d1cde0ab66302ae45c
 private static class LambdaMatcher<T> extends BaseMatcher<T> {

  private final Function<T, Boolean> matcher;

  private final String description;

  public LambdaMatcher(Function<T, Boolean> matcher, String description) {
   this.matcher = matcher;
   this.description = description;
  }

  @Override
  public boolean matches(Object argument) {
   return matcher.apply((T) argument);
  }

  @Override
  public void describeTo(Description description) {
   description.appendText(this.description);
  }
 }
}
