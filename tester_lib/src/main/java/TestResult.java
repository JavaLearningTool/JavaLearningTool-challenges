public abstract class TestResult {

	protected String info;
	protected boolean passed;

	protected long time;
	protected boolean timeout;

	protected boolean runtimeExceptionOccurred = false;
	protected String errorMessage;

	public TestResult(String info, boolean passed, long time, boolean timeout, String errorMessage) {
		this.info = info;
		this.passed = passed;
		this.time = time;
		this.timeout = timeout;
		this.errorMessage = errorMessage;
		this.runtimeExceptionOccurred = errorMessage != null;
	}

	public String toJsonString() {
		return String.format(
				"{\"info\": \"%s\", \"passed\": \"%s\", \"time\": \"%s\", \"timeout\": \"%s\", \"runtimeException\": \"%s\", \"parts\": [%s]}",
				jsonEscapeString(info), passed ? "true" : "false", time, timeout ? "true" : "false",
				runtimeExceptionOccurred ? "true" : "false",
				getPartsString(timeout, runtimeExceptionOccurred).toString());
	}

	protected StringBuilder getPartsString(boolean timeout, boolean runtimeExceptionOccurred) {

		StringBuilder builder = new StringBuilder("");

		if (runtimeExceptionOccurred) {
			builder.append(getPartString("Runtime Exception", errorMessage, true));
		}

		return builder;
	}

	protected String getPartString(String label, String message, boolean multiLine) {
		return String.format("{\"label\": \"%s\", \"message\": \"%s\", \"multiLine\": \"%s\"}", jsonEscapeString(label),
				jsonEscapeString(message), multiLine ? "true" : "false");
	}

	public static String jsonEscapeString(String escape) {
		if (escape == null) {
			return "null";
		}
		String ret = escape;
		ret = ret.replace("\\", "\\\\");
		ret = ret.replace("\"", "\\\"");
		ret = ret.replace("'", "&quot;"); // JSON can't accept ' for some reason
		return ret;
	}

	public boolean getPassed() {
		return passed;
	}
}