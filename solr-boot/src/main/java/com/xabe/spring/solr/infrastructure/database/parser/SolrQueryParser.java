package com.xabe.spring.solr.infrastructure.database.parser;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.solr.parser.CharStream;
import org.apache.solr.parser.FastCharStream;
import org.apache.solr.parser.ParseException;
import org.apache.solr.parser.QueryParserConstants;
import org.apache.solr.parser.QueryParserTokenManager;
import org.apache.solr.parser.Token;
import org.apache.solr.search.SyntaxError;

/**
 * This class has been copied over from Solr core project because the default Lucene parser doesn't support negative boosting, because by
 * default Lucene's parser doesn't allow them.
 * <p>
 * The modifications done to this class are to remove specific behaviour tied to solr core classes (such as Solr schema, SolrCore, etc
 * classes) that aimed to give the flexibility needed by solr (which here is not needed).
 * <p>
 * This class mimics org.apache.solr.search.SolrQueryParser, containing all the jj rules (lucene syntax parsing).
 */
public class SolrQueryParser extends SolrQueryParserBase implements QueryParserConstants {

  static private int[] jj_la1_0;

  static private int[] jj_la1_1;

  static {
    jj_la1_init_0();
    jj_la1_init_1();
  }

  final private int[] jj_la1 = new int[22];

  final private JJCalls[] jj_2_rtns = new JJCalls[1];

  final private LookaheadSuccess jj_ls = new LookaheadSuccess();

  /**
   * Generated Token Manager.
   */
  public QueryParserTokenManager token_source;

  /**
   * Current token.
   */
  public Token token;

  /**
   * Next token.
   */
  public Token jj_nt;

  private int jj_ntk;

  private Token jj_scanpos, jj_lastpos;

  private int jj_la;

  private int jj_gen;

  private boolean jj_rescan = false;

  private int jj_gc = 0;

  private final List<int[]> jj_expentries = new ArrayList<>();

  private int[] jj_expentry;

  private int jj_kind = -1;

  private final int[] jj_lasttokens = new int[100];

  private int jj_endpos;

  public SolrQueryParser(final String defaultField, final Analyzer analyzer) {
    this(new FastCharStream(new StringReader("")));
    this.init(defaultField, analyzer);
  }

  /**
   * Constructor with user supplied CharStream.
   */
  protected SolrQueryParser(final CharStream stream) {
    this.token_source = new QueryParserTokenManager(stream);
    this.token = new Token();
    this.jj_ntk = -1;
    this.jj_gen = 0;
    for (int i = 0; i < 22; i++) {
      this.jj_la1[i] = -1;
    }
    for (int i = 0; i < this.jj_2_rtns.length; i++) {
      this.jj_2_rtns[i] = new JJCalls();
    }
  }

  /**
   * Constructor with generated Token Manager.
   */
  protected SolrQueryParser(final QueryParserTokenManager tm) {
    this.token_source = tm;
    this.token = new Token();
    this.jj_ntk = -1;
    this.jj_gen = 0;
    for (int i = 0; i < 22; i++) {
      this.jj_la1[i] = -1;
    }
    for (int i = 0; i < this.jj_2_rtns.length; i++) {
      this.jj_2_rtns[i] = new JJCalls();
    }
  }

  private static void jj_la1_init_0() {
    jj_la1_0 =
        new int[]{0x600, 0x600, 0x3800, 0x3800, 0x3fb4fe00, 0x240000, 0x80000, 0x80000, 0x3fb4c000, 0x23a44000, 0x400000, 0x400000, 0x80000,
            0xc000000, 0x0, 0x40000000, 0x0, 0x80000000, 0x80000, 0x400000, 0x80000, 0x2fb44000,};
  }

  private static void jj_la1_init_1() {
    jj_la1_1 = new int[]{0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x6, 0x0, 0x6, 0x1, 0x0, 0x0, 0x0, 0x0,};
  }

  // *   Query  ::= ( Clause )*
  // *   Clause ::= ["+", "-"] [<TERM> ":"] ( <TERM> | "(" Query ")" )
  final public int Conjunction() throws ParseException {
    int ret = CONJ_NONE;
    switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
      case AND:
      case OR:
        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
          case AND:
            this.jj_consume_token(AND);
            ret = CONJ_AND;
            break;
          case OR:
            this.jj_consume_token(OR);
            ret = CONJ_OR;
            break;
          default:
            this.jj_la1[0] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
        }
        break;
      default:
        this.jj_la1[1] = this.jj_gen;
    }
    {
      if (true) {
        return ret;
      }
    }
    throw new Error("Missing return statement in function");
  }

  final public int Modifiers() throws ParseException {
    int ret = MOD_NONE;
    switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
      case NOT:
      case PLUS:
      case MINUS:
        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
          case PLUS:
            this.jj_consume_token(PLUS);
            ret = MOD_REQ;
            break;
          case MINUS:
            this.jj_consume_token(MINUS);
            ret = MOD_NOT;
            break;
          case NOT:
            this.jj_consume_token(NOT);
            ret = MOD_NOT;
            break;
          default:
            this.jj_la1[2] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
        }
        break;
      default:
        this.jj_la1[3] = this.jj_gen;
    }
    {
      if (true) {
        return ret;
      }
    }
    throw new Error("Missing return statement in function");
  }

  // This makes sure that there is no garbage after the query string
  @Override
  final public Query TopLevelQuery(final String field) throws ParseException, SyntaxError {
    final Query q;
    q = this.Query(field);
    this.jj_consume_token(0);
    {
      if (true) {
        return q;
      }
    }
    throw new Error("Missing return statement in function");
  }

  final public Query Query(final String field) throws ParseException, SyntaxError {
    final List<BooleanClause> clauses = new ArrayList<>();
    Query q, firstQuery = null;
    int conj, mods;
    mods = this.Modifiers();
    q = this.Clause(field);
    this.addClause(clauses, CONJ_NONE, mods, q);
    if (mods == MOD_NONE) {
      firstQuery = q;
    }
    label_1:
    while (true) {
      switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
        case AND:
        case OR:
        case NOT:
        case PLUS:
        case MINUS:
        case BAREOPER:
        case LPAREN:
        case STAR:
        case QUOTED:
        case TERM:
        case PREFIXTERM:
        case WILDTERM:
        case REGEXPTERM:
        case RANGEIN_START:
        case RANGEEX_START:
        case LPARAMS:
        case NUMBER:
          break;
        default:
          this.jj_la1[4] = this.jj_gen;
          break label_1;
      }
      conj = this.Conjunction();
      mods = this.Modifiers();
      q = this.Clause(field);
      this.addClause(clauses, conj, mods, q);
    }
    if (clauses.size() == 1 && firstQuery != null) {
      if (true) {
        return firstQuery;
      }
    } else {
      {
        if (true) {
          return this.getBooleanQuery(clauses);
        }
      }
    }
    throw new Error("Missing return statement in function");
  }

  final public Query Clause(String field) throws ParseException, SyntaxError {
    final Query q;
    Token fieldToken = null, boost = null;
    Token localParams = null;
    if (this.jj_2_1(2)) {
      switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
        case TERM:
          fieldToken = this.jj_consume_token(TERM);
          this.jj_consume_token(COLON);
          field = this.discardEscapeChar(fieldToken.image);
          break;
        case STAR:
          this.jj_consume_token(STAR);
          this.jj_consume_token(COLON);
          field = "*";
          break;
        default:
          this.jj_la1[5] = this.jj_gen;
          this.jj_consume_token(-1);
          throw new ParseException();
      }
    } else {
    }
    switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
      case BAREOPER:
      case STAR:
      case QUOTED:
      case TERM:
      case PREFIXTERM:
      case WILDTERM:
      case REGEXPTERM:
      case RANGEIN_START:
      case RANGEEX_START:
      case NUMBER:
        q = this.Term(field);
        break;
      case LPAREN:
        this.jj_consume_token(LPAREN);
        q = this.Query(field);
        this.jj_consume_token(RPAREN);
        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
          case CARAT:
            this.jj_consume_token(CARAT);
            boost = this.jj_consume_token(NUMBER);
            break;
          default:
            this.jj_la1[6] = this.jj_gen;
        }
        break;
      case LPARAMS:
        localParams = this.jj_consume_token(LPARAMS);
        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
          case CARAT:
            this.jj_consume_token(CARAT);
            boost = this.jj_consume_token(NUMBER);
            break;
          default:
            this.jj_la1[7] = this.jj_gen;
        }
        //q = getLocalParams(field, localParams.image);
        q = new MatchAllDocsQuery();
        break;
      default:
        this.jj_la1[8] = this.jj_gen;
        this.jj_consume_token(-1);
        throw new ParseException();
    }
    {
      if (true) {
        return this.handleBoost(q, boost);
      }
    }
    throw new Error("Missing return statement in function");
  }

  final public Query Term(final String field) throws ParseException, SyntaxError {
    final Token term;
    Token boost = null;
    Token fuzzySlop = null;
    final Token goop1;
    final Token goop2;
    boolean prefix = false;
    boolean wildcard = false;
    boolean fuzzy = false;
    boolean regexp = false;
    boolean startInc = false;
    boolean endInc = false;
    final Query q;
    switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
      case BAREOPER:
      case STAR:
      case TERM:
      case PREFIXTERM:
      case WILDTERM:
      case REGEXPTERM:
      case NUMBER:
        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
          case TERM:
            term = this.jj_consume_token(TERM);
            break;
          case STAR:
            term = this.jj_consume_token(STAR);
            wildcard = true;
            break;
          case PREFIXTERM:
            term = this.jj_consume_token(PREFIXTERM);
            prefix = true;
            break;
          case WILDTERM:
            term = this.jj_consume_token(WILDTERM);
            wildcard = true;
            break;
          case REGEXPTERM:
            term = this.jj_consume_token(REGEXPTERM);
            regexp = true;
            break;
          case NUMBER:
            term = this.jj_consume_token(NUMBER);
            break;
          case BAREOPER:
            term = this.jj_consume_token(BAREOPER);
            term.image = term.image.substring(0, 1);
            break;
          default:
            this.jj_la1[9] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
        }
        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
          case FUZZY_SLOP:
            fuzzySlop = this.jj_consume_token(FUZZY_SLOP);
            fuzzy = true;
            break;
          default:
            this.jj_la1[10] = this.jj_gen;
        }
        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
          case CARAT:
            this.jj_consume_token(CARAT);
            boost = this.jj_consume_token(NUMBER);
            switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
              case FUZZY_SLOP:
                fuzzySlop = this.jj_consume_token(FUZZY_SLOP);
                fuzzy = true;
                break;
              default:
                this.jj_la1[11] = this.jj_gen;
            }
            break;
          default:
            this.jj_la1[12] = this.jj_gen;
        }
        q = this.handleBareTokenQuery(this.getField(field), term, fuzzySlop, prefix, wildcard, fuzzy, regexp);
        break;
      case RANGEIN_START:
      case RANGEEX_START:
        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
          case RANGEIN_START:
            this.jj_consume_token(RANGEIN_START);
            startInc = true;
            break;
          case RANGEEX_START:
            this.jj_consume_token(RANGEEX_START);
            break;
          default:
            this.jj_la1[13] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
        }
        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
          case RANGE_GOOP:
            goop1 = this.jj_consume_token(RANGE_GOOP);
            break;
          case RANGE_QUOTED:
            goop1 = this.jj_consume_token(RANGE_QUOTED);
            break;
          default:
            this.jj_la1[14] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
        }
        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
          case RANGE_TO:
            this.jj_consume_token(RANGE_TO);
            break;
          default:
            this.jj_la1[15] = this.jj_gen;
        }
        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
          case RANGE_GOOP:
            goop2 = this.jj_consume_token(RANGE_GOOP);
            break;
          case RANGE_QUOTED:
            goop2 = this.jj_consume_token(RANGE_QUOTED);
            break;
          default:
            this.jj_la1[16] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
        }
        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
          case RANGEIN_END:
            this.jj_consume_token(RANGEIN_END);
            endInc = true;
            break;
          case RANGEEX_END:
            this.jj_consume_token(RANGEEX_END);
            break;
          default:
            this.jj_la1[17] = this.jj_gen;
            this.jj_consume_token(-1);
            throw new ParseException();
        }
        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
          case CARAT:
            this.jj_consume_token(CARAT);
            boost = this.jj_consume_token(NUMBER);
            break;
          default:
            this.jj_la1[18] = this.jj_gen;
        }
        boolean startOpen = false;
        boolean endOpen = false;
        if (goop1.kind == RANGE_QUOTED) {
          goop1.image = goop1.image.substring(1, goop1.image.length() - 1);
        } else if ("*".equals(goop1.image)) {
          startOpen = true;
        }
        if (goop2.kind == RANGE_QUOTED) {
          goop2.image = goop2.image.substring(1, goop2.image.length() - 1);
        } else if ("*".equals(goop2.image)) {
          endOpen = true;
        }
        q = this.getRangeQuery(this.getField(field), startOpen ? null : this.discardEscapeChar(goop1.image),
            endOpen ? null : this.discardEscapeChar(goop2.image), startInc, endInc);
        break;
      case QUOTED:
        term = this.jj_consume_token(QUOTED);
        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
          case FUZZY_SLOP:
            fuzzySlop = this.jj_consume_token(FUZZY_SLOP);
            break;
          default:
            this.jj_la1[19] = this.jj_gen;
        }
        switch ((this.jj_ntk == -1) ? this.jj_ntk() : this.jj_ntk) {
          case CARAT:
            this.jj_consume_token(CARAT);
            boost = this.jj_consume_token(NUMBER);
            break;
          default:
            this.jj_la1[20] = this.jj_gen;
        }
        q = this.handleQuotedTerm(this.getField(field), term, fuzzySlop);
        break;
      default:
        this.jj_la1[21] = this.jj_gen;
        this.jj_consume_token(-1);
        throw new ParseException();
    }
    {
      if (true) {
        return this.handleBoost(q, boost);
      }
    }
    throw new Error("Missing return statement in function");
  }

  private boolean jj_2_1(final int xla) {
    this.jj_la = xla;
    this.jj_lastpos = this.jj_scanpos = this.token;
    try {
      return !this.jj_3_1();
    } catch (final LookaheadSuccess ls) {
      return true;
    } finally {
      this.jj_save(0, xla);
    }
  }

  private boolean jj_3R_2() {
    if (this.jj_scan_token(TERM)) {
      return true;
    }
    return this.jj_scan_token(COLON);
  }

  private boolean jj_3_1() {
    final Token xsp;
    xsp = this.jj_scanpos;
    if (this.jj_3R_2()) {
      this.jj_scanpos = xsp;
      return this.jj_3R_3();
    }
    return false;
  }

  private boolean jj_3R_3() {
    if (this.jj_scan_token(STAR)) {
      return true;
    }
    return this.jj_scan_token(COLON);
  }

  /**
   * Reinitialise.
   */
  @Override
  public void ReInit(final CharStream stream) {
    this.token_source.ReInit(stream);
    this.token = new Token();
    this.jj_ntk = -1;
    this.jj_gen = 0;
    for (int i = 0; i < 22; i++) {
      this.jj_la1[i] = -1;
    }
    for (int i = 0; i < this.jj_2_rtns.length; i++) {
      this.jj_2_rtns[i] = new JJCalls();
    }
  }

  /**
   * Reinitialise.
   */
  public void ReInit(final QueryParserTokenManager tm) {
    this.token_source = tm;
    this.token = new Token();
    this.jj_ntk = -1;
    this.jj_gen = 0;
    for (int i = 0; i < 22; i++) {
      this.jj_la1[i] = -1;
    }
    for (int i = 0; i < this.jj_2_rtns.length; i++) {
      this.jj_2_rtns[i] = new JJCalls();
    }
  }

  private Token jj_consume_token(final int kind) throws ParseException {
    final Token oldToken;
    if ((oldToken = this.token).next != null) {
      this.token = this.token.next;
    } else {
      this.token = this.token.next = this.token_source.getNextToken();
    }
    this.jj_ntk = -1;
    if (this.token.kind == kind) {
      this.jj_gen++;
      if (++this.jj_gc > 100) {
        this.jj_gc = 0;
        for (int i = 0; i < this.jj_2_rtns.length; i++) {
          JJCalls c = this.jj_2_rtns[i];
          while (c != null) {
            if (c.gen < this.jj_gen) {
              c.first = null;
            }
            c = c.next;
          }
        }
      }
      return this.token;
    }
    this.token = oldToken;
    this.jj_kind = kind;
    throw this.generateParseException();
  }

  private boolean jj_scan_token(final int kind) {
    if (this.jj_scanpos == this.jj_lastpos) {
      this.jj_la--;
      if (this.jj_scanpos.next == null) {
        this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.next = this.token_source.getNextToken();
      } else {
        this.jj_lastpos = this.jj_scanpos = this.jj_scanpos.next;
      }
    } else {
      this.jj_scanpos = this.jj_scanpos.next;
    }
    if (this.jj_rescan) {
      int i = 0;
      Token tok = this.token;
      while (tok != null && tok != this.jj_scanpos) {
        i++;
        tok = tok.next;
      }
      if (tok != null) {
        this.jj_add_error_token(kind, i);
      }
    }
    if (this.jj_scanpos.kind != kind) {
      return true;
    }
    if (this.jj_la == 0 && this.jj_scanpos == this.jj_lastpos) {
      throw this.jj_ls;
    }
    return false;
  }

  /**
   * Get the next Token.
   */
  final public Token getNextToken() {
    if (this.token.next != null) {
      this.token = this.token.next;
    } else {
      this.token = this.token.next = this.token_source.getNextToken();
    }
    this.jj_ntk = -1;
    this.jj_gen++;
    return this.token;
  }

  /**
   * Get the specific Token.
   */
  final public Token getToken(final int index) {
    Token t = this.token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) {
        t = t.next;
      } else {
        t = t.next = this.token_source.getNextToken();
      }
    }
    return t;
  }

  private int jj_ntk() {
    if ((this.jj_nt = this.token.next) == null) {
      return (this.jj_ntk = (this.token.next = this.token_source.getNextToken()).kind);
    } else {
      return (this.jj_ntk = this.jj_nt.kind);
    }
  }

  private void jj_add_error_token(final int kind, final int pos) {
    if (pos >= 100) {
      return;
    }
    if (pos == this.jj_endpos + 1) {
      this.jj_lasttokens[this.jj_endpos++] = kind;
    } else if (this.jj_endpos != 0) {
      this.jj_expentry = new int[this.jj_endpos];
      for (int i = 0; i < this.jj_endpos; i++) {
        this.jj_expentry[i] = this.jj_lasttokens[i];
      }
      jj_entries_loop:
      for (java.util.Iterator<?> it = this.jj_expentries.iterator(); it.hasNext(); ) {
        final int[] oldentry = (int[]) (it.next());
        if (oldentry.length == this.jj_expentry.length) {
          for (int i = 0; i < this.jj_expentry.length; i++) {
            if (oldentry[i] != this.jj_expentry[i]) {
              continue jj_entries_loop;
            }
          }
          this.jj_expentries.add(this.jj_expentry);
          break jj_entries_loop;
        }
      }
      if (pos != 0) {
        this.jj_lasttokens[(this.jj_endpos = pos) - 1] = kind;
      }
    }
  }

  /**
   * Generate ParseException.
   */
  public ParseException generateParseException() {
    this.jj_expentries.clear();
    final boolean[] la1tokens = new boolean[35];
    if (this.jj_kind >= 0) {
      la1tokens[this.jj_kind] = true;
      this.jj_kind = -1;
    }
    for (int i = 0; i < 22; i++) {
      if (this.jj_la1[i] == this.jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1 << j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1 << j)) != 0) {
            la1tokens[32 + j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 35; i++) {
      if (la1tokens[i]) {
        this.jj_expentry = new int[1];
        this.jj_expentry[0] = i;
        this.jj_expentries.add(this.jj_expentry);
      }
    }
    this.jj_endpos = 0;
    this.jj_rescan_token();
    this.jj_add_error_token(0, 0);
    final int[][] exptokseq = new int[this.jj_expentries.size()][];
    for (int i = 0; i < this.jj_expentries.size(); i++) {
      exptokseq[i] = this.jj_expentries.get(i);
    }
    return new ParseException(this.token, exptokseq, tokenImage);
  }

  /**
   * Enable tracing.
   */
  final public void enable_tracing() {
  }

  /**
   * Disable tracing.
   */
  final public void disable_tracing() {
  }

  private void jj_rescan_token() {
    this.jj_rescan = true;
    for (int i = 0; i < 1; i++) {
      try {
        JJCalls p = this.jj_2_rtns[i];
        do {
          if (p.gen > this.jj_gen) {
            this.jj_la = p.arg;
            this.jj_lastpos = this.jj_scanpos = p.first;
            switch (i) {
              case 0:
                this.jj_3_1();
                break;
            }
          }
          p = p.next;
        } while (p != null);
      } catch (final LookaheadSuccess ls) {
      }
    }
    this.jj_rescan = false;
  }

  private void jj_save(final int index, final int xla) {
    JJCalls p = this.jj_2_rtns[index];
    while (p.gen > this.jj_gen) {
      if (p.next == null) {
        p = p.next = new JJCalls();
        break;
      }
      p = p.next;
    }
    p.gen = this.jj_gen + xla - this.jj_la;
    p.first = this.token;
    p.arg = xla;
  }

  static private final class LookaheadSuccess extends Error {

  }

  static final class JJCalls {

    int gen;

    Token first;

    int arg;

    JJCalls next;
  }

}
