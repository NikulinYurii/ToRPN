import java.io.*;
import java.util.*;

public class Main {
    private static final int LEFT_ASSOC = 0;
    private static final int RIGHT_ASSOC = 1;
    private static final Map<String, int[]> OPERATORS = new HashMap<String, int[]>();
    private static final String FILEREAD_PATH = "/home/yurii/Documents/idea progect/TransformationToRPN/read";
    private static final String FILEWREATE_PATH = "/home/yurii/Documents/idea progect/TransformationToRPN/write";


    static {
        OPERATORS.put("+", new int[]{0, LEFT_ASSOC});
        OPERATORS.put("-", new int[]{0, LEFT_ASSOC});
        OPERATORS.put("*", new int[]{5, LEFT_ASSOC});
        OPERATORS.put("/", new int[]{5, LEFT_ASSOC});
    }

    private static boolean isOperator(String token) {
        return OPERATORS.containsKey(token);
    }

    private static boolean isAssociative(String token, int type) {
        if (!isOperator(token)) {
            throw new IllegalArgumentException("Invalid token: " + token);
        }

        if (OPERATORS.get(token)[1] == type) {
            return true;
        }
        return false;
    }

    private static final int cmpPrecedence(String token1, String token2) {
        if (!isOperator(token1) || !isOperator(token2)) {
            throw new IllegalArgumentException("Invalid tokens: " + token1
                    + " " + token2);
        }
        return OPERATORS.get(token1)[0] - OPERATORS.get(token2)[0];
    }

    public static String[] infixToRPN(String[] inputTokens) {
        ArrayList<String> out = new ArrayList<>();
        Stack<String> stack = new Stack<>();

        for (String token : inputTokens) {
            if (isOperator(token)) {
                while (!stack.empty() && isOperator(stack.peek())) {
                    if ((isAssociative(token, LEFT_ASSOC) &&
                            cmpPrecedence(token, stack.peek()) <= 0) ||
                            (isAssociative(token, RIGHT_ASSOC) &&
                                    cmpPrecedence(token, stack.peek()) < 0)) {
                        out.add(stack.pop());
                        continue;
                    }
                    break;
                }
                stack.push(token);
            } else if (token.equals("(")) {
                stack.push(token);  //
            } else if (token.equals(")")) {
                while (!stack.empty() && !stack.peek().equals("(")) {
                    out.add(stack.pop());
                }
                stack.pop();
            } else {
                out.add(token);
            }
        }
        while (!stack.empty()) {
            out.add(stack.pop());
        }
        String[] output = new String[out.size()];
        return out.toArray(output);
    }

    public static List<String[]> read(String pathToFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(FILEREAD_PATH));
        List<String[]> lines = new ArrayList<>();
        int linesToRead = Integer.valueOf(reader.readLine());

        if (linesToRead >= 100) {
            linesToRead = 100;
            System.out.printf("There can be no more 100 expressions");
        }

        for (int i = 1; reader.ready(); i++) {
            if (linesToRead >= i && conditions(reader.readLine())) {
                lines.add(reader.readLine().split(""));
            } else {
                break;
            }
        }

        return lines;
    }

    public static boolean conditions(String text) {

        String[] textArr = text.split("");
        List<String> newTexrArr = new ArrayList<>();

        for (int i = 0; i < textArr.length; i++) {
            if (textArr[i] == "[") {
                i++;
                while (textArr[i] != "]" || textArr.length <= i) {
                    i++;
                }
            } else {
                newTexrArr.add(textArr[i]);
            }
        }
        if (newTexrArr.size() >= 400) {
            return false;
        }

        return true;
    }




    public static void main(String[] args) throws IOException {

        FileWriter fileWriter = new FileWriter(FILEWREATE_PATH);
        List<String[]> inputList = read(FILEREAD_PATH);

        for (int i = 0; i < inputList.size(); i++) {
            String[] output = infixToRPN(inputList.get(i));

            for (String token : output) {
                fileWriter.write(token + " ");
            }
            fileWriter.write("\n");
            fileWriter.flush();
        }
        fileWriter.close();
    }
}
