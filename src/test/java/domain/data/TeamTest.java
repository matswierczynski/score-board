package domain.data;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class TeamTest {

  @Test
  public void shouldMarkSingleCharacterTeamNameAsValid() {
    // given
    final var singleCharacterTeam = Team.of("a");

    // when
    final var isValid = singleCharacterTeam.isValid();

    // then
    assertThat(isValid).isTrue();
  }

  @Test
  public void shouldMarkWhitespaceNamedTeamAsInvalid() {
    // given
    final var whitespaceTeam = Team.of("   ");

    // when
    final var isValid = whitespaceTeam.isValid();

    // then
    assertThat(isValid).isFalse();
  }

}