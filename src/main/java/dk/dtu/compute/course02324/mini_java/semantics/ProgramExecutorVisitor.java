package dk.dtu.compute.course02324.mini_java.semantics;

import dk.dtu.compute.course02324.mini_java.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static dk.dtu.compute.course02324.mini_java.model.Operator.*;
import static dk.dtu.compute.course02324.mini_java.utils.Shortcuts.FLOAT;
import static dk.dtu.compute.course02324.mini_java.utils.Shortcuts.INT;
import static java.util.Map.entry;

/**
 * Executes a Mini-Java program by evaluating expressions and executing statements.
 * <p>
 * This class extends {@link ProgramVisitor} and interprets a Mini-Java program
 * by computing values for expressions and handling different arithmetic operations.
 * It maintains a mapping of expression values and supports operations such as
 * addition, subtraction, multiplication, division, and modulo for integers and floats.
 * </p>
 */
public class ProgramExecutorVisitor extends ProgramVisitor {

    final private ProgramTypeVisitor pv;

    final public Map<Expression, Number> values = new HashMap<>();

    // Arithmetic operations for integers and floats

    /** Adds two integers. */
    private Function<List<Number>, Number> plus2int = args -> {
        int arg1 = args.get(0).intValue();
        int arg2 = args.get(1).intValue();
        return arg1 + arg2;
    };
    /** Returns a single integer without modification. */
    private Function<List<Number>, Number> plus1int = args -> {
        int arg1 = args.get(0).intValue();

        return arg1;
    };

    /** Adds two floating-point numbers. */
    private Function<List<Number>, Number> plus2float = args -> {
        float arg1 = args.get(0).floatValue();
        float arg2 = args.get(1).floatValue();
        return arg1 + arg2;
    };

    /** Returns a single floating-point number without modification. */
    private Function<List<Number>, Number> plus1float = args -> {
        float arg1 = args.get(0).floatValue();

        return arg1;
    };

    /** Subtracts two floating-point numbers. */
    private Function<List<Number>, Number> minus2float = args -> {
        float arg1 = args.get(0).floatValue();
        float arg2 = args.get(1).floatValue();
        return arg1 - arg2;
    };

    /** Subtracts two integers. */
    private Function<List<Number>, Number> minus2int = args -> {
        int arg1 = args.get(0).intValue();
        int arg2 = args.get(1).intValue();
        return arg1 - arg2;
    };

    /** Negates an integer. */
    private Function<List<Number>, Number> minus1int = args -> {
        int arg1 = args.get(0).intValue();

        return -arg1;
    };

    /** Negates a floating-point number. */
    private Function<List<Number>, Number> minus1float = args -> {
        float arg1 = args.get(0).floatValue();

        return -arg1;
    };

    /** Multiplies two integers. */
    private Function<List<Number>, Number> multint = args -> {
        int arg1 = args.get(0).intValue();
        int arg2 = args.get(1).intValue();
        return arg1 * arg2;
    };

    /** Multiplies two floating-point numbers. */
    private Function<List<Number>, Number> multfloat = args -> {
        float arg1 = args.get(0).floatValue();
        float arg2 = args.get(1).floatValue();
        return arg1 * arg2;
    };

    /** Divides two integers (integer division). */
    private Function<List<Number>, Number> divInt = args -> {
        int arg1 = args.get(0).intValue();
        int arg2 = args.get(1).intValue();
        return arg1 / arg2;
    };

    /** Divides two floats (floats division). */
    private Function<List<Number>, Number> divfloat = args -> {
        float arg1 = args.get(0).floatValue();
        float arg2 = args.get(1).floatValue();
        return arg1 / arg2;
    };

    /** Computes the remainder of integer division (modulo operation). */
    private Function<List<Number>, Number> modInt = args -> {
        int arg1 = args.get(0).intValue();
        int arg2 = args.get(1).intValue();
        return arg1 % arg2;
    };


    /**
     * The map below associates each operator for each possible type with a function
     * (lambda expression), that represents the semantics of that operation. These
     * define what happens when the operator needs to be executed.<p>
     * <p>
     *  Assignment 6a: This map and the functions above need to be extended in Assignment 6a
     *      (all operations with the respective types required in assignment must be defined above
     *      and added to the mapping below).
     */
    final private Map<Operator, Map<Type, Function<List<Number>, Number>>> operatorFunctions = Map.ofEntries(entry(PLUS2, Map.ofEntries(entry(INT, plus2int), entry(FLOAT, plus2float))), entry(PLUS1, Map.ofEntries(entry(INT, plus1int), entry(FLOAT, plus1float))), entry(MINUS2, Map.ofEntries(entry(INT, minus2int), entry(FLOAT, minus2float))), entry(MINUS1, Map.ofEntries(entry(INT, minus1int), entry(FLOAT, minus1float))), entry(MULT, Map.ofEntries(entry(INT, multint), entry(FLOAT, multfloat))), entry(DIV, Map.ofEntries(entry(INT, divInt), entry(FLOAT, divfloat))

    ), entry(MOD, Map.ofEntries(entry(INT, modInt))));

    public ProgramExecutorVisitor(ProgramTypeVisitor pv) {
        this.pv = pv;
    }

    public void visit(Statement statement) {
        statement.accept(this);
    }

    @Override
    public void visit(Sequence sequence) {
        for (Statement substatement : sequence.statements) {
            visit(substatement);
        }
    }

    @Override
    public void visit(Declaration declaration) {
        if (declaration.expression != null) {
            declaration.expression.accept(this);
            Number result = values.get(declaration.expression);
            values.put(declaration.variable, result);
        }
    }

    /**
     * Visits a {@link PrintStatement} and executes the print operation.
     * <p>
     * This method first evaluates the expression in the print statement,
     * then prints the prefix followed by the current value of the evaluated expression.
     * </p>
     *
     * @param printStatement the {@link PrintStatement} to be executed
     */
    @Override
    public void visit(PrintStatement printStatement) {

        printStatement.expression.accept(this);

        System.out.println(printStatement.prefix + values);


    }

    /**
     * Visits a {@link WhileLoop} node and executes its logic.
     * <p>
     * This method interprets and executes a while-loop structure.
     * It continuously evaluates the loop condition and executes the loop body
     * until the condition evaluates to a negative number (stopping the loop).
     * </p>
     *
     * @param whileLoop the {@link WhileLoop} node representing the loop
     */
    @Override
    public void visit(WhileLoop whileLoop) {
        while (true) {
            // Start an infinite loop – it will be stopped manually with a break
            whileLoop.expression.accept(this);
            // Evaluate the loop condition, e.g., an expression like i + j, using accept(this)
            Number value = values.get(whileLoop.expression);
            // Retrieve the computed value of the condition from the values map

            // If the value is negative, break the loop.
            if (value instanceof Integer && value.intValue() < 0) break;
            if (value instanceof Float && value.floatValue() < 0) break;

            // Otherwise, execute the loop body
            whileLoop.statement.accept(this);
        }
    }



    /**
     * Visits an {@link Assignment} and updates the variable with the evaluated expression result.
     * <p>
     * This method first evaluates the right-hand side expression of the assignment.
     * Then it stores the result both for the assignment itself and for the assigned variable
     * in the {@code values} map.
     * </p>
     *
     * @param assignment the {@link Assignment} to be executed
     */
    @Override
    public void visit(Assignment assignment) {
        assignment.expression.accept(this);
        Number result = values.get(assignment.expression);

        // Store the result for the assignment and the variable
        values.put(assignment, result);
        values.put(assignment.variable, result);
    }

    @Override
    public void visit(Literal literal) {
        if (literal instanceof IntLiteral) {
            values.put(literal, ((IntLiteral) literal).literal);
        } else if (literal instanceof FloatLiteral) {
            values.put(literal, ((FloatLiteral) literal).literal);
        }
    }

    @Override
    public void visit(Var var) {
        // We do not need to do anything here; if the variable was assigned a
        // value already by an assignment or a declaration, this value will be
        // in the values map already (the respective assignment or declaration
        // should have added this value for variable already).
    }

    /**
     * Visits an {@link OperatorExpression}, evaluates its operands,
     * applies the corresponding arithmetic function, and stores the result.
     * <p>
     * The method retrieves the type of the expression and uses it to look up
     * the correct function (e.g., addition, multiplication) from {@code operatorFunctions}.
     * It then evaluates all operand expressions, applies the function, and stores
     * the final result in the {@code values} map.
     * </p>
     *
     * @param operatorExpression the {@link OperatorExpression} to be evaluated
     * @throws RuntimeException if the function or any operand value is missing
     */
    @Override
    public void visit(OperatorExpression operatorExpression) {
        Type type = pv.typeMapping.get(operatorExpression);

        // Get the function map for this operator
        Map<Type, Function<List<Number>, Number>> typeMap = operatorFunctions.get(operatorExpression.operator);


        // Retrieve the specific function for the current type
        // Function<List<Number>,Number> function = typeMap != null && type!= null ? typeMap.get(type) : null;
        Function<List<Number>, Number> function = null;
        if (typeMap != null && type != null) {
            function = typeMap.get(type);
        }

        // If no matching function is found, throw an error
        if (function == null) {
            throw new RuntimeException("No function of this type available");
        }

        // Evaluate all operands and collect their values
        List<Number> args = new ArrayList<>();
        for (Expression subexpression : operatorExpression.operands) {
            subexpression.accept(this);
            Number arg = values.get(subexpression);
            if (arg == null) {
                throw new RuntimeException("Value of subexpression does not exist");
            }
            args.add(arg);
        }

        // Apply the operator function to the evaluated operands
        Number result = function.apply(args);

        // Store the final result
        values.put(operatorExpression, result);
    }

}
