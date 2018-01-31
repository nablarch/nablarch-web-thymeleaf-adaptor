package nablarch.fw.web.handler.responsewriter.thymeleaf;

import nablarch.test.support.web.servlet.MockServletResponse;

import java.io.PrintWriter;
import java.io.StringWriter;

public class WritableMockResponse extends MockServletResponse {

    private final StringWriter writer = new StringWriter();

    @Override
    public PrintWriter getWriter() {
        return new PrintWriter(writer);
    }

    public String getBodyString() {
        return writer.toString();
    }
}
