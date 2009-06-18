public class Fib {

  public static void main(String[] args) {
    for(int i=0; i < 50; i++)
      System.out.println(fib(i));
  }
  
  public static int fib(int term) {
    if(term == 0 || term == 1)
      return 1;
    else
      return fib(term-1) + fib(term-2);
  }
  
}
