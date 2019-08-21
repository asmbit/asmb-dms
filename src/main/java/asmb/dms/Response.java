package asmb.dms;

import java.io.InputStream;
import java.util.Map;

public interface Response {

	public int getCode();

	public boolean isSuccess();

	public Map<String, Object> getBody();

	public InputStream getByteStream();

	public void close();

}
