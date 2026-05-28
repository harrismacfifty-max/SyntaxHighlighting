package highlighting.regex;

import static org.junit.jupiter.api.Assertions.*;

import highlighting.core.HighlightRegion;
import highlighting.presets.MiniJavaColours;
import java.awt.Color;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link RegexHighlighter}. */
class RegexHighlighterTest {

  @Test
  void givenSimpleKeywordsWithoutOverlaps_whenComputeRegions_thenAllKeywordRegionsRemain() {
    // given
    RegexHighlighter highlighter = new RegexHighlighter();
    String text = "public class Demo { return null; }";

    // when
    List<HighlightRegion> regions = highlighter.computeRegions(text);

    // then
    List<HighlightRegion> keywordRegions =
        regionsWithColour(regions, MiniJavaColours.KEYWORD_COLOUR);

    assertEquals(4, keywordRegions.size());
    assertRegion(keywordRegions.get(0), 0, 6, MiniJavaColours.KEYWORD_COLOUR); // public
    assertRegion(keywordRegions.get(1), 7, 12, MiniJavaColours.KEYWORD_COLOUR); // class
    assertRegion(keywordRegions.get(2), 20, 26, MiniJavaColours.KEYWORD_COLOUR); // return
    assertRegion(keywordRegions.get(3), 27, 31, MiniJavaColours.KEYWORD_COLOUR); // null
  }

  @Test
  void givenStringAndCharWithoutOverlaps_whenComputeRegions_thenBothRegionsRemain() {
    // given
    RegexHighlighter highlighter = new RegexHighlighter();
    String text = "\"hello\" 'x'";

    // when
    List<HighlightRegion> regions = highlighter.computeRegions(text);

    // then
    List<HighlightRegion> stringRegions =
        regionsWithColour(regions, MiniJavaColours.STRING_LITERAL_COLOUR);
    List<HighlightRegion> charRegions =
        regionsWithColour(regions, MiniJavaColours.CHAR_LITERAL_COLOUR);

    assertEquals(1, stringRegions.size());
    assertRegion(stringRegions.get(0), 0, 7, MiniJavaColours.STRING_LITERAL_COLOUR);

    assertEquals(1, charRegions.size());
    assertRegion(charRegions.get(0), 8, 11, MiniJavaColours.CHAR_LITERAL_COLOUR);
  }

  @Test
  void givenKeywordInsideLineComment_whenComputeRegions_thenCommentWinsAndKeywordIsRemoved() {
    // given
    RegexHighlighter highlighter = new RegexHighlighter();
    String text = "// return null";

    // when
    List<HighlightRegion> regions = highlighter.computeRegions(text);

    // then
    assertEquals(1, regions.size());
    assertRegion(regions.get(0), 0, 14, MiniJavaColours.LINE_COMMENT_COLOUR);
  }

  @Test
  void givenKeywordInsideBlockComment_whenComputeRegions_thenBlockCommentWinsAndKeywordIsRemoved() {
    // given
    RegexHighlighter highlighter = new RegexHighlighter();
    String text = "/* public class return null */";

    // when
    List<HighlightRegion> regions = highlighter.computeRegions(text);

    // then
    assertEquals(1, regions.size());
    assertRegion(regions.get(0), 0, 30, MiniJavaColours.BLOCK_COMMENT_COLOUR);
  }

  @Test
  void givenJavadocCommentWithKeywordText_whenComputeRegions_thenJavadocWins() {
    // given
    RegexHighlighter highlighter = new RegexHighlighter();
    String text = "/** return null */";

    // when
    List<HighlightRegion> regions = highlighter.computeRegions(text);

    // then
    assertEquals(1, regions.size());
    assertRegion(regions.get(0), 0, 18, MiniJavaColours.JAVADOC_COMMENT_COLOUR);
  }

  @Test
  void givenCommentLikeTextInsideString_whenComputeRegions_thenStringWins() {
    // given
    RegexHighlighter highlighter = new RegexHighlighter();
    String text = "\"not // comment and not /* block */\"";

    // when
    List<HighlightRegion> regions = highlighter.computeRegions(text);

    // then
    assertEquals(1, regions.size());
    assertRegion(regions.get(0), 0, 36, MiniJavaColours.STRING_LITERAL_COLOUR);
  }

  @Test
  void givenAdjacentRegions_whenResolveConflicts_thenBothRegionsRemain() {
    // given
    RegexHighlighter highlighter = new RegexHighlighter();
    HighlightRegion first = new HighlightRegion(0, 5, Color.RED);
    HighlightRegion second = new HighlightRegion(5, 10, Color.BLUE);

    // when
    List<HighlightRegion> resolved = highlighter.resolveConflicts(List.of(first, second));

    // then
    assertEquals(2, resolved.size());
    assertEquals(first, resolved.get(0));
    assertEquals(second, resolved.get(1));
  }

  @Test
  void givenOverlappingRegions_whenResolveConflicts_thenFirstRegionWins() {
    // given
    RegexHighlighter highlighter = new RegexHighlighter();
    HighlightRegion first = new HighlightRegion(0, 10, Color.RED);
    HighlightRegion second = new HighlightRegion(3, 8, Color.BLUE);

    // when
    List<HighlightRegion> resolved = highlighter.resolveConflicts(List.of(first, second));

    // then
    assertEquals(1, resolved.size());
    assertEquals(first, resolved.get(0));
  }

  @Test
  void
      givenNaiveCollectionWithKeywordInsideComment_whenCollectMatches_thenBothMatchesAreCollected() {
    // given
    RegexHighlighter highlighter = new RegexHighlighter();
    String text = "// return";

    // when
    List<HighlightRegion> matches = highlighter.collectMatches(text);

    // then
    // collectMatches ist absichtlich naiv:
    // Es sammelt erst einmal Kommentar und Keyword.
    // Die Konfliktauflösung passiert später in resolveConflicts().
    assertEquals(2, matches.size());

    assertTrue(
        matches.stream()
            .anyMatch(
                region ->
                    region.start() == 0
                        && region.end() == 9
                        && region.colour().equals(MiniJavaColours.LINE_COMMENT_COLOUR)));

    assertTrue(
        matches.stream()
            .anyMatch(
                region ->
                    region.start() == 3
                        && region.end() == 9
                        && region.colour().equals(MiniJavaColours.KEYWORD_COLOUR)));
  }

  @Test
  void givenEmptyString_whenComputeRegions_thenNoRegionsAreReturned() {
    // given
    RegexHighlighter highlighter = new RegexHighlighter();

    // when
    List<HighlightRegion> regions = highlighter.computeRegions("");

    // then
    assertTrue(regions.isEmpty());
  }

  @Test
  void givenTextWithoutTokens_whenComputeRegions_thenNoRegionsAreReturned() {
    // given
    RegexHighlighter highlighter = new RegexHighlighter();
    String text = "ordinaryIdentifier anotherIdentifier";

    // when
    List<HighlightRegion> regions = highlighter.computeRegions(text);

    // then
    assertTrue(regions.isEmpty());
  }

  private static List<HighlightRegion> regionsWithColour(
      List<HighlightRegion> regions, Color colour) {
    return regions.stream().filter(region -> region.colour().equals(colour)).toList();
  }

  private static void assertRegion(
      HighlightRegion region, int expectedStart, int expectedEnd, Color expectedColour) {
    assertEquals(expectedStart, region.start(), "start index");
    assertEquals(expectedEnd, region.end(), "end index");
    assertEquals(expectedColour, region.colour(), "highlight colour");
  }
}
