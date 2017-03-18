// Generated from Language.g4 by ANTLR 4.6
package org.wotopul;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class LanguageLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.6", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, ST_SKIP=5, READ=6, WRITE=7, OP_MUL=8, 
		OP_DIV=9, OP_MOD=10, OP_ADD=11, OP_SUB=12, NUM=13, ID=14, WS=15;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "ST_SKIP", "READ", "WRITE", "OP_MUL", 
		"OP_DIV", "OP_MOD", "OP_ADD", "OP_SUB", "NUM", "ID", "WS"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "';'", "':='", "'('", "')'", "'skip'", "'read'", "'write'", "'*'", 
		"'/'", "'%'", "'+'", "'-'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, "ST_SKIP", "READ", "WRITE", "OP_MUL", "OP_DIV", 
		"OP_MOD", "OP_ADD", "OP_SUB", "NUM", "ID", "WS"
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


	public LanguageLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Language.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\21W\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\3\2\3\2\3\3\3\3\3\3"+
		"\3\4\3\4\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3"+
		"\b\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\6\16F\n\16\r"+
		"\16\16\16G\3\17\3\17\7\17L\n\17\f\17\16\17O\13\17\3\20\6\20R\n\20\r\20"+
		"\16\20S\3\20\3\20\2\2\21\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f"+
		"\27\r\31\16\33\17\35\20\37\21\3\2\6\3\2\62;\4\2C\\c|\5\2\62;C\\c|\5\2"+
		"\13\f\17\17\"\"Y\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13"+
		"\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2"+
		"\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\3"+
		"!\3\2\2\2\5#\3\2\2\2\7&\3\2\2\2\t(\3\2\2\2\13*\3\2\2\2\r/\3\2\2\2\17\64"+
		"\3\2\2\2\21:\3\2\2\2\23<\3\2\2\2\25>\3\2\2\2\27@\3\2\2\2\31B\3\2\2\2\33"+
		"E\3\2\2\2\35I\3\2\2\2\37Q\3\2\2\2!\"\7=\2\2\"\4\3\2\2\2#$\7<\2\2$%\7?"+
		"\2\2%\6\3\2\2\2&\'\7*\2\2\'\b\3\2\2\2()\7+\2\2)\n\3\2\2\2*+\7u\2\2+,\7"+
		"m\2\2,-\7k\2\2-.\7r\2\2.\f\3\2\2\2/\60\7t\2\2\60\61\7g\2\2\61\62\7c\2"+
		"\2\62\63\7f\2\2\63\16\3\2\2\2\64\65\7y\2\2\65\66\7t\2\2\66\67\7k\2\2\67"+
		"8\7v\2\289\7g\2\29\20\3\2\2\2:;\7,\2\2;\22\3\2\2\2<=\7\61\2\2=\24\3\2"+
		"\2\2>?\7\'\2\2?\26\3\2\2\2@A\7-\2\2A\30\3\2\2\2BC\7/\2\2C\32\3\2\2\2D"+
		"F\t\2\2\2ED\3\2\2\2FG\3\2\2\2GE\3\2\2\2GH\3\2\2\2H\34\3\2\2\2IM\t\3\2"+
		"\2JL\t\4\2\2KJ\3\2\2\2LO\3\2\2\2MK\3\2\2\2MN\3\2\2\2N\36\3\2\2\2OM\3\2"+
		"\2\2PR\t\5\2\2QP\3\2\2\2RS\3\2\2\2SQ\3\2\2\2ST\3\2\2\2TU\3\2\2\2UV\b\20"+
		"\2\2V \3\2\2\2\6\2GMS\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}