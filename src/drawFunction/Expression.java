package drawFunction;

import java.util.Scanner;

class Expression {// 0-9 + - * / % ^ F ( )   （F(x)为单参数常用数学函数）

    public static void main(String[] args) {
        String input = new String();
        @SuppressWarnings("resource")
        Scanner sc = new Scanner(System.in);
        System.out.println("表达式计算程序,支持0-9 + - * / % ^ F(x),F(x)为常用单参数函数(比如三角函数)");
        System.out.println("可以有一个变量,变量名必须为x，注意无空格且括号必须为英文,负数要用括号括起来");
        System.out.println("请输入表达式：");
        input = sc.nextLine();
//        System.out.println("请输入x的值:");
//        double x = sc.nextDouble();
        int count = 0;
        for (double i=-392*0.02;i<408*0.02;i+=0.02) {
            Expression exp = new Expression(input, i);
            System.out.println(i+":"+exp.calculator());
            count++;
        }
        System.out.println(count);
        
    }
    private final int MAX_SIZE = 100;
    private String expression = new String();
    private double x;
    private String[] op = new String[MAX_SIZE]; //操作符栈
    private String[] output = new String[MAX_SIZE]; //后缀表达式数组
    private double[] answer = new double[MAX_SIZE]; //运算结果栈
    private int top = -1;
    private int pos = 0;
    
    public Expression(String _expression, double _x) {
        expression = _expression;
        x = _x;
    }
    
    private int operatorType(char ch) {
        if (ch == '+' || ch == '-')
            return 1;
        if (ch == '*' || ch == '/' || ch == '%')
            return 2;
        if (ch == '^')
            return 3;
        return 0;
    }
    
    private boolean isAlpha(char ch) {
        if (ch >= 65 && ch <= 90 || 
                ch >= 97 && ch <=122 )
                return true;
            return false;
    }
    
    private boolean isNum(char ch) {
        if (ch >= 48 && ch <= 57)
            return true;
        return false;
    }
  
    private void change() {
        boolean xNegFlag = false; //x前面是否有-号即输入为：(-x)
        int lenNum = 0; //数字的长度（位数）
        int lenAlpha = 0; //函数的长度
        for (int i = 0; i < expression.length(); i++) {
            if (isNum(expression.charAt(i)) || expression.charAt(i) == '.' 
                    || (i > 0 && expression.charAt(i - 1) == '(' && expression.charAt(i) == '-')
                    && (expression.charAt(i + 1) == 'x' || expression.charAt(i + 1) == 'X')) {//数字(小数或者负数)
                lenNum++;

                //说明前面是字母，这个不是字母，就把完整函数名存进output数组，并把len设为0
                //如果为0则说明前面就已经不是字母了，就不用存，下面一个意思
                if (lenAlpha != 0)
                    op[++top] = expression.substring(i - lenAlpha, i);
                lenAlpha = 0;
            }
            else if (operatorType(expression.charAt(i)) != 0) {//操作符
                int now = operatorType(expression.charAt(i));
                if (lenNum != 0)
                    output[pos++] = expression.substring(i - lenNum, i);
                lenNum = 0;

                if (lenAlpha != 0)
                    op[++top] = expression.substring(i - lenAlpha, i);
                lenAlpha = 0;

                if (expression.charAt(i) == '-' && i > 0 && expression.charAt(i - 1) == '(' 
                        && (expression.charAt(i + 1) == 'x' || expression.charAt(i + 1) == 'X')) {//x前面的负号
                        xNegFlag = true;
                        continue; //直接continue。因为-不可能是末尾，别的都是else if的逻辑
                    }
                
                if (top > -1) {
                    int opTop = operatorType(op[top].charAt(0)); //如果是函数或者括号，都会返回0
                    if (isAlpha(op[top].charAt(0))) //如果是函数，优先级提到最大
                        opTop = 3;
                    while (opTop >= now) {//操作符栈顶元素优先级大于等于当前操作符
                        output[pos++] = op[top--];
                        if (top == -1 || op[top].charAt(0) == '(') //栈空或者遇到左括号跳出
                            break;
                        opTop = operatorType(op[top].charAt(0));
                        if (isAlpha(op[top].charAt(0)))
                            opTop = 3;
                    }
                }
                
                op[++top] = expression.substring(i, i + 1); //当前操作符入栈

            }
            else if (expression.charAt(i) == 'x' || expression.charAt(i) == 'X') {//变量x
            
                if (lenNum != 0)
                    output[pos++] = expression.substring(i - lenNum, i);
                lenNum = 0;

                if (lenAlpha != 0)
                    op[++top] = expression.substring(i - lenAlpha, i);
                lenAlpha = 0;

                double d = x; //下面如果直接x*=-1会修改原本的输入，所以弄个d
                if (xNegFlag) //如果x前面有负号
                {
                    xNegFlag = false;
                    d *= -1;
                }
                output[pos++] = Double.toString(d);
            }
            else if (isAlpha(expression.charAt(i))) {//字母（函数名比如sin这样的）
                lenAlpha++;

                if (lenNum != 0) //如果前面不是数字的话肯定之前len已经置为0了。
                    output[pos++] = expression.substring(i - lenNum, i);
                lenNum = 0;
            }
            else if (expression.charAt(i) == '(') {//左括号直接入栈
                if (lenAlpha != 0) //先判定字符，因为右括号左边的有效内容只能是函数
                    op[++top] = expression.substring(i - lenAlpha, i);
                lenAlpha = 0;
                op[++top] = "(";
            }
            else if (expression.charAt(i) == ')') {//遇到右括号，栈里的元素依次出栈到左括号
                if (lenNum != 0) //先判定数字，因为右括号左边的有效内容只能是数字
                    output[pos++] = expression.substring(i - lenNum, i);
                lenNum = 0;
                while (op[top] != "(") 
                    output[pos++] = op[top--];
                --top; //左括号出栈
            }
            if (i == expression.length() - 1) {//到达末尾
                if (lenNum != 0)
                    output[pos++] = expression.substring(i - lenNum + 1, i + 1);
                lenNum = 0;
            }
        }
        while (top != -1)
            output[pos++] = op[top--];
    }
    
    public double calculator()
    {
        change();
        for (int i = 0; i < pos; i++) {
            if (isNum(output[i].charAt(0)) || (output[i].length()>1 && isNum(output[i].charAt(1))))
                answer[++top] = Double.parseDouble(output[i]);
            else if (operatorType(output[i].charAt(0)) != 0) {
                char ch = output[i].charAt(0);//得到操作符
                double a, b;
                b = answer[top--];
                a = answer[top--]; //两个操作数出栈（注意顺序）
                switch (ch) {
                case '+':
                    answer[++top] = a + b;
                    break;
                case '-':
                    answer[++top] = a - b;
                    break;
                case '*':
                    answer[++top] = a * b;
                    break;
                case '/':
                    answer[++top] = a / b;
                    break;
                case '%':
                    answer[++top] = a % b;
                    break;
                case '^':
                    answer[++top] = Math.pow(a, b);
                    break;
                default:
                    System.out.println("calculator ERROR!");
                }
            }
            else if (isAlpha(output[i].charAt(0))) {
                String function = new String(output[i]); //取得函数名
                double d = answer[top--]; //取得一个操作数
                if (function.equals("ln"))
                    answer[++top] = Math.log(d);//数学库里面log是e为底log10才是10为底
                else if(function.equals("log"))
                    answer[++top] = Math.log10(d);
                else if(function.equals("ceil"))
                    answer[++top] = Math.ceil(d);
                else if(function.equals("floor"))
                    answer[++top] = Math.floor(d);
                else if(function.equals("sqrt"))
                    answer[++top] = Math.sqrt(d);
                else if(function.equals("abs"))
                    answer[++top] = Math.abs(d);
                else if (function.equals("sin"))
                    answer[++top] = Math.sin(d);
                else if (function.equals("sinh"))
                    answer[++top] = Math.sinh(d);
                else if (function.equals("asin"))
                    answer[++top] = Math.asin(d);
                else if (function.equals("cos"))
                    answer[++top] = Math.cos(d);
                else if (function.equals("cosh"))
                    answer[++top] = Math.cosh(d);
                else if (function.equals("acos"))
                    answer[++top] = Math.acos(d);
                else if (function.equals("tan"))
                    answer[++top] = Math.tan(d);
                else if (function.equals("tanh"))
                    answer[++top] = Math.tanh(d);
                else if (function.equals("atan"))
                    answer[++top] = Math.atan(d);
                else
                    answer[++top] = 0;
            }
        }
        return answer[top];
    }
};



