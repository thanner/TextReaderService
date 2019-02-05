package edu.stanford.nlp.parser.lexparser;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import edu.stanford.nlp.io.EncodingPrintWriter;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.ling.WordTag;
import edu.stanford.nlp.ling.Tag;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.stats.ClassicCounter;
import edu.stanford.nlp.trees.Tree;

/**
 * Stores, trains, and scores with an unknown word model.  A couple
 * of filters deterministically force rewrites for certain proper
 * nouns, dates, and cardinal and ordinal numbers; when none of these
 * filters are met, either the distribution of terminals with the same
 * first character is used, or Good-Turing smoothing is used. Although
 * this is developed for Chinese, the training and storage methods
 * could be used cross-linguistically.
 *
 * @author Roger Levy
 */
public class ChineseUnknownWordModel extends BaseUnknownWordModel {

  private static final String encoding = "GB18030"; // used only for debugging

  boolean useUnicodeType = false;


  /* These strings are stored in ascii-stype Unicode encoding.  To
   * edit them, either use the Unicode codes or use native2ascii or a
   * similar program to convert the file into a Chinese encoding, then
   * convert back. */
  private static final String numberMatch = ".*[0-9\uff10-\uff19\u4e00\u4e8c\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u5341\u767e\u5343\u4e07\u4ebf\u96F6\u3007\u25cb\u25ef].*";
  private static final String dateMatch = numberMatch + "[\u5e74\u6708\u65e5\u53f7]";
  private static final String ordinalMatch = "\u7b2c.*";
  // uses midDot characters as one clue of being proper name
  private static final String properNameMatch = ".*[\u00b7\u0387\u2022\u2024\u2027\u2219\u22C5\u30FB].*";

  private Set<String> seenFirst = new HashSet<String>();


  public ChineseUnknownWordModel(Options.LexOptions op, Lexicon lex) {
    super(op, lex);
    useFirst = true;
    if (ChineseLexicon.useGoodTuringUnknownWordModel) {
      useGoodTuring();
    }
    this.useUnicodeType = op.useUnicodeType;
  }

  final void useGoodTuring() {
    useGT = true;
    useFirst = false;
  }

  @Override
  public float score(IntTaggedWord itw) {
    String word = itw.wordString();
    // Label tagL = itw.tagLabel();
    // String tag = tagL.value();
    String tag = itw.tagString();
    Label tagL = new Tag(tag);

    float logProb;

    if (VERBOSE) EncodingPrintWriter.out.println("Scoring unknown word |" + word + "| with tag " + tag, encoding);

    if (word.matches(dateMatch)) {
      //EncodingPrintWriter.out.println("Date match for " + word,encoding);
      if (tag.equals("NT")) {
        logProb = 0.0f;
      } else {
        logProb = Float.NEGATIVE_INFINITY;
      }
    } else if (word.matches(numberMatch)) {
      //EncodingPrintWriter.out.println("Number match for " + word,encoding);
      if (tag.equals("CD") && (!word.matches(ordinalMatch))) {
        logProb = 0.0f;
      } else if (tag.equals("OD") && word.matches(ordinalMatch)) {
        logProb = 0.0f;
      } else {
        logProb = Float.NEGATIVE_INFINITY;
      }
    } else if (word.matches(properNameMatch)) {
      //EncodingPrintWriter.out.println("Proper name match for " + word,encoding);
      if (tag.equals("NR")) {
        logProb = 0.0f;
      } else {
        logProb = Float.NEGATIVE_INFINITY;
      }
    /* -------------
      // this didn't seem to work -- too categorical
      int type = Character.getType(word.charAt(0));
      // the below may not normalize probs over options, but is probably okay
      if (type == Character.START_PUNCTUATION) {
        if (tag.equals("PU-LPAREN") || tag.equals("PU-PAREN") ||
            tag.equals("PU-LQUOTE") || tag.equals("PU-QUOTE") ||
            tag.equals("PU")) {
          // if (VERBOSE) System.err.println("ChineseUWM: unknown L Punc");
          logProb = 0.0f;
        } else {
          logProb = Float.NEGATIVE_INFINITY;
        }
      } else if (type == Character.END_PUNCTUATION) {
        if (tag.equals("PU-RPAREN") || tag.equals("PU-PAREN") ||
            tag.equals("PU-RQUOTE") || tag.equals("PU-QUOTE") ||
            tag.equals("PU")) {
          // if (VERBOSE) System.err.println("ChineseUWM: unknown R Punc");
          logProb = 0.0f;
        } else {
          logProb = Float.NEGATIVE_INFINITY;
        }
      } else {
        if (tag.equals("PU-OTHER") || tag.equals("PU-ENDSENT") ||
            tag.equals("PU")) {
          // if (VERBOSE) System.err.println("ChineseUWM: unknown O Punc");
          logProb = 0.0f;
        } else {
          logProb = Float.NEGATIVE_INFINITY;
        }
      }
    ------------- */
    } else {
      first:
        if (useFirst) {
          String first = word.substring(0, 1);
          if (useUnicodeType) {
            char ch = word.charAt(0);
            int type = Character.getType(ch);
            if (type != Character.OTHER_LETTER) {
              // standard Chinese characters are of type "OTHER_LETTER"!!
              first = Integer.toString(type);
            }
          }
          if ( ! seenFirst.contains(first)) {
            if (useGT) {
              logProb = scoreGT(tagL);
              break first;
            } else {
              first = unknown;
            }
          }

          /* get the Counter of terminal rewrites for the relevant tag */
          ClassicCounter<String> wordProbs = tagHash.get(tagL);

          /* if the proposed tag has never been seen before, issue a
             warning and return probability 0. */
          if (wordProbs == null) {
            if (VERBOSE) System.err.println("Warning: proposed tag is unseen in training data!");
            logProb = Float.NEGATIVE_INFINITY;
          } else if (wordProbs.containsKey(first)) {
            logProb = (float) wordProbs.getCount(first);
          } else {
            logProb = (float) wordProbs.getCount(unknown);
          }
        } else if (useGT) {
          logProb = scoreGT(tagL);
        } else {
          if (VERBOSE) System.err.println("Warning: no unknown word model in place!\nGiving the combination " + word + " " + tag + " zero probability.");
          logProb = Float.NEGATIVE_INFINITY; // should never get this!
        }
    }

    if (VERBOSE) EncodingPrintWriter.out.println("Unknown word estimate for " + word + " as " + tag + ": " + logProb, encoding);
    return logProb;
  }


  /**
   * trains the first-character based unknown word model.
   *
   * @param trees the collection of trees to be trained over
   */
  @Override
  public void train(Collection<Tree> trees) {
    if (useFirst) {
      System.err.println("ChineseUWM: treating unknown word as the average of their equivalents by first-character identity. useUnicodeType: " + useUnicodeType);
    }
    if (useGT) {
      System.err.println("ChineseUWM: using Good-Turing smoothing for unknown words.");
    }

    trainUnknownGT(trees);

    // Records the number of times word/tag pair was seen in training data.
    ClassicCounter<IntTaggedWord> seenCounter = new ClassicCounter<IntTaggedWord>();
    // c has a map from tags as Label to a Counter from word signatures to Strings; it is used to collect counts that will initialize the probabilities in tagHash
    HashMap<Label,ClassicCounter<String>> c = new HashMap<Label,ClassicCounter<String>>();
    // tc record the marginal counts for each tag as an unknown.  It should be the same as c's totalCount ??
    ClassicCounter<Label> tc = new ClassicCounter<Label>();

    // scan data
    int tNum = 0;
    int tSize = trees.size();
    int indexToStartUnkCounting = (int) (tSize * Train.fractionBeforeUnseenCounting);
    IntTaggedWord iTotal = new IntTaggedWord(nullWord, nullTag);
    for (Tree t : trees) {
      tNum++;
      for (Tree node : t) {
        if (node.isPreTerminal()) {
          Label tagL = node.label();
          String word = node.firstChild().label().value();
          String first = word.substring(0, 1);
          if (useUnicodeType) {
            char ch = word.charAt(0);
            int type = Character.getType(ch);
            if (type != Character.OTHER_LETTER) {
              // standard Chinese characters are of type "OTHER_LETTER"!!
              first = Integer.toString(type);
            }
          }
          String tag = tagL.value();

          if ( ! c.containsKey(tagL)) {
            c.put(tagL, new ClassicCounter<String>());
          }
          c.get(tagL).incrementCount(first);

          tc.incrementCount(tagL);

          seenFirst.add(first);

          IntTaggedWord iW = new IntTaggedWord(word, IntTaggedWord.ANY);
          seenCounter.incrementCount(iW);
          if (tNum > indexToStartUnkCounting) {
            // start doing this once some way through trees; tNum is 1 based counting
            if (seenCounter.getCount(iW) < 2) {
              IntTaggedWord iT = new IntTaggedWord(IntTaggedWord.ANY, tag);
              unSeenCounter.incrementCount(iT);
              unSeenCounter.incrementCount(iTotal);
            }
          }
        }
      }
    }

    for (Label tagLab : c.keySet()) {
      /* outer iteration is over tags as Labels */
      ClassicCounter<String> wc = c.get(tagLab); // counts for words given a tag

      if ( ! tagHash.containsKey(tagLab)) {
        tagHash.put(tagLab, new ClassicCounter<String>());
      }

      /* the UNKNOWN first character is assumed to be seen once in each tag */
      // this is really sort of broken!
      tc.incrementCount(tagLab);
      wc.setCount(unknown, 1.0);

      /* inner iteration is over words  as strings */
      for (String first : wc.keySet()) {
        double prob = Math.log(((wc.getCount(first))) / tc.getCount(tagLab));
        tagHash.get(tagLab).setCount(first, prob);
        //if (ExampleApp.verbose)
        //EncodingPrintWriter.out.println(tag + " rewrites as " + first + " firstchar with probability " + prob,encoding);
      }
    }
  }


  public static void main(String[] args) {
    System.out.println("Testing unknown matching");
    String s = "\u5218\u00b7\u9769\u547d";
    if (s.matches(properNameMatch)) {
      System.out.println("hooray names!");
    } else {
      System.out.println("Uh-oh names!");
    }
    String s1 = "\uff13\uff10\uff10\uff10";
    if (s1.matches(numberMatch)) {
      System.out.println("hooray numbers!");
    } else {
      System.out.println("Uh-oh numbers!");
    }
    String s11 = "\u767e\u5206\u4e4b\u56db\u5341\u4e09\u70b9\u4e8c";
    if (s11.matches(numberMatch)) {
      System.out.println("hooray numbers!");
    } else {
      System.out.println("Uh-oh numbers!");
    }
    String s12 = "\u767e\u5206\u4e4b\u4e09\u5341\u516b\u70b9\u516d";
    if (s12.matches(numberMatch)) {
      System.out.println("hooray numbers!");
    } else {
      System.out.println("Uh-oh numbers!");
    }
    String s2 = "\u4e09\u6708";
    if (s2.matches(dateMatch)) {
      System.out.println("hooray dates!");
    } else {
      System.out.println("Uh-oh dates!");
    }

    System.out.println("Testing tagged word");
    ClassicCounter<TaggedWord> c = new ClassicCounter<TaggedWord>();
    TaggedWord tw1 = new TaggedWord("w", "t");
    c.incrementCount(tw1);
    TaggedWord tw2 = new TaggedWord("w", "t2");
    System.out.println(c.containsKey(tw2));
    System.out.println(tw1.equals(tw2));

    WordTag wt1 = toWordTag(tw1);
    WordTag wt2 = toWordTag(tw2);
    WordTag wt3 = new WordTag("w", "t2");
    System.out.println(wt1.equals(wt2));
    System.out.println(wt2.equals(wt3));
  }

  private static WordTag toWordTag(TaggedWord tw) {
    return new WordTag(tw.word(), tw.tag());
  }

  private static final long serialVersionUID = 221L;


  @Override
  public String getSignature(String word, int loc) {
    throw new UnsupportedOperationException();
  }

}

