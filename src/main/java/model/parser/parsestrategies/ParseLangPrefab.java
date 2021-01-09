package model.parser.parsestrategies;

import model.parser.Parser;
import model.objects.Article;
import model.objects.LangFile;
import local.Markers;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseLangPrefab implements ParseStrategy {

    @Override
    public Article parseObject(String splitString, String absPath) throws ParseException {

        // for debugging
        System.out.println("Processing " + absPath);

        // instantiate the identifiers and variables
        Markers m = new Markers();
        Map<String, String> pairs = new HashMap<>(1000);
        Pattern p = Pattern.compile(m.alphabetExtended);

        // trim the initial part, which is irrelevant, then split by "$prefab"
        if (!splitString.contains("24 70 72 65 66 61 62 73")) {
            throw new ParseException("Lang File parsing failed at " + absPath + "This is not a valid file.");
        }
        String trimmedString = splitString.substring(splitString.indexOf(" 24 70"));
        String[] pathsAndStrings = trimmedString.split(m.prefabSpaced);
//        System.out.println(pathsAndStrings.length); // used for troubleshooting

        // split each item into the path and the actual string
        for (int i = 1; i < pathsAndStrings.length; i++) {
            String[] itemNameList = pathsAndStrings[i].split(" 18 \\w\\w ");
            String stringPath = "24 70 72 65 66 61 62 73 " + itemNameList[0]; // add the "$prefab" back in since it was split
            Matcher m1 = p.matcher(itemNameList[1]);

            // first check if there is a valid string: if there isn't, then skip over this object; done by looking at the first 3 'block's of itemNameList[1]
            if (!itemNameList[1].substring(0,2).matches(m.alphabetExtended) ||
                    !itemNameList[1].substring(3,5).matches(m.alphabetExtended) ||
                    !itemNameList[1].substring(6,8).matches(m.alphabetExtended)) {
                continue;
            }

            // find the string, convert to ASCII, and put the array into the list, if no string is found, break the loop
            if (m1.find()) {
                String string = itemNameList[1].substring(m1.start(), itemNameList[1].indexOf(" BE"));
                pairs.put(Parser.hexToAscii(stringPath).toLowerCase(), Parser.hexToAscii(string));
            } else {
                System.out.println("Something is problematic, check " + pathsAndStrings[i] + " in " + absPath);
                throw new ParseException("Lang File parsing failed at " + absPath + ".\n" + pathsAndStrings[i] + "\n could not be converted to ASCII.");
            }
        }

        // extract the path
        String name = absPath.substring(absPath.lastIndexOf("\\")+1, absPath.indexOf(m.endFile));
        String rPath = absPath.substring(absPath.indexOf("language"), absPath.indexOf(m.endFile));
        rPath = rPath.replaceAll("\\\\", "/");

        return new LangFile(name, rPath, pairs);
    }
}