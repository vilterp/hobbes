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
		System.out.println(p.readRules());
	}
	
	public Parser() {
		pos = 0;
		waiting = false;
	}
	
	public SyntaxNode parse(String line) {
		
		return null;
	}
	
	private void loadRules() {
		for(String rule: readRules()) {
			String[] nameAndRule = rule.split(" = ",1);
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
		int pos = 0;
		while(pos < rule.length()) {
			String remainder = rule.substring(pos);
			// ...
		}
		return null;
	}
	
	private MatchResult matchRuleType(Pattern rulePattern, String rule) {
		return rulePattern.matcher(rule).toMatchResult();
	}
	
}
