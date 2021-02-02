package com.xabe.spring.solr.infrastructure.database.parser;

import java.io.StringReader;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.util.TokenFilterFactory;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.DateTools.Resolution;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RegexpQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.QueryBuilder;
import org.apache.lucene.util.automaton.Operations;
import org.apache.lucene.util.automaton.RegExp;
import org.apache.solr.analysis.ReversedWildcardFilterFactory;
import org.apache.solr.analysis.TokenizerChain;
import org.apache.solr.common.SolrException;
import org.apache.solr.parser.CharStream;
import org.apache.solr.parser.FastCharStream;
import org.apache.solr.parser.ParseException;
import org.apache.solr.parser.QueryParser.Operator;
import org.apache.solr.parser.Token;
import org.apache.solr.parser.TokenMgrError;
import org.apache.solr.schema.FieldType;
import org.apache.solr.search.QParser;
import org.apache.solr.search.SyntaxError;

/**
 * This class has been copied over from Solr core project because the default Lucene parser doesn't support negative boosting, because by
 * default Lucene's parser doesn't allow them.
 * <p>
 * The modifications done to this class are to remove specific behaviour tied to solr core classes (such as Solr schema, SolrCore, etc
 * classes) that aimed to give the flexibility needed by solr (which here is not needed).
 * <p>
 * This class mimics org.apache.solr.parser.SolrQueryParserBase which contains the factory methods to create the required queries based on
 * the syntax
 */
public abstract class SolrQueryParserBase extends QueryBuilder {

  /**
   * Alternative form of QueryParser.Operator.AND
   */
  public static final Operator AND_OPERATOR = Operator.AND;

  /**
   * Alternative form of QueryParser.Operator.OR
   */
  public static final Operator OR_OPERATOR = Operator.OR;

  static final int CONJ_NONE = 0;

  static final int CONJ_AND = 1;

  static final int CONJ_OR = 2;

  static final int MOD_NONE = 0;

  // make it possible to call setDefaultOperator() without accessing
  // the nested class:
  static final int MOD_NOT = 10;

  static final int MOD_REQ = 11;

  protected String explicitField;

  /**
   * The default operator that parser uses to combine query terms
   */
  Operator operator = OR_OPERATOR;

  MultiTermQuery.RewriteMethod multiTermRewriteMethod = MultiTermQuery.CONSTANT_SCORE_REWRITE;

  boolean allowLeadingWildcard = true;

  Locale locale = Locale.getDefault();

  TimeZone timeZone = TimeZone.getDefault();

  String defaultField;

  int phraseSlop = 0;     // default slop for phrase queries

  float fuzzyMinSim = FuzzyQuery.defaultMinSimilarity;

  int fuzzyPrefixLength = FuzzyQuery.defaultPrefixLength;

  boolean autoGeneratePhraseQueries = false;

  // implementation detail - caching ReversedWildcardFilterFactory based on type
  private Map<FieldType, ReversedWildcardFilterFactory> leadingWildcards;

  private final QParser subQParser = null;

  // So the generated QueryParser(CharStream) won't error out
  protected SolrQueryParserBase() {
    super(null);
  }

  /**
   * Returns the numeric value of the hexadecimal character
   */
  static final int hexToInt(final char c) throws SyntaxError {
    if ('0' <= c && c <= '9') {
      return c - '0';
    } else if ('a' <= c && c <= 'f') {
      return c - 'a' + 10;
    } else if ('A' <= c && c <= 'F') {
      return c - 'A' + 10;
    } else {
      throw new SyntaxError("Non-hex character in Unicode escape sequence: " + c);
    }
  }

  /**
   * Returns a String where those characters that QueryParser expects to be escaped are escaped by a preceding <code>\</code>.
   */
  public static String escape(final String s) {
    final StringBuilder sb = new StringBuilder();
    for (int i = 0; i < s.length(); i++) {
      final char c = s.charAt(i);
      // These characters are part of the query syntax and must be escaped
      if (c == '\\' || c == '+' || c == '-' || c == '!' || c == '(' || c == ')' || c == ':' || c == '^' || c == '[' || c == ']' || c == '\"'
          || c == '{' || c == '}' || c == '~' || c == '*' || c == '?' || c == '|' || c == '&' || c == '/') {
        sb.append('\\');
      }
      sb.append(c);
    }
    return sb.toString();
  }

  // the generated parser will create these in QueryParser
  public abstract void ReInit(CharStream stream);

  public abstract Query TopLevelQuery(String field) throws ParseException, SyntaxError;

  public void init(final String defaultField, final Analyzer analyzer) {
    this.defaultField = defaultField;
    this.setAnalyzer(analyzer);
  }

  /**
   * Parses a query string, returning a {@link Query}.
   *
   * @param query the query string to be parsed.
   */
  public Query parse(final String query) throws SyntaxError {
    this.ReInit(new FastCharStream(new StringReader(query)));
    try {
      // TopLevelQuery is a Query followed by the end-of-input (EOF)
      final Query res = this.TopLevelQuery(null);  // pass null so we can tell later if an explicit field was provided or not
      return res != null ? res : this.newBooleanQuery().build();
    } catch (final ParseException | TokenMgrError tme) {
      throw new SyntaxError("Cannot parse '" + query + "': " + tme.getMessage(), tme);
    } catch (final BooleanQuery.TooManyClauses tmc) {
      throw new SyntaxError("Cannot parse '" + query + "': too many boolean clauses", tmc);
    }
  }

  /**
   * @return Returns the default field.
   */
  public String getDefaultField() {
    return this.defaultField;
  }

  /**
   * Handles the default field if null is passed
   */
  public String getField(final String fieldName) {
    this.explicitField = fieldName;
    return fieldName != null ? fieldName : this.defaultField;
  }

  /**
   * For a fielded query, returns the actual field specified (i.e. null if default is being used) myfield:A or myfield:(A B C) will both
   * return "myfield"
   */
  public String getExplicitField() {
    return this.explicitField;
  }

  /**
   * @see #setAutoGeneratePhraseQueries(boolean)
   */
  public final boolean getAutoGeneratePhraseQueries() {
    return this.autoGeneratePhraseQueries;
  }

  /**
   * Set to true if phrase queries will be automatically generated when the analyzer returns more than one term from whitespace delimited
   * text. NOTE: this behavior may not be suitable for all languages.
   * <p>
   * Set to false if phrase queries should only be generated when surrounded by double quotes.
   */
  public final void setAutoGeneratePhraseQueries(final boolean value) {
    this.autoGeneratePhraseQueries = value;
  }

  /**
   * Get the minimal similarity for fuzzy queries.
   */
  public float getFuzzyMinSim() {
    return this.fuzzyMinSim;
  }

  /**
   * Set the minimum similarity for fuzzy queries. Default is 2f.
   */
  public void setFuzzyMinSim(final float fuzzyMinSim) {
    this.fuzzyMinSim = fuzzyMinSim;
  }

  /**
   * Get the prefix length for fuzzy queries.
   *
   * @return Returns the fuzzyPrefixLength.
   */
  public int getFuzzyPrefixLength() {
    return this.fuzzyPrefixLength;
  }

  /**
   * Set the prefix length for fuzzy queries. Default is 0.
   *
   * @param fuzzyPrefixLength The fuzzyPrefixLength to set.
   */
  public void setFuzzyPrefixLength(final int fuzzyPrefixLength) {
    this.fuzzyPrefixLength = fuzzyPrefixLength;
  }

  /**
   * Gets the default slop for phrases.
   */
  public int getPhraseSlop() {
    return this.phraseSlop;
  }

  /**
   * Sets the default slop for phrases.  If zero, then exact phrase matches are required.  Default value is zero.
   */
  public void setPhraseSlop(final int phraseSlop) {
    this.phraseSlop = phraseSlop;
  }

  /**
   * @see #setAllowLeadingWildcard(boolean)
   */
  public boolean getAllowLeadingWildcard() {
    return this.allowLeadingWildcard;
  }

  /**
   * Set to <code>true</code> to allow leading wildcard characters.
   * <p>
   * When set, <code>*</code> or <code>?</code> are allowed as the first character of a PrefixQuery and WildcardQuery. Note that this can
   * produce very slow queries on big indexes.
   * <p>
   * Default: false.
   */
  public void setAllowLeadingWildcard(final boolean allowLeadingWildcard) {
    this.allowLeadingWildcard = allowLeadingWildcard;
  }

  /**
   * Gets implicit operator setting, which will be either AND_OPERATOR or OR_OPERATOR.
   */
  public Operator getDefaultOperator() {
    return this.operator;
  }

  /**
   * Sets the boolean operator of the QueryParser. In default mode (<code>OR_OPERATOR</code>) terms without any modifiers are considered
   * optional: for example <code>capital of Hungary</code> is equal to
   * <code>capital OR of OR Hungary</code>.<br>
   * In <code>AND_OPERATOR</code> mode terms are considered to be in conjunction: the above mentioned query is parsed as <code>capital AND
   * of AND Hungary</code>
   */
  public void setDefaultOperator(final Operator op) {
    this.operator = op;
  }

  /**
   * @see #setMultiTermRewriteMethod
   */
  public MultiTermQuery.RewriteMethod getMultiTermRewriteMethod() {
    return this.multiTermRewriteMethod;
  }

  /**
   * By default QueryParser uses {@link MultiTermQuery#CONSTANT_SCORE_REWRITE} when creating a PrefixQuery, WildcardQuery or RangeQuery.
   * This implementation is generally preferable because it a) Runs faster b) Does not have the scarcity of terms unduly influence score c)
   * avoids any "TooManyBooleanClauses" exception. However, if your application really needs to use the old-fashioned BooleanQuery expansion
   * rewriting and the above points are not relevant then use this to change the rewrite method.
   */
  public void setMultiTermRewriteMethod(final MultiTermQuery.RewriteMethod method) {
    this.multiTermRewriteMethod = method;
  }

  protected void addClause(final List<BooleanClause> clauses, final int conj, final int mods, final Query q) {
    boolean required;
    final boolean prohibited;

    // If this term is introduced by AND, make the preceding term required,
    // unless it's already prohibited
    if (clauses.size() > 0 && conj == CONJ_AND) {
      final BooleanClause c = clauses.get(clauses.size() - 1);
      if (!c.isProhibited()) {
        clauses.set(clauses.size() - 1, new BooleanClause(c.getQuery(), BooleanClause.Occur.MUST));
      }
    }

    if (clauses.size() > 0 && this.operator == AND_OPERATOR && conj == CONJ_OR) {
      // If this term is introduced by OR, make the preceding term optional,
      // unless it's prohibited (that means we leave -a OR b but +a OR b-->a OR b)
      // notice if the input is a OR b, first term is parsed as required; without
      // this modification a OR b would parsed as +a OR b
      final BooleanClause c = clauses.get(clauses.size() - 1);
      if (!c.isProhibited()) {
        clauses.set(clauses.size() - 1, new BooleanClause(c.getQuery(), BooleanClause.Occur.SHOULD));
      }
    }

    // We might have been passed a null query; the term might have been
    // filtered away by the analyzer.
    if (q == null) {
      return;
    }

    if (this.operator == OR_OPERATOR) {
      // We set REQUIRED if we're introduced by AND or +; PROHIBITED if
      // introduced by NOT or -; make sure not to set both.
      prohibited = (mods == MOD_NOT);
      required = (mods == MOD_REQ);
      if (conj == CONJ_AND && !prohibited) {
        required = true;
      }
    } else {
      // We set PROHIBITED if we're introduced by NOT or -; We set REQUIRED
      // if not PROHIBITED and not introduced by OR
      prohibited = (mods == MOD_NOT);
      required = (!prohibited && conj != CONJ_OR);
    }
    if (required && !prohibited) {
      clauses.add(this.newBooleanClause(q, BooleanClause.Occur.MUST));
    } else if (!required && !prohibited) {
      clauses.add(this.newBooleanClause(q, BooleanClause.Occur.SHOULD));
    } else if (!required && prohibited) {
      clauses.add(this.newBooleanClause(q, BooleanClause.Occur.MUST_NOT));
    } else {
      throw new RuntimeException("Clause cannot be both required and prohibited");
    }
  }

  protected Query newFieldQuery(final Analyzer analyzer, final String field, final String queryText, final boolean quoted) {
    final BooleanClause.Occur occur = this.operator == Operator.AND ? BooleanClause.Occur.MUST : BooleanClause.Occur.SHOULD;
    return this.createFieldQuery(analyzer, occur, field, queryText, quoted || this.autoGeneratePhraseQueries, this.phraseSlop);
  }

  /**
   * Base implementation delegates to {@link #getFieldQuery(String, String, boolean)}. This method may be overridden, for example, to return
   * a SpanNearQuery instead of a PhraseQuery.
   */
  protected Query getFieldQuery(final String field, final String queryText, final int slop) throws SyntaxError {
    Query query = this.getFieldQuery(field, queryText, true);

    // only set slop of the phrase query was a result of this parser
    // and not a sub-parser.
    if (this.subQParser == null) {

      if (query instanceof PhraseQuery) {
        final PhraseQuery pq = (PhraseQuery) query;
        final Term[] terms = pq.getTerms();
        final int[] positions = pq.getPositions();
        final PhraseQuery.Builder builder = new PhraseQuery.Builder();
        for (int i = 0; i < terms.length; ++i) {
          builder.add(terms[i], positions[i]);
        }
        builder.setSlop(slop);
        query = builder.build();
      }

    }

    return query;
  }

  /**
   * Builds a new BooleanClause instance
   *
   * @param q sub query
   * @param occur how this clause should occur when matching documents
   * @return new BooleanClause instance
   */
  protected BooleanClause newBooleanClause(final Query q, final BooleanClause.Occur occur) {
    return new BooleanClause(q, occur);
  }

  /**
   * Builds a new PrefixQuery instance
   *
   * @param prefix Prefix term
   * @return new PrefixQuery instance
   */
  protected Query newPrefixQuery(final Term prefix) {
    final PrefixQuery query = new PrefixQuery(prefix);
    query.setRewriteMethod(this.multiTermRewriteMethod);
    return query;
  }

  /**
   * Builds a new RegexpQuery instance
   *
   * @param regexp Regexp term
   * @return new RegexpQuery instance
   */
  protected Query newRegexpQuery(final Term regexp) {
    final RegexpQuery query = new RegexpQuery(regexp, RegExp.ALL, Operations.DEFAULT_MAX_DETERMINIZED_STATES);
    query.setRewriteMethod(this.multiTermRewriteMethod);
    return query;
  }

  /**
   * Builds a new FuzzyQuery instance
   *
   * @param term Term
   * @param minimumSimilarity minimum similarity
   * @param prefixLength prefix length
   * @return new FuzzyQuery Instance
   */
  protected Query newFuzzyQuery(final Term term, final float minimumSimilarity, final int prefixLength) {
    // FuzzyQuery doesn't yet allow constant score rewrite
    final String text = term.text();
    final int numEdits = FuzzyQuery.floatToEdits(minimumSimilarity, text.codePointCount(0, text.length()));
    return new FuzzyQuery(term, numEdits, prefixLength);
  }

  /**
   * Builds a new MatchAllDocsQuery instance
   *
   * @return new MatchAllDocsQuery instance
   */
  protected Query newMatchAllDocsQuery() {
    return new MatchAllDocsQuery();
  }

  /**
   * Builds a new WildcardQuery instance
   *
   * @param t wildcard term
   * @return new WildcardQuery instance
   */
  protected Query newWildcardQuery(final Term t) {
    final WildcardQuery query = new WildcardQuery(t, Operations.DEFAULT_MAX_DETERMINIZED_STATES);
    query.setRewriteMethod(this.multiTermRewriteMethod);
    return query;
  }

  /**
   * Factory method for generating query, given a set of clauses. By default creates a boolean query composed of clauses passed in.
   * <p>
   * Can be overridden by extending classes, to modify query being returned.
   *
   * @param clauses List that contains {@link BooleanClause} instances to join.
   * @return Resulting {@link Query} object.
   */
  protected Query getBooleanQuery(final List<BooleanClause> clauses) {
    return this.getBooleanQuery(clauses, false);
  }

  /**
   * Factory method for generating query, given a set of clauses. By default creates a boolean query composed of clauses passed in.
   * <p>
   * Can be overridden by extending classes, to modify query being returned.
   *
   * @param clauses List that contains {@link BooleanClause} instances to join.
   * @param disableCoord true if coord scoring should be disabled.
   * @return Resulting {@link Query} object.
   */
  protected Query getBooleanQuery(final List<BooleanClause> clauses, final boolean disableCoord) {
    if (clauses.size() == 0) {
      return null; // all clause words were filtered away by the analyzer.
    }
    final BooleanQuery.Builder query = this.newBooleanQuery();
    for (final BooleanClause clause : clauses) {
      query.add(clause);
    }
    return query.build();
  }

  // called from parser
  Query handleBareTokenQuery(final String qfield, final Token term, final Token fuzzySlop, final boolean prefix, final boolean wildcard,
      final boolean fuzzy, final boolean regexp)
      throws SyntaxError {
    final Query q;

    if (wildcard) {
      q = this.getWildcardQuery(qfield, term.image);
    } else if (prefix) {
      q = this.getPrefixQuery(qfield, this.discardEscapeChar(term.image.substring(0, term.image.length() - 1)));
    } else if (regexp) {
      q = this.getRegexpQuery(qfield, term.image.substring(1, term.image.length() - 1));
    } else if (fuzzy) {
      float fms = this.fuzzyMinSim;
      try {
        fms = Float.parseFloat(fuzzySlop.image.substring(1));
      } catch (final Exception ignored) {
      }
      if (fms < 0.0f) {
        throw new SyntaxError("Minimum similarity for a FuzzyQuery has to be between 0.0f and 1.0f !");
      } else if (fms >= 1.0f && fms != (int) fms) {
        throw new SyntaxError("Fractional edit distances are not allowed!");
      }
      final String termImage = this.discardEscapeChar(term.image);
      q = this.getFuzzyQuery(qfield, termImage, fms);
    } else {
      final String termImage = this.discardEscapeChar(term.image);
      q = this.getFieldQuery(qfield, termImage, false);
    }
    return q;
  }

  // called from parser
  Query handleQuotedTerm(final String qfield, final Token term, final Token fuzzySlop) throws SyntaxError {
    int s = this.phraseSlop;  // default
    if (fuzzySlop != null) {
      try {
        s = Float.valueOf(fuzzySlop.image.substring(1)).intValue();
      } catch (final Exception ignored) {
      }
    }
    return this.getFieldQuery(qfield, this.discardEscapeChar(term.image.substring(1, term.image.length() - 1)), s);
  }

  // called from parser
  Query handleBoost(final Query q, final Token boost) {
    // q==null check is to avoid boosting null queries, such as those caused by stop words
    if (boost == null || boost.image.length() == 0 || q == null) {
      return q;
    }

    if (boost.image.charAt(0) == '=') {
      // syntax looks like foo:x^=3
      final float val = Float.parseFloat(boost.image.substring(1));
      Query newQ = q;
      if (// q instanceof FilterQuery ||  // TODO: fix this when FilterQuery is introduced to avoid needless wrapping: SOLR-7219
          q instanceof ConstantScoreQuery) {
        //skip
      } else {
        newQ = new ConstantScoreQuery(q);
      }
      return newQ;
    }

    final float boostVal = Float.parseFloat(boost.image);

    return new BoostQuery(q, boostVal);
  }

  /**
   * Returns a String where the escape char has been removed, or kept only once if there was a double escape.
   * <p>
   * Supports escaped unicode characters, e. g. translates
   * <code>\\u0041</code> to <code>A</code>.
   */
  String discardEscapeChar(final String input) throws SyntaxError {
    // Create char array to hold unescaped char sequence
    final char[] output = new char[input.length()];

    // The length of the output can be less than the input
    // due to discarded escape chars. This variable holds
    // the actual length of the output
    int length = 0;

    // We remember whether the last processed character was
    // an escape character
    boolean lastCharWasEscapeChar = false;

    // The multiplier the current unicode digit must be multiplied with.
    // E. g. the first digit must be multiplied with 16^3, the second with 16^2...
    int codePointMultiplier = 0;

    // Used to calculate the codepoint of the escaped unicode character
    int codePoint = 0;

    for (int i = 0; i < input.length(); i++) {
      final char curChar = input.charAt(i);
      if (codePointMultiplier > 0) {
        codePoint += hexToInt(curChar) * codePointMultiplier;
        codePointMultiplier >>>= 4;
        if (codePointMultiplier == 0) {
          output[length++] = (char) codePoint;
          codePoint = 0;
        }
      } else if (lastCharWasEscapeChar) {
        if (curChar == 'u') {
          // found an escaped unicode character
          codePointMultiplier = 16 * 16 * 16;
        } else {
          // this character was escaped
          output[length] = curChar;
          length++;
        }
        lastCharWasEscapeChar = false;
      } else {
        if (curChar == '\\') {
          lastCharWasEscapeChar = true;
        } else {
          output[length] = curChar;
          length++;
        }
      }
    }

    if (codePointMultiplier > 0) {
      throw new SyntaxError("Truncated unicode escape sequence.");
    }

    if (lastCharWasEscapeChar) {
      throw new SyntaxError("Term can not end with escape character.");
    }

    return new String(output, 0, length);
  }

  protected ReversedWildcardFilterFactory getReversedWildcardFilterFactory(final FieldType fieldType) {
    if (this.leadingWildcards == null) {
      this.leadingWildcards = new HashMap<>();
    }
    ReversedWildcardFilterFactory fac = this.leadingWildcards.get(fieldType);
    if (fac != null || this.leadingWildcards.containsKey(fac)) {
      return fac;
    }

    final Analyzer a = fieldType.getIndexAnalyzer();
    if (a instanceof TokenizerChain) {
      // examine the indexing analysis chain if it supports leading wildcards
      final TokenizerChain tc = (TokenizerChain) a;
      final TokenFilterFactory[] factories = tc.getTokenFilterFactories();
      for (final TokenFilterFactory factory : factories) {
        if (factory instanceof ReversedWildcardFilterFactory) {
          fac = (ReversedWildcardFilterFactory) factory;
          break;
        }
      }
    }

    this.leadingWildcards.put(fieldType, fac);
    return fac;
  }

  private void checkNullField(final String field) throws SolrException {
    if (field == null && this.defaultField == null) {
      throw new SolrException(SolrException.ErrorCode.BAD_REQUEST,
          "no field name specified in query and no default specified via 'df' param");
    }
  }

  protected Query getFieldQuery(final String field, final String queryText, final boolean quoted) {
    return this.newFieldQuery(this.getAnalyzer(), field, queryText, quoted);
  }

  // called from parser
  protected Query getRangeQuery(final String field, String part1, String part2, final boolean startInclusive, final boolean endInclusive) {
    part1 = part1 == null ? null : part1.toLowerCase(this.locale);
    part2 = part2 == null ? null : part2.toLowerCase(this.locale);

    final DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, this.locale);
    df.setLenient(true);

    try {
      part1 = DateTools.dateToString(df.parse(part1), Resolution.MILLISECOND);
    } catch (final Exception e) {
    }

    try {
      Date d2 = df.parse(part2);
      if (endInclusive) {
        // The user can only specify the date, not the time, so make sure
        // the time is set to the latest possible time of that date to really
        // include all documents:
        final Calendar cal = Calendar.getInstance(this.timeZone, this.locale);
        cal.setTime(d2);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        d2 = cal.getTime();
      }
      part2 = DateTools.dateToString(d2, Resolution.MILLISECOND);
    } catch (final Exception e) {
    }

    return this.newRangeQuery(field, part1, part2, startInclusive, endInclusive);
  }

  /**
   * Builds a new {@link TermRangeQuery} instance
   *
   * @param field Field
   * @param part1 min
   * @param part2 max
   * @param startInclusive true if the start of the range is inclusive
   * @param endInclusive true if the end of the range is inclusive
   * @return new {@link TermRangeQuery} instance
   */
  protected Query newRangeQuery(final String field, final String part1, final String part2, final boolean startInclusive,
      final boolean endInclusive) {
    final BytesRef start;
    final BytesRef end;

    if (part1 == null) {
      start = null;
    } else {
      start = new BytesRef(part1);
    }

    if (part2 == null) {
      end = null;
    } else {
      end = new BytesRef(part2);
    }

    final TermRangeQuery query = new TermRangeQuery(field, start, end, startInclusive, endInclusive);

    query.setRewriteMethod(this.multiTermRewriteMethod);
    return query;
  }

  // called from parser
  protected Query getPrefixQuery(final String field, String termStr) throws SyntaxError {
    if (!this.allowLeadingWildcard && termStr.startsWith("*")) {
      throw new SyntaxError("'*' not allowed as first character in PrefixQuery");
    }
    termStr = termStr.toLowerCase(this.locale);
    final Term t = new Term(field, termStr);
    return this.newPrefixQuery(t);
  }

  // called from parser
  protected Query getWildcardQuery(final String field, String termStr) throws SyntaxError {
    if ("*".equals(field)) {
      if ("*".equals(termStr)) {
        return this.newMatchAllDocsQuery();
      }
    }
    if (!this.allowLeadingWildcard && (termStr.startsWith("*") || termStr.startsWith("?"))) {
      throw new SyntaxError("'*' or '?' not allowed as first character in WildcardQuery");
    }

    termStr = termStr.toLowerCase(this.locale);

    final Term t = new Term(field, termStr);
    return this.newWildcardQuery(t);
  }

  // called from parser
  protected Query getRegexpQuery(final String field, String termStr) {
    termStr = termStr.toLowerCase(this.locale);
    final Term t = new Term(field, termStr);
    return this.newRegexpQuery(t);
  }

  // called from parser
  protected Query getFuzzyQuery(final String field, String termStr, final float minSimilarity) {
    termStr = termStr.toLowerCase(this.locale);
    final Term t = new Term(field, termStr);
    return this.newFuzzyQuery(t, minSimilarity, this.fuzzyPrefixLength);
  }

  /**
   * Identifies the list of all known "magic fields" that trigger special parsing behavior
   */
  public enum MagicFieldName {
    VAL("_val_", "func"),
    QUERY("_query_", null);

    private final static Map<String, org.apache.solr.parser.SolrQueryParserBase.MagicFieldName> lookup = new HashMap<>();

    static {
      for (final org.apache.solr.parser.SolrQueryParserBase.MagicFieldName s : EnumSet
          .allOf(org.apache.solr.parser.SolrQueryParserBase.MagicFieldName.class)) {
        lookup.put(s.toString(), s);
      }
    }

    public final String field;

    public final String subParser;

    MagicFieldName(final String field, final String subParser) {
      this.field = field;
      this.subParser = subParser;
    }

    public static org.apache.solr.parser.SolrQueryParserBase.MagicFieldName get(final String field) {
      return lookup.get(field);
    }

    @Override
    public String toString() {
      return this.field;
    }
  }

}
