package domain.data;

import lombok.Value;

@Value(staticConstructor = "of")
public class Team {

  String name;

  @Override
  public String toString() {
    return name;
  }
}
