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
		ST_SKIP=18, READ=19, WRITE=20, IF=21, THEN=22, ELIF=23, ELSE=24, FI=25, 
		WHILE=26, DO=27, OD=28, NUM=29, ID=30, WS=31;
	public static final int
		RULE_program = 0, RULE_stmt = 1, RULE_elif = 2, RULE_expr = 3;
	public static final String[] ruleNames = {
		"program", "stmt", "elif", "expr"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "';'", "':='", "'('", "')'", "'*'", "'/'", "'%'", "'+'", "'-'", 
		"'<'", "'<='", "'>'", "'>='", "'=='", "'!='", "'&&'", "'||'", "'skip'", 
		"'read'", "'write'", "'if'", "'then'", "'elif'", "'else'", "'fi'", "'while'", 
		"'do'", "'od'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, "ST_SKIP", "READ", "WRITE", "IF", 
		"THEN", "ELIF", "ELSE", "FI", "WHILE", "DO", "OD", "NUM", "ID", "WS"
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
			setState(47);
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
			}
			_ctx.stop = _input.LT(-1);
			setState(54);
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
					setState(49);
					if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
					setState(50);
					match(T__0);
					setState(51);
					((SequenceContext)_localctx).rest = stmt(7);
					}
					} 
				}
				setState(56);
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
			setState(57);
			match(ELIF);
			setState(58);
			((ElifContext)_localctx).cond = expr(0);
			setState(59);
			match(THEN);
			setState(60);
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
			setState(69);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__2:
				{
				_localctx = new ParenthesisContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(63);
				match(T__2);
				setState(64);
				expr(0);
				setState(65);
				match(T__3);
				}
				break;
			case ID:
				{
				_localctx = new VariableContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(67);
				((VariableContext)_localctx).name = match(ID);
				}
				break;
			case NUM:
				{
				_localctx = new ConstContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(68);
				((ConstContext)_localctx).value = match(NUM);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(91);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(89);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
					case 1:
						{
						_localctx = new InfixContext(new ExprContext(_parentctx, _parentState));
						((InfixContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(71);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(72);
						((InfixContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__4) | (1L << T__5) | (1L << T__6))) != 0)) ) {
							((InfixContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(73);
						((InfixContext)_localctx).right = expr(9);
						}
						break;
					case 2:
						{
						_localctx = new InfixContext(new ExprContext(_parentctx, _parentState));
						((InfixContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(74);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(75);
						((InfixContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==T__7 || _la==T__8) ) {
							((InfixContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(76);
						((InfixContext)_localctx).right = expr(8);
						}
						break;
					case 3:
						{
						_localctx = new InfixContext(new ExprContext(_parentctx, _parentState));
						((InfixContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(77);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(78);
						((InfixContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__9) | (1L << T__10) | (1L << T__11) | (1L << T__12))) != 0)) ) {
							((InfixContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(79);
						((InfixContext)_localctx).right = expr(7);
						}
						break;
					case 4:
						{
						_localctx = new InfixContext(new ExprContext(_parentctx, _parentState));
						((InfixContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(80);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(81);
						((InfixContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==T__13 || _la==T__14) ) {
							((InfixContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(82);
						((InfixContext)_localctx).right = expr(6);
						}
						break;
					case 5:
						{
						_localctx = new InfixContext(new ExprContext(_parentctx, _parentState));
						((InfixContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(83);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(84);
						((InfixContext)_localctx).op = match(T__15);
						setState(85);
						((InfixContext)_localctx).right = expr(5);
						}
						break;
					case 6:
						{
						_localctx = new InfixContext(new ExprContext(_parentctx, _parentState));
						((InfixContext)_localctx).left = _prevctx;
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(86);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(87);
						((InfixContext)_localctx).op = match(T__16);
						setState(88);
						((InfixContext)_localctx).right = expr(4);
						}
						break;
					}
					} 
				}
				setState(93);
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
			return precpred(_ctx, 6);
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3!a\4\2\t\2\4\3\t\3"+
		"\4\4\t\4\4\5\t\5\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\7\3!\n\3\f\3\16\3$\13\3\3\3\3\3\5\3"+
		"(\n\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3\62\n\3\3\3\3\3\3\3\7\3\67\n"+
		"\3\f\3\16\3:\13\3\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\5\5"+
		"H\n\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5"+
		"\3\5\3\5\7\5\\\n\5\f\5\16\5_\13\5\3\5\2\4\4\b\6\2\4\6\b\2\6\3\2\7\t\3"+
		"\2\n\13\3\2\f\17\3\2\20\21l\2\n\3\2\2\2\4\61\3\2\2\2\6;\3\2\2\2\bG\3\2"+
		"\2\2\n\13\5\4\3\2\13\3\3\2\2\2\f\r\b\3\1\2\r\62\7\24\2\2\16\17\7 \2\2"+
		"\17\20\7\4\2\2\20\62\5\b\5\2\21\22\7 \2\2\22\23\7\4\2\2\23\24\7\25\2\2"+
		"\24\25\7\5\2\2\25\62\7\6\2\2\26\27\7\26\2\2\27\30\7\5\2\2\30\31\5\b\5"+
		"\2\31\32\7\6\2\2\32\62\3\2\2\2\33\34\7\27\2\2\34\35\5\b\5\2\35\36\7\30"+
		"\2\2\36\"\5\4\3\2\37!\5\6\4\2 \37\3\2\2\2!$\3\2\2\2\" \3\2\2\2\"#\3\2"+
		"\2\2#\'\3\2\2\2$\"\3\2\2\2%&\7\32\2\2&(\5\4\3\2\'%\3\2\2\2\'(\3\2\2\2"+
		"()\3\2\2\2)*\7\33\2\2*\62\3\2\2\2+,\7\34\2\2,-\5\b\5\2-.\7\35\2\2./\5"+
		"\4\3\2/\60\7\36\2\2\60\62\3\2\2\2\61\f\3\2\2\2\61\16\3\2\2\2\61\21\3\2"+
		"\2\2\61\26\3\2\2\2\61\33\3\2\2\2\61+\3\2\2\2\628\3\2\2\2\63\64\f\b\2\2"+
		"\64\65\7\3\2\2\65\67\5\4\3\t\66\63\3\2\2\2\67:\3\2\2\28\66\3\2\2\289\3"+
		"\2\2\29\5\3\2\2\2:8\3\2\2\2;<\7\31\2\2<=\5\b\5\2=>\7\30\2\2>?\5\4\3\2"+
		"?\7\3\2\2\2@A\b\5\1\2AB\7\5\2\2BC\5\b\5\2CD\7\6\2\2DH\3\2\2\2EH\7 \2\2"+
		"FH\7\37\2\2G@\3\2\2\2GE\3\2\2\2GF\3\2\2\2H]\3\2\2\2IJ\f\n\2\2JK\t\2\2"+
		"\2K\\\5\b\5\13LM\f\t\2\2MN\t\3\2\2N\\\5\b\5\nOP\f\b\2\2PQ\t\4\2\2Q\\\5"+
		"\b\5\tRS\f\7\2\2ST\t\5\2\2T\\\5\b\5\bUV\f\6\2\2VW\7\22\2\2W\\\5\b\5\7"+
		"XY\f\5\2\2YZ\7\23\2\2Z\\\5\b\5\6[I\3\2\2\2[L\3\2\2\2[O\3\2\2\2[R\3\2\2"+
		"\2[U\3\2\2\2[X\3\2\2\2\\_\3\2\2\2][\3\2\2\2]^\3\2\2\2^\t\3\2\2\2_]\3\2"+
		"\2\2\t\"\'\618G[]";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}