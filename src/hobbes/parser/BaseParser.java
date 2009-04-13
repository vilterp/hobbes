package hobbes.parser;

import hobbes.parser.rules.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.*;

public abstract class BaseParser {
	
	private static final String GRAMMARFILE = "src" + File.separator + "grammar.ebnf";
	
	// these will be used to see what type of rule segment each segment is
	// FIXME: can only have one literal segment in a rule
	private static final Pattern LITERAL_PATTERN = Pattern.compile("\"(.*)\"");
	private static final Pattern REGEX_PATTERN = Pattern.compile("\\? (.*) \\?");
	private static final Pattern REPEAT_PATTERN = Pattern.compile("\\{ (.*) \\}");
	private static final Pattern OPTIONS_PATTERN = Pattern.compile("\\[ (.*) \\]");
	private static final Pattern OPTIONAL_PATTERN = Pattern.compile("\\( (.*) \\)");
	private static final Pattern OTHER_RULE_PATTERN = Pattern.compile("([a-zA-Z_])");
	
	private String code;
	private int pos;
	private boolean waiting;
	private ArrayList<String> ruleNames;
	private HashMap<String,Rule> rules; // rule name => rule
	private HashMap<String,Method> methods; // method name => method
	
	public BaseParser() {
		pos = 0;
		code = "";
		waiting = false;
		ruleNames = new ArrayList<String>();
		rules = new HashMap<String,Rule>();
		methods = new HashMap<String,Method>();
		loadRules();
		loadMethods();
	}
	
	public boolean isWaiting() {
		return waiting;
	}
	
	public void parse(String line) throws MatchError {
		code += line;
		for(String ruleName: ruleNames) {
			try {
				matchRule(ruleName);
				code = "";
				return;
			} catch(MatchError e) {
				continue;
			}
		}
		throw new MatchError("Couldn't find a rule to match \""+line+"\".");
	}
	
	private void matchRule(String ruleName) throws MatchError {
		Rule rule = getRule(ruleName);
		ArrayList<String> results = matchSegments(rule.getSegments());
		try {
			getMethod(ruleName).invoke(this, results.toArray());
		} catch (NullPointerException e) {
			System.out.println("No method for rule \""+ruleName+"\"");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	private ArrayList<String> matchSegments(ArrayList<RuleSegment> segments) throws MatchError {
		ArrayList<String> results = new ArrayList<String>();
		for(RuleSegment segment: segments) {
			results.add(matchSegment(segment));
		}
		return results;
	}
	
	private String matchSegment(RuleSegment segment) throws MatchError {
		if(segment instanceof LiteralSegment)
			return matchLiteralSegment((LiteralSegment)segment);
		else if(segment instanceof RegexSegment)
			return matchRegexSegment((RegexSegment)segment);
		throw new MatchError();
	}
	
	private String matchLiteralSegment(LiteralSegment segment) throws MatchError {
		if(getRemainder().startsWith(segment.getValue())) {
			pos += segment.getValue().length();
			return segment.getValue();
		} else {
			throw new MatchError();
		}
	}
	
	private String matchRegexSegment(RegexSegment segment) throws MatchError {
		MatchResult result = segment.matchAgainst(getRemainder());
		if(result != null)
			return result.group(1);
		else
			throw new MatchError();
	}
	
	private String getRemainder() {
		return code.substring(pos);
	}

	private void loadMethods() {
		for(Method method: getClass().getDeclaredMethods())
			methods.put(method.getName(),method);
	}
	
	private void loadRules() {
		for(String rule: readRules()) {
			String[] nameAndRule = rule.split(" = ",0);
			ruleNames.add(nameAndRule[0]);
			rules.put(nameAndRule[0], parseRule(nameAndRule[1]));
		}
	}
	
	private ArrayList<String> readRules() {
		ArrayList<String> rules = new ArrayList<String>();
		// load grammar file
		Scanner grammar = null;
		try {
			grammar = new Scanner(new File(GRAMMARFILE));
		} catch (FileNotFoundException e) {
			System.out.println("can't find the grammar!");
		}
		// read rules, allowing for multi-liners and comments
		String currentrule = "";
		while(grammar.hasNext()) {
			String line = grammar.nextLine();
			if(line.startsWith("#")) // comment
				continue;
			else if(line.endsWith(";")) {
				currentrule += line.substring(0, line.length()-2);
				rules.add(currentrule);
				currentrule = "";
			} else
				currentrule += line;
		}
		return rules;
	}
	
	private Rule parseRule(String rule) {
		Rule result = new Rule();
		int posInRule = 0;
		while(posInRule < rule.length()) {
			String remainder = rule.substring(posInRule);
			if(remainder.startsWith(" , ")) {
				posInRule += 3;
				continue;
			}
			// literal rule, eg '"foo"'
			MatchResult literalMatch = matchRuleType(LITERAL_PATTERN,remainder);
			if(literalMatch != null) { // it's a literal rule segment, eg '"foo"'
				result.addSegment(new LiteralSegment(literalMatch.group(1)));
				posInRule += literalMatch.end();
				continue;
			}
			// regex rule segment, eg "? [a-z] ?"
			MatchResult regexMatch = matchRuleType(REGEX_PATTERN,remainder);
			if(regexMatch != null) {
				result.addSegment(new RegexSegment(Pattern.compile("("+regexMatch.group(1)+")")));
				posInRule += regexMatch.end();
				continue;
			}
			// repeated segment, eg '{ "bar" }'
			MatchResult repeatMatch = matchRuleType(REPEAT_PATTERN,remainder);
			if(repeatMatch != null) {
				result.addSegment(new RepeatSegment(parseRule(repeatMatch.group(1))));
				posInRule += repeatMatch.end();
				continue;
			}
			// options rule segment, eg '[ "this" | "or this" ]'
			MatchResult optionsMatch = matchRuleType(OPTIONS_PATTERN,remainder);
			if(optionsMatch != null) {
				OptionsSegment segment = new OptionsSegment();
				// FIXME: won't split correctly if the rule in option has a "|" in it
				for(String option: optionsMatch.group(1).split(" \\| "))
					segment.addOption(parseRule(option));
				result.addSegment(segment);
				posInRule += optionsMatch.end();
				continue;
			}
			// optional rule segment, eg '( "Im optional" )'
			MatchResult optionalMatch = matchRuleType(OPTIONAL_PATTERN,remainder);
			if(optionalMatch != null) {
				result.addSegment(new OptionalSegment(parseRule(optionalMatch.group(1))));
				posInRule += optionalMatch.end();
				continue;
			}
			// name of another rule
			MatchResult otherRuleMatch = matchRuleType(OTHER_RULE_PATTERN,remainder);
			if(otherRuleMatch != null) {
				String ruleName = otherRuleMatch.group(1);
				// FIXME: what if the rule isn't in rules yet cuz it's later in grammar?
				result.addSegment(new OtherRuleSegment(ruleName,rules.get(ruleName)));
				posInRule += otherRuleMatch.end();
				continue;
			}
			throw new GrammarError("In rule \""+rule+"\": can't tell what kind of rule \""+remainder+"\" is.");
		}
		return result;
	}
	
	private Rule getRule(String ruleName) {
		return rules.get(ruleName);
	}
	
	private Method getMethod(String methodName) {
		return methods.get(methodName);
	}
	
	private MatchResult matchRuleType(Pattern rulePattern, String rule) {
		Matcher matcher = rulePattern.matcher(rule);
		if(matcher.lookingAt())
			return matcher.toMatchResult();
		else
			return null;
	}
	
}
