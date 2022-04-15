package domain.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import domain.exceptions.DuplicatedGameKeyException;
import domain.exceptions.GameNotFoundException;
import domain.exceptions.IllegalGameException;
import java.time.Clock;
import java.time.Instant;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ScoreBoardTest {

  private static final Team MEXICO_TEAM = Team.of("Mexico");
  private static final Team CANADA_TEAM = Team.of("Canada");
  private static final Team SPAIN_TEAM = Team.of("Spain");
  private static final Team BRAZIL_TEAM = Team.of("Brazil");
  private static final Team GERMANY_TEAM = Team.of("Germany");
  private static final Team URUGUAY_TEAM = Team.of("Uruguay");
  private static final Team ARGENTINA_TEAM = Team.of("Argentina");
  private static final Team AUSTRALIA_TEAM = Team.of("Australia");
  private static final Instant NOW = Instant.now();

  @InjectMocks
  private ScoreBoard scoreBoard;

  @Mock
  private Clock clock;

  private int passedSeconds;

  @Before
  public void setup() {
    when(clock.instant()).thenAnswer(invocation -> {
      passedSeconds++;
      return NOW.plusSeconds(passedSeconds);
    });
  }

  @Test
  public void shouldInitializeNewGameBetweenTeams() {
    // when
    final Game startedGame = scoreBoard.startGame(MEXICO_TEAM, CANADA_TEAM);

    // then
    assertThat(startedGame.getHomeTeam()).isEqualTo(MEXICO_TEAM);
    assertThat(startedGame.getAwayTeam()).isEqualTo(CANADA_TEAM);
    assertThat(startedGame)
        .extracting(Game::getAwayTeamScore, Game::getHomeTeamScore)
        .allSatisfy(score -> assertThat(score).isEqualTo(0));
    assertThat(startedGame.getStartDate()).isEqualTo(NOW.plusSeconds(1));

    assertThat(scoreBoard.getSummary())
        .singleElement()
        .isEqualTo(startedGame);

    verify(clock).instant();
  }

  @Test
  public void shouldInitializeMultipleNewGames() {
    // given
    scoreBoard.startGame(MEXICO_TEAM, CANADA_TEAM);

    // when
    scoreBoard.startGame(CANADA_TEAM, MEXICO_TEAM);

    // then
    assertThat(scoreBoard.getSummary())
        .hasSize(2)
        .extracting(Game::getHomeTeam, Game::getAwayTeam, Game::getStartDate)
        .containsExactlyInAnyOrder(
            tuple(MEXICO_TEAM, CANADA_TEAM, NOW.plusSeconds(1)),
            tuple(CANADA_TEAM, MEXICO_TEAM, NOW.plusSeconds(2))
        );

    verify(clock, times(2)).instant();
  }

  @Test
  public void shouldThrowExceptionDuringNewGameInitializationIfAnyTeamIsNull() {
    // when
    final var thrown = catchThrowable(() -> scoreBoard.startGame(null, MEXICO_TEAM));

    // then
    assertThat(thrown)
        .isInstanceOf(IllegalGameException.class)
        .hasMessage("Game cannot be started for a null team.");
  }

  @Test
  public void shouldThrowExceptionWhenUnfinishedGameIsAddedOneMoreTime() {
    // given
    final var startedGame = scoreBoard.startGame(MEXICO_TEAM, CANADA_TEAM);

    // when
    final var thrown = catchThrowable(() -> scoreBoard.startGame(MEXICO_TEAM, CANADA_TEAM));

    // then
    assertThat(thrown)
        .isInstanceOf(DuplicatedGameKeyException.class)
        .hasMessage(String.format("New game cannot be started for %s and %s until the ongoing game is not finished.", MEXICO_TEAM.getName(), CANADA_TEAM.getName()));
    assertThat(scoreBoard.getSummary())
        .singleElement()
        .isEqualTo(startedGame);
  }

  @Test
  public void shouldFinishGameAndRemoveItFromScoreboard() {
    // given
    final var firstStartedGame = scoreBoard.startGame(MEXICO_TEAM, CANADA_TEAM);
    final var secondStartedGame = scoreBoard.startGame(CANADA_TEAM, MEXICO_TEAM);

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
    final var startedGame = scoreBoard.startGame(MEXICO_TEAM, CANADA_TEAM);
    final var additionalGameToStart = Game.of(CANADA_TEAM, MEXICO_TEAM, NOW);

    // when
    scoreBoard.finishGame(additionalGameToStart);

    // then
    assertThat(scoreBoard.getSummary())
        .singleElement()
        .isEqualTo(startedGame);
  }

  @Test
  public void shouldUpdateScoreOfOngoingGame() {
    // given
    final var firstStartedGame = scoreBoard.startGame(MEXICO_TEAM, CANADA_TEAM);
    scoreBoard.startGame(CANADA_TEAM, MEXICO_TEAM);

    // when
    scoreBoard.updateScore(firstStartedGame, 2, 1);

    // then
    assertThat(firstStartedGame)
        .extracting(Game::getHomeTeamScore, Game::getAwayTeamScore, Game::getStartDate)
        .containsExactly(2, 1, NOW.plusSeconds(1));

    assertThat(scoreBoard.getSummary())
        .extracting(Game::getHomeTeamScore, Game::getAwayTeamScore)
        .containsExactlyInAnyOrder(
            tuple(2, 1),
            tuple(0, 0)
        );

    verify(clock, times(2)).instant();
  }

  @Test
  public void shouldThrowExceptionWhenGameToBeUpdatedDoesNotExist() {
    // given
    scoreBoard.startGame(MEXICO_TEAM, CANADA_TEAM);
    final var scheduledGame = Game.of(CANADA_TEAM, MEXICO_TEAM, NOW);

    // when
    final var thrown = catchThrowable(() -> scoreBoard.updateScore(scheduledGame, 1, 0));

    // then
    assertThat(thrown)
        .isInstanceOf(GameNotFoundException.class)
        .hasMessage("Cannot update score of an unknown game");

    assertThat(scoreBoard.getSummary())
        .singleElement()
        .extracting(Game::getHomeTeamScore, Game::getAwayTeamScore)
        .containsExactly(0, 0);
  }

  @Test
  public void shouldSummarizeOngoingGamesInDescendingOrderByStartDate() {
    // given
    final var firstGame = scoreBoard.startGame(MEXICO_TEAM, SPAIN_TEAM);
    final var secondGame = scoreBoard.startGame(CANADA_TEAM, BRAZIL_TEAM);
    final var thirdGame = scoreBoard.startGame(GERMANY_TEAM, URUGUAY_TEAM);
    final var fourthGame = scoreBoard.startGame(ARGENTINA_TEAM, AUSTRALIA_TEAM);

    scoreBoard.updateScore(firstGame, 2, 1);
    scoreBoard.updateScore(secondGame, 6, 1);
    scoreBoard.updateScore(thirdGame, 3, 3);
    scoreBoard.updateScore(fourthGame, 1, 5);

    // when
    final var gamesSummary = scoreBoard.getSummary();
    assertThat(gamesSummary).containsExactly(secondGame, fourthGame, thirdGame, firstGame);

    final var summaryBuilder = new StringBuilder();
    stringifyGame(summaryBuilder, secondGame);
    stringifyGame(summaryBuilder, fourthGame);
    stringifyGame(summaryBuilder, thirdGame);
    stringifyGame(summaryBuilder, firstGame);

    assertThat(gamesSummary.toString()).isEqualTo(summaryBuilder.toString());
  }

  private void stringifyGame(final StringBuilder builder, final Game game) {
    builder.append(game.getHomeTeam().getName());
    builder.append(" ");
    builder.append(game.getHomeTeamScore());
    builder.append(" - ");
    builder.append(game.getAwayTeam().getName());
    builder.append(" ");
    builder.append(game.getAwayTeamScore());
    builder.append("\n");
  }

}