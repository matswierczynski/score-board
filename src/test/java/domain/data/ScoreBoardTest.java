package domain.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.tuple;

import domain.exceptions.DuplicatedGameKeyException;
import domain.exceptions.IllegalGameException;
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

    assertThat(scoreBoard.getSummary())
        .singleElement()
        .isEqualTo(startedGame);
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
    assertThat(thrown)
        .isInstanceOf(IllegalGameException.class)
        .hasMessage("Game cannot be started for a null team.");
  }

  @Test
  public void shouldThrowExceptionWhenUnfinishedGameIsAddedOneMoreTime() {
    // given
    final var startedGame = scoreBoard.startGame(HOME_TEAM, AWAY_TEAM);

    // when
    final var thrown = catchThrowable(() -> scoreBoard.startGame(HOME_TEAM, AWAY_TEAM));

    // then
    assertThat(thrown)
        .isInstanceOf(DuplicatedGameKeyException.class)
        .hasMessage("New game cannot be started for Home Team and Away Team until the ongoing game is not finished.");
    assertThat(scoreBoard.getSummary())
        .singleElement()
        .isEqualTo(startedGame);
  }

  @Test
  public void shouldFinishGameAndRemoveItFromScoreboard() {
    // given
    final var firstStartedGame = scoreBoard.startGame(HOME_TEAM, AWAY_TEAM);
    final var secondStartedGame = scoreBoard.startGame(AWAY_TEAM, HOME_TEAM);

    // when
    scoreBoard.finishGame(firstStartedGame);

    // then
    assertThat(scoreBoard.getSummary())
        .singleElement()
        .isEqualTo(secondStartedGame);
  }

  @Test
  public void shouldNotThrowAnyErrorWhenGameToBeFinishedDoesNotExist() {
    // given
    final var startedGame = scoreBoard.startGame(HOME_TEAM, AWAY_TEAM);
    final var additionalGameToStart = Game.of(AWAY_TEAM, HOME_TEAM);

    // when
    scoreBoard.finishGame(additionalGameToStart);

    // then
    assertThat(scoreBoard.getSummary())
        .singleElement()
        .isEqualTo(startedGame);
  }

}