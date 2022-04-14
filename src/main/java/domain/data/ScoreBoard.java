package domain.data;

import domain.exceptions.DuplicatedGameKeyException;
import domain.exceptions.IllegalGameException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ScoreBoard {

  private final Set<Game> ongoingGames;

  public ScoreBoard() {
    this.ongoingGames = new HashSet<>();
  }

  public Game startGame(final Team homeTeam, final Team awayTeam) {
    final var gameToStart = Game.of(homeTeam, awayTeam);

    if (!gameToStart.isValidGame()) {
      throw new IllegalGameException("Game cannot be started for a null team.");
    }

    if (isOngoingGame(gameToStart)) {
      throw new DuplicatedGameKeyException(String.format("New game cannot be started for %s and %s until the ongoing game is not finished.", homeTeam.getName(), awayTeam.getName()));
    }

    ongoingGames.add(gameToStart);
    return gameToStart;
  }

  public void finishGame(final Game game) {
    ongoingGames.remove(game);
  }

  public Set<Game> getSummary() {
    return Collections.unmodifiableSet(ongoingGames);
  }

  private boolean isOngoingGame(final Game game) {
    return ongoingGames.contains(game);
  }
}
