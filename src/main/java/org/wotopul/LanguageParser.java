// Generated from Language.g4 by ANTLR 4.6
package org.wotopul;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class LanguageParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.6", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, ST_SKIP=19, READ=20, WRITE=21, IF=22, THEN=23, ELIF=24, ELSE=25, 
		FI=26, DO=27, OD=28, WHILE=29, REPEAT=30, UNTIL=31, FOR=32, NUM=33, ID=34, 
		WS=35;
	public static final int
		RULE_program = 0, RULE_stmt = 1, RULE_elif = 2, RULE_expr = 3;
	public static final String[] ruleNames = {
		"program", "stmt", "elif", "expr"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "';'", "':='", "'('", "')'", "','", "'*'", "'/'", "'%'", "'+'", 
		"'-'", "'<'", "'<='", "'>'", "'>='", "'=='", "'!='", "'&&'", "'||'", "'skip'", 
		"'read'", "'write'", "'if'", "'then'", "'elif'", "'else'", "'fi'", "'do'", 
		"'od'", "'while'", "'repeat'", "'until'", "'for'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, "ST_SKIP", "READ", "WRITE", 
		"IF", "THEN", "ELIF", "ELSE", "FI", "DO", "OD", "WHILE", "REPEAT", "UNTIL", 
		"FOR", "NUM", "ID", "WS"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Language.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public LanguageParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ProgramContext extends ParserRuleContext {
		public StmtContext stmt() {
			return getRuleContext(StmtContext.class,0);
		}
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitProgram(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_program);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(8);
			stmt(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StmtContext extends ParserRuleContext {
		public StmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stmt; }
	 
		public StmtContext() { }
		public void copyFrom(StmtContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class SequenceContext extends StmtContext {
		public StmtContext first;
		public StmtContext rest;
		public List<StmtContext> stmt() {
			return getRuleContexts(StmtContext.class);
		}
		public StmtContext stmt(int i) {
			return getRuleContext(StmtContext.class,i);
		}
		public SequenceContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitSequence(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ReadContext extends StmtContext {
		public TerminalNode ID() { return getToken(LanguageParser.ID, 0); }
		public TerminalNode READ() { return getToken(LanguageParser.READ, 0); }
		public ReadContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitRead(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AssignmentContext extends StmtContext {
		public TerminalNode ID() { return getToken(LanguageParser.ID, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public AssignmentContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitAssignment(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RepeatContext extends StmtContext {
		public StmtContext body;
		public ExprContext cond;
		public TerminalNode REPEAT() { return getToken(LanguageParser.REPEAT, 0); }
		public TerminalNode UNTIL() { return getToken(LanguageParser.UNTIL, 0); }
		public StmtContext stmt() {
			return getRuleContext(StmtContext.class,0);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public RepeatContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitRepeat(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ForContext extends StmtContext {
		public StmtContext init;
		public ExprContext cond;
		public StmtContext after;
		public StmtContext body;
		public TerminalNode FOR() { return getToken(LanguageParser.FOR, 0); }
		public TerminalNode DO() { return getToken(LanguageParser.DO, 0); }
		public TerminalNode OD() { return getToken(LanguageParser.OD, 0); }
		public List<StmtContext> stmt() {
			return getRuleContexts(StmtContext.class);
		}
		public StmtContext stmt(int i) {
			return getRuleContext(StmtContext.class,i);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ForContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitFor(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class SkipContext extends StmtContext {
		public TerminalNode ST_SKIP() { return getToken(LanguageParser.ST_SKIP, 0); }
		public SkipContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitSkip(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class WhileContext extends StmtContext {
		public ExprContext cond;
		public StmtContext body;
		public TerminalNode WHILE() { return getToken(LanguageParser.WHILE, 0); }
		public TerminalNode DO() { return getToken(LanguageParser.DO, 0); }
		public TerminalNode OD() { return getToken(LanguageParser.OD, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public StmtContext stmt() {
			return getRuleContext(StmtContext.class,0);
		}
		public WhileContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitWhile(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class WriteContext extends StmtContext {
		public TerminalNode WRITE() { return getToken(LanguageParser.WRITE, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public WriteContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitWrite(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class IfContext extends StmtContext {
		public ExprContext cond;
		public StmtContext thenClause;
		public StmtContext elseClause;
		public TerminalNode IF() { return getToken(LanguageParser.IF, 0); }
		public TerminalNode THEN() { return getToken(LanguageParser.THEN, 0); }
		public TerminalNode FI() { return getToken(LanguageParser.FI, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public List<StmtContext> stmt() {
			return getRuleContexts(StmtContext.class);
		}
		public StmtContext stmt(int i) {
			return getRuleContext(StmtContext.class,i);
		}
		public List<ElifContext> elif() {
			return getRuleContexts(ElifContext.class);
		}
		public ElifContext elif(int i) {
			return getRuleContext(ElifContext.class,i);
		}
		public TerminalNode ELSE() { return getToken(LanguageParser.ELSE, 0); }
		public IfContext(StmtContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitIf(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StmtContext stmt() throws RecognitionException {
		return stmt(0);
	}

	private StmtContext stmt(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		StmtContext _localctx = new StmtContext(_ctx, _parentState);
		StmtContext _prevctx = _localctx;
		int _startState = 2;
		enterRecursionRule(_localctx, 2, RULE_stmt, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(62);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				{
				_localctx = new SkipContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(11);
				match(ST_SKIP);
				}
				break;
			case 2:
				{
				_localctx = new AssignmentContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(12);
				match(ID);
				setState(13);
				match(T__1);
				setState(14);
				expr(0);
				}
				break;
			case 3:
				{
				_localctx = new ReadContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(15);
				match(ID);
				setState(16);
				match(T__1);
				setState(17);
				match(READ);
				setState(18);
				match(T__2);
				setState(19);
				match(T__3);
				}
				break;
			case 4:
				{
				_localctx = new WriteContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(20);
				match(WRITE);
				setState(21);
				match(T__2);
				setState(22);
				expr(0);
				setState(23);
				match(T__3);
				}
				break;
			case 5:
				{
				_localctx = new IfContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(25);
				match(IF);
				setState(26);
				((IfContext)_localctx).cond = expr(0);
				setState(27);
				match(THEN);
				setState(28);
				((IfContext)_localctx).thenClause = stmt(0);
				setState(32);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==ELIF) {
					{
					{
					setState(29);
					elif();
					}
					}
					setState(34);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(37);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==ELSE) {
					{
					setState(35);
					match(ELSE);
					setState(36);
					((IfContext)_localctx).elseClause = stmt(0);
					}
				}

				setState(39);
				match(FI);
				}
				break;
			case 6:
				{
				_localctx = new WhileContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(41);
				match(WHILE);
				setState(42);
				((WhileContext)_localctx).cond = expr(0);
				setState(43);
				match(DO);
				setState(44);
				((WhileContext)_localctx).body = stmt(0);
				setState(45);
				match(OD);
				}
				break;
			case 7:
				{
				_localctx = new RepeatContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(47);
				match(REPEAT);
				setState(48);
				((RepeatContext)_localctx).body = stmt(0);
				setState(49);
				match(UNTIL);
				setState(50);
				((RepeatContext)_localctx).cond = expr(0);
				}
				break;
			case 8:
				{
				_localctx = new ForContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(52);
				match(FOR);
				setState(53);
				((ForContext)_localctx).init = stmt(0);
				setState(54);
				match(T__4);
				setState(55);
				((ForContext)_localctx).cond = expr(0);
				setState(56);
				match(T__4);
				setState(57);
				((ForContext)_localctx).after = stmt(0);
				setState(58);
				match(DO);
				setState(59);
				((ForContext)_localctx).body = stmt(0);
				setState(60);
				match(OD);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(69);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new SequenceContext(new StmtContext(_parentctx, _parentState));
					((SequenceContext)_localctx).first = _prevctx;
					pushNewRecursionContext(_localctx, _startState, RULE_stmt);
					setState(64);
					if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
					setState(65);
					match(T__0);
					setState(66);
					((SequenceContext)_localctx).rest = stmt(9);
					}
					} 
				}
				setState(71);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class ElifContext extends ParserRuleContext {
		public ExprContext cond;
		public StmtContext elifClause;
		public TerminalNode ELIF() { return getToken(LanguageParser.ELIF, 0); }
		public TerminalNode THEN() { return getToken(LanguageParser.THEN, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public StmtContext stmt() {
			return getRuleContext(StmtContext.class,0);
		}
		public ElifContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_elif; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitElif(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ElifContext elif() throws RecognitionException {
		ElifContext _localctx = new ElifContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_elif);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(72);
			match(ELIF);
			setState(73);
			((ElifContext)_localctx).cond = expr(0);
			setState(74);
			match(THEN);
			setState(75);
			((ElifContext)_localctx).elifClause = stmt(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExprContext extends ParserRuleContext {
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
	 
		public ExprContext() { }
		public void copyFrom(ExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ConstContext extends ExprContext {
		public Token value;
		public TerminalNode NUM() { return getToken(LanguageParser.NUM, 0); }
		public ConstContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitConst(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class VariableContext extends ExprContext {
		public Token name;
		public TerminalNode ID() { return getToken(LanguageParser.ID, 0); }
		public VariableContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitVariable(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class InfixContext extends ExprContext {
		public ExprContext left;
		public Token op;
		public ExprContext right;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public InfixContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitInfix(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ParenthesisContext extends ExprContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ParenthesisContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LanguageVisitor ) return ((LanguageVisitor<? extends T>)visitor).visitParenthesis(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		return expr(0);
	}

	private ExprContext expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExprContext _localctx = new ExprContext(_ctx, _parentState);
		ExprContext _prevctx = _localctx;
		int _startState = 6;
		enterRecursionRule(_localctx, 6, RULE_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(84);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__2:
				{
				_localctx = new ParenthesisContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(78);
				match(T__2);
				setState(79);
				expr(0);
				setState(80);
				match(T__3);
				}
				break;
			case ID:
				{
				_localctx = new VariableContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(82);
				((VariableContext)_localctx).name = match(ID);
				}
				break;
			case NUM:
				{
				_localctx = new ConstContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(83);
				((ConstContext)_localctx).value = match(NUM);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(106);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(104);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
					case 1:
						{
						_localctx = new InfixContext(new ExprContext(_parentctx, _parentState));
						((InfixContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(86);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(87);
						((InfixContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__5) | (1L << T__6) | (1L << T__7))) != 0)) ) {
							((InfixContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(88);
						((InfixContext)_localctx).right = expr(9);
						}
						break;
					case 2:
						{
						_localctx = new InfixContext(new ExprContext(_parentctx, _parentState));
						((InfixContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(89);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(90);
						((InfixContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==T__8 || _la==T__9) ) {
							((InfixContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(91);
						((InfixContext)_localctx).right = expr(8);
						}
						break;
					case 3:
						{
						_localctx = new InfixContext(new ExprContext(_parentctx, _parentState));
						((InfixContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(92);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(93);
						((InfixContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__10) | (1L << T__11) | (1L << T__12) | (1L << T__13))) != 0)) ) {
							((InfixContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(94);
						((InfixContext)_localctx).right = expr(7);
						}
						break;
					case 4:
						{
						_localctx = new InfixContext(new ExprContext(_parentctx, _parentState));
						((InfixContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(95);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(96);
						((InfixContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==T__14 || _la==T__15) ) {
							((InfixContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(97);
						((InfixContext)_localctx).right = expr(6);
						}
						break;
					case 5:
						{
						_localctx = new InfixContext(new ExprContext(_parentctx, _parentState));
						((InfixContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(98);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(99);
						((InfixContext)_localctx).op = match(T__16);
						setState(100);
						((InfixContext)_localctx).right = expr(5);
						}
						break;
					case 6:
						{
						_localctx = new InfixContext(new ExprContext(_parentctx, _parentState));
						((InfixContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(101);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(102);
						((InfixContext)_localctx).op = match(T__17);
						setState(103);
						((InfixContext)_localctx).right = expr(4);
						}
						break;
					}
					} 
				}
				setState(108);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 1:
			return stmt_sempred((StmtContext)_localctx, predIndex);
		case 3:
			return expr_sempred((ExprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean stmt_sempred(StmtContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 8);
		}
		return true;
	}
	private boolean expr_sempred(ExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return precpred(_ctx, 8);
		case 2:
			return precpred(_ctx, 7);
		case 3:
			return precpred(_ctx, 6);
		case 4:
			return precpred(_ctx, 5);
		case 5:
			return precpred(_ctx, 4);
		case 6:
			return precpred(_ctx, 3);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3%p\4\2\t\2\4\3\t\3"+
		"\4\4\t\4\4\5\t\5\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\7\3!\n\3\f\3\16\3$\13\3\3\3\3\3\5\3"+
		"(\n\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3A\n\3\3\3\3\3\3\3\7\3F\n\3\f\3\16\3I\13"+
		"\3\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5W\n\5\3\5\3\5\3"+
		"\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\7\5k\n"+
		"\5\f\5\16\5n\13\5\3\5\2\4\4\b\6\2\4\6\b\2\6\3\2\b\n\3\2\13\f\3\2\r\20"+
		"\3\2\21\22}\2\n\3\2\2\2\4@\3\2\2\2\6J\3\2\2\2\bV\3\2\2\2\n\13\5\4\3\2"+
		"\13\3\3\2\2\2\f\r\b\3\1\2\rA\7\25\2\2\16\17\7$\2\2\17\20\7\4\2\2\20A\5"+
		"\b\5\2\21\22\7$\2\2\22\23\7\4\2\2\23\24\7\26\2\2\24\25\7\5\2\2\25A\7\6"+
		"\2\2\26\27\7\27\2\2\27\30\7\5\2\2\30\31\5\b\5\2\31\32\7\6\2\2\32A\3\2"+
		"\2\2\33\34\7\30\2\2\34\35\5\b\5\2\35\36\7\31\2\2\36\"\5\4\3\2\37!\5\6"+
		"\4\2 \37\3\2\2\2!$\3\2\2\2\" \3\2\2\2\"#\3\2\2\2#\'\3\2\2\2$\"\3\2\2\2"+
		"%&\7\33\2\2&(\5\4\3\2\'%\3\2\2\2\'(\3\2\2\2()\3\2\2\2)*\7\34\2\2*A\3\2"+
		"\2\2+,\7\37\2\2,-\5\b\5\2-.\7\35\2\2./\5\4\3\2/\60\7\36\2\2\60A\3\2\2"+
		"\2\61\62\7 \2\2\62\63\5\4\3\2\63\64\7!\2\2\64\65\5\b\5\2\65A\3\2\2\2\66"+
		"\67\7\"\2\2\678\5\4\3\289\7\7\2\29:\5\b\5\2:;\7\7\2\2;<\5\4\3\2<=\7\35"+
		"\2\2=>\5\4\3\2>?\7\36\2\2?A\3\2\2\2@\f\3\2\2\2@\16\3\2\2\2@\21\3\2\2\2"+
		"@\26\3\2\2\2@\33\3\2\2\2@+\3\2\2\2@\61\3\2\2\2@\66\3\2\2\2AG\3\2\2\2B"+
		"C\f\n\2\2CD\7\3\2\2DF\5\4\3\13EB\3\2\2\2FI\3\2\2\2GE\3\2\2\2GH\3\2\2\2"+
		"H\5\3\2\2\2IG\3\2\2\2JK\7\32\2\2KL\5\b\5\2LM\7\31\2\2MN\5\4\3\2N\7\3\2"+
		"\2\2OP\b\5\1\2PQ\7\5\2\2QR\5\b\5\2RS\7\6\2\2SW\3\2\2\2TW\7$\2\2UW\7#\2"+
		"\2VO\3\2\2\2VT\3\2\2\2VU\3\2\2\2Wl\3\2\2\2XY\f\n\2\2YZ\t\2\2\2Zk\5\b\5"+
		"\13[\\\f\t\2\2\\]\t\3\2\2]k\5\b\5\n^_\f\b\2\2_`\t\4\2\2`k\5\b\5\tab\f"+
		"\7\2\2bc\t\5\2\2ck\5\b\5\bde\f\6\2\2ef\7\23\2\2fk\5\b\5\7gh\f\5\2\2hi"+
		"\7\24\2\2ik\5\b\5\6jX\3\2\2\2j[\3\2\2\2j^\3\2\2\2ja\3\2\2\2jd\3\2\2\2"+
		"jg\3\2\2\2kn\3\2\2\2lj\3\2\2\2lm\3\2\2\2m\t\3\2\2\2nl\3\2\2\2\t\"\'@G"+
		"Vjl";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}