package drawFunction;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class DrawFunction extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public DrawFunction() {
        Function f = new Function();
        String s = new String();
//        s = "x^2";
//        s = "sin(x)";
        s = JOptionPane.showInputDialog("欢迎使用函数绘图，请输入表达式：");
        if (s == null || s.trim().equals(""))//取消事件不会产生String对象，所以顺序不能反
        {
           JOptionPane.showMessageDialog(null,"输入错误","提示信息",JOptionPane.ERROR_MESSAGE);
           System.exit(0);
        }
        f.setExpression(s);
        add(f);
    }

    public static void main(String[] args) {
        DrawFunction frame = new DrawFunction();
        frame.setTitle("DrawFunction");
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

}

class Function extends AbstractDrawFunction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    @Override
    protected double f(double x) {
        Expression exp = new Expression(expression, x);
        return exp.calculator();
    }

}
