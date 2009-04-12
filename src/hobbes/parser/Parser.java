package hobbes.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.*;

public class Parser {
	
	private static final String GRAMMARFILE = "src" + File.separator + "grammar.ebnf";
	
	// these will be used to see what type of rule segment each segment is
	private static final Pattern LITERAL_PATTERN = Pattern.compile("\"(.*)\"");
	private static final Pattern REGEX_PATTERN = Pattern.compile("\\? (.*) \\?");
	private static final Pattern REPEAT_PATTERN = Pattern.compile("\\{ (.*) \\}");
	private static final Pattern OPTIONS_PATTERN = Pattern.compile("\\[ (.*) \\]");
	private static final Pattern OPTIONAL_PATTERN = Pattern.compile("\\( (.*) \\)");
	
	private String code;
	private int pos;
	private boolean waiting;
	private ArrayList<String> ruleNames;
	private HashMap<String,Rule> rules; // rule name => rule
	
	public static void main(String[] args) {
		Parser p = new Parser();
		p.loadRules();
		System.out.println(p.rules);
		System.out.println(p.ruleNames);
	}
	
	public Parser() {
		pos = 0;
		waiting = false;
		ruleNames = new ArrayList<String>();
		rules = new HashMap<String,Rule>();
	}
	
	public SyntaxNode parse(String line) {
		
		return null;
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
			// check for literal rule
			MatchResult literalMatch = matchRuleType(LITERAL_PATTERN,remainder);
			if(literalMatch != null) { // it's a literal rule segment, eg '"foo"'
				result.addSegment(new LiteralSegment(literalMatch.group(1)));
				posInRule += literalMatch.end();
				continue;
			}
			MatchResult regexMatch = matchRuleType(REGEX_PATTERN,remainder);
			if(regexMatch != null) { // it's a literal rule segment, eg '"foo"'
				result.addSegment(new RegexSegment(Pattern.compile("("+regexMatch.group(1)+")")));
				posInRule += regexMatch.end();
				continue;
			}
			MatchResult repeatMatch = matchRuleType(REPEAT_PATTERN,remainder);
			MatchResult optionsMatch = matchRuleType(OPTIONS_PATTERN,remainder);
			MatchResult optionalMatch = matchRuleType(OPTIONAL_PATTERN,remainder);
		}
		return result;
	}
	
	private MatchResult matchRuleType(Pattern rulePattern, String rule) {
		Matcher matcher = rulePattern.matcher(rule);
		if(matcher.lookingAt())
			return matcher.toMatchResult();
		else
			return null;
	}
	
}
