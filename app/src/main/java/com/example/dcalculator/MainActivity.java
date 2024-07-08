package com.example.dcalculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private TextView tvDisplay;
    private StringBuilder input;
    private BigDecimal num1, num2;
    private String operator;
    private boolean isNewCalculation;
    private final DecimalFormat decimalFormat = new DecimalFormat("#.##########");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDisplay = findViewById(R.id.tv_display);
        input = new StringBuilder();
        isNewCalculation = true;

        // Initialize buttons
        initializeButtons();
    }

    private void initializeButtons() {
        Button btnAC = findViewById(R.id.btn_ac);
        Button btnNegate = findViewById(R.id.btn_negate);
        Button btnPercent = findViewById(R.id.btn_percent);
        Button btnDivide = findViewById(R.id.btn_divide);
        Button btnMultiply = findViewById(R.id.btn_multiply);
        Button btnSubtract = findViewById(R.id.btn_subtract);
        Button btnAdd = findViewById(R.id.btn_add);
        Button btnEqual = findViewById(R.id.btn_equal);
        Button btnDecimal = findViewById(R.id.btn_decimal);

        // Set onClickListeners for number buttons
        setNumberButtonListeners();

        // Set onClickListeners for operator buttons
        setOperatorButtonListeners();

        // AC button clears display and resets variables
        btnAC.setOnClickListener(v -> {
            clearCalculator();
        });

        // Decimal button
        btnDecimal.setOnClickListener(v -> {
            if (isNewCalculation) {
                input.setLength(0); // Clear input if starting a new calculation
                isNewCalculation = false;
            }
            if (!input.toString().contains(".")) {
                input.append(".");
                tvDisplay.setText(input.toString());
                adjustFontSize();
            }
        });

        // Negate button
        btnNegate.setOnClickListener(v -> {
            if (input.length() > 0) {
                String currentInput = input.toString();
                if (currentInput.charAt(0) == '-') {
                    input.deleteCharAt(0);
                } else {
                    input.insert(0, '-');
                }
                tvDisplay.setText(input.toString());
                adjustFontSize();
            }
        });

        // Percent button (not implemented for large calculations)
        btnPercent.setOnClickListener(v -> {
            // Handle percentage calculation if needed
        });

        // Equal button
        btnEqual.setOnClickListener(v -> {
            performEqualOperation();
        });
    }

    // Set onClickListeners for number buttons
    private void setNumberButtonListeners() {
        for (int i = 0; i <= 9; i++) {
            int buttonId = getResources().getIdentifier("btn_" + i, "id", getPackageName());
            Button button = findViewById(buttonId);
            final int finalI = i;
            button.setOnClickListener(v -> {
                if (isNewCalculation) {
                    input.setLength(0); // Clear input if starting a new calculation
                    isNewCalculation = false;
                }
                input.append(finalI);
                tvDisplay.setText(input.toString());
                adjustFontSize();
            });
        }
    }

    // Set onClickListeners for operator buttons
    private void setOperatorButtonListeners() {
        Button btnDivide = findViewById(R.id.btn_divide);
        Button btnMultiply = findViewById(R.id.btn_multiply);
        Button btnSubtract = findViewById(R.id.btn_subtract);
        Button btnAdd = findViewById(R.id.btn_add);

        View.OnClickListener operatorClickListener = v -> {
            if (input.length() > 0) {
                if (operator != null && !isNewCalculation) {
                    // Evaluate ongoing calculation if there's an operator already
                    performEqualOperation();
                } else {
                    // First operator after entering a number
                    num1 = new BigDecimal(input.toString());
                }

                operator = ((Button) v).getText().toString();
                input.append(" ").append(operator).append(" ");
                tvDisplay.setText(input.toString());
                adjustFontSize();
                isNewCalculation = false;
            }
        };

        btnDivide.setOnClickListener(operatorClickListener);
        btnMultiply.setOnClickListener(operatorClickListener);
        btnSubtract.setOnClickListener(operatorClickListener);
        btnAdd.setOnClickListener(operatorClickListener);
    }

    // Adjust font size based on input length
    private void adjustFontSize() {
        int length = input.length();
        if (length <= 10) {
            tvDisplay.setTextSize(32); // Default size
        } else if (length <= 20) {
            tvDisplay.setTextSize(26);
        } else if (length <= 30) {
            tvDisplay.setTextSize(20);
        } else {
            tvDisplay.setTextSize(15); // Smallest size
        }
    }

    // Perform equal operation
    private void performEqualOperation() {
        if (operator != null && input.length() > 0) {
            // Ensure there is a number after the operator
            String[] parts = input.toString().split("\\s+");
            if (parts.length >= 3) {
                num2 = new BigDecimal(parts[parts.length - 1]);
                BigDecimal result = num1;
                for (int i = 1; i < parts.length; i += 2) {
                    String currentOperator = parts[i];
                    BigDecimal nextNum = new BigDecimal(parts[i + 1]);
                    switch (currentOperator) {
                        case "+":
                            result = result.add(nextNum);
                            break;
                        case "-":
                            result = result.subtract(nextNum); // Correct subtraction operation
                            break;
                        case "×":
                            result = result.multiply(nextNum);
                            break;
                        case "÷":
                            // Handle division with non-zero check
                            if (nextNum.compareTo(BigDecimal.ZERO) != 0)
                                result = result.divide(nextNum, 10, RoundingMode.HALF_UP);
                            else
                                result = BigDecimal.ZERO;
                            break;
                        default:
                            break;
                    }
                }
                input.setLength(0);
                input.append(decimalFormat.format(result));
                tvDisplay.setText(input.toString());
                adjustFontSize();
                num1 = result;
                operator = null;
                isNewCalculation = true;
            }
        }
    }

    // Clear calculator state
    private void clearCalculator() {
        input.setLength(0);
        tvDisplay.setText("0");
        adjustFontSize();
        num1 = null;
        num2 = null;
        operator = null;
        isNewCalculation = true;
    }
}
