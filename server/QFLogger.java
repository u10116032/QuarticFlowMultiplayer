import java.io.*;
import java.time.*;

public enum QFLogger {
	INSTANCE;

	public void Log(String logMessage) {

		System.out.println(LocalDateTime.now().toString() + "\t" + logMessage);
	}
}