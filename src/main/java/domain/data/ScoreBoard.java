package domain.data;

import domain.exceptions.DuplicatedGameKeyException;
import domain.exceptions.GameNotFoundException;
import domain.exceptions.IllegalGameException;
import java.time.Clock;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ScoreBoard {

  private final Clock clock;
  private final Set<Game> ongoingGames;

  public ScoreBoard(final Clock clock) {
    this.clock = clock;
    this.ongoingGames = new HashSet<>();
  }

  public Game startGame(final Team homeTeam, final Team awayTeam) {
    final var gameToStart = Game.of(homeTeam, awayTeam, clock.instant());

    if (!gameToStart.isValidGame()) {
      throw new IllegalGameException("Game cannot be started for a null team.");
    }

    if (isOngoingGame(gameToStart)) {
      throw new DuplicatedGameKeyException(String.format("New game cannot be started for %s and %s until the ongoing game is not finished.", homeTeam.getName(), awayTeam.getName()));
    }

    ongoingGames.add(gameToStart);
    return gameToStart;
  }

  public void updateScore(final Game game, final int homeTeamScore, final int awayTeamScore) {
    if (isOngoingGame(game)) {
      game.updateScore(homeTeamScore, awayTeamScore);
    } else {
      throw new GameNotFoundException("Cannot update score of an unknown game");
    }
  }

  public void finishGame(final Game game) {
    ongoingGames.remove(game);
  }

  public List<Game> getSummary() {
    return ongoingGames.stream()
        .sorted(getReversedTotalScoreAndStartDateComparator())
        .collect(Collectors.toUnmodifiableList());
  }

  private boolean isOngoingGame(final Game game) {
    return ongoingGames.contains(game);
  }

  private Comparator<Game> getReversedTotalScoreAndStartDateComparator() {
    return Comparator
        .comparingInt((Game game) -> game.getHomeTeamScore() + game.getAwayTeamScore())
        .thenComparing(Game::getStartDate)
        .reversed();
  }

  @Override
  public String toString() {
    final var stringifiedGames = ongoingGames.stream()
        .sorted(getReversedTotalScoreAndStartDateComparator())
        .map(Game::toString)
        .collect(Collectors.joining("\n"));
    return "\n" + stringifiedGames + "\n";
  }
}
