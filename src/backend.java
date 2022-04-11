import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Random;

public class backend {
    int[] numbers = new int[90];
    int j = 1;

    public int[] getNumbers() {
        return numbers;
    }

    public backend() {
        //init array
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = j;
            j++;
        }
        numbers = rand(numbers, numbers.length);
    }

    public static int[] rand (int[] arr, int a) {
        Random rd = new Random();
        for (int i = a-1; i > 0; i--) {
            int j = rd.nextInt(i+1);

            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
        return arr;
    }

    public static void reset(JLabel num, int[] numbers) {
        int[] newNum = new int[90];
        int j = 1;
        for (int i = 0; i < newNum.length; i++) {
            newNum[i] = j;
            j++;
        }
        numbers = rand(newNum, newNum.length);
        num.setText("0");
    }
}
