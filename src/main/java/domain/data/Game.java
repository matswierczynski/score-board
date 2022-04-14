package domain.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Game {

  private final Team homeTeam;

  private final Team awayTeam;

  private int homeTeamScore;

  private int awayTeamScore;

}
