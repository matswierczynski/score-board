package domain.data;

import java.util.Objects;
import lombok.Value;

@Value(staticConstructor = "of")
public class Team {

  String name;

  boolean isValid() {
    return Objects.nonNull(name) && !name.isBlank();
  }

  @Override
  public String toString() {
    return name;
  }
}
