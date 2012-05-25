package com.marklogic.client.io;

import com.marklogic.client.Format;
import com.marklogic.client.io.marker.AbstractReadHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;

public class HandleAccessor {
	static public void checkHandle(Object object, String type) {
		if (!isHandle(object))
			throw new IllegalArgumentException(
					type+" handle does not extend BaseHandle: "+object.getClass().getName()
					);
	}
	static public boolean isHandle(Object object) {
		return object == null || object instanceof BaseHandle;
	}

	static public <R extends AbstractReadHandle> Class<R> receiveAs(R handle) {
		if (handle == null)
			return null;
		return ((BaseHandle) handle).receiveAs();
	}
	static public <R extends AbstractReadHandle> void receiveContent(R handle, Object content) {
		if (handle == null)
			return;
		((BaseHandle) handle).receiveContent(content);
	}
	static public <W extends AbstractWriteHandle> Object sendContent(W handle) {
		if (handle == null)
			return null;
		return ((BaseHandle) handle).sendContent();
	}
	static public Format getFormat(Object handle) {
		if (handle == null)
			return null;
		return ((BaseHandle) handle).getFormat();
	}
	static public void setFormat(Object handle, Format format) {
		if (handle == null)
			return;
		((BaseHandle) handle).setFormat(format);
	}
}