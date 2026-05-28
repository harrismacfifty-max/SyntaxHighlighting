package highlighting.core;

import java.awt.Color;
import java.util.Objects;

/** Represents a highlighted region within the text together with its colour. */
public final class HighlightRegion {
  private final int start;
  private final int end;
  private final Color colour;

  /**
   * @param start the start offset of the highlighted region (inclusive)
   * @param end the end offset of the highlighted region (exclusive)
   * @param colour the colour used to highlight this region
   */
  public HighlightRegion(int start, int end, Color colour) {
    this.start = start;
    this.end = end;
    this.colour = Objects.requireNonNull(colour, "colour must not be null");
  }

  public int start() {
    return start;
  }

  public int end() {
    return end;
  }

  public Color colour() {
    return colour;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof HighlightRegion that)) {
      return false;
    }

    return start == that.start && end == that.end && Objects.equals(colour, that.colour);
  }

  @Override
  public int hashCode() {
    return Objects.hash(start, end, colour);
  }

  @Override
  public String toString() {
    return "HighlightRegion{" + "start=" + start + ", end=" + end + ", colour=" + colour + '}';
  }
}
