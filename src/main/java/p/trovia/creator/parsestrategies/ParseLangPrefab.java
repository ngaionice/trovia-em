package p.trovia.creator.parsestrategies;

import p.trovia.creator.Parser;
import p.trovia.objects.Article;
import p.trovia.objects.LangFile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseLangPrefab implements ParseStrategy {

    public Article parseObject(String splitString, String absPath) {

        // instantiate the identifiers and variables
        String itemIdentifier = " 24 70 72 65 66 61 62 73 "; // $prefab
        String hexAlphabetExtended = "0A|[4][1-9A-F]|[5][0-9A]|[6][0-9A-F]|[7][0-9A]|2[017CE]|3[AF]|5[CF]";
        List<String[]> pairs = new ArrayList<>();
        Pattern p = Pattern.compile(hexAlphabetExtended);

        // trim the initial part, which is irrelevant, then split by "$prefab"
        String trimmedString = splitString.substring(splitString.indexOf("24 70"));
        String[] pathsAndStrings = trimmedString.split(itemIdentifier);
//        System.out.println(pathsAndStrings.length); // used for troubleshooting

        // split each item into the path and the actual string
        for (String s : pathsAndStrings) {
            String[] itemNameList = s.split(" 18 \\w\\w ");
            String stringPath = "24 70 72 65 66 61 62 73 " + itemNameList[0]; // add the "$prefab" back in since it was split
            Matcher m1 = p.matcher(itemNameList[1]);

            // find the string, convert to ASCII, and put the array into the list, if no string is found, break the loop
            if (m1.find()) {
                String string = itemNameList[1].substring(m1.start(), itemNameList[1].indexOf(" BE"));
                pairs.add(new String[]{Parser.hexToAscii(stringPath), Parser.hexToAscii(string)});
            } else {
                System.out.println("Something is problematic, check " + s + " in " + absPath);
                break;
            }
        }

        // extract the path
        String path = absPath.substring(absPath.lastIndexOf("\\")+1, absPath.indexOf(".binfab"));

        return new LangFile(path, pairs);
    }
}