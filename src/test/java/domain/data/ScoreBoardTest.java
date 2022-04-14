package domain.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.tuple;

import org.junit.Before;
import org.junit.Test;

public class ScoreBoardTest {

  private static final Team HOME_TEAM = Team.of("Home Team");
  private static final Team AWAY_TEAM = Team.of("Away Team");

  private ScoreBoard scoreBoard;

  @Before
  public void setup() {
    scoreBoard = new ScoreBoard();
  }

  @Test
  public void shouldInitializeNewGameBetweenTeams() {
    // when
    final Game startedGame = scoreBoard.startGame(HOME_TEAM, AWAY_TEAM);

    // then
    assertThat(startedGame.getHomeTeam()).isEqualTo(HOME_TEAM);
    assertThat(startedGame.getAwayTeam()).isEqualTo(AWAY_TEAM);
    assertThat(startedGame)
        .extracting(Game::getAwayTeamScore, Game::getHomeTeamScore)
        .allSatisfy(score -> assertThat(score).isEqualTo(0));
  }

  @Test
  public void shouldInitializeMultipleNewGames() {
    // given
    scoreBoard.startGame(HOME_TEAM, AWAY_TEAM);

    // when
    scoreBoard.startGame(AWAY_TEAM, HOME_TEAM);

    // then
    assertThat(scoreBoard.getSummary())
        .hasSize(2)
        .extracting(Game::getHomeTeam, Game::getAwayTeam)
        .containsExactlyInAnyOrder(
            tuple(HOME_TEAM, AWAY_TEAM),
            tuple(AWAY_TEAM, HOME_TEAM)
        );
  }

  @Test
  public void shouldThrowExceptionDuringNewGameInitializationIfAnyTeamIsNull() {
    // when
    final var thrown = catchThrowable(() -> scoreBoard.startGame(null, HOME_TEAM));

    // then
    assertThat(thrown).isInstanceOf(RuntimeException.class);
  }

  @Test
  public void shouldThrowExceptionWhenUnfinishedGameIsAddedOneMoreTime() {
    // given
    scoreBoard.startGame(HOME_TEAM, AWAY_TEAM);

    // when
    final var thrown = catchThrowable(() -> scoreBoard.startGame(HOME_TEAM, AWAY_TEAM));

    // then
    assertThat(thrown).isInstanceOf(RuntimeException.class);
    assertThat(scoreBoard.getSummary()).hasSize(1);
  }


}