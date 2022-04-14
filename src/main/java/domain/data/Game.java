package domain.data;

import java.util.Objects;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "of")
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Game {

  @EqualsAndHashCode.Include
  private final Team homeTeam;

  @EqualsAndHashCode.Include
  private final Team awayTeam;

  private int homeTeamScore;

  private int awayTeamScore;

  boolean isValidGame() {
    return Objects.nonNull(homeTeam) && Objects.nonNull(awayTeam);
  }
}
