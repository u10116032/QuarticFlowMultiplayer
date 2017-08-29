import java.net.*;
import java.io.*;
import java.util.*;

public class ServerTest{



	private static List<byte[]> splitRequestLine(byte[] requestBytes)
	{
		List<byte[]> tokenList = new ArrayList<byte[]>();

		int delimIndex = 0;
		for (int i = 0; i < requestBytes.length; ++i) {
			if (requestBytes[i] == ' ') {
				tokenList.add(Arrays.copyOfRange(requestBytes, delimIndex, i));
				delimIndex = i+1;
				break;
			}
		}

		if (delimIndex != requestBytes.length)
			tokenList.add(Arrays.copyOfRange(requestBytes, delimIndex, requestBytes.length));

		return tokenList;
	}

	public static void main(String argv[])
	{
		byte tests[] = new byte[1];

		List <byte[]> testList = splitRequestLine(tests);
		try{
			String requestType = new String(testList.get(0), "UTF-8");
		}
		catch(Exception e){
			e.printStackTrace();
		}
		

		// try{
		// 	String requestType = new String(tests, "UTF-8");
		// 	System.out.println(requestType);

		// 	if (requestType.equals("\0") )
		// 		System.out.println("correct");
		// }
		// catch(IOException e){}
		

		// for (int i = 0; i < tests.length; ++i){
		// 	System.out.print(tests[i]);	
		// 	System.out.print(", ");
		// }
		// System.out.println("");
		// List <byte[]> testList = splitRequestLine(tests);

		// for (int i=0; i < testList.size(); ++i){
		// 	for (int j = 0; j < testList.get(i).length; j++){
		// 		System.out.print(testList.get(i)[j]);
		// 		System.out.print(", ");
		// 	}
		// 	System.out.println("");
		// }



	}
}