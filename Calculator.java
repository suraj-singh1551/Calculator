import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Stack;

public class Calculator {
    private JTextField displayBox;
    private StringBuilder input = new StringBuilder();

    public Calculator() {
        // Set up the frame
        JFrame frame = new JFrame("Simple Calculator");
        frame.setSize(400, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Display box
        displayBox = new JTextField();
        displayBox.setEditable(false);
        displayBox.setFont(new Font("Arial", Font.BOLD, 28));
        displayBox.setHorizontalAlignment(JTextField.RIGHT);
        displayBox.setPreferredSize(new Dimension(400, 80));
        frame.add(displayBox, BorderLayout.NORTH);

        // Button panel with GridLayout for better control
        JPanel buttonPanel = new JPanel(new GridLayout(5, 4, 5, 5)); // 5 rows, 4 columns
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Button labels for the calculator
        String[] buttonLabels = {
                "C", "←", "%", "/",
                "7", "8", "9", "*",
                "4", "5", "6", "-",
                "1", "2", "3", "+",
                "0", ".", "Result", "Reset"
        };

        // Add buttons dynamically to the panel
        ActionListener listener = e -> handleButtonClick(e.getActionCommand());
        for (String label : buttonLabels) {
            JButton button = createButton(label);
            button.addActionListener(listener);
            buttonPanel.add(button);
        }

        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    // Create buttons with improved styling
    private JButton createButton(String label) {
        JButton button = new JButton(label);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(Color.LIGHT_GRAY);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        return button;
    }

    private void handleButtonClick(String command) {
        switch (command) {
            case "Result" -> calculateResult();
            case "Reset" -> input.setLength(0);
            case "C" -> clearEntry();
            case "←" -> backspace();
            default -> input.append(command);
        }
        displayBox.setText(input.toString());
    }

    private void clearEntry() {
        input.setLength(0);
    }

    private void backspace() {
        if (input.length() > 0) {
            input.deleteCharAt(input.length() - 1);
        }
    }

    private void calculateResult() {
        try {
            double result = evaluateExpression(input.toString());
            input.setLength(0);
            input.append(result);
        } catch (Exception e) {
            input.setLength(0);
            input.append("Error");
        }
    }

    // Evaluate complex expressions
    private double evaluateExpression(String expression) {
        // Handle the percentage
        if (expression.contains("%")) {
            String[] parts = expression.split("%");
            if (parts.length > 1) {
                double base = evaluateExpression(parts[0]);
                double percent = evaluateExpression(parts[1]);
                return base * (percent / 100);
            }
        }

        Stack<Double> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();

        int i = 0;
        while (i < expression.length()) {
            char c = expression.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                StringBuilder number = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    number.append(expression.charAt(i++));
                }
                numbers.push(Double.parseDouble(number.toString()));
                continue;
            } else if (c == '(') {
                operators.push(c);
            } else if (c == ')') {
                while (operators.peek() != '(') {
                    numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.pop();
            } else if (c == '+' || c == '-' || c == '*' || c == '/' || c == '^') {
                while (!operators.isEmpty() && hasPrecedence(c, operators.peek())) {
                    numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.push(c);
            }
            i++;
        }

        while (!operators.isEmpty()) {
            numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
        }

        return numbers.pop();
    }

    private boolean hasPrecedence(char op1, char op2) {
        if ((op1 == '+' || op1 == '-') && (op2 == '*' || op2 == '/')) return false;
        return true;
    }

    private double applyOperation(char operator, double b, double a) {
        return switch (operator) {
            case '+' -> a + b;
            case '-' -> a - b;
            case '*' -> a * b;
            case '/' -> {
                if (b == 0) throw new ArithmeticException("Cannot divide by zero");
                yield a / b;
            }
            case '^' -> Math.pow(a, b);
            default -> 0;
        };
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Calculator::new);
    }
}