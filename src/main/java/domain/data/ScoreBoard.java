package domain.data;

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
      throw new IllegalArgumentException("Game cannot be started for a null Team");
    }

    if (isOngoingGame(gameToStart)) {
      throw new IllegalStateException("Cannot start unfinished game");
    }

    ongoingGames.add(gameToStart);
    return gameToStart;
  }

  public Set<Game> getSummary() {
    return Collections.unmodifiableSet(ongoingGames);
  }

  private boolean isOngoingGame(final Game game) {
    return ongoingGames.contains(game);
  }
}
