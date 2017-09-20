public class Test{
	public static void main(String argv[]){
		Test t1 = new Test();
		Test t2 = new Test();

		Test t1Clone = t1;

		System.out.println(t1 == t2);
		System.out.println(t1);
		System.out.println(t2);
		System.out.println(t1 == t1Clone);
	}
}