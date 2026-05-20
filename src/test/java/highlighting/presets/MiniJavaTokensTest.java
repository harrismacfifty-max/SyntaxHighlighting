package highlighting.presets;

import static org.junit.jupiter.api.Assertions.*;

import highlighting.core.HighlightRegion;
import java.awt.Color;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.Test;

class MiniJavaTokensTest {

  @Test
  void givenStringAtBeginning_whenTokensAreApplied_thenStringRegionIsFound() {
    // given
    String text = "\"hello\" int x = 1;";

    // when
    List<HighlightRegion> regions = regionsForColour(text, MiniJavaColours.STRING_LITERAL_COLOUR);

    // then
    assertSingleRegion(regions, 0, 7);
  }

  @Test
  void givenStringInMiddle_whenTokensAreApplied_thenStringRegionIsFound() {
    // given
    String text = "var s = \"hello\";";

    // when
    List<HighlightRegion> regions = regionsForColour(text, MiniJavaColours.STRING_LITERAL_COLOUR);

    // then
    assertSingleRegion(regions, 8, 15);
  }

  @Test
  void givenStringAtEnd_whenTokensAreApplied_thenStringRegionIsFound() {
    // given
    String text = "return \"done\"";

    // when
    List<HighlightRegion> regions = regionsForColour(text, MiniJavaColours.STRING_LITERAL_COLOUR);

    // then
    assertSingleRegion(regions, 7, 13);
  }

  @Test
  void givenStringContainingCommentLikeText_whenTokensAreApplied_thenWholeStringIsFound() {
    // given
    String text = "String s = \"not // a comment and not /* block */\";";

    // when
    List<HighlightRegion> regions = regionsForColour(text, MiniJavaColours.STRING_LITERAL_COLOUR);

    // then
    assertSingleRegion(regions, 11, 49);
  }

  @Test
  void givenSeveralStrings_whenTokensAreApplied_thenAllStringRegionsAreFound() {
    // given
    String text = "\"a\" + \"b\" + \"c\"";

    // when
    List<HighlightRegion> regions = regionsForColour(text, MiniJavaColours.STRING_LITERAL_COLOUR);

    // then
    assertEquals(3, regions.size());
    assertRegion(regions.get(0), 0, 3);
    assertRegion(regions.get(1), 6, 9);
    assertRegion(regions.get(2), 12, 15);
  }

  @Test
  void givenCharacterLiteral_whenTokensAreApplied_thenCharacterRegionIsFound() {
    // given
    String text = "char c = 'x';";

    // when
    List<HighlightRegion> regions = regionsForColour(text, MiniJavaColours.CHAR_LITERAL_COLOUR);

    // then
    assertSingleRegion(regions, 9, 12);
  }

  @Test
  void givenEscapedCharacterLiteral_whenTokensAreApplied_thenCharacterRegionIsFound() {
    // given
    String text = "char newline = '\\n';";

    // when
    List<HighlightRegion> regions = regionsForColour(text, MiniJavaColours.CHAR_LITERAL_COLOUR);

    // then
    assertSingleRegion(regions, 15, 19);
  }

  @Test
  void givenKeywordsAsWholeWords_whenTokensAreApplied_thenKeywordRegionsAreFound() {
    // given
    String text = "public final class Demo { return null; }";

    // when
    List<HighlightRegion> regions = regionsForColour(text, MiniJavaColours.KEYWORD_COLOUR);

    // then
    assertEquals(5, regions.size());
    assertRegion(regions.get(0), 0, 6); // public
    assertRegion(regions.get(1), 7, 12); // final
    assertRegion(regions.get(2), 13, 18); // class
    assertRegion(regions.get(3), 26, 32); // return
    assertRegion(regions.get(4), 33, 37); // null
  }

  @Test
  void givenKeywordInsideIdentifier_whenTokensAreApplied_thenNoKeywordRegionIsFound() {
    // given
    String text = "classification newValue returnValue";

    // when
    List<HighlightRegion> regions = regionsForColour(text, MiniJavaColours.KEYWORD_COLOUR);

    // then
    assertTrue(regions.isEmpty());
  }

  @Test
  void givenAnnotationAtLineBeginning_whenTokensAreApplied_thenAnnotationRegionIsFound() {
    // given
    String text = "@Override\npublic String toString() { return \"x\"; }";

    // when
    List<HighlightRegion> regions = regionsForColour(text, MiniJavaColours.ANNOTATION_COLOUR);

    // then
    assertSingleRegion(regions, 0, 9);
  }

  @Test
  void givenAnnotationAfterWhitespace_whenTokensAreApplied_thenAnnotationRegionIsFound() {
    // given
    String text = "    @Deprecated";

    // when
    List<HighlightRegion> regions = regionsForColour(text, MiniJavaColours.ANNOTATION_COLOUR);

    // then
    assertSingleRegion(regions, 4, 15);
  }

  @Test
  void givenLineComment_whenTokensAreApplied_thenCommentRegionEndsBeforeLineBreak() {
    // given
    String text = "int x = 1; // return null\nint y = 2;";

    // when
    List<HighlightRegion> regions = regionsForColour(text, MiniJavaColours.LINE_COMMENT_COLOUR);

    // then
    assertSingleRegion(regions, 11, 25);
  }

  @Test
  void givenSeveralLineComments_whenTokensAreApplied_thenAllLineCommentsAreFound() {
    // given
    String text = "// first\nint x = 1;\n// second";

    // when
    List<HighlightRegion> regions = regionsForColour(text, MiniJavaColours.LINE_COMMENT_COLOUR);

    // then
    assertEquals(2, regions.size());
    assertRegion(regions.get(0), 0, 8);
    assertRegion(regions.get(1), 20, 29);
  }

  @Test
  void givenBlockComment_whenTokensAreApplied_thenBlockCommentRegionIsFound() {
    // given
    String text = "int x; /* block return null */ int y;";

    // when
    List<HighlightRegion> regions = regionsForColour(text, MiniJavaColours.BLOCK_COMMENT_COLOUR);

    // then
    assertSingleRegion(regions, 7, 30);
  }

  @Test
  void givenMultilineBlockComment_whenTokensAreApplied_thenWholeBlockCommentRegionIsFound() {
    // given
    String text = "a /* first line\nsecond line */ b";

    // when
    List<HighlightRegion> regions = regionsForColour(text, MiniJavaColours.BLOCK_COMMENT_COLOUR);

    // then
    assertSingleRegion(regions, 2, 30);
  }

  @Test
  void givenJavadocComment_whenTokensAreApplied_thenJavadocRegionIsFound() {
    // given
    String text = "/**\n * return null\n */\npublic class Demo {}";

    // when
    List<HighlightRegion> regions = regionsForColour(text, MiniJavaColours.JAVADOC_COMMENT_COLOUR);

    // then
    assertSingleRegion(regions, 0, 22);
  }

  @Test
  void givenPlainTextWithoutTokens_whenTokensAreApplied_thenNoRegionsAreFound() {
    // given
    String text = "ordinaryIdentifier anotherIdentifier";

    // when
    List<HighlightRegion> regions = allRegions(text);

    // then
    assertTrue(regions.isEmpty());
  }

  private static List<HighlightRegion> regionsForColour(String text, Color colour) {
    return allRegions(text).stream()
        .filter(region -> region.colour().equals(colour))
        .sorted(Comparator.comparingInt(HighlightRegion::start))
        .toList();
  }

  private static List<HighlightRegion> allRegions(String text) {
    return MiniJavaTokens.defaultTokens().stream()
        .map(token -> token.test(text))
        .flatMap(List::stream)
        .sorted(Comparator.comparingInt(HighlightRegion::start))
        .toList();
  }

  private static void assertSingleRegion(
      List<HighlightRegion> regions, int expectedStart, int expectedEnd) {
    assertEquals(1, regions.size(), "expected exactly one highlight region");
    assertRegion(regions.get(0), expectedStart, expectedEnd);
  }

  private static void assertRegion(HighlightRegion region, int expectedStart, int expectedEnd) {
    assertEquals(expectedStart, region.start(), "start index");
    assertEquals(expectedEnd, region.end(), "end index");
  }
}
