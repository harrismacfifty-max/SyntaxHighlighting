package highlighting.presets;

import highlighting.regex.Token;
import java.util.List;
import java.util.regex.Pattern;

public final class MiniJavaTokens {

  private MiniJavaTokens() {}

  public static List<Token> defaultTokens() {
    return List.of(
        // Reihenfolge ist wichtig:
        // Spezifische und lange Token zuerst, allgemeine Token später.

        // Javadoc-Kommentare müssen vor normalen Block-Kommentaren stehen,
        // weil /** ... */ sonst auch von /\* ... \*/ getroffen würde.
        Token.of(
            Pattern.compile("/\\*\\*.*?\\*/", Pattern.DOTALL),
            MiniJavaColours.JAVADOC_COMMENT_COLOUR),

        // Normale Block-Kommentare, auch über mehrere Zeilen.
        Token.of(
            Pattern.compile("/\\*(?!\\*).*?\\*/", Pattern.DOTALL),
            MiniJavaColours.BLOCK_COMMENT_COLOUR),

        // Zeilenkommentare bis zum Zeilenende.
        Token.of(Pattern.compile("//[^\\r\\n]*"), MiniJavaColours.LINE_COMMENT_COLOUR),

        // String-Literale mit Escape-Sequenzen.
        Token.of(Pattern.compile("\"([^\"\\\\]|\\\\.)*\""), MiniJavaColours.STRING_LITERAL_COLOUR),

        // Character-Literale, z. B. 'a', '\n', '\''.
        Token.of(Pattern.compile("'([^'\\\\]|\\\\.)'"), MiniJavaColours.CHAR_LITERAL_COLOUR),

        // Annotationen wie @Override, @Test, @Deprecated.
        Token.of(Pattern.compile("@[A-Za-z_$][A-Za-z0-9_$]*"), MiniJavaColours.ANNOTATION_COLOUR),

        // Java-Keywords. Wortgrenzen verhindern Treffer innerhalb längerer Identifier.
        Token.of(
            Pattern.compile(
                "\\b(?:abstract|assert|boolean|break|byte|case|catch|char|class|const|continue|"
                    + "default|do|double|else|enum|extends|final|finally|float|for|goto|if|"
                    + "implements|import|instanceof|int|interface|long|native|new|null|package|"
                    + "private|protected|public|return|short|static|strictfp|super|switch|"
                    + "synchronized|this|throw|throws|transient|try|void|volatile|while|"
                    + "true|false|var|record|sealed|permits|non-sealed|yield)\\b"),
            MiniJavaColours.KEYWORD_COLOUR));
  }
}
