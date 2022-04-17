package domain.data;

import java.time.Instant;
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

  private final Instant startDate;

  private int homeTeamScore;

  private int awayTeamScore;

  boolean isValidGame() {
    return Objects.nonNull(homeTeam) && Objects.nonNull(awayTeam) && homeTeam.isValid() && awayTeam.isValid();
  }

  void updateScore(final int homeTeamScore, final int awayTeamScore) {
    this.homeTeamScore = homeTeamScore;
    this.awayTeamScore = awayTeamScore;
  }

  @Override
  public String toString() {
    return homeTeam.toString() + " " + homeTeamScore + " - " + awayTeam.getName() + " " + awayTeamScore;
  }
}
