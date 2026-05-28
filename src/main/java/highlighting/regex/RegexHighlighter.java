package highlighting.regex;

import highlighting.core.HighlightRegion;
import highlighting.core.SyntaxHighlighter;
import highlighting.presets.MiniJavaTokens;
import java.util.ArrayList;
import java.util.List;

public class RegexHighlighter extends SyntaxHighlighter {

  @Override
  public List<HighlightRegion> collectMatches(String text) {
    List<HighlightRegion> regions = new ArrayList<>();

    for (Token token : MiniJavaTokens.defaultTokens()) {
      regions.addAll(token.test(text));
    }

    return regions;
  }

  @Override
  public List<HighlightRegion> resolveConflicts(List<HighlightRegion> regions) {
    List<HighlightRegion> resolved = new ArrayList<>();

    for (HighlightRegion candidate : regions) {
      if (!overlapsAny(candidate, resolved)) {
        resolved.add(candidate);
      }
    }

    return resolved;
  }

  private boolean overlapsAny(HighlightRegion candidate, List<HighlightRegion> acceptedRegions) {
    for (HighlightRegion accepted : acceptedRegions) {
      if (overlaps(candidate, accepted)) {
        return true;
      }
    }

    return false;
  }

  private boolean overlaps(HighlightRegion first, HighlightRegion second) {
    return first.start() < second.end() && second.start() < first.end();
  }
}
